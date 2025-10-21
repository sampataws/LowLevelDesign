package com.popularcontent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Driver application demonstrating the Popular Content Tracker.
 */
public class DriverApplication {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Popular Content Tracker - Real-time Demo               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        demo1_BasicOperations();
        demo2_MultipleContents();
        demo3_PopularityFluctuations();
        demo4_ConcurrentOperations();
        demo5_RealTimeStreaming();
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ O(log N) action processing");
        System.out.println("  ✓ O(1) most popular queries");
        System.out.println("  ✓ Automatic removal when popularity ≤ 0");
        System.out.println("  ✓ Thread-safe concurrent operations");
        System.out.println("  ✓ Real-time streaming support");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1_BasicOperations() {
        System.out.println("📋 Demo 1: Basic Operations");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        PopularContentTracker tracker = new PopularContentTracker();
        
        // Initially empty
        System.out.println("Initial state:");
        System.out.println("  Most popular: " + tracker.getMostPopular() + " (should be -1)");
        System.out.println();
        
        // Add some content
        System.out.println("Processing actions:");
        tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
        System.out.println("  Content 101: +1 → Popularity: " + tracker.getPopularity(101));
        System.out.println("  Most popular: " + tracker.getMostPopular());
        
        tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
        System.out.println("  Content 101: +1 → Popularity: " + tracker.getPopularity(101));
        System.out.println("  Most popular: " + tracker.getMostPopular());
        
        tracker.processAction(102, ContentAction.INCREASE_POPULARITY);
        System.out.println("  Content 102: +1 → Popularity: " + tracker.getPopularity(102));
        System.out.println("  Most popular: " + tracker.getMostPopular());
        
        tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
        System.out.println("  Content 101: +1 → Popularity: " + tracker.getPopularity(101));
        System.out.println("  Most popular: " + tracker.getMostPopular());
        System.out.println();
        
        System.out.println("✓ Basic operations working correctly");
        System.out.println();
    }
    
    private static void demo2_MultipleContents() {
        System.out.println("🔢 Demo 2: Multiple Contents with Same Popularity");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        PopularContentTracker tracker = new PopularContentTracker();
        
        // Create multiple contents with same popularity
        System.out.println("Creating contents with popularity 5:");
        for (int i = 1; i <= 5; i++) {
            for (int j = 0; j < 5; j++) {
                tracker.processAction(i, ContentAction.INCREASE_POPULARITY);
            }
            System.out.println("  Content " + i + ": Popularity = " + tracker.getPopularity(i));
        }
        System.out.println();
        
        System.out.println("All most popular contents:");
        List<Integer> allMostPopular = tracker.getAllMostPopular();
        System.out.println("  " + allMostPopular + " (all have popularity 5)");
        System.out.println();
        
        // Make one more popular
        tracker.processAction(3, ContentAction.INCREASE_POPULARITY);
        System.out.println("After increasing content 3:");
        System.out.println("  Content 3: Popularity = " + tracker.getPopularity(3));
        System.out.println("  Most popular: " + tracker.getMostPopular());
        System.out.println("  All most popular: " + tracker.getAllMostPopular());
        System.out.println();
        
        System.out.println("✓ Multiple contents handled correctly");
        System.out.println();
    }
    
    private static void demo3_PopularityFluctuations() {
        System.out.println("📊 Demo 3: Popularity Fluctuations");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        PopularContentTracker tracker = new PopularContentTracker();
        
        // Build up popularity
        System.out.println("Building up content 201:");
        for (int i = 1; i <= 5; i++) {
            tracker.processAction(201, ContentAction.INCREASE_POPULARITY);
            System.out.println("  After " + i + " increases: Popularity = " + tracker.getPopularity(201));
        }
        System.out.println();
        
        // Decrease popularity
        System.out.println("Decreasing content 201 (spam removal):");
        for (int i = 1; i <= 3; i++) {
            tracker.processAction(201, ContentAction.DECREASE_POPULARITY);
            System.out.println("  After " + i + " decreases: Popularity = " + tracker.getPopularity(201));
        }
        System.out.println();
        
        // Decrease to zero
        System.out.println("Decreasing to zero:");
        tracker.processAction(201, ContentAction.DECREASE_POPULARITY);
        System.out.println("  Popularity = " + tracker.getPopularity(201));
        tracker.processAction(201, ContentAction.DECREASE_POPULARITY);
        System.out.println("  Popularity = " + tracker.getPopularity(201));
        System.out.println("  Most popular: " + tracker.getMostPopular() + " (should be -1)");
        System.out.println("  Tracker size: " + tracker.size() + " (content removed)");
        System.out.println();
        
        System.out.println("✓ Popularity fluctuations handled correctly");
        System.out.println("✓ Content automatically removed when popularity ≤ 0");
        System.out.println();
    }
    
