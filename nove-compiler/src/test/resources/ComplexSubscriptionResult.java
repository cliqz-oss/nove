import java.lang.Class;
import java.lang.Object;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ComplexSubscription__$$Dispatcher$$ {
    public static final Set<Class> MESSAGE_TYPES = new HashSet<Class>(Arrays.asList(ComplexSubscription.Message1.class,ComplexSubscription.Message2.class,ComplexSubscription.Message3.class));

    private final ComplexSubscription subscriber;

    public ComplexSubscription__$$Dispatcher$$(ComplexSubscription subscriber) {
        this.subscriber = subscriber;
    }

    public void post(Object message) {
        if (message instanceof ComplexSubscription.Message1) {
            final ComplexSubscription.Message1 msg = (ComplexSubscription.Message1) message;
            subscriber.receiveMessage1(msg);
        }
        else if (message instanceof ComplexSubscription.Message2) {
            final ComplexSubscription.Message2 msg = (ComplexSubscription.Message2) message;
            subscriber.receiveMessage2(msg);
        }
        else if (message instanceof ComplexSubscription.Message3) {
            final ComplexSubscription.Message3 msg = (ComplexSubscription.Message3) message;
            subscriber.receiveMessage3(msg);
        }
    }
}