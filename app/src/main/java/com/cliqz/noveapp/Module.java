package com.cliqz.noveapp;

import android.util.Log;

import com.cliqz.nove.Bus;
import com.cliqz.nove.Subscribe;

/**
 * @author Stefano Pacifici
 * @date 2016/12/24
 */

public class Module {

    private final Bus bus;

    public Module(Bus bus) {
        this.bus = bus;
    }

    @Subscribe
    void onMessage(ModuleMessage msg) {
        Log.v(this.getClass().getSimpleName(), "Message received");
    }

    static class ModuleMessage {}
}
