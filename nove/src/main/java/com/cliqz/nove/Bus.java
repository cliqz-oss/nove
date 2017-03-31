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
    private final Map<Object, Dispatcher> dispatcherMap = new HashMap<>();
    private final Map<Class, Set<Dispatcher>> messageToDispatchers = new HashMap<>();

    /**
     * Registers the given object to the bus as messages listener
     *
     * @param object object to register as listener
     * @throws IllegalArgumentException if object is null
     */
    public void register(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Trying to register a null reference");
        }
        if (!dispatcherMap.containsKey(object)) {
            try {
                final Dispatcher dispatcher = new Dispatcher(object);
                addDispatcherFor(object, dispatcher);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Visible for testing, load the compile time generated dispatcher for the given object
    @SuppressWarnings("WeakerAccess")
    void addDispatcherFor(Object object, Dispatcher dispatcher) {
        dispatcherMap.put(object, dispatcher);
        for (Class clazz: dispatcher.getMessageTypes()) {
            Set<Dispatcher> dispatcherSet = messageToDispatchers.get(clazz);
            if (dispatcherSet == null) {
                dispatcherSet = new HashSet<>();
                messageToDispatchers.put(clazz, dispatcherSet);
            }
            dispatcherSet.add(dispatcher);
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
                    Set objects = messageToDispatchers.get(clazz);
                    if (objects != null) {
                        objects.remove(dispatcher);
                    }
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
        final Set<Dispatcher> dispatchers = messageToDispatchers.get(object.getClass());
        if (dispatchers != null) {
            for (Dispatcher dispatcher: dispatchers) {
                dispatcher.post(object);
            }
        }
    }

    // Visible for testing, utility inner class that encapsulate loading of the specific, generated
    // dispatcher via reflection. It forward the calls to the compile time generated post methods
    // by caching the reference
    @SuppressWarnings("WeakerAccess")
    static class Dispatcher {
        private final Object dispatcher;
        private final Method post;
        private final Class[] messageTypes;

        Dispatcher(Object object) throws Exception {
            final String dispatcherClassName =
                    object.getClass().getCanonicalName() + DISPATCHER_POSTFIX;
            final Class dispatcherClass = loader.loadClass(dispatcherClassName);
            //noinspection unchecked
            final Constructor constructor =
                    dispatcherClass.getConstructor(object.getClass());
            dispatcher = constructor.newInstance(object);

            //noinspection unchecked
            post = dispatcherClass
                    .getDeclaredMethod(POST_METHOD_NAME, Object.class);
            messageTypes = (Class[]) dispatcherClass
                    .getDeclaredField(MESSAGE_TYPES_FIELD_NAME).get(null);
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
