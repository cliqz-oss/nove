package com.cliqz.nove;

import java.util.*;

public class SubscribersRegisterImpl extends SubscribersRegister {

    private final Map<Class, Set<Dispatcher>> register = new HashMap<>();

    @Override
    public void register(Class clazz, Dispatcher dispatcher) {
        final Set<Dispatcher> dispatchers;
        if (register.containsKey(clazz)) {
            dispatchers = register.get(clazz);
        } else {
            dispatchers = new LinkedHashSet<>();
            register.put(clazz, dispatchers);
        }
        dispatchers.add(dispatcher);
    }

    @Override
    public void unregister(Class clazz, Dispatcher dispatcher) {

    }

    @Override
    public Collection<Dispatcher> findDispatchers(Object msg) {
        return register.get(msg.getClass());
    }
}
