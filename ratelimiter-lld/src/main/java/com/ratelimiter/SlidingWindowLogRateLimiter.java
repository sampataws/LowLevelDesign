package com.ratelimiter;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Sliding Window Log rate limiter implementation.
 * 
 * This algorithm maintains a log of request timestamps and counts requests
 * within a sliding time window. It's the most accurate algorithm but uses
 * more memory.
 * 
 * Characteristics:
 * - Most accurate rate limiting
 * - No boundary issues
 * - O(N) memory complexity where N is requests in window
 * - Thread-safe
 * 
 * Best for: Scenarios requiring strict, precise rate limiting
 */
public class SlidingWindowLogRateLimiter implements RateLimiter {
    
    private final long maxRequests;
    private final long windowSizeNanos;
    private final ConcurrentLinkedQueue<Long> requestTimestamps;
    private final Object lock = new Object();
    
    /**
     * Creates a new Sliding Window Log rate limiter.
     * 
     * @param maxRequests maximum requests allowed in the window
     * @param windowSeconds size of the sliding window in seconds
     */
    public SlidingWindowLogRateLimiter(long maxRequests, long windowSeconds) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("windowSeconds must be positive");
        }
        
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSeconds * 1_000_000_000L;
        this.requestTimestamps = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            long now = System.nanoTime();
            long windowStart = now - windowSizeNanos;
            
            // Remove timestamps outside the current window
            while (!requestTimestamps.isEmpty() && 
                   requestTimestamps.peek() < windowStart) {
                requestTimestamps.poll();
            }
            
            // Check if we're under the limit
            if (requestTimestamps.size() < maxRequests) {
                requestTimestamps.offer(now);
                return true;  // Request allowed
            }
            
            return false;  // Request denied - limit exceeded
        }
    }
    
    @Override
    public long getAvailableCapacity() {
        synchronized (lock) {
            cleanupOldTimestamps();
            return maxRequests - requestTimestamps.size();
        }
    }
    
    @Override
    public void reset() {
        synchronized (lock) {
            requestTimestamps.clear();
        }
    }
    
    /**
     * Removes timestamps outside the current window.
     * Called internally for cleanup.
     */
    private void cleanupOldTimestamps() {
        long now = System.nanoTime();
        long windowStart = now - windowSizeNanos;
        
        while (!requestTimestamps.isEmpty() && 
               requestTimestamps.peek() < windowStart) {
            requestTimestamps.poll();
        }
    }
    
    /**
     * Gets the current number of requests in the window (for testing/monitoring).
     * 
     * @return number of requests in current window
     */
    public int getCurrentRequestCount() {
        synchronized (lock) {
            cleanupOldTimestamps();
            return requestTimestamps.size();
        }
    }
    
    @Override
    public String toString() {
        return String.format("SlidingWindowLogRateLimiter{maxRequests=%d, windowSeconds=%d, currentCount=%d}",
                maxRequests, windowSizeNanos / 1_000_000_000L, getCurrentRequestCount());
    }
}

