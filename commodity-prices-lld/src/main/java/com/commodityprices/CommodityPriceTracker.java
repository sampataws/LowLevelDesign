package com.commodityprices;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe commodity price tracker optimized for frequent reads and writes.
 * 
 * Design:
 * - HashMap for O(1) timestamp lookups
 * - TreeSet for O(log N) max price queries
 * - ReadWriteLock for concurrent reads
 * - Handles out-of-order timestamps
 * - Handles duplicate timestamps (updates)
 * 
 * Time Complexity:
 * - update(): O(log N) - TreeSet insertion/removal
 * - getMaxPrice(): O(1) - TreeSet first element
 * - getPriceAt(): O(1) - HashMap lookup
 * 
 * Space Complexity: O(N) where N is number of unique timestamps
 */
public class CommodityPriceTracker {
    
    // HashMap for O(1) timestamp-to-price lookups
    private final Map<Long, Double> timestampToPriceMap;
    
    // TreeSet for O(log N) insertions and O(1) max queries
    // Sorted by price (descending), then timestamp (ascending)
    private final TreeSet<PriceDataPoint> pricesSortedByValue;
    
    // ReadWriteLock for concurrent reads
    private final ReadWriteLock lock;
    
    public CommodityPriceTracker() {
        this.timestampToPriceMap = new ConcurrentHashMap<>();
        this.pricesSortedByValue = new TreeSet<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    /**
     * Updates the price at a given timestamp.
     * If timestamp exists, updates the price.
     * If timestamp is new, adds the price.
     * 
     * Time Complexity: O(log N)
     * Space Complexity: O(1)
     */
    public void update(long timestamp, double price) {
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        lock.writeLock().lock();
        try {
            // Check if timestamp already exists
            Double oldPrice = timestampToPriceMap.get(timestamp);
            
            if (oldPrice != null) {
                // Remove old data point from TreeSet
                pricesSortedByValue.remove(new PriceDataPoint(timestamp, oldPrice));
            }
            
            // Add new data point
            timestampToPriceMap.put(timestamp, price);
            pricesSortedByValue.add(new PriceDataPoint(timestamp, price));
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the maximum commodity price across all timestamps.
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * @return maximum price, or null if no data points exist
     */
    public Double getMaxPrice() {
        lock.readLock().lock();
        try {
            if (pricesSortedByValue.isEmpty()) {
                return null;
            }
            return pricesSortedByValue.first().getPrice();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets the price at a specific timestamp.
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * @return price at timestamp, or null if timestamp doesn't exist
     */
    public Double getPriceAt(long timestamp) {
        lock.readLock().lock();
        try {
            return timestampToPriceMap.get(timestamp);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets the data point with maximum price.
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * @return data point with max price, or null if no data points exist
     */
    public PriceDataPoint getMaxPriceDataPoint() {
        lock.readLock().lock();
        try {
            if (pricesSortedByValue.isEmpty()) {
                return null;
            }
            return pricesSortedByValue.first();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets the total number of unique timestamps.
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public int size() {
        lock.readLock().lock();
        try {
            return timestampToPriceMap.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Checks if tracker has any data points.
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return timestampToPriceMap.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Removes all data points.
     * 
     * Time Complexity: O(N)
     * Space Complexity: O(1)
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            timestampToPriceMap.clear();
            pricesSortedByValue.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes a specific timestamp.
     * 
     * Time Complexity: O(log N)
     * Space Complexity: O(1)
     * 
     * @return true if timestamp was removed, false if it didn't exist
     */
    public boolean remove(long timestamp) {
        lock.writeLock().lock();
        try {
            Double price = timestampToPriceMap.remove(timestamp);
            if (price != null) {
                pricesSortedByValue.remove(new PriceDataPoint(timestamp, price));
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets statistics about the price data.
     */
    public PriceStatistics getStatistics() {
        lock.readLock().lock();
        try {
            if (timestampToPriceMap.isEmpty()) {
                return new PriceStatistics(0, null, null, null);
            }
            
            double max = pricesSortedByValue.first().getPrice();
            double min = pricesSortedByValue.last().getPrice();
            
            double sum = 0;
            for (Double price : timestampToPriceMap.values()) {
                sum += price;
            }
            double avg = sum / timestampToPriceMap.size();
            
            return new PriceStatistics(timestampToPriceMap.size(), min, max, avg);
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Statistics about price data.
     */
    public static class PriceStatistics {
        private final int count;
        private final Double minPrice;
        private final Double maxPrice;
        private final Double avgPrice;
        
        public PriceStatistics(int count, Double minPrice, Double maxPrice, Double avgPrice) {
            this.count = count;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.avgPrice = avgPrice;
        }
        
        public int getCount() { return count; }
        public Double getMinPrice() { return minPrice; }
        public Double getMaxPrice() { return maxPrice; }
        public Double getAvgPrice() { return avgPrice; }
        
        @Override
        public String toString() {
            return String.format("PriceStatistics{count=%d, min=%.2f, max=%.2f, avg=%.2f}",
                    count, minPrice, maxPrice, avgPrice);
        }
    }
}

