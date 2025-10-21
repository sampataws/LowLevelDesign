package com.ratelimiter;

/**
 * Interface for rate limiting implementations.
 * 
 * A rate limiter controls the rate at which requests are allowed to proceed.
 * Different implementations use different algorithms (Token Bucket, Sliding Window, etc.)
 */
public interface RateLimiter {
    
    /**
     * Checks if a request should be allowed based on the rate limiting policy.
     * 
     * @return true if the request is allowed, false if it should be denied
     */
    boolean allowRequest();
    
    /**
     * Gets the current number of available tokens/capacity.
     * This is optional and may not be supported by all implementations.
     * 
     * @return the number of available requests, or -1 if not supported
     */
    default long getAvailableCapacity() {
        return -1;
    }
    
    /**
     * Resets the rate limiter to its initial state.
     * This is useful for testing or manual resets.
     */
    default void reset() {
        // Default implementation does nothing
    }
}

