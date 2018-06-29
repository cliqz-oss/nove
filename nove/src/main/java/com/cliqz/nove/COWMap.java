package com.cliqz.nove;

import java.util.*;

/**
 * A COW map implementation, it uses an HashMap internally.
 *
 * @param <K> key type
 * @param <V> value type
 */
class COWMap<K, V> extends COWObject<Map<K, V>> implements Map<K, V> {

    private Map<K, V> readOnlyMap = new HashMap<>();

    @Override
    protected Map<K, V> getRef() {
        return readOnlyMap;
    }

    @Override
    protected void replaceRef(Map<K, V> newRef) {
        readOnlyMap = newRef;
    }

    @Override
    protected Map<K, V> cloneRef() {
        return new HashMap<>(readOnlyMap);
    }

    @Override
    public int size() {
        return readOnlyMap.size();
    }

    @Override
    public boolean isEmpty() {
        return readOnlyMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return readOnlyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return readOnlyMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return readOnlyMap.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return performOp(new COWOp<Map<K, V>, V>() {
            @Override
            public V call(Map<K, V> ref) {
                return ref.put(key, value);
            }
        });
    }

    @Override
    public V remove(final Object key) {
        return performOp(new COWOp<Map<K, V>, V>() {
            @Override
            public V call(Map<K, V> ref) {
                //noinspection SuspiciousMethodCalls
                return ref.remove(key);
            }
        });
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        performOp(new COWOp<Map<K,V>, V>() {
            @Override
            public V call(Map<K, V> ref) {
                ref.putAll(m);
                return null;
            }
        });
    }

    @Override
    public void clear() {
        final Map<K, V> m = new HashMap<>();
        final Map<K, V> copyRef = readOnlyMap;
        //noinspection StatementWithEmptyBody
        while (!syncSwapRefs(copyRef, m)) {
            // Just try to swap the references
        }
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(readOnlyMap.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(readOnlyMap.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(readOnlyMap.entrySet());
    }
}
