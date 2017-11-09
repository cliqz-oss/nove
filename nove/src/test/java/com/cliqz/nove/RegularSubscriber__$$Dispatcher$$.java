package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
class RegularSubscriber__$$Dispatcher$$ {
    public static final Class[] MESSAGE_TYPES = new Class[] { String.class };

    private final RegularSubscriber subscriber;

    public RegularSubscriber__$$Dispatcher$$(RegularSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void post(Object message) {
        if (message instanceof String) {
            final String msg = (String) message;
            subscriber.receiveString(msg);
        }
    }
}
