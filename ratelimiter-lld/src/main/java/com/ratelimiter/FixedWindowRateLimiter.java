package com.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Fixed Window Counter rate limiter implementation.
 * 
 * This algorithm uses a counter that resets at fixed time intervals.
 * It's the simplest algorithm but has a boundary issue where requests
 * can spike at window edges.
 * 
 * Characteristics:
 * - Simple implementation
 * - O(1) memory complexity
 * - Has boundary issue (can allow 2x requests at window edges)
 * - Thread-safe
 * 
 * Best for: Simple use cases where approximate rate limiting is acceptable
 * 
 * Boundary Issue Example:
 * - Window: 0-60s, limit: 100 req/min
 * - 100 requests at 59s (allowed)
 * - Window resets at 60s
 * - 100 requests at 61s (allowed)
 * - Result: 200 requests in 2 seconds!
 */
public class FixedWindowRateLimiter implements RateLimiter {
    
    private final long maxRequests;
    private final long windowSizeNanos;
    private final AtomicLong counter;
    private volatile long windowStart;
    private final Object lock = new Object();
    
    /**
     * Creates a new Fixed Window rate limiter.
     * 
     * @param maxRequests maximum requests allowed per window
     * @param windowSeconds size of the fixed window in seconds
     */
    public FixedWindowRateLimiter(long maxRequests, long windowSeconds) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("windowSeconds must be positive");
        }
        
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSeconds * 1_000_000_000L;
        this.counter = new AtomicLong(0);
        this.windowStart = System.nanoTime();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            long now = System.nanoTime();
            
            // Check if current window has expired
            if (now - windowStart >= windowSizeNanos) {
                // Reset for new window
                counter.set(0);
                windowStart = now;
            }
            
            // Check if under limit
            long currentCount = counter.get();
            if (currentCount < maxRequests) {
                counter.incrementAndGet();
                return true;  // Request allowed
            }
            
            return false;  // Request denied - limit exceeded
        }
    }
    
    @Override
    public long getAvailableCapacity() {
        synchronized (lock) {
            checkAndResetWindow();
            return maxRequests - counter.get();
        }
    }
    
    @Override
    public void reset() {
        synchronized (lock) {
            counter.set(0);
            windowStart = System.nanoTime();
        }
    }
    
    /**
     * Checks if window has expired and resets if needed.
     */
    private void checkAndResetWindow() {
        long now = System.nanoTime();
        if (now - windowStart >= windowSizeNanos) {
            counter.set(0);
            windowStart = now;
        }
    }
    
    /**
     * Gets the current request count in this window (for testing/monitoring).
     * 
     * @return current request count
     */
    public long getCurrentCount() {
        synchronized (lock) {
            checkAndResetWindow();
            return counter.get();
        }
    }
    
    /**
     * Gets the time remaining in the current window in milliseconds.
     * 
     * @return milliseconds until window reset
     */
    public long getTimeUntilReset() {
        synchronized (lock) {
            long now = System.nanoTime();
            long elapsed = now - windowStart;
            long remaining = windowSizeNanos - elapsed;
            return Math.max(0, remaining / 1_000_000);
        }
    }
    
    @Override
    public String toString() {
        return String.format("FixedWindowRateLimiter{maxRequests=%d, windowSeconds=%d, currentCount=%d, resetIn=%dms}",
                maxRequests, windowSizeNanos / 1_000_000_000L, getCurrentCount(), getTimeUntilReset());
    }
}

