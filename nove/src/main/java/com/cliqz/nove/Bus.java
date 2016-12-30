package com.cliqz.nove;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Bus {

    static final String DISPATCHER_POSTFIX = "__$$Dispatcher$$";
    static final String POST_METHOD_NAME = "post";
    static final String MESSAGE_TYPES_FIELD_NAME = "MESSAGE_TYPES";

    private final static ClassLoader loader = Bus.class.getClassLoader();
    private final Map<Object, Dispatcher> dispatcherMap = new HashMap<>();
    private final Map<Class, Set<Dispatcher>> messageToDispatchers = new HashMap<>();

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

    // Visible for testing
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

    public void post(Object object) {
        final Set<Dispatcher> dispatchers = messageToDispatchers.get(object.getClass());
        if (dispatchers != null) {
            for (Dispatcher dispatcher: dispatchers) {
                dispatcher.post(object);
            }
        }
    }

    // Visible for testing
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
