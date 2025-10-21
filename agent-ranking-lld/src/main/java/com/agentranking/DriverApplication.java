package com.agentranking;

import java.util.UUID;

/**
 * Main driver application demonstrating the Agent Ranking System.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     Customer Support Agent Ranking System - Demo              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            runDemo();
        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runDemo() {
        RankingSystem system = new RankingSystem();
        LeaderboardView view = new LeaderboardView(system);
        
        // Demo 1: Register agents
        System.out.println("📋 Demo 1: Registering Agents");
        System.out.println("─".repeat(64));
        
        Agent[] agents = {
            new Agent("A001", "Alice Johnson", "alice@company.com", "Technical Support"),
            new Agent("A002", "Bob Smith", "bob@company.com", "Technical Support"),
            new Agent("A003", "Carol White", "carol@company.com", "Billing"),
            new Agent("A004", "David Brown", "david@company.com", "Technical Support"),
            new Agent("A005", "Emma Davis", "emma@company.com", "Billing"),
            new Agent("A006", "Frank Wilson", "frank@company.com", "Sales"),
            new Agent("A007", "Grace Lee", "grace@company.com", "Sales"),
            new Agent("A008", "Henry Taylor", "henry@company.com", "Technical Support")
        };
        
        for (Agent agent : agents) {
            system.registerAgent(agent);
            System.out.println("✓ Registered: " + agent.getName() + " (" + agent.getDepartment() + ")");
        }
        
        System.out.println("\nTotal agents registered: " + system.getTotalAgents());
        System.out.println();
        
        // Demo 2: Submit ratings
        System.out.println("⭐ Demo 2: Submitting Customer Ratings");
        System.out.println("─".repeat(64));
        
        // Alice - Excellent performance
        submitRatings(system, "A001", new int[]{5, 5, 5, 4, 5, 5, 5, 4, 5, 5});
        System.out.println("✓ Alice: 10 ratings (mostly 5 stars)");
        
        // Bob - Good performance
        submitRatings(system, "A002", new int[]{4, 4, 5, 4, 3, 4, 5, 4, 4});
        System.out.println("✓ Bob: 9 ratings (mostly 4 stars)");
        
        // Carol - Average performance
        submitRatings(system, "A003", new int[]{3, 4, 3, 3, 4, 3, 3});
        System.out.println("✓ Carol: 7 ratings (mostly 3 stars)");
        
        // David - Excellent but fewer ratings
        submitRatings(system, "A004", new int[]{5, 5, 5, 5});
        System.out.println("✓ David: 4 ratings (all 5 stars)");
        
        // Emma - Mixed performance
        submitRatings(system, "A005", new int[]{2, 3, 4, 3, 2, 3, 4, 2});
        System.out.println("✓ Emma: 8 ratings (mixed)");
        
        // Frank - Good performance
        submitRatings(system, "A006", new int[]{4, 5, 4, 4, 5, 4});
        System.out.println("✓ Frank: 6 ratings (4-5 stars)");
        
        // Grace - Excellent performance
        submitRatings(system, "A007", new int[]{5, 5, 4, 5, 5, 5, 5});
        System.out.println("✓ Grace: 7 ratings (mostly 5 stars)");
        
        // Henry - Few ratings
        submitRatings(system, "A008", new int[]{4, 5});
        System.out.println("✓ Henry: 2 ratings");
        
        System.out.println("\nTotal ratings submitted: " + system.getTotalRatings());
        System.out.println();
        
        // Demo 3: Display leaderboard
        System.out.println("🏆 Demo 3: Overall Leaderboard");
        System.out.println(view.displayTopAgents(8));
        
        // Demo 4: Display agent details
        System.out.println("📊 Demo 4: Agent Details - Alice Johnson");
        System.out.println(view.displayAgentDetails("A001"));
        
        // Demo 5: Department leaderboard
        System.out.println("🏢 Demo 5: Technical Support Department Leaderboard");
        System.out.println(view.displayDepartmentLeaderboard("Technical Support"));
        
        // Demo 6: System statistics
        System.out.println("📈 Demo 6: System Statistics");
        System.out.println(view.displaySystemStats());
        
        // Demo 7: Real-time ranking update
        System.out.println("🔄 Demo 7: Real-time Ranking Update");
        System.out.println("─".repeat(64));
        System.out.println("Bob's current rank: " + system.getAgentRank("A002"));
        System.out.println("Adding 5 new 5-star ratings for Bob...");
        
        submitRatings(system, "A002", new int[]{5, 5, 5, 5, 5});
        
        System.out.println("Bob's new rank: " + system.getAgentRank("A002"));
        System.out.println("Bob's new average: " + 
            String.format("%.2f", system.getAgentStats("A002").getAverageRating()));
        System.out.println();
        
        System.out.println("Updated Leaderboard:");
        System.out.println(view.displayTopAgents(5));
        
        // Demo 8: Concurrent access simulation
        System.out.println("⚡ Demo 8: Concurrent Rating Submission");
        System.out.println("─".repeat(64));
        System.out.println("Simulating concurrent ratings from multiple customers...");
        
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    String agentId = "A00" + ((threadId % 3) + 1);
                    int score = 3 + (threadId % 3);
                    Rating rating = new Rating(
                        UUID.randomUUID().toString(),
                        agentId,
                        "CUST" + threadId,
                        score,
                        "Concurrent test rating",
                        "INT" + threadId + j
                    );
                    system.submitRating(rating);
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("✓ Concurrent ratings submitted successfully");
        System.out.println("Total ratings now: " + system.getTotalRatings());
        System.out.println();
        
        System.out.println("Final Leaderboard:");
        System.out.println(view.displayTopAgents(8));
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("═".repeat(64));
        System.out.println("For more examples, see examples/UsageExamples.java");
        System.out.println("═".repeat(64));
    }
    
    /**
     * Helper method to submit multiple ratings for an agent.
     */
    private static void submitRatings(RankingSystem system, String agentId, int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            Rating rating = new Rating(
                UUID.randomUUID().toString(),
                agentId,
                "CUST" + UUID.randomUUID().toString().substring(0, 8),
                scores[i],
                "Customer feedback " + (i + 1),
                "INT" + UUID.randomUUID().toString().substring(0, 8)
            );
            system.submitRating(rating);
        }
    }
}

