package com.ratelimiter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for TokenBucketRateLimiter.
 */
public class TokenBucketRateLimiterTest {
    
    private TokenBucketRateLimiter rateLimiter;
    
    @Before
    public void setUp() {
        // 5 requests per second, capacity 5
        rateLimiter = new TokenBucketRateLimiter(5, 5.0);
    }
    
    @Test
    public void testAllowRequestWithinLimit() {
        // First 5 requests should be allowed
        for (int i = 0; i < 5; i++) {
            assertTrue("Request " + (i + 1) + " should be allowed", 
                      rateLimiter.allowRequest());
        }
    }
    
    @Test
    public void testDenyRequestExceedingLimit() {
        // Exhaust the bucket
        for (int i = 0; i < 5; i++) {
            rateLimiter.allowRequest();
        }
        
        // Next request should be denied
        assertFalse("Request exceeding limit should be denied", 
                   rateLimiter.allowRequest());
    }
    
    @Test
    public void testTokenRefill() throws InterruptedException {
        // Exhaust the bucket
        for (int i = 0; i < 5; i++) {
            rateLimiter.allowRequest();
        }
        
        // Should be denied
        assertFalse(rateLimiter.allowRequest());
        
        // Wait for 1 second (5 tokens should refill)
        Thread.sleep(1000);
        
        // Should be allowed again
        assertTrue("Request should be allowed after refill", 
                  rateLimiter.allowRequest());
    }
    
    @Test
    public void testPartialRefill() throws InterruptedException {
        // Exhaust the bucket
        for (int i = 0; i < 5; i++) {
            rateLimiter.allowRequest();
        }
        
        // Wait for 0.5 seconds (2.5 tokens should refill)
        Thread.sleep(500);
        
        // Should allow 2 requests
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertFalse("Third request should be denied", 
                   rateLimiter.allowRequest());
    }
    
    @Test
    public void testCapacityLimit() throws InterruptedException {
        // Wait for 2 seconds (would refill 10 tokens, but capacity is 5)
        Thread.sleep(2000);
        
        // Should only allow 5 requests
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allowRequest());
        }
        assertFalse(rateLimiter.allowRequest());
    }
    
    @Test
    public void testReset() {
        // Exhaust the bucket
        for (int i = 0; i < 5; i++) {
            rateLimiter.allowRequest();
        }
        assertFalse(rateLimiter.allowRequest());
        
        // Reset
        rateLimiter.reset();
        
        // Should be allowed again
        assertTrue("Request should be allowed after reset", 
                  rateLimiter.allowRequest());
    }
    
    @Test
    public void testGetAvailableCapacity() {
        assertEquals(5, rateLimiter.getAvailableCapacity());
        
        rateLimiter.allowRequest();
        assertEquals(4, rateLimiter.getAvailableCapacity());
        
        rateLimiter.allowRequest();
        rateLimiter.allowRequest();
        assertEquals(2, rateLimiter.getAvailableCapacity());
    }
    
    @Test
    public void testConcurrentRequests() throws InterruptedException {
        final int threadCount = 10;
        final int requestsPerThread = 5;
        final int[] allowedCount = {0};
        
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    if (rateLimiter.allowRequest()) {
                        synchronized (allowedCount) {
                            allowedCount[0]++;
                        }
                    }
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should only allow 5 requests total (capacity)
        assertEquals("Should only allow capacity number of requests", 
                    5, allowedCount[0]);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity() {
        new TokenBucketRateLimiter(0, 5.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRefillRate() {
        new TokenBucketRateLimiter(5, 0);
    }
}

