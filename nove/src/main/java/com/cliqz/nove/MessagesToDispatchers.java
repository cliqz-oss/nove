package com.cliqz.nove;

/**
 * A messages to dispatchers table that supports concurrent modifications trought {@link COWMap} and {@link COWSet}.
 */
class MessagesToDispatchers {

    private COWMap<Class, COWSet<Dispatcher>> messagesToDispatchers = new COWMap<>();

    void addDispatcherFor(Class clazz, Dispatcher dispatcher) {
        final COWSet<Dispatcher> set = getDispatchers(clazz);
        set.add(dispatcher);
    }

    void removeDispatcher(Class clazz, Dispatcher dispatcher) {
        final COWSet<Dispatcher> set = getDispatchers(clazz);
        set.remove(dispatcher);
    }

    private COWSet<Dispatcher> getDispatchers(Class clazz) {
        COWSet<Dispatcher> set;
        while ((set = messagesToDispatchers.get(clazz)) == null) {
            synchronized (this) {
                if (messagesToDispatchers.get(clazz) == null) {
                    messagesToDispatchers.put(clazz, new COWSet<Dispatcher>());
                }
            }
        }
        return set;
    }

    void dispatch(Object object) {
        final COWSet<Dispatcher> set = getDispatchers(object.getClass());
        for (Dispatcher dispatcher: set) {
            dispatcher.post(object);
        }
    }
}
