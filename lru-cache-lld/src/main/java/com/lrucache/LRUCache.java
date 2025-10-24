package com.lrucache;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) Cache Implementation
 * 
 * Uses a combination of:
 * 1. HashMap for O(1) key lookup
 * 2. Doubly Linked List for O(1) insertion/deletion and maintaining order
 * 
 * Time Complexity:
 * - get(key): O(1)
 * - put(key, value): O(1)
 * 
 * Space Complexity: O(capacity)
 */
public class LRUCache {
    
    /**
     * Node in the doubly linked list
     */
    private class Node {
        int key;      // Store key for HashMap removal during eviction
        int value;    // Cached value
        Node prev;    // Previous node (towards tail/LRU)
        Node next;    // Next node (towards head/MRU)
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;  // Dummy head (most recently used)
    private final Node tail;  // Dummy tail (least recently used)
    
    /**
     * Initialize LRU Cache with given capacity
     * 
     * @param capacity Maximum number of items the cache can hold
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Initialize dummy head and tail
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * Get value for the given key
     * Marks the key as recently used by moving it to the head
     * 
     * @param key The key to look up
     * @return The value associated with the key, or -1 if not found
     */
    public int get(int key) {
        Node node = cache.get(key);
        
        if (node == null) {
            return -1;  // Key not found
        }
        
        // Move accessed node to head (mark as most recently used)
        moveToHead(node);
        
        return node.value;
    }
    
    /**
     * Put key-value pair into the cache
     * If key exists, update value and mark as recently used
     * If key doesn't exist, insert new node
     * If cache is full, evict least recently used item
     * 
     * @param key The key to insert/update
     * @param value The value to associate with the key
     */
    public void put(int key, int value) {
        Node node = cache.get(key);
        
        if (node != null) {
            // Key exists - update value and move to head
            node.value = value;
            moveToHead(node);
            return;
        }
        
        // Key doesn't exist - create new node
        Node newNode = new Node(key, value);
        
        // Check if cache is full
        if (cache.size() >= capacity) {
            // Evict least recently used item (node before tail)
            Node lru = tail.prev;
            removeNode(lru);
            cache.remove(lru.key);
        }
        
        // Add new node to head and cache
        addToHead(newNode);
        cache.put(key, newNode);
    }
    
    /**
     * Get current size of the cache
     * 
     * @return Number of items currently in the cache
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Check if cache is empty
     * 
     * @return true if cache is empty, false otherwise
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * Clear all items from the cache
     */
    public void clear() {
        cache.clear();
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * Check if key exists in cache
     * 
     * @param key The key to check
     * @return true if key exists, false otherwise
     */
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    }
    
    /**
     * Remove a node from its current position in the list
     * 
     * @param node The node to remove
     */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    /**
     * Add a node right after the head (most recently used position)
     * 
     * @param node The node to add
     */
    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    /**
     * Move a node to the head (mark as most recently used)
     * 
     * @param node The node to move
     */
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    /**
     * Get a string representation of the cache (for debugging)
     * Shows items from most recently used to least recently used
     * 
     * @return String representation of the cache
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LRUCache[capacity=").append(capacity)
          .append(", size=").append(cache.size())
          .append(", items=(");
        
        Node current = head.next;
        boolean first = true;
        while (current != tail) {
            if (!first) {
                sb.append(" → ");
            }
            sb.append(current.key).append(":").append(current.value);
            current = current.next;
            first = false;
        }
        
        sb.append(")]");
        return sb.toString();
    }
    
    /**
     * Get detailed state of the cache for debugging
     * 
     * @return Detailed string representation
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LRU Cache State:\n");
        sb.append("  Capacity: ").append(capacity).append("\n");
        sb.append("  Size: ").append(cache.size()).append("\n");
        sb.append("  Order (MRU → LRU): ");
        
        Node current = head.next;
        while (current != tail) {
            sb.append("[").append(current.key).append(":").append(current.value).append("] ");
            current = current.next;
        }
        
        return sb.toString();
    }
}

