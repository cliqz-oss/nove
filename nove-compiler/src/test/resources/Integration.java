import com.cliqz.nove.Subscribe;

public class Integration {

    private String message = null;

    public String getMessage() {
        return message;
    }

    @Subscribe
    public void onBusMessage(BusMessage message) {
        this.message = message.content;
    }

    public static final class BusMessage {
        final String content;

        public BusMessage(String message) {
            content = message;
        }
    }
}