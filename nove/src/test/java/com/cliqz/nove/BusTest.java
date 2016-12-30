package com.cliqz.nove;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by stefano on 30/12/16.
 */
@SuppressWarnings("UnnecessaryBoxing")
public class BusTest {

    private Bus bus;

    @Before
    public void init() {
        bus = new Bus();
    }
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowTryingToRegisterNullObject() {
        bus.register(null);
    }

    @Test
    public void shouldDeliverMessages() {
        Bus.Dispatcher dispatcher = mock(Bus.Dispatcher.class);
        when(dispatcher.getMessageTypes())
                .thenReturn(new Class[] { Integer.class });
        bus.addDispatcherFor(this, dispatcher);
        bus.post(Integer.valueOf(5));
        verify(dispatcher).post(Integer.valueOf(5));
    }

    @Test
    public void shouldNotDeliverUnexpectedTypes() {
        Bus.Dispatcher dispatcher = mock(Bus.Dispatcher.class);
        when(dispatcher.getMessageTypes())
                .thenReturn(new Class[] { Integer.class });
        bus.addDispatcherFor(this, dispatcher);
        reset(dispatcher);
        bus.post(Long.valueOf(4));
        verifyZeroInteractions(dispatcher);
    }

    @Test
    public void shouldLoadADispatcher() {
        final String msg = "Hello, nove!!!";
        final Subscriber subscriber = new Subscriber();
        bus.register(subscriber);
        bus.post(msg);
        assertThat(subscriber.msg, is(notNullValue()));
        assertThat(subscriber.msg, equalTo(msg));
    }

}
