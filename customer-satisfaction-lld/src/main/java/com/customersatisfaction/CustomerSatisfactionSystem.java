package com.customersatisfaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Thread-safe customer satisfaction tracking system for support agents.
 * 
 * Features:
 * - Accept ratings (1-5) for agents
 * - Get all agents with average ratings (sorted highest to lowest)
 * - Handle ties with configurable strategies
 * - Monthly performance tracking
 * - Export ratings in multiple formats (CSV, JSON, XML)
 */
public class CustomerSatisfactionSystem {
    
    // All ratings stored chronologically
    private final List<Rating> allRatings;
    
    // Agent ID -> List of ratings
    private final Map<String, List<Rating>> agentRatings;
    
    // YearMonth -> Agent ID -> List of ratings
    private final Map<YearMonth, Map<String, List<Rating>>> monthlyRatings;
    
    // Lock for thread safety
    private final ReadWriteLock lock;
    
    // Default tie-break strategy
    private TieBreakStrategy tieBreakStrategy;
    
    public CustomerSatisfactionSystem() {
        this.allRatings = new ArrayList<>();
        this.agentRatings = new HashMap<>();
        this.monthlyRatings = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.tieBreakStrategy = TieBreakStrategy.AGENT_ID;
    }
    
    /**
     * Sets the tie-break strategy.
     * 
     * @param strategy the strategy to use
     */
    public void setTieBreakStrategy(TieBreakStrategy strategy) {
        lock.writeLock().lock();
        try {
            this.tieBreakStrategy = strategy;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Accepts a rating for an agent.
     * 
     * @param agentId the agent ID
     * @param score the rating score (1-5)
     * @throws IllegalArgumentException if score is not between 1 and 5
     */
    public void addRating(String agentId, int score) {
        addRating(new Rating(agentId, score));
    }
    
    /**
     * Accepts a rating for an agent with a specific timestamp.
     * 
     * @param agentId the agent ID
     * @param score the rating score (1-5)
     * @param timestamp the timestamp
     */
    public void addRating(String agentId, int score, LocalDateTime timestamp) {
        addRating(new Rating(agentId, score, timestamp));
    }
    
    /**
     * Accepts a rating.
     * 
     * @param rating the rating
     */
    public void addRating(Rating rating) {
        lock.writeLock().lock();
        try {
            allRatings.add(rating);
            
            // Add to agent ratings
            agentRatings.computeIfAbsent(rating.getAgentId(), k -> new ArrayList<>())
                .add(rating);
            
            // Add to monthly ratings
            YearMonth yearMonth = rating.getYearMonth();
            monthlyRatings.computeIfAbsent(yearMonth, k -> new HashMap<>())
                .computeIfAbsent(rating.getAgentId(), k -> new ArrayList<>())
                .add(rating);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets all agents with their average ratings, sorted highest to lowest.
     * 
     * @return list of agent statistics sorted by average rating (descending)
     */
    public List<AgentStats> getAllAgentsSorted() {
        lock.readLock().lock();
        try {
            List<AgentStats> agentStatsList = new ArrayList<>();

            for (Map.Entry<String, List<Rating>> entry : agentRatings.entrySet()) {
                String agentId = entry.getKey();
                List<Rating> ratings = entry.getValue();
                int totalScore = 0;

                for (Rating rating : ratings) {
                    totalScore += rating.getScore();
                }

                agentStatsList.add(new AgentStats(agentId, ratings.size(), totalScore));
            }

            // Sort the list in descending order of average rating
            Collections.sort(agentStatsList, new Comparator<AgentStats>() {
                @Override
                public int compare(AgentStats a, AgentStats b) {
                    return Double.compare(b.getAverageRating(), a.getAverageRating());
                }
            });

            return agentStatsList;
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Gets all agents with their average ratings, unsorted.
     * 
     * @return list of agent statistics (unsorted)
     */
    public List<AgentStats> getAllAgentsUnsorted() {
        lock.readLock().lock();
        try {
            return agentRatings.entrySet().stream()
                .map(entry -> {
                    String agentId = entry.getKey();
                    List<Rating> ratings = entry.getValue();
                    int totalScore = ratings.stream().mapToInt(Rating::getScore).sum();
                    return new AgentStats(agentId, ratings.size(), totalScore);
                })
                .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets the best agents for a specific month.
     * 
     * @param yearMonth the year-month
     * @return list of agent statistics for that month, sorted by average rating
     */
    public List<AgentStats> getBestAgentsForMonth(YearMonth yearMonth) {
        lock.readLock().lock();
        try {
            Map<String, List<Rating>> monthData = monthlyRatings.get(yearMonth);
            if (monthData == null) {
                return Collections.emptyList();
            }
            
            List<AgentStats> stats = monthData.entrySet().stream()
                .map(entry -> {
                    String agentId = entry.getKey();
                    List<Rating> ratings = entry.getValue();
                    int totalScore = ratings.stream().mapToInt(Rating::getScore).sum();
                    return new AgentStats(agentId, ratings.size(), totalScore);
                })
                .collect(Collectors.toList());
            
            sortByStrategy(stats);
            return stats;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all available months with ratings.
     * 
     * @return set of year-months
     */
    public Set<YearMonth> getAvailableMonths() {
        lock.readLock().lock();
        try {
            return new TreeSet<>(monthlyRatings.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Exports each agent's average ratings per month.
     * 
     * @param format the export format
     * @return the exported data as a string
     */
    public String exportMonthlyAverages(ExportFormat format) {
        lock.readLock().lock();
        try {
            Map<String, Map<String, Double>> data = new LinkedHashMap<>();
            
            // Build data structure: agentId -> (month -> avgRating)
            for (Map.Entry<YearMonth, Map<String, List<Rating>>> monthEntry : monthlyRatings.entrySet()) {
                YearMonth month = monthEntry.getKey();
                String monthStr = month.toString();
                
                for (Map.Entry<String, List<Rating>> agentEntry : monthEntry.getValue().entrySet()) {
                    String agentId = agentEntry.getKey();
                    List<Rating> ratings = agentEntry.getValue();
                    
                    double avgRating = ratings.stream()
                        .mapToInt(Rating::getScore)
                        .average()
                        .orElse(0.0);
                    
                    data.computeIfAbsent(agentId, k -> new LinkedHashMap<>())
                        .put(monthStr, avgRating);
                }
            }
            
            switch (format) {
                case CSV:
                    return exportAsCSV(data);
                case JSON:
                    return exportAsJSON(data);
                case XML:
                    return exportAsXML(data);
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Exports total ratings (not averages) per month.
     * 
     * @param format the export format
     * @return the exported data as a string
     */
    public String exportMonthlyTotals(ExportFormat format) {
        lock.readLock().lock();
        try {
            Map<String, Map<String, Integer>> data = new LinkedHashMap<>();
            
            // Build data structure: agentId -> (month -> totalScore)
            for (Map.Entry<YearMonth, Map<String, List<Rating>>> monthEntry : monthlyRatings.entrySet()) {
                YearMonth month = monthEntry.getKey();
                String monthStr = month.toString();
                
                for (Map.Entry<String, List<Rating>> agentEntry : monthEntry.getValue().entrySet()) {
                    String agentId = agentEntry.getKey();
                    List<Rating> ratings = agentEntry.getValue();
                    
                    int totalScore = ratings.stream()
                        .mapToInt(Rating::getScore)
                        .sum();
                    
                    data.computeIfAbsent(agentId, k -> new LinkedHashMap<>())
                        .put(monthStr, totalScore);
                }
            }
            
            switch (format) {
                case CSV:
                    return exportTotalsAsCSV(data);
                case JSON:
                    return exportTotalsAsJSON(data);
                case XML:
                    return exportTotalsAsXML(data);
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private void sortByStrategy(List<AgentStats> stats) {
        stats.sort((a, b) -> {
            // First compare by average rating (descending)
            int avgCompare = Double.compare(b.getAverageRating(), a.getAverageRating());
            if (avgCompare != 0) {
                return avgCompare;
            }
            
            // Handle ties based on strategy
            switch (tieBreakStrategy) {
                case AGENT_ID:
                    return a.getAgentId().compareTo(b.getAgentId());
                case TOTAL_RATINGS:
                    return Integer.compare(b.getTotalRatings(), a.getTotalRatings());
                case NONE:
                    return 0;
                default:
                    return a.getAgentId().compareTo(b.getAgentId());
            }
        });
    }
    
    private String exportAsCSV(Map<String, Map<String, Double>> data) {
        StringBuilder sb = new StringBuilder();
        
        // Get all unique months
        Set<String> allMonths = new TreeSet<>();
        data.values().forEach(monthMap -> allMonths.addAll(monthMap.keySet()));
        
        // Header
        sb.append("AgentID");
        for (String month : allMonths) {
            sb.append(",").append(month);
        }
        sb.append("\n");
        
        // Data rows
        for (Map.Entry<String, Map<String, Double>> entry : data.entrySet()) {
            sb.append(entry.getKey());
            for (String month : allMonths) {
                sb.append(",");
                Double avg = entry.getValue().get(month);
                if (avg != null) {
                    sb.append(String.format("%.2f", avg));
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private String exportAsJSON(Map<String, Map<String, Double>> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }
    
    private String exportAsXML(Map<String, Map<String, Double>> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<agents>\n");
        
        for (Map.Entry<String, Map<String, Double>> entry : data.entrySet()) {
            sb.append("  <agent id=\"").append(entry.getKey()).append("\">\n");
            for (Map.Entry<String, Double> monthEntry : entry.getValue().entrySet()) {
                sb.append("    <month name=\"").append(monthEntry.getKey()).append("\">");
                sb.append(String.format("%.2f", monthEntry.getValue()));
                sb.append("</month>\n");
            }
            sb.append("  </agent>\n");
        }
        
        sb.append("</agents>");
        return sb.toString();
    }
    
    private String exportTotalsAsCSV(Map<String, Map<String, Integer>> data) {
        StringBuilder sb = new StringBuilder();
        
        Set<String> allMonths = new TreeSet<>();
        data.values().forEach(monthMap -> allMonths.addAll(monthMap.keySet()));
        
        sb.append("AgentID");
        for (String month : allMonths) {
            sb.append(",").append(month);
        }
        sb.append("\n");
        
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            sb.append(entry.getKey());
            for (String month : allMonths) {
                sb.append(",");
                Integer total = entry.getValue().get(month);
                if (total != null) {
                    sb.append(total);
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private String exportTotalsAsJSON(Map<String, Map<String, Integer>> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }
    
    private String exportTotalsAsXML(Map<String, Map<String, Integer>> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<agents>\n");
        
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            sb.append("  <agent id=\"").append(entry.getKey()).append("\">\n");
            for (Map.Entry<String, Integer> monthEntry : entry.getValue().entrySet()) {
                sb.append("    <month name=\"").append(monthEntry.getKey()).append("\">");
                sb.append(monthEntry.getValue());
                sb.append("</month>\n");
            }
            sb.append("  </agent>\n");
        }
        
        sb.append("</agents>");
        return sb.toString();
    }
}

