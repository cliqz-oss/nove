package com.cliqz.nove;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Bus {

    private final static ClassLoader loader = Bus.class.getClassLoader();
    private final Map<Object, Dispatcher> dispatcherMap = new HashMap<>();
    private final Map<Class, Set<Dispatcher>> messagesToObject = new HashMap<>();

    public void register(Object object) {
        if (!dispatcherMap.containsKey(object)) {
            try {
                final Dispatcher dispatcher = new Dispatcher(object);
                dispatcherMap.put(object, dispatcher);
                final Class[] messages = (Class[]) dispatcher.getMessagesType.invoke(dispatcher.dispatcher);
                for (Class clazz: messages) {
                    Set<Dispatcher> objects = messagesToObject.get(clazz);
                    if (objects == null) {
                        objects = new HashSet<>();
                        messagesToObject.put(clazz, objects);
                    }
                    objects.add(dispatcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unregister(Object object) {
        if (dispatcherMap.containsKey(object)) {
            final Dispatcher dispatcher = dispatcherMap.remove(object);
            try {
                final Class[] messages = (Class[]) dispatcher.getMessagesType.invoke(dispatcher.dispatcher);
                for (Class clazz: messages) {
                    Set objects = messagesToObject.get(clazz);
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
        Set<Dispatcher> dispatchers = messagesToObject.get(object.getClass());
        if (dispatchers != null) {
            for (Dispatcher dispatcher: dispatchers) {
                try {
                    dispatcher.post.invoke(dispatcher.dispatcher, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Dispatcher {
        final Object dispatcher;
        final Method post;
        final Method getMessagesType;

        Dispatcher(Object object) throws Exception {
            final String dispatcherClassName =
                    object.getClass().getCanonicalName() + "__$$Dispatcher$$";
            final Class dispatcherClass = loader.loadClass(dispatcherClassName);
            final Constructor cstr = dispatcherClass.getConstructor(object.getClass());
            dispatcher = cstr.newInstance(object);

            post = dispatcherClass.getDeclaredMethod("post", Object.class);
            getMessagesType = dispatcherClass.getDeclaredMethod("getMessagesType");
        }
    }
}