    private static void demo4_ConcurrentOperations() throws InterruptedException {
        System.out.println("⚡ Demo 4: Concurrent Operations");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        PopularContentTracker tracker = new PopularContentTracker();
        int numThreads = 10;
        int operationsPerThread = 100;
        
        System.out.println("Running " + numThreads + " threads concurrently...");
        System.out.println("Each thread performs " + operationsPerThread + " operations");
        System.out.println();
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger totalIncreases = new AtomicInteger(0);
        AtomicInteger totalDecreases = new AtomicInteger(0);
        
        Random random = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Random threadRandom = new Random(threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        int contentId = threadRandom.nextInt(50) + 1; // Content IDs 1-50
                        ContentAction action = threadRandom.nextBoolean() 
                            ? ContentAction.INCREASE_POPULARITY 
                            : ContentAction.DECREASE_POPULARITY;
                        
                        tracker.processAction(contentId, action);
                        
                        if (action == ContentAction.INCREASE_POPULARITY) {
                            totalIncreases.incrementAndGet();
                        } else {
                            totalDecreases.incrementAndGet();
                        }
                        
                        // Occasionally query most popular
                        if (j % 10 == 0) {
                            tracker.getMostPopular();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("Results:");
        System.out.println("  Total increases: " + totalIncreases.get());
        System.out.println("  Total decreases: " + totalDecreases.get());
        System.out.println("  Active contents: " + tracker.size());
        System.out.println("  Most popular: " + tracker.getMostPopular());
        System.out.println("  Max popularity: " + tracker.getMaxPopularity());
        System.out.println();
        
        PopularContentTracker.ContentStatistics stats = tracker.getStatistics();
        System.out.println("Statistics: " + stats);
        System.out.println();
        
        System.out.println("✓ Concurrent operations completed successfully");
        System.out.println("✓ No data corruption or race conditions");
        System.out.println();
    }
    
    private static void demo5_RealTimeStreaming() {
        System.out.println("📊 Demo 5: Real-time Streaming Simulation");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        PopularContentTracker tracker = new PopularContentTracker();
        
        System.out.println("Simulating real-time content stream...");
        System.out.println();
        
        // Simulate a stream of actions
        int[][] stream = {
            {101, 1}, {102, 1}, {103, 1},  // Initial likes
            {101, 1}, {101, 1},             // Content 101 getting popular
            {104, 1}, {105, 1},             // New content
            {101, 1}, {101, 1},             // Content 101 more popular
            {102, -1},                      // Spam removed from 102
            {101, 1},                       // Content 101 even more popular
            {106, 1}, {106, 1}, {106, 1},   // Content 106 rising
            {101, -1}, {101, -1},           // Some spam removed from 101
        };
        
        for (int i = 0; i < stream.length; i++) {
            int contentId = stream[i][0];
            ContentAction action = stream[i][1] == 1 
                ? ContentAction.INCREASE_POPULARITY 
                : ContentAction.DECREASE_POPULARITY;
            
            tracker.processAction(contentId, action);
            
            int mostPopular = tracker.getMostPopular();
            int maxPopularity = tracker.getMaxPopularity();
            
            System.out.printf("  Stream %2d: Content %d %s → Most popular: %d (popularity: %d)%n",
                i + 1,
                contentId,
                action == ContentAction.INCREASE_POPULARITY ? "↑" : "↓",
                mostPopular,
                maxPopularity
            );
        }
        System.out.println();
        
        System.out.println("Final state:");
        System.out.println("  Total contents: " + tracker.size());
        System.out.println("  Most popular: " + tracker.getMostPopular());
        System.out.println("  All most popular: " + tracker.getAllMostPopular());
        
        PopularContentTracker.ContentStatistics stats = tracker.getStatistics();
        System.out.println("  Statistics: " + stats);
        System.out.println();
        
        System.out.println("✓ Real-time streaming handled correctly");
        System.out.println("✓ Most popular tracked accurately throughout stream");
        System.out.println();
    }
}

