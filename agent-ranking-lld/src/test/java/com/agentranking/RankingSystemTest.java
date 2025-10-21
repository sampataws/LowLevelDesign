package com.agentranking;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

/**
 * Unit tests for RankingSystem.
 */
public class RankingSystemTest {
    
    private RankingSystem system;
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    
    @Before
    public void setUp() {
        system = new RankingSystem();
        agent1 = new Agent("A001", "Alice", "alice@test.com", "Support");
        agent2 = new Agent("A002", "Bob", "bob@test.com", "Support");
        agent3 = new Agent("A003", "Carol", "carol@test.com", "Billing");
        
        system.registerAgent(agent1);
        system.registerAgent(agent2);
        system.registerAgent(agent3);
    }
    
    @Test
    public void testRegisterAgent() {
        Agent newAgent = new Agent("A004", "David", "david@test.com", "Sales");
        system.registerAgent(newAgent);
        
        assertEquals(4, system.getTotalAgents());
        assertNotNull(system.getAgent("A004"));
    }
    
    @Test
    public void testSubmitRating() {
        Rating rating = new Rating("R001", "A001", "C001", 5, "Great service", "INT001");
        system.submitRating(rating);
        
        assertEquals(1, system.getTotalRatings());
        
        AgentStats stats = system.getAgentStats("A001");
        assertEquals(1, stats.getTotalRatings());
        assertEquals(5.0, stats.getAverageRating(), 0.01);
    }
    
    @Test
    public void testAverageRatingCalculation() {
        submitRatings("A001", new int[]{5, 4, 5, 3, 5});
        
        AgentStats stats = system.getAgentStats("A001");
        assertEquals(5, stats.getTotalRatings());
        assertEquals(4.4, stats.getAverageRating(), 0.01);
    }
    
    @Test
    public void testRankingOrder() {
        // Alice: avg 4.5
        submitRatings("A001", new int[]{5, 4, 5, 4});
        
        // Bob: avg 5.0
        submitRatings("A002", new int[]{5, 5, 5});
        
        // Carol: avg 3.0
        submitRatings("A003", new int[]{3, 3, 3});
        
        assertEquals(1, system.getAgentRank("A002")); // Bob is #1
        assertEquals(2, system.getAgentRank("A001")); // Alice is #2
        assertEquals(3, system.getAgentRank("A003")); // Carol is #3
    }
    
    @Test
    public void testRankingWithTies() {
        // Both get 4.0 average, but Alice has more ratings
        submitRatings("A001", new int[]{4, 4, 4, 4, 4});
        submitRatings("A002", new int[]{4, 4, 4});
        
        AgentStats stats1 = system.getAgentStats("A001");
        AgentStats stats2 = system.getAgentStats("A002");
        
        assertEquals(4.0, stats1.getAverageRating(), 0.01);
        assertEquals(4.0, stats2.getAverageRating(), 0.01);
        
        // Alice should rank higher due to more ratings
        assertTrue(stats1.getRank() < stats2.getRank());
    }
    
    @Test
    public void testGetTopAgents() {
        submitRatings("A001", new int[]{5, 5, 5});
        submitRatings("A002", new int[]{4, 4, 4});
        submitRatings("A003", new int[]{3, 3, 3});
        
        List<AgentStats> top2 = system.getTopAgents(2);
        
        assertEquals(2, top2.size());
        assertEquals("A001", top2.get(0).getAgentId());
        assertEquals("A002", top2.get(1).getAgentId());
    }
    
    @Test
    public void testDynamicRankingUpdate() {
        submitRatings("A001", new int[]{5, 5});
        submitRatings("A002", new int[]{4, 4});

        assertEquals(1, system.getAgentRank("A001"));
        assertEquals(2, system.getAgentRank("A002"));

        // Bob gets excellent ratings
        submitRatings("A002", new int[]{5, 5, 5, 5});

        // Bob now has avg 4.67 (4+4+5+5+5+5)/6, Alice has 5.0
        // Alice should still be ranked higher due to better average
        // But Bob's rank should improve or stay same
        int bobNewRank = system.getAgentRank("A002");
        assertTrue("Bob's rank should be 1 or 2", bobNewRank <= 2);
    }
    
    @Test
    public void testRemoveRating() {
        Rating rating = new Rating("R001", "A001", "C001", 5, "Great", "INT001");
        system.submitRating(rating);
        
        assertEquals(1, system.getTotalRatings());
        assertEquals(5.0, system.getAgentStats("A001").getAverageRating(), 0.01);
        
        system.removeRating("R001");
        
        assertEquals(0, system.getTotalRatings());
        assertEquals(0.0, system.getAgentStats("A001").getAverageRating(), 0.01);
    }
    
    @Test
    public void testGetAgentsByDepartment() {
        Agent agent4 = new Agent("A004", "David", "david@test.com", "Support");
        system.registerAgent(agent4);
        
        submitRatings("A001", new int[]{5, 5});
        submitRatings("A002", new int[]{4, 4});
        submitRatings("A003", new int[]{3, 3});
        submitRatings("A004", new int[]{5, 5, 5});
        
        List<AgentStats> supportAgents = system.getAgentsByDepartment("Support");
        
        assertEquals(3, supportAgents.size());
        // Should be sorted by rank
        assertTrue(supportAgents.get(0).getAverageRating() >= 
                  supportAgents.get(1).getAverageRating());
    }
    
    @Test
    public void testSystemAverageRating() {
        submitRatings("A001", new int[]{5, 5});
        submitRatings("A002", new int[]{3, 3});
        
        double systemAvg = system.getSystemAverageRating();
        assertEquals(4.0, systemAvg, 0.01);
    }
    
    @Test
    public void testConcurrentRatingSubmission() throws InterruptedException {
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    Rating rating = new Rating(
                        UUID.randomUUID().toString(),
                        "A001",
                        "C" + threadId,
                        4,
                        "Test",
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
            thread.join();
        }
        
        AgentStats stats = system.getAgentStats("A001");
        assertEquals(50, stats.getTotalRatings());
        assertEquals(4.0, stats.getAverageRating(), 0.01);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSubmitRatingForNonExistentAgent() {
        Rating rating = new Rating("R001", "A999", "C001", 5, "Test", "INT001");
        system.submitRating(rating);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullAgent() {
        system.registerAgent(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSubmitNullRating() {
        system.submitRating(null);
    }
    
    @Test
    public void testGetAgentRatings() {
        submitRatings("A001", new int[]{5, 4, 5});
        
        List<Rating> ratings = system.getAgentRatings("A001");
        
        assertEquals(3, ratings.size());
        // Should be sorted by timestamp (most recent first)
        assertTrue(ratings.get(0).getTimestamp().isAfter(ratings.get(2).getTimestamp()) ||
                  ratings.get(0).getTimestamp().isEqual(ratings.get(2).getTimestamp()));
    }
    
    @Test
    public void testClear() {
        submitRatings("A001", new int[]{5, 4, 5});
        
        assertEquals(3, system.getTotalRatings());
        
        system.clear();
        
        assertEquals(0, system.getTotalAgents());
        assertEquals(0, system.getTotalRatings());
    }
    
    // Helper method
    private void submitRatings(String agentId, int[] scores) {
        for (int i = 0; i < scores.length; i++) {
            Rating rating = new Rating(
                UUID.randomUUID().toString(),
                agentId,
                "CUST" + i,
                scores[i],
                "Test rating " + i,
                "INT" + i
            );
            system.submitRating(rating);
        }
    }
}

