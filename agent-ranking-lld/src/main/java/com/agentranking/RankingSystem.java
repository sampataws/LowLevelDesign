package com.agentranking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Main ranking system that manages agents, ratings, and rankings.
 * Thread-safe implementation for concurrent access.
 */
public class RankingSystem {
    
    private final ConcurrentHashMap<String, Agent> agents;
    private final ConcurrentHashMap<String, AgentStats> agentStats;
    private final ConcurrentHashMap<String, Rating> ratings;
    private final Object rankingLock = new Object();
    
    public RankingSystem() {
        this.agents = new ConcurrentHashMap<>();
        this.agentStats = new ConcurrentHashMap<>();
        this.ratings = new ConcurrentHashMap<>();
    }
    
    /**
     * Registers a new agent in the system.
     */
    public void registerAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null");
        }
        
        agents.put(agent.getAgentId(), agent);
        agentStats.putIfAbsent(agent.getAgentId(), new AgentStats(agent.getAgentId()));
    }
    
    /**
     * Submits a rating for an agent.
     */
    public void submitRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }
        
        String agentId = rating.getAgentId();
        
        // Check if agent exists
        if (!agents.containsKey(agentId)) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        
        // Store the rating
        ratings.put(rating.getRatingId(), rating);
        
        // Update agent statistics
        AgentStats stats = agentStats.get(agentId);
        if (stats != null) {
            stats.addRating(rating.getScore());
        }
        
        // Recalculate rankings
        updateRankings();
    }
    
    /**
     * Removes a rating from the system.
     */
    public void removeRating(String ratingId) {
        Rating rating = ratings.remove(ratingId);
        if (rating != null) {
            AgentStats stats = agentStats.get(rating.getAgentId());
            if (stats != null) {
                stats.removeRating(rating.getScore());
            }
            updateRankings();
        }
    }
    
    /**
     * Updates the rankings of all agents based on their average ratings.
     */
    private void updateRankings() {
        synchronized (rankingLock) {
            // Get all stats and sort them
            List<AgentStats> sortedStats = new ArrayList<>(agentStats.values());
            Collections.sort(sortedStats);
            
            // Assign ranks
            int rank = 1;
            for (AgentStats stats : sortedStats) {
                stats.setRank(rank++);
            }
        }
    }
    
    /**
     * Gets the current rank of an agent.
     */
    public int getAgentRank(String agentId) {
        AgentStats stats = agentStats.get(agentId);
        return stats != null ? stats.getRank() : -1;
    }
    
    /**
     * Gets the statistics for an agent.
     */
    public AgentStats getAgentStats(String agentId) {
        return agentStats.get(agentId);
    }
    
    /**
     * Gets an agent by ID.
     */
    public Agent getAgent(String agentId) {
        return agents.get(agentId);
    }
    
    /**
     * Gets the top N agents by ranking.
     */
    public List<AgentStats> getTopAgents(int n) {
        synchronized (rankingLock) {
            return agentStats.values().stream()
                .sorted()
                .limit(n)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Gets all agents sorted by rank.
     */
    public List<AgentStats> getAllAgentsByRank() {
        synchronized (rankingLock) {
            return agentStats.values().stream()
                .sorted()
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Gets agents by department, sorted by rank.
     */
    public List<AgentStats> getAgentsByDepartment(String department) {
        synchronized (rankingLock) {
            return agentStats.values().stream()
                .filter(stats -> {
                    Agent agent = agents.get(stats.getAgentId());
                    return agent != null && department.equals(agent.getDepartment());
                })
                .sorted()
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Gets the total number of agents.
     */
    public int getTotalAgents() {
        return agents.size();
    }
    
    /**
     * Gets the total number of ratings.
     */
    public int getTotalRatings() {
        return ratings.size();
    }
    
    /**
     * Gets all ratings for a specific agent.
     */
    public List<Rating> getAgentRatings(String agentId) {
        return ratings.values().stream()
            .filter(r -> r.getAgentId().equals(agentId))
            .sorted(Comparator.comparing(Rating::getTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets the average rating across all agents.
     */
    public double getSystemAverageRating() {
        return agentStats.values().stream()
            .filter(stats -> stats.getTotalRatings() > 0)
            .mapToDouble(AgentStats::getAverageRating)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Clears all data from the system.
     */
    public void clear() {
        agents.clear();
        agentStats.clear();
        ratings.clear();
    }
}

