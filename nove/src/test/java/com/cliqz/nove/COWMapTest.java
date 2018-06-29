package com.cliqz.nove;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class COWMapTest {

    @Test
    public void shouldElementsBeAdded() {
        final COWMap<String, Integer> map = new COWMap<>();
        Assert.assertNull(map.put("test", 1));
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertTrue(map.containsValue(1));
        Assert.assertEquals(Integer.valueOf(1), map.put("test", 2));
        Assert.assertNull(map.put("abc", 3));
        Assert.assertTrue(map.containsKey("test"));
        Assert.assertEquals(Integer.valueOf(2), map.get("test"));
        Assert.assertTrue(map.containsKey("abc"));
        Assert.assertEquals(Integer.valueOf(3), map.get("abc"));
    }


    @Test
    public void shouldSupportConcurrentModifications() throws BrokenBarrierException, InterruptedException {
        final int thrNo = 100;
        final String[] keys = generateRandomStrings(thrNo);
        final int[] values = generateRandomInts(thrNo);
        final COWMap<String, Integer> map = new COWMap<>();
        final CyclicBarrier barrier = new CyclicBarrier(thrNo + 1);
        for (int i = 0; i < thrNo; i++) {
            new Thread(new PerformPut(map, keys[i], values[i], barrier)).start();
        }
        barrier.await();
        barrier.await();

        Assert.assertEquals(thrNo, map.size());
        for (int i = 0; i < thrNo; i++) {
            Assert.assertEquals(Integer.valueOf(values[i]), map.get(keys[i]));
        }
    }

    @Test
    public void shouldNotThrowConcurrentModificationException() {
        final COWMap<String, Integer> map = new COWMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        final Iterator<String> it = map.keySet().iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("a", it.next());
        Assert.assertEquals(Integer.valueOf(2), map.remove("b"));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("b", it.next());
        Assert.assertFalse(map.containsKey("b"));
    }

    private static int[] generateRandomInts(int no) {
        final int[] result = new int[no];
        for (int i = 0; i < no; i++) {
            result[i] = (int) (Integer.MAX_VALUE * Math.random());
        }
        return result;
    }

    private static String[] generateRandomStrings(int no) {
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";
        final int len = 4;
        final StringBuilder builder = new StringBuilder(len);
        final HashSet<String> set = new HashSet<>(no);
        while (set.size() < no) {
            builder.delete(0, builder.length());
            for (int i = 0; i < len; i++) {
                final int index = (int) (Math.random() * alphabet.length());
                builder.append(alphabet.charAt(index));
            }
            set.add(builder.toString());
        }
        return set.toArray(new String[no]);
    }

    private static class PerformPut implements Runnable {
        private final CyclicBarrier barrier;
        private final int value;
        private final String key;
        private final Map<String, Integer> map;

        PerformPut(Map<String, Integer> map, String key, int value, CyclicBarrier barrier) {
            this.map = map;
            this.key = key;
            this.value = value;
            this.barrier = barrier;
        }

        public void run() {
            try {
                barrier.await();
                map.put(key, value);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
