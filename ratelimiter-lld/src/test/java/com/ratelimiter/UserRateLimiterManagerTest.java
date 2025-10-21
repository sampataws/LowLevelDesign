package com.ratelimiter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for UserRateLimiterManager.
 */
public class UserRateLimiterManagerTest {
    
    private UserRateLimiterManager manager;
    private RateLimiterConfig config;
    
    @Before
    public void setUp() {
        config = new RateLimiterConfig()
            .setMaxRequests(3)
            .setTimeWindowSeconds(1);
        manager = new UserRateLimiterManager(config);
    }
    
    @Test
    public void testPerUserIsolation() {
        // User1 makes 3 requests (all allowed)
        assertTrue(manager.allowRequest("user1"));
        assertTrue(manager.allowRequest("user1"));
        assertTrue(manager.allowRequest("user1"));
        assertFalse(manager.allowRequest("user1"));  // 4th denied
        
        // User2 should have own limit
        assertTrue(manager.allowRequest("user2"));
        assertTrue(manager.allowRequest("user2"));
        assertTrue(manager.allowRequest("user2"));
        assertFalse(manager.allowRequest("user2"));  // 4th denied
    }
    
    @Test
    public void testUserCreation() {
        assertFalse(manager.hasUser("newUser"));
        
        manager.allowRequest("newUser");
        
        assertTrue(manager.hasUser("newUser"));
        assertEquals(1, manager.getUserCount());
    }
    
    @Test
    public void testRemoveUser() {
        manager.allowRequest("user1");
        assertTrue(manager.hasUser("user1"));
        
        boolean removed = manager.removeUser("user1");
        
        assertTrue(removed);
        assertFalse(manager.hasUser("user1"));
    }
    
    @Test
    public void testResetUser() {
        // Exhaust limit
        manager.allowRequest("user1");
        manager.allowRequest("user1");
        manager.allowRequest("user1");
        assertFalse(manager.allowRequest("user1"));
        
        // Reset
        manager.resetUser("user1");
        
        // Should be allowed again
        assertTrue(manager.allowRequest("user1"));
    }
    
    @Test
    public void testClear() {
        manager.allowRequest("user1");
        manager.allowRequest("user2");
        manager.allowRequest("user3");
        
        assertEquals(3, manager.getUserCount());
        
        manager.clear();
        
        assertEquals(0, manager.getUserCount());
    }
    
    @Test
    public void testGetRateLimiter() {
        manager.allowRequest("user1");
        
        RateLimiter limiter = manager.getRateLimiter("user1");
        
        assertNotNull(limiter);
    }
    
    @Test
    public void testGetAvailableCapacity() {
        manager.allowRequest("user1");
        
        long capacity = manager.getAvailableCapacity("user1");
        
        assertTrue(capacity >= 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullUserId() {
        manager.allowRequest(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyUserId() {
        manager.allowRequest("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullConfig() {
        new UserRateLimiterManager(null);
    }
}

