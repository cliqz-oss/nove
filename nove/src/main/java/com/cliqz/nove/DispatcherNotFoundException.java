package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
public final class DispatcherNotFoundException extends RuntimeException {
    DispatcherNotFoundException(String dispatcherClassName) {
        super("Can not find the " + dispatcherClassName + " class");
    }
}
