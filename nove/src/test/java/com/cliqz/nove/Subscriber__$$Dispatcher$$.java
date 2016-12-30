package com.cliqz.nove;

/**
 * Created by stefano on 30/12/16.
 */
class Subscriber__$$Dispatcher$$ {
    public static final Class[] MESSAGE_TYPES = new Class[] { String.class };

    private final Subscriber subscriber;

    public Subscriber__$$Dispatcher$$(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void post(Object message) {
        if (message instanceof String) {
            final String msg = (String) message;
            subscriber.receiveString(msg);
        }
    }
}
