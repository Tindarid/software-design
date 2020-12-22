package com.sd.lib;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LRUCache<K, V> {
    private final Node<K, V> dummy;
    private final HashMap<K, Node<K, V>> map;
    private int limit;

    public LRUCache(int limit) {
        assert (limit > 0) : "Size must be positive";
        this.limit = limit;
        this.map = new HashMap<>();

        this.dummy = new Node<>(null, null);
        this.dummy.next = this.dummy;
        this.dummy.prev = this.dummy;
    }

    public V get(@NotNull K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }

        node.unlink();
        node.insertAfter(dummy);
        return node.value;
    }

    public void put(@NotNull K key, @NotNull V value) {
        Node<K, V> node = map.get(key);
        if (node != null) {
            exactRemove(node);
        }

        node = new Node<>(key, value);
        if (size() == limit) {
            Node<K, V> leastUsed = dummy.prev;
            assert (dummy.prev != null);
            exactRemove(leastUsed);
        }
        node.insertAfter(dummy);
        map.put(key, node);
    }

    public void remove(@NotNull K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return;
        }

        node.unlink();
        map.remove(key);
    }

    private void exactRemove(@NotNull Node<K, V> node) {
        boolean removed = map.remove(node.key, node);
        assert (removed);
        node.unlink();
    }

    public int size() {
        return map.size();
    }

    private static class Node<K, V> {
        private final K key;
        private final V value;
        private Node<K, V> next;
        private Node<K, V> prev;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = this.prev = null;
        }

        public void unlink() {
            if (next == null) {
                return;
            }
            assert (prev != null) : "Cyclic dependency failed";

            next.prev = prev;
            prev.next = next;
            next = prev = null;
        }

        public void insertAfter(@NotNull Node<K, V> node) {
            assert (node.next != null) : "Cyclic dependency failed";
            this.next = node.next;
            this.prev = node;
            node.next = this;
            this.next.prev = this;
        }
    }
}
