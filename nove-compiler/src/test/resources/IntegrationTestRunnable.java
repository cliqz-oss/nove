package com.cliqz.nove.integration;

import com.cliqz.nove.*;

public class IntegrationTestRunnable implements Runnable {

    private Bus bus = null;
    public boolean message1Received = false;
    public boolean message2Received = false;
    public boolean message3Received = false;

    @Override
    public void run() {
        bus = new Bus();
        bus.register(this);
        bus.post(new Message1());
        bus.unregister(this);
        bus.post(new Message3());
    }

    public static class Message1 {}
    public static class Message2 {}
    public static class Message3 {}

    @Subscribe
    void onMessage1(Message1 msg) {
        message1Received = true;
        bus.post(new Message2());
    }

    @Subscribe
    void onMessage2(Message2 msg) {
        message2Received = true;
    }

    @Subscribe
    void onMessage3(Message3 msg) {
        message3Received = true;
    }
}
