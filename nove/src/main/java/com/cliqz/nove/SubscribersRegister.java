package com.cliqz.nove;

interface SubscribersRegister {
    void register(Class clazz, Dispatcher dispatcher);
    void unregister(Class clazz, Dispatcher dispatcher);
}
