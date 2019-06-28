package com.cliqz.nove;

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
                for (Class clazz: dispatcher.getMessageTypes()) {
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

}
