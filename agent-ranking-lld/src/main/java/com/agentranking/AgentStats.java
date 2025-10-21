package com.agentranking;

/**
 * Represents statistics for an agent including ratings and ranking.
 */
public class AgentStats implements Comparable<AgentStats> {
    private final String agentId;
    private int totalRatings;
    private int totalScore;
    private double averageRating;
    private int rank;
    
    // Rating distribution
    private int fiveStarCount;
    private int fourStarCount;
    private int threeStarCount;
    private int twoStarCount;
    private int oneStarCount;
    
    public AgentStats(String agentId) {
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }
        this.agentId = agentId;
        this.totalRatings = 0;
        this.totalScore = 0;
        this.averageRating = 0.0;
        this.rank = 0;
    }
    
    /**
     * Adds a new rating to the agent's statistics.
     */
    public synchronized void addRating(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }
        
        totalRatings++;
        totalScore += score;
        averageRating = (double) totalScore / totalRatings;
        
        // Update distribution
        switch (score) {
            case 5: fiveStarCount++; break;
            case 4: fourStarCount++; break;
            case 3: threeStarCount++; break;
            case 2: twoStarCount++; break;
            case 1: oneStarCount++; break;
        }
    }
    
    /**
     * Removes a rating from the agent's statistics.
     */
    public synchronized void removeRating(int score) {
        if (totalRatings == 0) {
            return;
        }
        
        totalRatings--;
        totalScore -= score;
        averageRating = totalRatings > 0 ? (double) totalScore / totalRatings : 0.0;
        
        // Update distribution
        switch (score) {
            case 5: fiveStarCount = Math.max(0, fiveStarCount - 1); break;
            case 4: fourStarCount = Math.max(0, fourStarCount - 1); break;
            case 3: threeStarCount = Math.max(0, threeStarCount - 1); break;
            case 2: twoStarCount = Math.max(0, twoStarCount - 1); break;
            case 1: oneStarCount = Math.max(0, oneStarCount - 1); break;
        }
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public int getTotalRatings() {
        return totalRatings;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public int getFiveStarCount() {
        return fiveStarCount;
    }
    
    public int getFourStarCount() {
        return fourStarCount;
    }
    
    public int getThreeStarCount() {
        return threeStarCount;
    }
    
    public int getTwoStarCount() {
        return twoStarCount;
    }
    
    public int getOneStarCount() {
        return oneStarCount;
    }
    
    /**
     * Compares agents by average rating (descending) and then by total ratings (descending).
     */
    @Override
    public int compareTo(AgentStats other) {
        // First compare by average rating (higher is better)
        int avgCompare = Double.compare(other.averageRating, this.averageRating);
        if (avgCompare != 0) {
            return avgCompare;
        }
        
        // If average is same, compare by total ratings (more ratings is better)
        return Integer.compare(other.totalRatings, this.totalRatings);
    }
    
    @Override
    public String toString() {
        return String.format("AgentStats{id='%s', avg=%.2f, total=%d, rank=%d}", 
                           agentId, averageRating, totalRatings, rank);
    }
}

