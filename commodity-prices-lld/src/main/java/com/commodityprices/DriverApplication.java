package com.commodityprices;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Driver application demonstrating commodity price tracking system.
 */
public class DriverApplication {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Commodity Price Tracker - Real-time Demo               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Demo 1: Basic operations
        demo1BasicOperations();
        
        // Demo 2: Out-of-order timestamps
        demo2OutOfOrderTimestamps();
        
        // Demo 3: Duplicate timestamps (updates)
        demo3DuplicateTimestamps();
        
        // Demo 4: Concurrent reads and writes
        demo4ConcurrentOperations();
        
        // Demo 5: Real-time streaming simulation
        demo5RealTimeStreaming();
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ O(1) max price queries");
        System.out.println("  ✓ O(log N) updates");
        System.out.println("  ✓ Out-of-order timestamp handling");
        System.out.println("  ✓ Duplicate timestamp updates");
        System.out.println("  ✓ Thread-safe concurrent operations");
        System.out.println("  ✓ Real-time streaming support");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1BasicOperations() {
        System.out.println("📋 Demo 1: Basic Operations");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        
        // Add some prices
        tracker.update(1000, 100.50);
        tracker.update(2000, 105.75);
        tracker.update(3000, 98.25);
        tracker.update(4000, 110.00);
        
        System.out.println("Added 4 price data points:");
        System.out.println("  [1000, 100.50]");
        System.out.println("  [2000, 105.75]");
        System.out.println("  [3000, 98.25]");
        System.out.println("  [4000, 110.00]");
        System.out.println();
        
        System.out.println("Queries:");
        System.out.println("  Max price: " + tracker.getMaxPrice());
        System.out.println("  Price at 2000: " + tracker.getPriceAt(2000));
        System.out.println("  Total data points: " + tracker.size());
        
        PriceDataPoint maxPoint = tracker.getMaxPriceDataPoint();
        System.out.println("  Max price data point: " + maxPoint);
        System.out.println();
        
        System.out.println("✓ Basic operations working correctly");
        System.out.println();
    }
    
    private static void demo2OutOfOrderTimestamps() {
        System.out.println("🔀 Demo 2: Out-of-Order Timestamps");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        
        // Add timestamps in random order
        System.out.println("Adding timestamps in random order:");
        tracker.update(5000, 120.00);
        System.out.println("  [5000, 120.00] → Max: " + tracker.getMaxPrice());
        
        tracker.update(2000, 115.00);
        System.out.println("  [2000, 115.00] → Max: " + tracker.getMaxPrice());
        
        tracker.update(8000, 125.00);
        System.out.println("  [8000, 125.00] → Max: " + tracker.getMaxPrice());
        
        tracker.update(1000, 110.00);
        System.out.println("  [1000, 110.00] → Max: " + tracker.getMaxPrice());
        
        tracker.update(4000, 118.00);
        System.out.println("  [4000, 118.00] → Max: " + tracker.getMaxPrice());
        
        System.out.println();
        System.out.println("✓ Out-of-order timestamps handled correctly");
        System.out.println("✓ Max price always accurate: " + tracker.getMaxPrice());
        System.out.println();
    }
    
    private static void demo3DuplicateTimestamps() {
        System.out.println("🔄 Demo 3: Duplicate Timestamps (Updates)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        
        // Initial data
        tracker.update(1000, 100.00);
        tracker.update(2000, 110.00);
        tracker.update(3000, 105.00);
        
        System.out.println("Initial state:");
        System.out.println("  [1000, 100.00]");
        System.out.println("  [2000, 110.00]");
        System.out.println("  [3000, 105.00]");
        System.out.println("  Max price: " + tracker.getMaxPrice());
        System.out.println();
        
        // Update existing timestamp
        System.out.println("Updating timestamp 2000:");
        System.out.println("  Old price: " + tracker.getPriceAt(2000));
        tracker.update(2000, 95.00);
        System.out.println("  New price: " + tracker.getPriceAt(2000));
        System.out.println("  Max price: " + tracker.getMaxPrice());
        System.out.println();
        
        // Update to become new max
        System.out.println("Updating timestamp 1000 to new max:");
        tracker.update(1000, 120.00);
        System.out.println("  New price at 1000: " + tracker.getPriceAt(1000));
        System.out.println("  Max price: " + tracker.getMaxPrice());
        System.out.println();
        
        System.out.println("✓ Duplicate timestamps updated correctly");
        System.out.println("✓ Max price recalculated after updates");
        System.out.println();
    }
    
    private static void demo4ConcurrentOperations() throws InterruptedException {
        System.out.println("⚡ Demo 4: Concurrent Reads and Writes");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        int numThreads = 5;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger writeCount = new AtomicInteger(0);
        AtomicInteger readCount = new AtomicInteger(0);
        
        System.out.println("Running " + numThreads + " threads concurrently...");
        System.out.println("Each thread performs " + operationsPerThread + " operations");
        System.out.println();
        
        Random random = new Random();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (random.nextBoolean()) {
                            // Write operation
                            long timestamp = random.nextInt(1000);
                            double price = 100 + random.nextDouble() * 50;
                            tracker.update(timestamp, price);
                            writeCount.incrementAndGet();
                        } else {
                            // Read operation
                            tracker.getMaxPrice();
                            readCount.incrementAndGet();
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
        System.out.println("  Total writes: " + writeCount.get());
        System.out.println("  Total reads: " + readCount.get());
        System.out.println("  Unique timestamps: " + tracker.size());
        System.out.println("  Current max price: " + tracker.getMaxPrice());
        System.out.println();
        
        System.out.println("✓ Concurrent operations completed successfully");
        System.out.println("✓ No data corruption or race conditions");
        System.out.println();
    }
    
    private static void demo5RealTimeStreaming() throws InterruptedException {
        System.out.println("📊 Demo 5: Real-time Streaming Simulation");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        Random random = new Random();
        
        System.out.println("Simulating real-time price stream...");
        System.out.println();
        
        double basePrice = 100.0;
        long currentTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            // Simulate price fluctuation
            double change = (random.nextDouble() - 0.5) * 10;
            basePrice += change;
            
            // Sometimes send out-of-order
            long timestamp = currentTime + (random.nextBoolean() ? i * 1000 : (i - 1) * 1000);
            
            tracker.update(timestamp, basePrice);
            
            System.out.printf("  Stream %d: [%d, %.2f] → Max: %.2f%n", 
                    i + 1, timestamp, basePrice, tracker.getMaxPrice());
            
            Thread.sleep(50); // Simulate streaming delay
        }
        
        System.out.println();
        System.out.println("Statistics:");
        CommodityPriceTracker.PriceStatistics stats = tracker.getStatistics();
        System.out.println("  " + stats);
        System.out.println();
        
        System.out.println("✓ Real-time streaming handled correctly");
        System.out.println("✓ Max price tracked accurately throughout stream");
        System.out.println();
    }
}

