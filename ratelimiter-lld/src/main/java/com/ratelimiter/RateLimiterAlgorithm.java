package com.ratelimiter;

/**
 * Enum representing different rate limiting algorithms.
 */
public enum RateLimiterAlgorithm {
    /**
     * Token Bucket algorithm.
     * - Allows bursts up to bucket capacity
     * - Tokens refill at a constant rate
     * - O(1) memory complexity
     * - Best for most use cases
     */
    TOKEN_BUCKET,
    
    /**
     * Sliding Window Log algorithm.
     * - Tracks exact timestamp of each request
     * - Most accurate, no boundary issues
     * - O(N) memory complexity where N is requests in window
     * - Best for strict rate limiting
     */
    SLIDING_WINDOW_LOG,
    
    /**
     * Fixed Window Counter algorithm.
     * - Simple counter that resets at fixed intervals
     * - O(1) memory complexity
     * - Has boundary issue (can allow 2x requests at window edges)
     * - Best for simple use cases
     */
    FIXED_WINDOW,
    
    /**
     * Sliding Window Counter algorithm.
     * - Hybrid approach using weighted counts from current and previous windows
     * - O(1) memory complexity
     * - Good accuracy without high memory cost
     * - Best for high-traffic scenarios
     */
    SLIDING_WINDOW_COUNTER
}

