package com.customersatisfaction;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class CustomerSatisfactionSystemTest {
    
    private CustomerSatisfactionSystem system;
    
    @Before
    public void setUp() {
        system = new CustomerSatisfactionSystem();
    }
    
    // ========== Part (a): Basic Functionality ==========
    
    @Test
    public void testAddSingleRating() {
        system.addRating("agent1", 5);
        List<AgentStats> stats = system.getAllAgentsSorted();
        
        assertEquals(1, stats.size());
        assertEquals("agent1", stats.get(0).getAgentId());
        assertEquals(5.0, stats.get(0).getAverageRating(), 0.001);
    }
    
    @Test
    public void testAddMultipleRatingsForSameAgent() {
        system.addRating("agent1", 5);
        system.addRating("agent1", 3);
        system.addRating("agent1", 4);
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        assertEquals(1, stats.size());
        assertEquals("agent1", stats.get(0).getAgentId());
        assertEquals(4.0, stats.get(0).getAverageRating(), 0.001);
        assertEquals(3, stats.get(0).getTotalRatings());
    }
    
    @Test
    public void testMultipleAgentsSortedByAverage() {
        system.addRating("alice", 5);
        system.addRating("alice", 5);
        
        system.addRating("bob", 3);
        system.addRating("bob", 3);
        
        system.addRating("charlie", 4);
        system.addRating("charlie", 4);
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        assertEquals(3, stats.size());
        
        // Should be sorted: alice (5.0), charlie (4.0), bob (3.0)
        assertEquals("alice", stats.get(0).getAgentId());
        assertEquals(5.0, stats.get(0).getAverageRating(), 0.001);
        
        assertEquals("charlie", stats.get(1).getAgentId());
        assertEquals(4.0, stats.get(1).getAverageRating(), 0.001);
        
        assertEquals("bob", stats.get(2).getAgentId());
        assertEquals(3.0, stats.get(2).getAverageRating(), 0.001);
    }
    
    @Test
    public void testEmptySystem() {
        List<AgentStats> stats = system.getAllAgentsSorted();
        assertTrue(stats.isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRatingScore() {
        system.addRating("agent1", 6);
    }
    
    // ========== Part (b): Tie Breaking ==========
    
    @Test
    public void testTieBreaking_AgentId() {
        system.setTieBreakStrategy(TieBreakStrategy.AGENT_ID);
        
        // All have average 4.0
        system.addRating("charlie", 4);
        system.addRating("charlie", 4);
        
        system.addRating("alice", 3);
        system.addRating("alice", 5);
        
        system.addRating("bob", 4);
        system.addRating("bob", 4);
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        
        // Should be sorted alphabetically: alice, bob, charlie
        assertEquals("alice", stats.get(0).getAgentId());
        assertEquals("bob", stats.get(1).getAgentId());
        assertEquals("charlie", stats.get(2).getAgentId());
    }
    
    @Test
    public void testTieBreaking_TotalRatings() {
        system.setTieBreakStrategy(TieBreakStrategy.TOTAL_RATINGS);
        
        // All have average 4.0
        system.addRating("alice", 4);
        system.addRating("alice", 4);
        system.addRating("alice", 4);
        system.addRating("alice", 4);
        
        system.addRating("bob", 4);
        system.addRating("bob", 4);
        
        system.addRating("charlie", 3);
        system.addRating("charlie", 5);
        system.addRating("charlie", 4);
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        
        // Should be sorted by total ratings: alice (4), charlie (3), bob (2)
        assertEquals("alice", stats.get(0).getAgentId());
        assertEquals(4, stats.get(0).getTotalRatings());
        
        assertEquals("charlie", stats.get(1).getAgentId());
        assertEquals(3, stats.get(1).getTotalRatings());
        
        assertEquals("bob", stats.get(2).getAgentId());
        assertEquals(2, stats.get(2).getTotalRatings());
    }
    
    @Test
    public void testTieBreaking_None() {
        system.setTieBreakStrategy(TieBreakStrategy.NONE);
        
        system.addRating("alice", 4);
        system.addRating("bob", 4);
        system.addRating("charlie", 4);
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        
        // All should have same average
        assertEquals(3, stats.size());
        for (AgentStats stat : stats) {
            assertEquals(4.0, stat.getAverageRating(), 0.001);
        }
    }
    
    // ========== Part (c): Monthly Performance ==========
    
    @Test
    public void testMonthlyPerformance() {
        LocalDateTime jan1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime jan2 = LocalDateTime.of(2024, 1, 20, 14, 0);
        LocalDateTime feb1 = LocalDateTime.of(2024, 2, 10, 9, 0);
        
        system.addRating("alice", 5, jan1);
        system.addRating("alice", 3, jan2);
        system.addRating("alice", 4, feb1);
        
        system.addRating("bob", 4, jan1);
        system.addRating("bob", 5, feb1);
        
        // January stats
        List<AgentStats> janStats = system.getBestAgentsForMonth(YearMonth.of(2024, 1));
        assertEquals(2, janStats.size());
        
        AgentStats aliceJan = janStats.stream()
            .filter(s -> s.getAgentId().equals("alice"))
            .findFirst()
            .orElse(null);
        assertNotNull(aliceJan);
        assertEquals(4.0, aliceJan.getAverageRating(), 0.001); // (5+3)/2
        assertEquals(2, aliceJan.getTotalRatings());
        
        // February stats
        List<AgentStats> febStats = system.getBestAgentsForMonth(YearMonth.of(2024, 2));
        assertEquals(2, febStats.size());
        
        AgentStats aliceFeb = febStats.stream()
            .filter(s -> s.getAgentId().equals("alice"))
            .findFirst()
            .orElse(null);
        assertNotNull(aliceFeb);
        assertEquals(4.0, aliceFeb.getAverageRating(), 0.001);
        assertEquals(1, aliceFeb.getTotalRatings());
    }
    
    @Test
    public void testBestAgentsForMonth_Sorted() {
        LocalDateTime jan1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        system.addRating("alice", 5, jan1);
        system.addRating("alice", 5, jan1);
        
        system.addRating("bob", 3, jan1);
        system.addRating("bob", 3, jan1);
        
        system.addRating("charlie", 4, jan1);
        system.addRating("charlie", 4, jan1);
        
        List<AgentStats> stats = system.getBestAgentsForMonth(YearMonth.of(2024, 1));
        
        // Should be sorted: alice (5.0), charlie (4.0), bob (3.0)
        assertEquals("alice", stats.get(0).getAgentId());
        assertEquals("charlie", stats.get(1).getAgentId());
        assertEquals("bob", stats.get(2).getAgentId());
    }
    
    @Test
    public void testGetAvailableMonths() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        LocalDateTime mar = LocalDateTime.of(2024, 3, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 4, feb);
        system.addRating("alice", 3, mar);
        
        Set<YearMonth> months = system.getAvailableMonths();
        assertEquals(3, months.size());
        assertTrue(months.contains(YearMonth.of(2024, 1)));
        assertTrue(months.contains(YearMonth.of(2024, 2)));
        assertTrue(months.contains(YearMonth.of(2024, 3)));
    }
    
    @Test
    public void testNonExistentMonth() {
        List<AgentStats> stats = system.getBestAgentsForMonth(YearMonth.of(2024, 12));
        assertTrue(stats.isEmpty());
    }
    
    // ========== Part (d): Export Formats ==========
    
    @Test
    public void testExportAsCSV() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        system.addRating("alice", 4, feb);
        
        String csv = system.exportMonthlyAverages(ExportFormat.CSV);
        
        assertNotNull(csv);
        assertTrue(csv.contains("AgentID"));
        assertTrue(csv.contains("2024-01"));
        assertTrue(csv.contains("2024-02"));
        assertTrue(csv.contains("alice"));
        assertTrue(csv.contains("4.00")); // (5+3)/2
    }
    
    @Test
    public void testExportAsJSON() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        
        String json = system.exportMonthlyAverages(ExportFormat.JSON);
        
        assertNotNull(json);
        assertTrue(json.contains("alice"));
        assertTrue(json.contains("2024-01"));
    }
    
    @Test
    public void testExportAsXML() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        
        String xml = system.exportMonthlyAverages(ExportFormat.XML);
        
        assertNotNull(xml);
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<agents>"));
        assertTrue(xml.contains("alice"));
        assertTrue(xml.contains("2024-01"));
        assertTrue(xml.contains("</agents>"));
    }
    
    @Test
    public void testExportMultipleAgentsMultipleMonths() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 4, feb);
        
        system.addRating("bob", 3, jan);
        system.addRating("bob", 5, feb);
        
        String csv = system.exportMonthlyAverages(ExportFormat.CSV);
        
        assertTrue(csv.contains("alice"));
        assertTrue(csv.contains("bob"));
        assertTrue(csv.contains("2024-01"));
        assertTrue(csv.contains("2024-02"));
    }
    
    // ========== Part (e): Unsorted and Totals ==========
    
    @Test
    public void testGetAllAgentsUnsorted() {
        system.addRating("charlie", 5);
        system.addRating("alice", 3);
        system.addRating("bob", 4);
        
        List<AgentStats> unsorted = system.getAllAgentsUnsorted();
        assertEquals(3, unsorted.size());
        
        // Verify all agents are present (order doesn't matter)
        Set<String> agentIds = Set.of("alice", "bob", "charlie");
        for (AgentStats stat : unsorted) {
            assertTrue(agentIds.contains(stat.getAgentId()));
        }
    }
    
    @Test
    public void testExportMonthlyTotals_CSV() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        system.addRating("alice", 4, feb);
        
        String csv = system.exportMonthlyTotals(ExportFormat.CSV);
        
        assertNotNull(csv);
        assertTrue(csv.contains("alice"));
        assertTrue(csv.contains("8")); // 5+3 for January
        assertTrue(csv.contains("4")); // 4 for February
    }
    
    @Test
    public void testExportMonthlyTotals_JSON() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        
        String json = system.exportMonthlyTotals(ExportFormat.JSON);
        
        assertNotNull(json);
        assertTrue(json.contains("alice"));
        assertTrue(json.contains("8")); // 5+3
    }
    
    @Test
    public void testExportMonthlyTotals_XML() {
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        system.addRating("alice", 5, jan);
        system.addRating("alice", 3, jan);
        
        String xml = system.exportMonthlyTotals(ExportFormat.XML);
        
        assertNotNull(xml);
        assertTrue(xml.contains("alice"));
        assertTrue(xml.contains("8")); // 5+3
    }
    
    // ========== Concurrent Operations ==========
    
    @Test
    public void testConcurrentRatings() throws InterruptedException {
        int numThreads = 10;
        int ratingsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ratingsPerThread; j++) {
                        system.addRating("agent" + (threadId % 3), (j % 5) + 1);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        List<AgentStats> stats = system.getAllAgentsSorted();
        assertFalse(stats.isEmpty());
        
        // Verify total ratings
        int totalRatings = stats.stream()
            .mapToInt(AgentStats::getTotalRatings)
            .sum();
        assertEquals(numThreads * ratingsPerThread, totalRatings);
    }
}

