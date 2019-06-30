package com.cliqz.nove;

import java.util.HashMap;
import java.util.Map;

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

    private final Map<Object, Dispatcher> dispatcherMap = new HashMap<>();
    private static SubscribersRegister sMessageToDispatchers;

    public Bus() {
        synchronized (Bus.class) {
            if (sMessageToDispatchers == null) {
                try {
                    final Class clazz = Bus.class.getClassLoader()
                            .loadClass("com.cliqz.nove.SubscribersRegisterImpl");
                    sMessageToDispatchers = (SubscribersRegister) clazz.newInstance();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Registers the given object to the bus as messages listener.
     *
     * @param object object to register as listener
     * @throws IllegalArgumentException if object is null
     */
    public synchronized <T> void register(T object) {
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
    public synchronized <T> void register(T object, Class<T> clazz) {
        if (object == null) {
            throw new IllegalArgumentException("Trying to register a null reference");
        }
        if (!dispatcherMap.containsKey(object)) {
            final Dispatcher<T> dispatcher = new Dispatcher<>(object, clazz);
            dispatcherMap.put(object, dispatcher);
            addDispatcherFor(dispatcher);
        }
    }

    // Visible for testing, load the compile time generated dispatcher for the given object
    void addDispatcherFor(Dispatcher dispatcher) {
        for (Class clazz: dispatcher.getMessageTypes()) {
            sMessageToDispatchers.register(clazz, dispatcher);
        }
    }

    /**
     * Removes the given object from the listeners register. After this call no message will be
     * forwarded to the object
     *
     * @param object the listener to unregister
     */
    public synchronized void unregister(Object object) {
        if (dispatcherMap.containsKey(object)) {
            final Dispatcher dispatcher = dispatcherMap.remove(object);
            try {
                for (Class clazz: dispatcher.getMessageTypes()) {
                    sMessageToDispatchers.unregister(clazz, dispatcher);
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
        sMessageToDispatchers.dispatch(object);
    }

}
