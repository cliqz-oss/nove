package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
class ClassEnforcedSubscriber {

    String msg = null;

    ClassEnforcedSubscriber(Bus bus) {
        bus.register(this, ClassEnforcedSubscriber.class);
    }

    void receiveString(String msg) {
        this.msg = msg;
    }

}
