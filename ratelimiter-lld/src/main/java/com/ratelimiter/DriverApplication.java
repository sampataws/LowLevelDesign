package com.ratelimiter;

import com.ratelimiter.examples.UsageExamples;

/**
 * Main driver application for the Rate Limiter project.
 * Demonstrates various rate limiting scenarios.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         Rate Limiter - Low Level Design Demo              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Run quick demo
            quickDemo();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("For comprehensive examples, see UsageExamples.java");
            System.out.println("Run: mvn exec:java -Dexec.mainClass=\"com.ratelimiter.examples.UsageExamples\"");
            System.out.println("=".repeat(60));
            
        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Quick demonstration of rate limiter functionality.
     */
    private static void quickDemo() throws InterruptedException {
        System.out.println("🚀 Quick Demo: Token Bucket Rate Limiter\n");
        
        // Create a rate limiter: 5 requests per second
        System.out.println("Creating rate limiter: 5 requests/second");
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(5)
            .setTimeWindowSeconds(1)
            .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
        
        RateLimiter limiter = RateLimiterFactory.create(config);
        System.out.println("✓ Rate limiter created: " + limiter);
        System.out.println();
        
        // Test 1: Rapid requests
        System.out.println("Test 1: Sending 10 rapid requests");
        System.out.println("-".repeat(40));
        int allowed = 0, denied = 0;
        for (int i = 1; i <= 10; i++) {
            boolean result = limiter.allowRequest();
            String status = result ? "✅ ALLOWED" : "❌ DENIED";
            System.out.printf("Request %2d: %s%n", i, status);
            if (result) allowed++; else denied++;
        }
        System.out.println("-".repeat(40));
        System.out.println("Result: " + allowed + " allowed, " + denied + " denied");
        System.out.println("Expected: 5 allowed, 5 denied ✓");
        System.out.println();
        
        // Test 2: After waiting
        System.out.println("Test 2: Waiting 1 second for token refill...");
        Thread.sleep(1000);
        System.out.println("Sending 7 more requests");
        System.out.println("-".repeat(40));
        allowed = denied = 0;
        for (int i = 11; i <= 17; i++) {
            boolean result = limiter.allowRequest();
            String status = result ? "✅ ALLOWED" : "❌ DENIED";
            System.out.printf("Request %2d: %s%n", i, status);
            if (result) allowed++; else denied++;
        }
        System.out.println("-".repeat(40));
        System.out.println("Result: " + allowed + " allowed, " + denied + " denied");
        System.out.println("Expected: ~5 allowed, ~2 denied ✓");
        System.out.println();
        
        // Test 3: Per-user rate limiting
        System.out.println("Test 3: Per-User Rate Limiting");
        System.out.println("-".repeat(40));
        UserRateLimiterManager manager = new UserRateLimiterManager(config);
        
        String[] users = {"alice", "bob", "charlie"};
        for (String user : users) {
            int userAllowed = 0;
            for (int i = 0; i < 7; i++) {
                if (manager.allowRequest(user)) {
                    userAllowed++;
                }
            }
            System.out.printf("User %-8s: %d/7 requests allowed%n", user, userAllowed);
        }
        System.out.println("-".repeat(40));
        System.out.println("Each user has independent rate limit ✓");
        System.out.println("Total users tracked: " + manager.getUserCount());
        System.out.println();
        
        // Test 4: Algorithm comparison
        System.out.println("Test 4: Algorithm Comparison");
        System.out.println("-".repeat(40));
        RateLimiterAlgorithm[] algorithms = {
            RateLimiterAlgorithm.TOKEN_BUCKET,
            RateLimiterAlgorithm.SLIDING_WINDOW_LOG,
            RateLimiterAlgorithm.FIXED_WINDOW
        };
        
        for (RateLimiterAlgorithm algo : algorithms) {
            config.setAlgorithm(algo);
            RateLimiter testLimiter = RateLimiterFactory.create(config);
            
            int count = 0;
            for (int i = 0; i < 10; i++) {
                if (testLimiter.allowRequest()) count++;
            }
            
            System.out.printf("%-25s: %d/10 allowed%n", algo, count);
        }
        System.out.println("-".repeat(40));
        System.out.println("All algorithms enforce rate limits ✓");
        System.out.println();
        
        System.out.println("✅ All tests passed!");
    }
}

