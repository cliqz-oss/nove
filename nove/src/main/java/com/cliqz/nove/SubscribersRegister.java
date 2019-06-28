package com.cliqz.nove;

import java.util.Collection;

abstract class SubscribersRegister {
    public abstract void register(Class clazz, Dispatcher dispatcher);

    public abstract void unregister(Class clazz, Dispatcher dispatcher);

    protected abstract Collection<Dispatcher> findDispatchers(Object msg);

    public final void dispatch(Object msg) {
        final Collection<Dispatcher> dispatchers = findDispatchers(msg);
        if (dispatchers != null) {
            for (Dispatcher dispatcher: dispatchers) {
                dispatcher.post(msg);
            }
        }
    }
}
