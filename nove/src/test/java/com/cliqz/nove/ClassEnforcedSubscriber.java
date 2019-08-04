package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
class ClassEnforcedSubscriber {

    ClassEnforcedSubscriber(Bus bus) {
        bus.register(this, ClassEnforcedSubscriber.class);
    }

    void receiveString(String msg) {
        System.out.println(msg);
    }

}
