package com.ratelimiter;

/**
 * Configuration class for rate limiters.
 * Uses builder pattern for easy configuration.
 */
public class RateLimiterConfig {
    private long maxRequests = 10;
    private long timeWindowSeconds = 1;
    private RateLimiterAlgorithm algorithm = RateLimiterAlgorithm.TOKEN_BUCKET;
    private long bucketCapacity = 0;  // 0 means use maxRequests as default
    
    /**
     * Sets the maximum number of requests allowed in the time window.
     * 
     * @param maxRequests maximum requests allowed
     * @return this config for chaining
     */
    public RateLimiterConfig setMaxRequests(long maxRequests) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        this.maxRequests = maxRequests;
        return this;
    }
    
    /**
     * Sets the time window in seconds.
     * 
     * @param timeWindowSeconds time window in seconds
     * @return this config for chaining
     */
    public RateLimiterConfig setTimeWindowSeconds(long timeWindowSeconds) {
        if (timeWindowSeconds <= 0) {
            throw new IllegalArgumentException("timeWindowSeconds must be positive");
        }
        this.timeWindowSeconds = timeWindowSeconds;
        return this;
    }
    
    /**
     * Sets the rate limiting algorithm to use.
     * 
     * @param algorithm the algorithm to use
     * @return this config for chaining
     */
    public RateLimiterConfig setAlgorithm(RateLimiterAlgorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm cannot be null");
        }
        this.algorithm = algorithm;
        return this;
    }
    
    /**
     * Sets the bucket capacity for Token Bucket algorithm.
     * If not set, defaults to maxRequests.
     * 
     * @param bucketCapacity the bucket capacity
     * @return this config for chaining
     */
    public RateLimiterConfig setBucketCapacity(long bucketCapacity) {
        if (bucketCapacity < 0) {
            throw new IllegalArgumentException("bucketCapacity cannot be negative");
        }
        this.bucketCapacity = bucketCapacity;
        return this;
    }
    
    // Getters
    
    public long getMaxRequests() {
        return maxRequests;
    }
    
    public long getTimeWindowSeconds() {
        return timeWindowSeconds;
    }
    
    public RateLimiterAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public long getBucketCapacity() {
        return bucketCapacity > 0 ? bucketCapacity : maxRequests;
    }
    
    /**
     * Calculates the refill rate for Token Bucket algorithm.
     * 
     * @return tokens per second
     */
    public double getRefillRate() {
        return (double) maxRequests / timeWindowSeconds;
    }
    
    @Override
    public String toString() {
        return "RateLimiterConfig{" +
                "maxRequests=" + maxRequests +
                ", timeWindowSeconds=" + timeWindowSeconds +
                ", algorithm=" + algorithm +
                ", bucketCapacity=" + getBucketCapacity() +
                ", refillRate=" + getRefillRate() + " req/sec" +
                '}';
    }
}

