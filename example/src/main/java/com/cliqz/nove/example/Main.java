package com.cliqz.nove.example;

import com.cliqz.nove.Bus;
import com.cliqz.nove.Subscribe;

public class Main {

    private static final int MAX_REQUESTS = 20;

    private final Bus bus;
    private int counter = 0;

    @Subscribe
    void printResponse(Messages.NextValueResponse response) {
        System.out.println(response.value);
        if (counter < MAX_REQUESTS) {
            counter++;
            bus.post(new Messages.RequestNextValue());
        }
    }

    private Main(Bus bus) {
        this.bus = bus;
        this.bus.register(this);
    }

    public static void main(String[] args) {
        final Bus bus = new Bus();
        new Main(bus);
        new FibonacciGenerator(bus);
        bus.post(new Messages.RequestNextValue());
    }
}
