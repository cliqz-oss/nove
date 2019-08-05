package com.cliqz.nove;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Stefano Pacifici
 */
class ClassEnforcedSubscriber__$$Dispatcher$$ {
    public static final Set<Class> MESSAGE_TYPES =
            new HashSet<Class>(Collections.singletonList(String.class));

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
