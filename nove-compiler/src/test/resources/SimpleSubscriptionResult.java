import java.lang.Class;
import java.lang.Integer;
import java.lang.Object;

public final class SimpleSubscription__$$Dispatcher$$ {
    public static final Class[] MESSAGE_TYPES = new Class[] { java.lang.Integer.class };

    private final SimpleSubscription subscriber;

    public SimpleSubscription__$$Dispatcher$$(SimpleSubscription subscriber) {
        this.subscriber = subscriber;
    }

    public void post(Object message) {
        if (message instanceof Integer) {
            final Integer msg = (Integer) message;
            subscriber.integerSubscription(msg);
        }
    }
}
