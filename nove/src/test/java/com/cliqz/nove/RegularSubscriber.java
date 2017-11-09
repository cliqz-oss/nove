package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
class RegularSubscriber {

    String msg = null;

    RegularSubscriber(Bus bus) {
        bus.register(this);
    }

    void receiveString(String msg) {
        this.msg = msg;
    }

}
