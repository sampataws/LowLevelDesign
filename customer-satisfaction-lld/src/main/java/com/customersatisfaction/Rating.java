package com.customersatisfaction;

import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * Represents a customer rating for a support agent.
 */
public class Rating {
    private final String agentId;
    private final int score;
    private final LocalDateTime timestamp;
    
    /**
     * Creates a new rating.
     * 
     * @param agentId the agent ID
     * @param score the rating score (1-5)
     * @throws IllegalArgumentException if score is not between 1 and 5
     */
    public Rating(String agentId, int score) {
        this(agentId, score, LocalDateTime.now());
    }
    
    /**
     * Creates a new rating with a specific timestamp.
     * 
     * @param agentId the agent ID
     * @param score the rating score (1-5)
     * @param timestamp the timestamp
     * @throws IllegalArgumentException if score is not between 1 and 5
     */
    public Rating(String agentId, int score, LocalDateTime timestamp) {
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5, got: " + score);
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        
        this.agentId = agentId;
        this.score = score;
        this.timestamp = timestamp;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public int getScore() {
        return score;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the year-month of this rating.
     * 
     * @return the year-month
     */
    public YearMonth getYearMonth() {
        return YearMonth.from(timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("Rating{agentId='%s', score=%d, timestamp=%s}",
            agentId, score, timestamp);
    }
}

