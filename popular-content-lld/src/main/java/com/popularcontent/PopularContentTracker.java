package com.popularcontent;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe tracker for content popularity with real-time most popular queries.
 * 
 * Design:
 * - HashMap: O(1) content ID to popularity lookups
 * - TreeMap: O(log N) popularity to content IDs mapping (sorted by popularity descending)
 * - ReadWriteLock: Concurrent reads, exclusive writes
 * 
 * Time Complexity:
 * - processAction(): O(log N)
 * - getMostPopular(): O(log N) worst case, O(1) average case
 * 
 * Space Complexity: O(N) where N = unique content IDs
 */
public class PopularContentTracker {
    
    // Maps content ID to its current popularity score
    private final Map<Integer, Integer> contentPopularity;
    
    // Maps popularity score to set of content IDs with that score (sorted descending)
    private final TreeMap<Integer, Set<Integer>> popularityToContents;
    
    // Lock for thread-safe operations
    private final ReadWriteLock lock;
    
    /**
     * Creates a new PopularContentTracker.
     */
    public PopularContentTracker() {
        this.contentPopularity = new HashMap<>();
        // TreeMap with reverse order (highest popularity first)
        this.popularityToContents = new TreeMap<>(Collections.reverseOrder());
        this.lock = new ReentrantReadWriteLock();
    }
    
    /**
     * Processes an action on a content ID.
     * 
     * @param contentId the content ID (must be positive)
     * @param action the action to perform
     * @throws IllegalArgumentException if contentId is not positive
     * @throws IllegalArgumentException if action is null
     * 
     * Time: O(log N)
     */
    public void processAction(int contentId, ContentAction action) {
        if (contentId <= 0) {
            throw new IllegalArgumentException("Content ID must be positive, got: " + contentId);
        }
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        
        lock.writeLock().lock();
        try {
            int currentPopularity = contentPopularity.getOrDefault(contentId, 0);
            int newPopularity;
            
            if (action == ContentAction.INCREASE_POPULARITY) {
                newPopularity = currentPopularity + 1;
            } else {
                newPopularity = currentPopularity - 1;
            }
            
            // Remove from old popularity bucket
            if (currentPopularity != 0) {
                Set<Integer> oldBucket = popularityToContents.get(currentPopularity);
                if (oldBucket != null) {
                    oldBucket.remove(contentId);
                    if (oldBucket.isEmpty()) {
                        popularityToContents.remove(currentPopularity);
                    }
                }
            }
            
            // Update popularity map
            if (newPopularity <= 0) {
                // Remove content if popularity is 0 or negative
                contentPopularity.remove(contentId);
            } else {
                contentPopularity.put(contentId, newPopularity);
                
                // Add to new popularity bucket
                popularityToContents
                    .computeIfAbsent(newPopularity, k -> new HashSet<>())
                    .add(contentId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Returns the most popular content ID.
     * If there are multiple content IDs with the same highest popularity,
     * returns any one of them (non-deterministic).
     * 
     * @return the most popular content ID, or -1 if no content has popularity > 0
     * 
     * Time: O(log N) worst case, O(1) average case
     */
    public int getMostPopular() {
        lock.readLock().lock();
        try {
            if (popularityToContents.isEmpty()) {
                return -1;
            }
            
            // Get the highest popularity bucket
            Map.Entry<Integer, Set<Integer>> highestEntry = popularityToContents.firstEntry();
            
            if (highestEntry == null || highestEntry.getValue().isEmpty()) {
                return -1;
            }
            
            // Return any content ID from the highest popularity bucket
            return highestEntry.getValue().iterator().next();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Returns all content IDs with the highest popularity.
     * 
     * @return list of most popular content IDs, or empty list if none
     * 
     * Time: O(K) where K = number of contents with max popularity
     */
    public List<Integer> getAllMostPopular() {
        lock.readLock().lock();
        try {
            if (popularityToContents.isEmpty()) {
                return Collections.emptyList();
            }
            
            Map.Entry<Integer, Set<Integer>> highestEntry = popularityToContents.firstEntry();
            
            if (highestEntry == null || highestEntry.getValue().isEmpty()) {
                return Collections.emptyList();
            }
            
            return new ArrayList<>(highestEntry.getValue());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Returns the popularity score of a content ID.
     * 
     * @param contentId the content ID
     * @return the popularity score, or 0 if content doesn't exist
     * 
     * Time: O(1)
     */
    public int getPopularity(int contentId) {
        lock.readLock().lock();
        try {
            return contentPopularity.getOrDefault(contentId, 0);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Returns the maximum popularity score.
     * 
     * @return the maximum popularity, or 0 if no content exists
     * 
     * Time: O(1)
     */
    public int getMaxPopularity() {
        lock.readLock().lock();
        try {
            if (popularityToContents.isEmpty()) {
                return 0;
            }
            
            Map.Entry<Integer, Set<Integer>> highestEntry = popularityToContents.firstEntry();
            return highestEntry != null ? highestEntry.getKey() : 0;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Returns the number of unique content IDs being tracked.
     * 
     * @return the number of content IDs with popularity > 0
     * 
     * Time: O(1)
     */
    public int size() {
        lock.readLock().lock();
        try {
            return contentPopularity.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Checks if the tracker is empty.
     * 
     * @return true if no content is being tracked
     * 
     * Time: O(1)
     */
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return contentPopularity.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Clears all content from the tracker.
     * 
     * Time: O(1)
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            contentPopularity.clear();
            popularityToContents.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Returns statistics about the tracked content.
     * 
     * @return ContentStatistics object
     * 
     * Time: O(N)
     */
    public ContentStatistics getStatistics() {
        lock.readLock().lock();
        try {
            if (contentPopularity.isEmpty()) {
                return new ContentStatistics(0, 0, 0, 0.0);
            }
            
            int count = contentPopularity.size();
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            long sum = 0;
            
            for (int popularity : contentPopularity.values()) {
                min = Math.min(min, popularity);
                max = Math.max(max, popularity);
                sum += popularity;
            }
            
            double avg = (double) sum / count;
            
            return new ContentStatistics(count, min, max, avg);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Statistics about tracked content.
     */
    public static class ContentStatistics {
        private final int count;
        private final int minPopularity;
        private final int maxPopularity;
        private final double avgPopularity;
        
        public ContentStatistics(int count, int minPopularity, int maxPopularity, double avgPopularity) {
            this.count = count;
            this.minPopularity = minPopularity;
            this.maxPopularity = maxPopularity;
            this.avgPopularity = avgPopularity;
        }
        
        public int getCount() {
            return count;
        }
        
        public int getMinPopularity() {
            return minPopularity;
        }
        
        public int getMaxPopularity() {
            return maxPopularity;
        }
        
        public double getAvgPopularity() {
            return avgPopularity;
        }
        
        @Override
        public String toString() {
            return String.format("ContentStatistics{count=%d, min=%d, max=%d, avg=%.2f}",
                count, minPopularity, maxPopularity, avgPopularity);
        }
    }
}

