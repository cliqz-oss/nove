package com.cliqz.nove;

/**
 * Basic Copy On Write support.
 *
 * @param <T> wrapped reference type
 */
abstract class COWObject<T> {

    /**
     * Copy On Write OPeration. Perform an operation over T1 that returns a V1
     *
     * @param <T1> The type on which the operation will be performed
     * @param <V1> The type of the operation return value
     */
    interface COWOp<T1, V1> {

        /**
         * Performs the COW operation
         *
         * @param ref the object reference on which the operation will be performed
         * @return a reference to the operation result value, it can be {@code null}
         */
        V1 call(T1 ref);
    }

    /**
     * The core of the COW support. It clones the the original reference, applies the operation to the clone and tries
     * to swap the references. It repeat the cycle if the original reference changed while we perform the operation.
     *
     * @param operation the operation to be performed
     * @param <V> the operation return value type
     * @return the value returned by calling {@link COWOp#call(Object)}
     */
    final <V> V performOp(COWOp<T, V> operation) {
        T origRef;
        T newRef;
        V value;

        do {
            origRef = getRef();
            newRef = cloneRef();
            assert origRef != null && newRef != null && origRef != newRef;
            value = operation.call(newRef);
        } while (!syncSwapRefs(origRef, newRef));
        return value;
    }

    /**
     * DO NOT USE THIS METHOD DIRECTLY. It swaps the wrapped reference with the a new reference if the first is equal
     * to the so called origRef.
     *
     * @param origRef the original reference value taken when {@link COWObject#performOp(COWOp)} cloned it
     * @param newRef the cloned reference on which we performed the operation
     * @return true if origRef is equal (in the == sense) to the wrapped reference and we swapped the references,
     *         false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    final synchronized boolean syncSwapRefs(T origRef, T newRef) {
        if (origRef != getRef()) {
            return false;
        }
        replaceRef(newRef);
        return true;
    }

    /**
     * The implementing class must return the wrapped reference with this call
     *
     * @return the wrapped reference
     */
    protected abstract T getRef();

    /**
     * The implementing class must replace the wrapped reference with the new one in this call.
     *
     * @param newRef the reference which replace the wrapped one
     */
    protected abstract void replaceRef(T newRef);

    /**
     * This method must perform a deep clone of the wrapped ref and return the clone reference
     *
     * @return a deep clone of the wrapped reference
     */
    protected abstract T cloneRef();
}