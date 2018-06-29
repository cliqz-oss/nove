package com.cliqz.nove;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A bus dispatches messages to the listeners and provides a method for listeners to register to the
 * bus itself.
 *
 * @author Stefano Pacifici
 */
public class Bus {

    static final String DISPATCHER_POSTFIX = "__$$Dispatcher$$";
    static final String POST_METHOD_NAME = "post";
    static final String MESSAGE_TYPES_FIELD_NAME = "MESSAGE_TYPES";

    private final static ClassLoader loader = Bus.class.getClassLoader();
    private final Map<Object, Dispatcher> dispatcherMap = new COWMap<>();
    private final MessagesToDispatchers messageToDispatchers = new MessagesToDispatchers();


    /**
     * Registers the given object to the bus as messages listener.
     *
     * @param object object to register as listener
     * @throws IllegalArgumentException if object is null
     */
    public <T> void register(T object) {
        @SuppressWarnings("unchecked")
        final Class<T> clazz = object != null ? (Class<T>) object.getClass() : null;
        register(object, clazz);
    }

    /**
     * Registers the given object to the bus as messages listener forcing the base class type. Use this in a base
     * class in case you extended it.
     *
     * @param object object to register as listener
     * @param clazz object class, used especially to register superclasses
     * @throws IllegalArgumentException if object is null
     */
    public <T> void register(T object, Class<T> clazz) {
        if (object == null) {
            throw new IllegalArgumentException("Trying to register a null reference");
        }
        if (!dispatcherMap.containsKey(object)) {
            final Dispatcher<T> dispatcher = new Dispatcher<>(object, clazz);
            dispatcherMap.put(object, dispatcher);
            addDispatcherFor(object, dispatcher);
        }
    }

    // Visible for testing, load the compile time generated dispatcher for the given object
    @SuppressWarnings("WeakerAccess")
    void addDispatcherFor(Object object, Dispatcher dispatcher) {
        for (Class clazz: dispatcher.getMessageTypes()) {
            messageToDispatchers.addDispatcherFor(clazz, dispatcher);
        }
    }

    /**
     * Removes the given object from the listeners register. After this call no message will be
     * forwarded to the object
     *
     * @param object the listener to unregister
     */
    public void unregister(Object object) {
        if (dispatcherMap.containsKey(object)) {
            final Dispatcher dispatcher = dispatcherMap.remove(object);
            try {
                for (Class clazz: dispatcher.messageTypes) {
                    messageToDispatchers.removeDispatcher(clazz, dispatcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Post a message to all the registered listeners
     *
     * @param object a message to be dispatched to the proper listeners
     */
    public void post(Object object) {
        messageToDispatchers.dispatch(object);
    }

    // Visible for testing, utility inner class that encapsulate loading of the specific, generated
    // dispatcher via reflection. It forward the calls to the compile time generated post methods
    // by caching the reference
    @SuppressWarnings("WeakerAccess")
    static class Dispatcher<T> {
        private final Object dispatcher;
        private final Method post;
        private final Class[] messageTypes;

        Dispatcher(T object, Class<T> clazz) {
            final String dispatcherClassName = clazz.getCanonicalName() + DISPATCHER_POSTFIX;
            try {
                final Class<?> dispatcherClass = loader.loadClass(dispatcherClassName);
                final Constructor<?> constructor =
                        dispatcherClass.getConstructor(clazz);
                dispatcher = constructor.newInstance(object);

                //noinspection unchecked
                post = dispatcherClass
                        .getDeclaredMethod(POST_METHOD_NAME, Object.class);
                messageTypes = (Class[]) dispatcherClass
                        .getDeclaredField(MESSAGE_TYPES_FIELD_NAME).get(null);
            } catch (ClassNotFoundException cnfe) {
                // This is only useful to properly address problems due to class hierarchies
                final Class sup = clazz.getSuperclass();
                if (sup != null && !sup.isInterface() && !sup.isPrimitive()) {
                    // Check if a concrete or abstract parent class has a Dispatcher
                    final String disName = sup.getCanonicalName() + DISPATCHER_POSTFIX;
                    try {
                        final Class dispatcherClass = loader.loadClass(disName);
                        throw new SubclassRegistrationException(sup);
                    } catch (ClassNotFoundException innerCnfe) {
                        // NOP
                    }
                }
                // The class should have at least one Subscribe annotated method
                throw new DispatcherNotFoundException(dispatcherClassName);
            } catch (Exception e) {
                // Re-throw any other exception as a RuntimeException
                throw new RuntimeException(e);
            }
        }

        void post(Object message) {
            try {
                post.invoke(dispatcher, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Class[] getMessageTypes() {
            return messageTypes;
        }
    }
}
