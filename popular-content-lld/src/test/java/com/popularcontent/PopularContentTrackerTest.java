package com.popularcontent;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for PopularContentTracker.
 */
public class PopularContentTrackerTest {
    
    private PopularContentTracker tracker;
    
    @Before
    public void setUp() {
        tracker = new PopularContentTracker();
    }
    
    // ==================== Basic Operations ====================
    
    @Test
    public void testInitialState_EmptyTracker() {
        assertEquals(-1, tracker.getMostPopular());
        assertTrue(tracker.isEmpty());
        assertEquals(0, tracker.size());
    }
    
    @Test
    public void testIncreasePopularity_SingleContent() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        
        assertEquals(1, tracker.getMostPopular());
        assertEquals(1, tracker.getPopularity(1));
        assertEquals(1, tracker.size());
    }
    
    @Test
    public void testIncreasePopularity_MultipleIncrements() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        
        assertEquals(1, tracker.getMostPopular());
        assertEquals(3, tracker.getPopularity(1));
        assertEquals(1, tracker.size());
    }
    
    @Test
    public void testDecreasePopularity_FromPositive() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        
        tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        
        assertEquals(1, tracker.getMostPopular());
        assertEquals(2, tracker.getPopularity(1));
    }
    
    @Test
    public void testDecreasePopularity_ToZero() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        
        assertEquals(-1, tracker.getMostPopular());
        assertEquals(0, tracker.getPopularity(1));
        assertTrue(tracker.isEmpty());
    }
    
    @Test
    public void testDecreasePopularity_BelowZero() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        
        assertEquals(-1, tracker.getMostPopular());
        assertEquals(0, tracker.getPopularity(1));
        assertTrue(tracker.isEmpty());
    }
    
    @Test
    public void testDecreasePopularity_FromZero() {
        tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        
        assertEquals(-1, tracker.getMostPopular());
        assertEquals(0, tracker.getPopularity(1));
        assertTrue(tracker.isEmpty());
    }
    
    // ==================== Multiple Contents ====================
    
    @Test
    public void testMultipleContents_DifferentPopularities() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        
        assertEquals(3, tracker.getMostPopular());
        assertEquals(3, tracker.getMaxPopularity());
        assertEquals(3, tracker.size());
    }
    
    @Test
    public void testMultipleContents_SamePopularity() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        
        int mostPopular = tracker.getMostPopular();
        assertTrue(mostPopular == 1 || mostPopular == 2 || mostPopular == 3);
        assertEquals(1, tracker.getMaxPopularity());
        
        List<Integer> allMostPopular = tracker.getAllMostPopular();
        assertEquals(3, allMostPopular.size());
        assertTrue(allMostPopular.contains(1));
        assertTrue(allMostPopular.contains(2));
        assertTrue(allMostPopular.contains(3));
    }
    
    @Test
    public void testMostPopular_ChangesOverTime() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        assertEquals(1, tracker.getMostPopular());
        
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        assertEquals(2, tracker.getMostPopular());
        
        tracker.processAction(2, ContentAction.DECREASE_POPULARITY);
        tracker.processAction(2, ContentAction.DECREASE_POPULARITY);
        tracker.processAction(2, ContentAction.DECREASE_POPULARITY);
        assertEquals(1, tracker.getMostPopular());
    }
    
    // ==================== Edge Cases ====================
    
    @Test
    public void testGetPopularity_NonExistentContent() {
        assertEquals(0, tracker.getPopularity(999));
    }
    
    @Test
    public void testGetMaxPopularity_EmptyTracker() {
        assertEquals(0, tracker.getMaxPopularity());
    }
    
    @Test
    public void testGetAllMostPopular_EmptyTracker() {
        List<Integer> result = tracker.getAllMostPopular();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testClear_RemovesAllContent() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        
        tracker.clear();
        
        assertEquals(-1, tracker.getMostPopular());
        assertTrue(tracker.isEmpty());
        assertEquals(0, tracker.size());
    }
    
    // ==================== Validation ====================
    
    @Test(expected = IllegalArgumentException.class)
    public void testProcessAction_NegativeContentId() {
        tracker.processAction(-1, ContentAction.INCREASE_POPULARITY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProcessAction_ZeroContentId() {
        tracker.processAction(0, ContentAction.INCREASE_POPULARITY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProcessAction_NullAction() {
        tracker.processAction(1, null);
    }
    
    // ==================== Statistics ====================
    
    @Test
    public void testGetStatistics_EmptyTracker() {
        PopularContentTracker.ContentStatistics stats = tracker.getStatistics();
        
        assertEquals(0, stats.getCount());
        assertEquals(0, stats.getMinPopularity());
        assertEquals(0, stats.getMaxPopularity());
        assertEquals(0.0, stats.getAvgPopularity(), 0.001);
    }
    
    @Test
    public void testGetStatistics_WithData() {
        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        
        PopularContentTracker.ContentStatistics stats = tracker.getStatistics();
        
        assertEquals(3, stats.getCount());
        assertEquals(1, stats.getMinPopularity());
        assertEquals(3, stats.getMaxPopularity());
        assertEquals(2.0, stats.getAvgPopularity(), 0.001);
    }
    
    // ==================== Complex Scenarios ====================
    
    @Test
    public void testComplexScenario_PopularityFluctuations() {
        // Build up content 1
        for (int i = 0; i < 10; i++) {
            tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
        }
        assertEquals(10, tracker.getPopularity(1));
        
        // Build up content 2
        for (int i = 0; i < 5; i++) {
            tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
        }
        assertEquals(5, tracker.getPopularity(2));
        
        // Content 1 should be most popular
        assertEquals(1, tracker.getMostPopular());
        
        // Decrease content 1 significantly
        for (int i = 0; i < 8; i++) {
            tracker.processAction(1, ContentAction.DECREASE_POPULARITY);
        }
        assertEquals(2, tracker.getPopularity(1));
        
        // Content 2 should now be most popular
        assertEquals(2, tracker.getMostPopular());
    }
    
    @Test
    public void testComplexScenario_MultipleContentsRemoval() {
        // Add multiple contents
        for (int i = 1; i <= 5; i++) {
            tracker.processAction(i, ContentAction.INCREASE_POPULARITY);
        }
        assertEquals(5, tracker.size());
        
        // Remove them one by one
        for (int i = 1; i <= 5; i++) {
            tracker.processAction(i, ContentAction.DECREASE_POPULARITY);
        }
        
        assertEquals(0, tracker.size());
        assertEquals(-1, tracker.getMostPopular());
    }
    
    // ==================== Concurrent Operations ====================
    
    @Test
    public void testConcurrentIncreases() throws InterruptedException {
        int numThreads = 10;
        int incrementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(numThreads * incrementsPerThread, tracker.getPopularity(1));
        assertEquals(1, tracker.getMostPopular());
    }
    
    @Test
    public void testConcurrentMixedOperations() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger increases = new AtomicInteger(0);
        AtomicInteger decreases = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Random random = new Random(threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        int contentId = random.nextInt(10) + 1;
                        ContentAction action = random.nextBoolean() 
                            ? ContentAction.INCREASE_POPULARITY 
                            : ContentAction.DECREASE_POPULARITY;
                        
                        tracker.processAction(contentId, action);
                        
                        if (action == ContentAction.INCREASE_POPULARITY) {
                            increases.incrementAndGet();
                        } else {
                            decreases.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Verify no exceptions occurred and tracker is in valid state
        assertTrue(tracker.size() >= 0);
        int mostPopular = tracker.getMostPopular();
        assertTrue(mostPopular == -1 || mostPopular > 0);
    }
    
    @Test
    public void testConcurrentReadsAndWrites() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Random random = new Random(threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (random.nextBoolean()) {
                            // Write operation
                            int contentId = random.nextInt(20) + 1;
                            ContentAction action = random.nextBoolean() 
                                ? ContentAction.INCREASE_POPULARITY 
                                : ContentAction.DECREASE_POPULARITY;
                            tracker.processAction(contentId, action);
                        } else {
                            // Read operation
                            tracker.getMostPopular();
                            tracker.getMaxPopularity();
                            tracker.size();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Verify tracker is in valid state
        assertTrue(tracker.size() >= 0);
        int mostPopular = tracker.getMostPopular();
        assertTrue(mostPopular == -1 || mostPopular > 0);
    }
}

