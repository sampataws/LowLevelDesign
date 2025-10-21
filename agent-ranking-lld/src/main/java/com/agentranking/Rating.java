package com.agentranking;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a customer rating for an agent interaction.
 */
public class Rating {
    private final String ratingId;
    private final String agentId;
    private final String customerId;
    private final int score;  // 1-5 stars
    private final String comment;
    private final LocalDateTime timestamp;
    private final String interactionId;
    
    public Rating(String ratingId, String agentId, String customerId, 
                  int score, String comment, String interactionId) {
        if (ratingId == null || ratingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rating ID cannot be null or empty");
        }
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }
        
        this.ratingId = ratingId;
        this.agentId = agentId;
        this.customerId = customerId;
        this.score = score;
        this.comment = comment;
        this.interactionId = interactionId;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getRatingId() {
        return ratingId;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public int getScore() {
        return score;
    }
    
    public String getComment() {
        return comment;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getInteractionId() {
        return interactionId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return ratingId.equals(rating.ratingId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ratingId);
    }
    
    @Override
    public String toString() {
        return String.format("Rating{id='%s', agent='%s', score=%d, time=%s}", 
                           ratingId, agentId, score, timestamp);
    }
}

