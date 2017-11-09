package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
class ClassEnforcedSubscriber__$$Dispatcher$$ {
    public static final Class[] MESSAGE_TYPES = new Class[] { String.class };

    private final ClassEnforcedSubscriber subscriber;

    public ClassEnforcedSubscriber__$$Dispatcher$$(ClassEnforcedSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void post(Object message) {
        if (message instanceof String) {
            final String msg = (String) message;
            subscriber.receiveString(msg);
        }
    }
}
