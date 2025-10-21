package com.ratelimiter;

/**
 * Factory class for creating rate limiter instances based on configuration.
 */
public class RateLimiterFactory {
    
    /**
     * Creates a rate limiter based on the provided configuration.
     * 
     * @param config the rate limiter configuration
     * @return a rate limiter instance
     * @throws IllegalArgumentException if the algorithm is not supported
     */
    public static RateLimiter create(RateLimiterConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        
        switch (config.getAlgorithm()) {
            case TOKEN_BUCKET:
                return new TokenBucketRateLimiter(
                    config.getBucketCapacity(),
                    config.getRefillRate()
                );
                
            case SLIDING_WINDOW_LOG:
                return new SlidingWindowLogRateLimiter(
                    config.getMaxRequests(),
                    config.getTimeWindowSeconds()
                );
                
            case FIXED_WINDOW:
                return new FixedWindowRateLimiter(
                    config.getMaxRequests(),
                    config.getTimeWindowSeconds()
                );
                
            case SLIDING_WINDOW_COUNTER:
                return new SlidingWindowCounterRateLimiter(
                    config.getMaxRequests(),
                    config.getTimeWindowSeconds()
                );
                
            default:
                throw new IllegalArgumentException(
                    "Unsupported algorithm: " + config.getAlgorithm()
                );
        }
    }
    
    /**
     * Creates a simple token bucket rate limiter with default settings.
     * 
     * @param requestsPerSecond number of requests allowed per second
     * @return a token bucket rate limiter
     */
    public static RateLimiter createSimple(long requestsPerSecond) {
        return new TokenBucketRateLimiter(requestsPerSecond, requestsPerSecond);
    }
    
    /**
     * Creates a token bucket rate limiter with custom capacity.
     * 
     * @param capacity maximum burst capacity
     * @param requestsPerSecond refill rate in requests per second
     * @return a token bucket rate limiter
     */
    public static RateLimiter createTokenBucket(long capacity, double requestsPerSecond) {
        return new TokenBucketRateLimiter(capacity, requestsPerSecond);
    }
}

