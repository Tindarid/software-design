package com.sd.test;

import com.sd.lib.LRUCache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class LRUCacheTest {
    @Test(expected = AssertionError.class)
    public void testEmptyIsNotAllowed() {
        LRUCache<String, String> lru = new LRUCache<>(0);
    }

    @Test
    public void testSimpleActions() {
        LRUCache<Integer, Integer> lru = new LRUCache<>(10);
        Random r = new Random();

        int k = r.nextInt();
        int v = r.nextInt();
        lru.put(k, v);

        int storedV = lru.get(k);
        assertEquals(v, storedV);
        lru.remove(k);
        assertNull(lru.get(k));

        lru.put(k, v);
        lru.put(k, v);
        assertEquals(v, lru.get(k).intValue());
    }

    @Test
    public void testSimpleActionsSet() {
        final int size = 100;
        final int iters = 10000;

        LRUCache<Integer, Integer> lru = new LRUCache<>(size);
        Random r = new Random();

        for (int i = 0; i < iters; ++i) {
            Integer k = r.nextInt();
            Integer v = r.nextInt();
            lru.put(k, v);
            assertEquals(v, lru.get(k));
        }
    }

    @Test
    public void testMapFunctionality() {
        final int size = 1000;

        LRUCache<Integer, Integer> lru = new LRUCache<>(size);
        HashMap<Integer, Integer> cache = new HashMap<>();
        Random r = new Random();

        for (int i = 0; i < size; ++i) {
            int k = r.nextInt();
            int v = r.nextInt();
            cache.put(k, v);
            lru.put(k, v);
        }

        for (Map.Entry<Integer, Integer> entry : cache.entrySet()) {
            assertEquals(entry.getValue(), lru.get(entry.getKey()));
            lru.remove(entry.getKey());
        }

        assertEquals(0, lru.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        LRUCache<Integer, Integer> lru = new LRUCache<>(1);
        lru.put(1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        LRUCache<Integer, Integer> lru = new LRUCache<>(1);
        lru.put(null, 1);
    }

    @Test
    public void testOnlyLast() {
        final int size = 1;
        final int iters = 10000;

        LRUCache<Integer, Integer> lru = new LRUCache<>(size);
        Random r = new Random();

        int k = 0, v = 0;
        for (int i = 0; i < iters; ++i) {
            k = r.nextInt();
            v = r.nextInt();
            lru.put(k, v);
        }
        assertEquals(v, lru.get(k).intValue());
        assertEquals(1, lru.size());
    }

    @Test
    public void testLeastRecentlyUsed() {
        final int rounds = 100;
        final int size = 1000;
        LRUCache<Integer, Integer> lru = new LRUCache<>(size);

        for (int r = 0; r < rounds; ++r) {
            for (int ind = 0; ind < size; ++ind) {
                int cur = ind + size * r;
                lru.put(cur, cur);
            }

            for (int ind = 0; ind < size; ++ind) {
                int cur = ind + size * r;
                assertNotNull(lru.get(cur));
            }
            assertEquals(size, lru.size());
        }
    }

    @Test
    public void testLeastRecentlyUsedRandomized() {
        final int rounds = 10000;
        final int size = 100;
        final int bound = 500;

        LRUCache<Integer, Integer> lru = new LRUCache<>(size);
        ArrayList<Integer> values = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < bound; ++i) {
            values.add(i);
        }

        for (int r = 0; r < rounds; ++r) {
            int ind = rand.nextInt(values.size());
            int next = values.get(ind);
            if (rand.nextBoolean()) {
                if (lru.get(next) != null) {
                    values.add(next);
                }
            } else {
                lru.put(next, next);
                values.add(next);
            }
        }

        HashMap<Integer, Integer> cache = new HashMap<>();
        for (int i = values.size() - 1; i >= 0; --i) {
            int value = values.get(i);
            if (cache.get(value) != null || cache.size() < size) {
                assertNotNull(lru.get(value));
                cache.put(value, value);
            } else {
                assertNull(lru.get(value));
            }
        }
    }
}
