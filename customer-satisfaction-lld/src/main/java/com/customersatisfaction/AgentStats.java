package com.customersatisfaction;

/**
 * Statistics for an agent's ratings.
 */
public class AgentStats implements Comparable<AgentStats> {
    private final String agentId;
    private final int totalRatings;
    private final int totalScore;
    private final double averageRating;
    
    public AgentStats(String agentId, int totalRatings, int totalScore) {
        this.agentId = agentId;
        this.totalRatings = totalRatings;
        this.totalScore = totalScore;
        this.averageRating = totalRatings > 0 ? (double) totalScore / totalRatings : 0.0;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public int getTotalRatings() {
        return totalRatings;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    /**
     * Compares by average rating (descending), then by agent ID (ascending) for ties.
     */
    @Override
    public int compareTo(AgentStats other) {
        // First compare by average rating (descending)
        int avgCompare = Double.compare(other.averageRating, this.averageRating);
        if (avgCompare != 0) {
            return avgCompare;
        }
        // If tied, compare by agent ID (ascending) for deterministic ordering
        return this.agentId.compareTo(other.agentId);
    }
    
    @Override
    public String toString() {
        return String.format("AgentStats{agentId='%s', totalRatings=%d, totalScore=%d, avgRating=%.2f}",
            agentId, totalRatings, totalScore, averageRating);
    }
}

