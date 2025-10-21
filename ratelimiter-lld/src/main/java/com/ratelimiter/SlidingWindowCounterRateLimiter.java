package com.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Sliding Window Counter rate limiter implementation.
 * 
 * This algorithm uses a hybrid approach that combines fixed windows with
 * weighted counting from the previous window. It provides better accuracy
 * than Fixed Window while using less memory than Sliding Window Log.
 * 
 * Algorithm:
 * - Maintains counters for current and previous windows
 * - Calculates weighted count based on position in current window
 * - Formula: prevCount * (1 - elapsed%) + currentCount
 * 
 * Characteristics:
 * - Good accuracy without high memory cost
 * - O(1) memory complexity
 * - Approximate counting (better than Fixed Window)
 * - Thread-safe
 * 
 * Best for: High-traffic scenarios where memory is a concern
 */
public class SlidingWindowCounterRateLimiter implements RateLimiter {
    
    private final long maxRequests;
    private final long windowSizeNanos;
    private final AtomicLong currentWindowCount;
    private final AtomicLong previousWindowCount;
    private volatile long currentWindowStart;
    private final Object lock = new Object();
    
    /**
     * Creates a new Sliding Window Counter rate limiter.
     * 
     * @param maxRequests maximum requests allowed in the window
     * @param windowSeconds size of the sliding window in seconds
     */
    public SlidingWindowCounterRateLimiter(long maxRequests, long windowSeconds) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("windowSeconds must be positive");
        }
        
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSeconds * 1_000_000_000L;
        this.currentWindowCount = new AtomicLong(0);
        this.previousWindowCount = new AtomicLong(0);
        this.currentWindowStart = System.nanoTime();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            long now = System.nanoTime();
            checkAndRotateWindow(now);
            
            // Calculate weighted count from both windows
            double weightedCount = getWeightedCount(now);
            
            // Check if under limit
            if (weightedCount < maxRequests) {
                currentWindowCount.incrementAndGet();
                return true;  // Request allowed
            }
            
            return false;  // Request denied - limit exceeded
        }
    }
    
    /**
     * Calculates the weighted request count across current and previous windows.
     * 
     * @param now current time in nanoseconds
     * @return weighted count of requests
     */
    private double getWeightedCount(long now) {
        long elapsed = now - currentWindowStart;
        double elapsedPercentage = (double) elapsed / windowSizeNanos;
        
        // Weight from previous window decreases as we move through current window
        double previousWeight = 1.0 - elapsedPercentage;
        
        return (previousWindowCount.get() * previousWeight) + currentWindowCount.get();
    }
    
    /**
     * Checks if we need to rotate to a new window and does so if needed.
     * 
     * @param now current time in nanoseconds
     */
    private void checkAndRotateWindow(long now) {
        long elapsed = now - currentWindowStart;
        
        if (elapsed >= windowSizeNanos) {
            // Move to new window
            previousWindowCount.set(currentWindowCount.get());
            currentWindowCount.set(0);
            currentWindowStart = now;
        }
    }
    
    @Override
    public long getAvailableCapacity() {
        synchronized (lock) {
            long now = System.nanoTime();
            checkAndRotateWindow(now);
            double weightedCount = getWeightedCount(now);
            return (long) Math.max(0, maxRequests - weightedCount);
        }
    }
    
    @Override
    public void reset() {
        synchronized (lock) {
            currentWindowCount.set(0);
            previousWindowCount.set(0);
            currentWindowStart = System.nanoTime();
        }
    }
    
    /**
     * Gets the current weighted count (for testing/monitoring).
     * 
     * @return weighted request count
     */
    public double getCurrentWeightedCount() {
        synchronized (lock) {
            long now = System.nanoTime();
            checkAndRotateWindow(now);
            return getWeightedCount(now);
        }
    }
    
    /**
     * Gets the current window count (for testing/monitoring).
     * 
     * @return current window request count
     */
    public long getCurrentWindowCount() {
        return currentWindowCount.get();
    }
    
    /**
     * Gets the previous window count (for testing/monitoring).
     * 
     * @return previous window request count
     */
    public long getPreviousWindowCount() {
        return previousWindowCount.get();
    }
    
    @Override
    public String toString() {
        return String.format("SlidingWindowCounterRateLimiter{maxRequests=%d, windowSeconds=%d, " +
                        "currentCount=%d, previousCount=%d, weightedCount=%.2f}",
                maxRequests, windowSizeNanos / 1_000_000_000L,
                currentWindowCount.get(), previousWindowCount.get(), getCurrentWeightedCount());
    }
}

