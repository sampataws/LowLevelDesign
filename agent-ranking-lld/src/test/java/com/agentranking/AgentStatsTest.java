package com.agentranking;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AgentStats.
 */
public class AgentStatsTest {
    
    private AgentStats stats;
    
    @Before
    public void setUp() {
        stats = new AgentStats("A001");
    }
    
    @Test
    public void testInitialState() {
        assertEquals("A001", stats.getAgentId());
        assertEquals(0, stats.getTotalRatings());
        assertEquals(0.0, stats.getAverageRating(), 0.01);
        assertEquals(0, stats.getRank());
    }
    
    @Test
    public void testAddSingleRating() {
        stats.addRating(5);
        
        assertEquals(1, stats.getTotalRatings());
        assertEquals(5.0, stats.getAverageRating(), 0.01);
        assertEquals(1, stats.getFiveStarCount());
    }
    
    @Test
    public void testAddMultipleRatings() {
        stats.addRating(5);
        stats.addRating(4);
        stats.addRating(5);
        stats.addRating(3);
        
        assertEquals(4, stats.getTotalRatings());
        assertEquals(4.25, stats.getAverageRating(), 0.01);
    }
    
    @Test
    public void testRatingDistribution() {
        stats.addRating(5);
        stats.addRating(5);
        stats.addRating(4);
        stats.addRating(3);
        stats.addRating(2);
        stats.addRating(1);
        
        assertEquals(2, stats.getFiveStarCount());
        assertEquals(1, stats.getFourStarCount());
        assertEquals(1, stats.getThreeStarCount());
        assertEquals(1, stats.getTwoStarCount());
        assertEquals(1, stats.getOneStarCount());
    }
    
    @Test
    public void testRemoveRating() {
        stats.addRating(5);
        stats.addRating(4);
        stats.addRating(5);
        
        assertEquals(3, stats.getTotalRatings());
        assertEquals(4.67, stats.getAverageRating(), 0.01);
        
        stats.removeRating(4);
        
        assertEquals(2, stats.getTotalRatings());
        assertEquals(5.0, stats.getAverageRating(), 0.01);
    }
    
    @Test
    public void testRemoveAllRatings() {
        stats.addRating(5);
        stats.removeRating(5);
        
        assertEquals(0, stats.getTotalRatings());
        assertEquals(0.0, stats.getAverageRating(), 0.01);
    }
    
    @Test
    public void testCompareTo_DifferentAverages() {
        AgentStats stats1 = new AgentStats("A001");
        AgentStats stats2 = new AgentStats("A002");
        
        stats1.addRating(5);
        stats1.addRating(5);
        
        stats2.addRating(4);
        stats2.addRating(4);
        
        // stats1 (avg 5.0) should be ranked higher than stats2 (avg 4.0)
        assertTrue(stats1.compareTo(stats2) < 0);
        assertTrue(stats2.compareTo(stats1) > 0);
    }
    
    @Test
    public void testCompareTo_SameAverage_DifferentCount() {
        AgentStats stats1 = new AgentStats("A001");
        AgentStats stats2 = new AgentStats("A002");
        
        stats1.addRating(4);
        stats1.addRating(4);
        stats1.addRating(4);
        
        stats2.addRating(4);
        stats2.addRating(4);
        
        // Both have avg 4.0, but stats1 has more ratings
        assertTrue(stats1.compareTo(stats2) < 0);
    }
    
    @Test
    public void testCompareTo_Equal() {
        AgentStats stats1 = new AgentStats("A001");
        AgentStats stats2 = new AgentStats("A002");
        
        stats1.addRating(4);
        stats1.addRating(5);
        
        stats2.addRating(4);
        stats2.addRating(5);
        
        // Same average, same count
        assertEquals(0, stats1.compareTo(stats2));
    }
    
    @Test
    public void testSetRank() {
        stats.setRank(5);
        assertEquals(5, stats.getRank());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidRating_TooLow() {
        stats.addRating(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidRating_TooHigh() {
        stats.addRating(6);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullAgentId() {
        new AgentStats(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyAgentId() {
        new AgentStats("");
    }
    
    @Test
    public void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    stats.addRating(4);
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        assertEquals(1000, stats.getTotalRatings());
        assertEquals(4.0, stats.getAverageRating(), 0.01);
    }
}

