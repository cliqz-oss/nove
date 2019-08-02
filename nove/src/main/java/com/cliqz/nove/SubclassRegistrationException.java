package com.cliqz.nove;

/**
 * @author Stefano Pacifici
 */
public final class SubclassRegistrationException extends RuntimeException {
    SubclassRegistrationException(Class dispatcherClass) {
        super("A dispatcher was found for superclass " + dispatcherClass.getSimpleName() +
            ". If you used bus.register(instance) in the superclass constructor, you should use the " +
            " bus.register(instance, class) variant instead."
        );
    }
}
