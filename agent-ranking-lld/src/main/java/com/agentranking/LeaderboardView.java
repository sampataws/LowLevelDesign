package com.agentranking;

import java.util.List;

/**
 * Provides formatted views of the agent leaderboard.
 */
public class LeaderboardView {
    
    private final RankingSystem rankingSystem;
    
    public LeaderboardView(RankingSystem rankingSystem) {
        this.rankingSystem = rankingSystem;
    }
    
    /**
     * Displays the top N agents in a formatted table.
     */
    public String displayTopAgents(int n) {
        List<AgentStats> topAgents = rankingSystem.getTopAgents(n);
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  Top %d Agents Leaderboard                                    ║\n", n));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Rank │ Agent ID      │ Name              │ Avg Rating │ Total ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        
        for (AgentStats stats : topAgents) {
            Agent agent = rankingSystem.getAgent(stats.getAgentId());
            String name = agent != null ? agent.getName() : "Unknown";
            
            sb.append(String.format("║ %-4d │ %-13s │ %-17s │ %-10.2f │ %-5d ║\n",
                stats.getRank(),
                truncate(stats.getAgentId(), 13),
                truncate(name, 17),
                stats.getAverageRating(),
                stats.getTotalRatings()));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    /**
     * Displays detailed statistics for a specific agent.
     */
    public String displayAgentDetails(String agentId) {
        Agent agent = rankingSystem.getAgent(agentId);
        AgentStats stats = rankingSystem.getAgentStats(agentId);
        
        if (agent == null || stats == null) {
            return "Agent not found: " + agentId;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║  Agent Details                                                 ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Agent ID:       %-46s ║\n", agent.getAgentId()));
        sb.append(String.format("║ Name:           %-46s ║\n", agent.getName()));
        sb.append(String.format("║ Department:     %-46s ║\n", agent.getDepartment()));
        sb.append(String.format("║ Email:          %-46s ║\n", agent.getEmail()));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Current Rank:   %-46d ║\n", stats.getRank()));
        sb.append(String.format("║ Average Rating: %-46.2f ║\n", stats.getAverageRating()));
        sb.append(String.format("║ Total Ratings:  %-46d ║\n", stats.getTotalRatings()));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Rating Distribution:                                           ║\n");
        sb.append(String.format("║   5 Stars: %-4d  %s\n", 
            stats.getFiveStarCount(), 
            getStarBar(stats.getFiveStarCount(), stats.getTotalRatings())));
        sb.append(String.format("║   4 Stars: %-4d  %s\n", 
            stats.getFourStarCount(), 
            getStarBar(stats.getFourStarCount(), stats.getTotalRatings())));
        sb.append(String.format("║   3 Stars: %-4d  %s\n", 
            stats.getThreeStarCount(), 
            getStarBar(stats.getThreeStarCount(), stats.getTotalRatings())));
        sb.append(String.format("║   2 Stars: %-4d  %s\n", 
            stats.getTwoStarCount(), 
            getStarBar(stats.getTwoStarCount(), stats.getTotalRatings())));
        sb.append(String.format("║   1 Star:  %-4d  %s\n", 
            stats.getOneStarCount(), 
            getStarBar(stats.getOneStarCount(), stats.getTotalRatings())));
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    /**
     * Displays leaderboard by department.
     */
    public String displayDepartmentLeaderboard(String department) {
        List<AgentStats> deptAgents = rankingSystem.getAgentsByDepartment(department);
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  %s Department Leaderboard                    ║\n", 
            truncate(department, 30)));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Rank │ Agent ID      │ Name              │ Avg Rating │ Total ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        
        int deptRank = 1;
        for (AgentStats stats : deptAgents) {
            Agent agent = rankingSystem.getAgent(stats.getAgentId());
            String name = agent != null ? agent.getName() : "Unknown";
            
            sb.append(String.format("║ %-4d │ %-13s │ %-17s │ %-10.2f │ %-5d ║\n",
                deptRank++,
                truncate(stats.getAgentId(), 13),
                truncate(name, 17),
                stats.getAverageRating(),
                stats.getTotalRatings()));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    /**
     * Displays system-wide statistics.
     */
    public String displaySystemStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║  System Statistics                                             ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Total Agents:          %-39d ║\n", 
            rankingSystem.getTotalAgents()));
        sb.append(String.format("║ Total Ratings:         %-39d ║\n", 
            rankingSystem.getTotalRatings()));
        sb.append(String.format("║ System Average Rating: %-39.2f ║\n", 
            rankingSystem.getSystemAverageRating()));
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    /**
     * Creates a visual bar for rating distribution.
     */
    private String getStarBar(int count, int total) {
        if (total == 0) return "║";
        
        int barLength = 40;
        int filled = (int) ((double) count / total * barLength);
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        for (int i = filled; i < barLength; i++) {
            bar.append("░");
        }
        bar.append(" ║");
        
        return bar.toString();
    }
    
    /**
     * Truncates a string to the specified length.
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}

