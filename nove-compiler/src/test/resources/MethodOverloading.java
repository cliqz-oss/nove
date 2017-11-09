import com.cliqz.nove.Subscribe;

public class MethodOverloading {

    @Subscribe
    void onEvent(String event) {}

    @Subscribe
    void onEvent(Integer event) {}
}