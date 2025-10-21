package com.middlewarerouter;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class RouteTest {
    
    @Test
    public void testExactRoute() {
        Route route = new Route("/users", "UsersHandler");
        
        assertTrue(route.matches("/users"));
        assertFalse(route.matches("/users/123"));
        assertFalse(route.matches("/products"));
        assertFalse(route.hasWildcard());
        assertFalse(route.hasPathParams());
    }
    
    @Test
    public void testWildcardRoute() {
        Route route = new Route("/api/*", "ApiHandler");
        
        assertTrue(route.matches("/api/users"));
        assertTrue(route.matches("/api/v1/users"));
        assertTrue(route.matches("/api/anything"));
        assertFalse(route.matches("/users"));
        assertTrue(route.hasWildcard());
        assertFalse(route.hasPathParams());
    }
    
    @Test
    public void testPathParamRoute() {
        Route route = new Route("/users/:id", "UserHandler");
        
        assertTrue(route.matches("/users/123"));
        assertTrue(route.matches("/users/abc"));
        assertFalse(route.matches("/users"));
        assertFalse(route.matches("/users/123/posts"));
        assertFalse(route.hasWildcard());
        assertTrue(route.hasPathParams());
        assertEquals(1, route.getPathParamNames().size());
        assertEquals("id", route.getPathParamNames().get(0));
    }
    
    @Test
    public void testMultiplePathParams() {
        Route route = new Route("/users/:userId/posts/:postId", "PostHandler");
        
        assertTrue(route.matches("/users/123/posts/456"));
        assertFalse(route.matches("/users/123"));
        assertTrue(route.hasPathParams());
        assertEquals(2, route.getPathParamNames().size());
        assertTrue(route.getPathParamNames().contains("userId"));
        assertTrue(route.getPathParamNames().contains("postId"));
    }
    
    @Test
    public void testExtractPathParams() {
        Route route = new Route("/users/:id", "UserHandler");
        
        Map<String, String> params = route.extractPathParams("/users/123");
        assertEquals(1, params.size());
        assertEquals("123", params.get("id"));
    }
    
    @Test
    public void testExtractMultiplePathParams() {
        Route route = new Route("/users/:userId/posts/:postId", "PostHandler");
        
        Map<String, String> params = route.extractPathParams("/users/alice/posts/hello-world");
        assertEquals(2, params.size());
        assertEquals("alice", params.get("userId"));
        assertEquals("hello-world", params.get("postId"));
    }
    
    @Test
    public void testExtractPathParamsNoMatch() {
        Route route = new Route("/users/:id", "UserHandler");
        
        Map<String, String> params = route.extractPathParams("/products/123");
        assertTrue(params.isEmpty());
    }
    
    @Test
    public void testPriorityCalculation() {
        Route exact = new Route("/users", "Handler");
        Route pathParam = new Route("/users/:id", "Handler");
        Route wildcard = new Route("/users/*", "Handler");
        
        // Exact should have highest priority (lowest number)
        assertTrue(exact.getPriority() < pathParam.getPriority());
        assertTrue(pathParam.getPriority() < wildcard.getPriority());
    }
    
    @Test
    public void testExplicitPriority() {
        Route route = new Route("/users", "Handler", 50);
        assertEquals(50, route.getPriority());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullPattern() {
        new Route(null, "Handler");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPattern() {
        new Route("", "Handler");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullHandler() {
        new Route("/users", null);
    }
    
    @Test
    public void testToString() {
        Route route = new Route("/users/:id", "UserHandler", 10);
        String str = route.toString();
        assertTrue(str.contains("/users/:id"));
        assertTrue(str.contains("UserHandler"));
        assertTrue(str.contains("10"));
    }
}

