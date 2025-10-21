package com.customersatisfaction;

import org.junit.Test;

import static org.junit.Assert.*;

public class AgentStatsTest {
    
    @Test
    public void testBasicStats() {
        AgentStats stats = new AgentStats("agent1", 3, 12);
        assertEquals("agent1", stats.getAgentId());
        assertEquals(3, stats.getTotalRatings());
        assertEquals(12, stats.getTotalScore());
        assertEquals(4.0, stats.getAverageRating(), 0.001);
    }
    
    @Test
    public void testAverageCalculation() {
        AgentStats stats = new AgentStats("agent1", 4, 18);
        assertEquals(4.5, stats.getAverageRating(), 0.001);
    }
    
    @Test
    public void testZeroRatings() {
        AgentStats stats = new AgentStats("agent1", 0, 0);
        assertEquals(0.0, stats.getAverageRating(), 0.001);
    }
    
    @Test
    public void testCompareTo_DifferentAverages() {
        AgentStats stats1 = new AgentStats("agent1", 2, 10); // avg = 5.0
        AgentStats stats2 = new AgentStats("agent2", 2, 6);  // avg = 3.0
        
        assertTrue(stats1.compareTo(stats2) < 0); // stats1 should come first (higher avg)
        assertTrue(stats2.compareTo(stats1) > 0);
    }
    
    @Test
    public void testCompareTo_SameAverage_DifferentIds() {
        AgentStats stats1 = new AgentStats("alice", 2, 8); // avg = 4.0
        AgentStats stats2 = new AgentStats("bob", 2, 8);   // avg = 4.0
        
        assertTrue(stats1.compareTo(stats2) < 0); // alice comes before bob
        assertTrue(stats2.compareTo(stats1) > 0);
    }
    
    @Test
    public void testCompareTo_SameEverything() {
        AgentStats stats1 = new AgentStats("agent1", 2, 8);
        AgentStats stats2 = new AgentStats("agent1", 2, 8);
        
        assertEquals(0, stats1.compareTo(stats2));
    }
    
    @Test
    public void testToString() {
        AgentStats stats = new AgentStats("agent1", 3, 12);
        String str = stats.toString();
        assertTrue(str.contains("agent1"));
        assertTrue(str.contains("3"));
        assertTrue(str.contains("12"));
        assertTrue(str.contains("4.00"));
    }
}

