import com.cliqz.nove.Subscribe;

public class NonVoidResultSubscriber {

    @Subscribe
    int nonVoidResultSubscriber(Integer msg) {
        return msg.intValue();
    }
}