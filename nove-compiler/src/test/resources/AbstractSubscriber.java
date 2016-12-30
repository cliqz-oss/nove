import com.cliqz.nove.Subscribe;

public abstract class AbstractSubscriber {

    @Subscribe
    abstract void abstractSubscriber(Integer msg) {}
}