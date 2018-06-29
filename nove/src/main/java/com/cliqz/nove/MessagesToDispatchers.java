package com.cliqz.nove;

/**
 * A messages to dispatchers table that supports concurrent modifications trought {@link COWMap} and {@link COWSet}.
 */
class MessagesToDispatchers {

    private COWMap<Class, COWSet<Bus.Dispatcher>> messagesToDispatchers = new COWMap<>();

    void addDispatcherFor(Class clazz, Bus.Dispatcher dispatcher) {
        final COWSet<Bus.Dispatcher> set = getDispatchers(clazz);
        set.add(dispatcher);
    }

    void removeDispatcher(Class clazz, Bus.Dispatcher dispatcher) {
        final COWSet<Bus.Dispatcher> set = getDispatchers(clazz);
        set.remove(dispatcher);
    }

    private COWSet<Bus.Dispatcher> getDispatchers(Class clazz) {
        COWSet<Bus.Dispatcher> set;
        while ((set = messagesToDispatchers.get(clazz)) == null) {
            synchronized (this) {
                if (messagesToDispatchers.get(clazz) == null) {
                    messagesToDispatchers.put(clazz, new COWSet<Bus.Dispatcher>());
                }
            }
        }
        return set;
    }

    void dispatch(Object object) {
        final COWSet<Bus.Dispatcher> set = getDispatchers(object.getClass());
        for (Bus.Dispatcher dispatcher: set) {
            dispatcher.post(object);
        }
    }
}
