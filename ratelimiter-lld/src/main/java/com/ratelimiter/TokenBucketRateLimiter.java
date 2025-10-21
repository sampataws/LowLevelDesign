package com.ratelimiter;

/**
 * Token Bucket rate limiter implementation.
 * 
 * The token bucket algorithm maintains a bucket of tokens that refills at a constant rate.
 * Each request consumes one token. If no tokens are available, the request is denied.
 * 
 * Characteristics:
 * - Allows bursts up to bucket capacity
 * - Smooth rate limiting over time
 * - O(1) memory complexity
 * - Thread-safe
 * 
 * Best for: Most API rate limiting scenarios where occasional bursts are acceptable
 */
public class TokenBucketRateLimiter implements RateLimiter {
    
    private final long capacity;        // Maximum number of tokens
    private final double refillRate;    // Tokens added per second
    private double tokens;              // Current number of tokens
    private long lastRefillTime;        // Last time tokens were refilled (nanoseconds)
    private final Object lock = new Object();
    
    /**
     * Creates a new Token Bucket rate limiter.
     * 
     * @param capacity maximum number of tokens (bucket size)
     * @param refillRate number of tokens added per second
     */
    public TokenBucketRateLimiter(long capacity, double refillRate) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (refillRate <= 0) {
            throw new IllegalArgumentException("Refill rate must be positive");
        }
        
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;  // Start with full bucket
        this.lastRefillTime = System.nanoTime();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            refillTokens();
            
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;  // Request allowed
            }
            
            return false;  // Request denied - no tokens available
        }
    }
    
    /**
     * Refills tokens based on elapsed time since last refill.
     * Called internally before checking if request is allowed.
     */
    private void refillTokens() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
        
        // Calculate tokens to add based on elapsed time
        double tokensToAdd = elapsedSeconds * refillRate;
        
        // Add tokens but don't exceed capacity
        tokens = Math.min(capacity, tokens + tokensToAdd);
        
        lastRefillTime = now;
    }
    
    @Override
    public long getAvailableCapacity() {
        synchronized (lock) {
            refillTokens();
            return (long) tokens;
        }
    }
    
    @Override
    public void reset() {
        synchronized (lock) {
            tokens = capacity;
            lastRefillTime = System.nanoTime();
        }
    }
    
    /**
     * Gets the current number of tokens (for testing/monitoring).
     * 
     * @return current token count
     */
    public double getCurrentTokens() {
        synchronized (lock) {
            refillTokens();
            return tokens;
        }
    }
    
    @Override
    public String toString() {
        return String.format("TokenBucketRateLimiter{capacity=%d, refillRate=%.2f/sec, currentTokens=%.2f}",
                capacity, refillRate, getCurrentTokens());
    }
}

