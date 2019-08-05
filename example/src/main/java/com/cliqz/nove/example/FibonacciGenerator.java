package com.cliqz.nove.example;

import com.cliqz.nove.Bus;
import com.cliqz.nove.Subscribe;

class FibonacciGenerator {

    private final Bus bus;
    private int prevValue = 0;
    private int currentValue = 1;

    FibonacciGenerator(Bus bus) {
        bus.register(this);
        this.bus = bus;
    }

    @Subscribe
    void processRequest(@SuppressWarnings("unused") Messages.RequestNextValue req) {
        final int response = currentValue;
        currentValue = prevValue + currentValue;
        prevValue = response;
        bus.post(new Messages.NextValueResponse(response));
    }
}
