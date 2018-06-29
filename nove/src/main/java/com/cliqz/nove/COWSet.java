package com.cliqz.nove;

import java.util.*;

/**
 * A COW Set implementation. It forwards every call to a {@link COWMap} in which the values are irrelevant.
 * This implementation does not support {@link Set#removeAll(Collection)} and {@link Set#retainAll(Collection)}.
 *
 * @param <T> the elements type
 */
class COWSet<T> implements Set<T> {
    private static final Object marker = new Object();
    private COWMap<T, Object> set = new COWMap<>();

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return set.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return set.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return set.keySet().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        //noinspection SuspiciousToArrayCall
        return set.keySet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return set.put(t, marker) == null;
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = true;
        for (T key: c) {
            result &= set.put(key, marker) == null;
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        set.clear();
    }
}
