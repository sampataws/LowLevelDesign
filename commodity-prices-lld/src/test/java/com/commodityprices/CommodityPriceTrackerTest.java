package com.commodityprices;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for CommodityPriceTracker.
 */
public class CommodityPriceTrackerTest {
    
    private CommodityPriceTracker tracker;
    
    @Before
    public void setUp() {
        tracker = new CommodityPriceTracker();
    }
    
    // ==================== Basic Operations ====================
    
    @Test
    public void testUpdate_SingleDataPoint() {
        tracker.update(1000, 100.50);
        
        assertEquals(Double.valueOf(100.50), tracker.getMaxPrice());
        assertEquals(Double.valueOf(100.50), tracker.getPriceAt(1000));
        assertEquals(1, tracker.size());
    }
    
    @Test
    public void testUpdate_MultipleDataPoints() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 105.00);
        tracker.update(3000, 98.00);
        
        assertEquals(Double.valueOf(105.00), tracker.getMaxPrice());
        assertEquals(3, tracker.size());
    }
    
    @Test
    public void testGetMaxPrice_EmptyTracker() {
        assertNull(tracker.getMaxPrice());
    }
    
    @Test
    public void testGetPriceAt_ExistingTimestamp() {
        tracker.update(1000, 100.50);
        
        assertEquals(Double.valueOf(100.50), tracker.getPriceAt(1000));
    }
    
    @Test
    public void testGetPriceAt_NonExistingTimestamp() {
        tracker.update(1000, 100.50);
        
        assertNull(tracker.getPriceAt(2000));
    }
    
    @Test
    public void testIsEmpty_EmptyTracker() {
        assertTrue(tracker.isEmpty());
    }
    
    @Test
    public void testIsEmpty_NonEmptyTracker() {
        tracker.update(1000, 100.00);
        
        assertFalse(tracker.isEmpty());
    }
    
    // ==================== Out-of-Order Timestamps ====================
    
    @Test
    public void testOutOfOrderTimestamps_MaxPriceCorrect() {
        tracker.update(5000, 120.00);
        tracker.update(2000, 115.00);
        tracker.update(8000, 125.00);
        tracker.update(1000, 110.00);
        
        assertEquals(Double.valueOf(125.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testOutOfOrderTimestamps_AllPricesAccessible() {
        tracker.update(5000, 120.00);
        tracker.update(2000, 115.00);
        tracker.update(8000, 125.00);
        
        assertEquals(Double.valueOf(120.00), tracker.getPriceAt(5000));
        assertEquals(Double.valueOf(115.00), tracker.getPriceAt(2000));
        assertEquals(Double.valueOf(125.00), tracker.getPriceAt(8000));
    }
    
    @Test
    public void testOutOfOrderTimestamps_SizeCorrect() {
        tracker.update(5000, 120.00);
        tracker.update(2000, 115.00);
        tracker.update(8000, 125.00);
        tracker.update(1000, 110.00);
        
        assertEquals(4, tracker.size());
    }
    
    // ==================== Duplicate Timestamps (Updates) ====================
    
    @Test
    public void testDuplicateTimestamp_UpdatesPrice() {
        tracker.update(1000, 100.00);
        tracker.update(1000, 110.00);
        
        assertEquals(Double.valueOf(110.00), tracker.getPriceAt(1000));
        assertEquals(1, tracker.size());
    }
    
    @Test
    public void testDuplicateTimestamp_UpdatesMaxPrice() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        
        assertEquals(Double.valueOf(110.00), tracker.getMaxPrice());
        
        // Update to become new max
        tracker.update(1000, 120.00);
        
        assertEquals(Double.valueOf(120.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testDuplicateTimestamp_MaxPriceChangesWhenMaxUpdated() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 105.00);
        
        assertEquals(Double.valueOf(110.00), tracker.getMaxPrice());
        
        // Update max to lower value
        tracker.update(2000, 95.00);
        
        assertEquals(Double.valueOf(105.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testDuplicateTimestamp_MultipleUpdates() {
        tracker.update(1000, 100.00);
        tracker.update(1000, 105.00);
        tracker.update(1000, 110.00);
        tracker.update(1000, 95.00);
        
        assertEquals(Double.valueOf(95.00), tracker.getPriceAt(1000));
        assertEquals(1, tracker.size());
    }
    
    // ==================== Max Price Queries ====================
    
    @Test
    public void testGetMaxPrice_SinglePrice() {
        tracker.update(1000, 100.00);
        
        assertEquals(Double.valueOf(100.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testGetMaxPrice_MultiplePrices() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 105.00);
        tracker.update(3000, 98.00);
        tracker.update(4000, 110.00);
        
        assertEquals(Double.valueOf(110.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testGetMaxPrice_AllSamePrices() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 100.00);
        tracker.update(3000, 100.00);
        
        assertEquals(Double.valueOf(100.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testGetMaxPriceDataPoint_ReturnsCorrectDataPoint() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 105.00);
        
        PriceDataPoint maxPoint = tracker.getMaxPriceDataPoint();
        
        assertNotNull(maxPoint);
        assertEquals(2000, maxPoint.getTimestamp());
        assertEquals(110.00, maxPoint.getPrice(), 0.001);
    }
    
    @Test
    public void testGetMaxPriceDataPoint_EmptyTracker() {
        assertNull(tracker.getMaxPriceDataPoint());
    }
    
    // ==================== Remove Operations ====================
    
    @Test
    public void testRemove_ExistingTimestamp() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        
        assertTrue(tracker.remove(1000));
        assertEquals(1, tracker.size());
        assertNull(tracker.getPriceAt(1000));
    }
    
    @Test
    public void testRemove_NonExistingTimestamp() {
        tracker.update(1000, 100.00);
        
        assertFalse(tracker.remove(2000));
        assertEquals(1, tracker.size());
    }
    
    @Test
    public void testRemove_UpdatesMaxPrice() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 105.00);
        
        assertEquals(Double.valueOf(110.00), tracker.getMaxPrice());
        
        tracker.remove(2000);
        
        assertEquals(Double.valueOf(105.00), tracker.getMaxPrice());
    }
    
    @Test
    public void testClear_RemovesAllData() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 105.00);
        
        tracker.clear();
        
        assertTrue(tracker.isEmpty());
        assertEquals(0, tracker.size());
        assertNull(tracker.getMaxPrice());
    }
    
    // ==================== Statistics ====================
    
    @Test
    public void testGetStatistics_EmptyTracker() {
        CommodityPriceTracker.PriceStatistics stats = tracker.getStatistics();
        
        assertEquals(0, stats.getCount());
        assertNull(stats.getMinPrice());
        assertNull(stats.getMaxPrice());
        assertNull(stats.getAvgPrice());
    }
    
    @Test
    public void testGetStatistics_WithData() {
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 90.00);
        
        CommodityPriceTracker.PriceStatistics stats = tracker.getStatistics();
        
        assertEquals(3, stats.getCount());
        assertEquals(Double.valueOf(90.00), stats.getMinPrice());
        assertEquals(Double.valueOf(110.00), stats.getMaxPrice());
        assertEquals(Double.valueOf(100.00), stats.getAvgPrice());
    }
    
    // ==================== Validation ====================
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_NegativeTimestamp() {
        tracker.update(-1, 100.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_NegativePrice() {
        tracker.update(1000, -100.00);
    }
    
    // ==================== Concurrent Operations ====================
    
    @Test
    public void testConcurrentWrites() throws InterruptedException {
        int numThreads = 10;
        int writesPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < writesPerThread; j++) {
                        long timestamp = threadId * writesPerThread + j;
                        tracker.update(timestamp, 100.0 + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(numThreads * writesPerThread, tracker.size());
        assertNotNull(tracker.getMaxPrice());
    }
    
    @Test
    public void testConcurrentReadsAndWrites() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successfulReads = new AtomicInteger(0);
        
        Random random = new Random();
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (random.nextBoolean()) {
                            tracker.update(random.nextInt(1000), 100 + random.nextDouble() * 50);
                        } else {
                            Double maxPrice = tracker.getMaxPrice();
                            if (maxPrice != null) {
                                successfulReads.incrementAndGet();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertTrue(tracker.size() > 0);
        assertTrue(successfulReads.get() > 0);
    }
    
    @Test
    public void testConcurrentUpdatesToSameTimestamp() throws InterruptedException {
        int numThreads = 10;
        int updatesPerThread = 100;
        long sharedTimestamp = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < updatesPerThread; j++) {
                        tracker.update(sharedTimestamp, 100.0 + threadId + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Should have only one entry for the shared timestamp
        assertEquals(1, tracker.size());
        assertNotNull(tracker.getPriceAt(sharedTimestamp));
    }
}

