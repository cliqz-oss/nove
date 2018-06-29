package com.cliqz.nove;

import org.junit.Test;

public class COWObjectTest {

    @Test(expected = AssertionError.class)
    public void shouldFailIfGetRefReturnsNull() {
        final COWObject obj = faultyCOWObject(null, 42);
        //noinspection unchecked
        obj.performOp(new COWObject.COWOp() {
            @Override
            public Object call(Object ref) {
                return null;
            }
        });
    }

    @Test(expected = AssertionError.class)
    public void shouldFailIfCloneRefReturnsNull() {
        final COWObject obj = faultyCOWObject(42, null);
        //noinspection unchecked
        obj.performOp(new COWObject.COWOp() {
            @Override
            public Object call(Object ref) {
                return null;
            }
        });
    }

    @Test(expected = AssertionError.class)
    public void shouldFailIfCloneRefReturnsTheOriginalRef() {
        final Integer ref = 42;
        final COWObject obj = faultyCOWObject(ref, ref);
        //noinspection unchecked
        obj.performOp(new COWObject.COWOp() {
            @Override
            public Object call(Object ref) {
                return null;
            }
        });
    }

    private COWObject<Integer> faultyCOWObject(final Integer getRef, final Integer cloneRef) {
        return new COWObject<Integer>() {
            @Override
            protected Integer getRef() {
                return getRef;
            }

            @Override
            protected void replaceRef(Integer newRef) {
                // NOP
            }

            @Override
            protected Integer cloneRef() {
                return cloneRef;
            }
        };
    }
}
