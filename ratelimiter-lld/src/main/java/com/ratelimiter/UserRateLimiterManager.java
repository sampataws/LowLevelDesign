package com.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-user rate limiters.
 * 
 * This class creates and manages separate rate limiter instances for each user,
 * ensuring that each user has their own independent rate limit.
 * 
 * Thread-safe and suitable for concurrent access.
 */
public class UserRateLimiterManager {
    
    private final ConcurrentHashMap<String, RateLimiter> userLimiters;
    private final RateLimiterConfig config;
    
    /**
     * Creates a new user rate limiter manager.
     * 
     * @param config the configuration to use for each user's rate limiter
     */
    public UserRateLimiterManager(RateLimiterConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.userLimiters = new ConcurrentHashMap<>();
        this.config = config;
    }
    
    /**
     * Checks if a request from the specified user should be allowed.
     * Creates a new rate limiter for the user if one doesn't exist.
     * 
     * @param userId the user identifier
     * @return true if the request is allowed, false otherwise
     */
    public boolean allowRequest(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
        
        RateLimiter limiter = userLimiters.computeIfAbsent(
            userId,
            id -> RateLimiterFactory.create(config)
        );
        
        return limiter.allowRequest();
    }
    
    /**
     * Gets the rate limiter for a specific user.
     * Returns null if no limiter exists for the user.
     * 
     * @param userId the user identifier
     * @return the rate limiter for the user, or null if not found
     */
    public RateLimiter getRateLimiter(String userId) {
        return userLimiters.get(userId);
    }
    
    /**
     * Removes the rate limiter for a specific user.
     * Useful for cleanup or when a user should be reset.
     * 
     * @param userId the user identifier
     * @return true if a limiter was removed, false if none existed
     */
    public boolean removeUser(String userId) {
        return userLimiters.remove(userId) != null;
    }
    
    /**
     * Resets the rate limiter for a specific user.
     * 
     * @param userId the user identifier
     */
    public void resetUser(String userId) {
        RateLimiter limiter = userLimiters.get(userId);
        if (limiter != null) {
            limiter.reset();
        }
    }
    
    /**
     * Clears all user rate limiters.
     */
    public void clear() {
        userLimiters.clear();
    }
    
    /**
     * Gets the number of users currently being tracked.
     * 
     * @return the number of users
     */
    public int getUserCount() {
        return userLimiters.size();
    }
    
    /**
     * Checks if a user has a rate limiter.
     * 
     * @param userId the user identifier
     * @return true if the user has a rate limiter
     */
    public boolean hasUser(String userId) {
        return userLimiters.containsKey(userId);
    }
    
    /**
     * Gets the available capacity for a specific user.
     * Returns -1 if the user doesn't exist or capacity is not supported.
     * 
     * @param userId the user identifier
     * @return available capacity or -1
     */
    public long getAvailableCapacity(String userId) {
        RateLimiter limiter = userLimiters.get(userId);
        return limiter != null ? limiter.getAvailableCapacity() : -1;
    }
    
    @Override
    public String toString() {
        return String.format("UserRateLimiterManager{userCount=%d, config=%s}",
                userLimiters.size(), config);
    }
}

