package com.cliqz.noveapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.cliqz.nove.Bus;
import com.cliqz.nove.Subscribe;

public class MainActivity extends AppCompatActivity {

    private Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBus = new Bus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
        mBus.post(new Message());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Subscribe
    void listener(Message message) {
        Log.v(MainActivity.class.getSimpleName(), "Message received");
    }

    @Subscribe
    void secondListener(Integer value) {
    }

    public static class Message {

    }
}
