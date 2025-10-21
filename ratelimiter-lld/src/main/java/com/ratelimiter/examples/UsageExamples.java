package com.ratelimiter.examples;

import com.ratelimiter.*;

/**
 * Comprehensive examples demonstrating rate limiter usage.
 */
public class UsageExamples {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Rate Limiter Usage Examples ===\n");
        
        example1_BasicTokenBucket();
        example2_PerUserRateLimiting();
        example3_CompareAlgorithms();
        example4_BurstTraffic();
        example5_ConcurrentRequests();
        
        System.out.println("\n=== All Examples Complete ===");
    }
    
    /**
     * Example 1: Basic Token Bucket usage
     */
    private static void example1_BasicTokenBucket() throws InterruptedException {
        System.out.println("--- Example 1: Basic Token Bucket ---");
        
        // Create a rate limiter: 5 requests per second
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(5)
            .setTimeWindowSeconds(1)
            .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
        
        RateLimiter limiter = RateLimiterFactory.create(config);
        
        // Try 10 requests immediately
        System.out.println("Sending 10 rapid requests:");
        int allowed = 0, denied = 0;
        for (int i = 1; i <= 10; i++) {
            if (limiter.allowRequest()) {
                System.out.println("  Request " + i + ": ✅ ALLOWED");
                allowed++;
            } else {
                System.out.println("  Request " + i + ": ❌ DENIED");
                denied++;
            }
        }
        System.out.println("Result: " + allowed + " allowed, " + denied + " denied\n");
        
        // Wait 1 second and try again
        System.out.println("Waiting 1 second for token refill...");
        Thread.sleep(1000);
        
        System.out.println("Sending 5 more requests:");
        for (int i = 11; i <= 15; i++) {
            if (limiter.allowRequest()) {
                System.out.println("  Request " + i + ": ✅ ALLOWED");
            } else {
                System.out.println("  Request " + i + ": ❌ DENIED");
            }
        }
        System.out.println();
    }
    
    /**
     * Example 2: Per-user rate limiting
     */
    private static void example2_PerUserRateLimiting() {
        System.out.println("--- Example 2: Per-User Rate Limiting ---");
        
        // Create manager with 3 requests per second per user
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(3)
            .setTimeWindowSeconds(1);
        
        UserRateLimiterManager manager = new UserRateLimiterManager(config);
        
        // Test multiple users
        String[] users = {"alice", "bob", "charlie"};
        
        for (String user : users) {
            System.out.println("User: " + user);
            int allowed = 0;
            for (int i = 0; i < 5; i++) {
                if (manager.allowRequest(user)) {
                    allowed++;
                }
            }
            System.out.println("  " + allowed + "/5 requests allowed");
        }
        
        System.out.println("Total users tracked: " + manager.getUserCount());
        System.out.println();
    }
    
    /**
     * Example 3: Compare different algorithms
     */
    private static void example3_CompareAlgorithms() throws InterruptedException {
        System.out.println("--- Example 3: Algorithm Comparison ---");
        
        RateLimiterAlgorithm[] algorithms = {
            RateLimiterAlgorithm.TOKEN_BUCKET,
            RateLimiterAlgorithm.SLIDING_WINDOW_LOG,
            RateLimiterAlgorithm.FIXED_WINDOW,
            RateLimiterAlgorithm.SLIDING_WINDOW_COUNTER
        };
        
        for (RateLimiterAlgorithm algo : algorithms) {
            System.out.println("Algorithm: " + algo);
            
            RateLimiterConfig config = new RateLimiterConfig()
                .setMaxRequests(5)
                .setTimeWindowSeconds(1)
                .setAlgorithm(algo);
            
            RateLimiter limiter = RateLimiterFactory.create(config);
            
            // Test with 10 requests
            int allowed = 0;
            for (int i = 0; i < 10; i++) {
                if (limiter.allowRequest()) {
                    allowed++;
                }
            }
            System.out.println("  Rapid 10 requests: " + allowed + " allowed");
            
            // Wait and test again
            Thread.sleep(1000);
            allowed = 0;
            for (int i = 0; i < 10; i++) {
                if (limiter.allowRequest()) {
                    allowed++;
                }
            }
            System.out.println("  After 1s: " + allowed + " allowed\n");
        }
    }
    
    /**
     * Example 4: Handling burst traffic
     */
    private static void example4_BurstTraffic() throws InterruptedException {
        System.out.println("--- Example 4: Burst Traffic Handling ---");
        
        // Token bucket with higher capacity allows bursts
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(10)
            .setTimeWindowSeconds(1)
            .setBucketCapacity(20)  // Allow bursts up to 20
            .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
        
        RateLimiter limiter = RateLimiterFactory.create(config);
        
        System.out.println("Config: 10 req/sec, burst capacity: 20");
        
        // Simulate burst
        System.out.println("Burst of 25 requests:");
        int allowed = 0;
        for (int i = 0; i < 25; i++) {
            if (limiter.allowRequest()) {
                allowed++;
            }
        }
        System.out.println("  " + allowed + "/25 allowed (burst capacity: 20)");
        
        // Wait for refill
        Thread.sleep(2000);
        System.out.println("After 2 seconds (20 tokens refilled):");
        allowed = 0;
        for (int i = 0; i < 25; i++) {
            if (limiter.allowRequest()) {
                allowed++;
            }
        }
        System.out.println("  " + allowed + "/25 allowed\n");
    }
    
    /**
     * Example 5: Concurrent requests from multiple threads
     */
    private static void example5_ConcurrentRequests() throws InterruptedException {
        System.out.println("--- Example 5: Concurrent Requests ---");
        
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(10)
            .setTimeWindowSeconds(1);
        
        RateLimiter limiter = RateLimiterFactory.create(config);
        
        // Create 5 threads, each making 5 requests
        Thread[] threads = new Thread[5];
        int[] results = new int[5];
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                int allowed = 0;
                for (int j = 0; j < 5; j++) {
                    if (limiter.allowRequest()) {
                        allowed++;
                    }
                }
                results[threadId] = allowed;
            });
        }
        
        // Start all threads
        System.out.println("Starting 5 threads, each making 5 requests (25 total):");
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Show results
        int totalAllowed = 0;
        for (int i = 0; i < 5; i++) {
            System.out.println("  Thread " + (i + 1) + ": " + results[i] + " allowed");
            totalAllowed += results[i];
        }
        System.out.println("Total: " + totalAllowed + "/25 allowed (limit: 10)");
        System.out.println("Thread-safe: " + (totalAllowed <= 10 ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
}

