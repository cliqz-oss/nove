import com.cliqz.nove.Subscribe;

public class ComplexSubscription {

    @Subscribe
    void receiveMessage1(Message1 msg) {}

    @Subscribe
    void receiveMessage2(Message2 msg) {}

    @Subscribe
    void receiveMessage3(Message3 msg) {}

    public static class Message1 {}

    public static class Message2 {}

    public static class Message3 {}
}