package com.middlewarerouter;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class MiddlewareRouterTest {
    
    private MiddlewareRouter router;
    
    @Before
    public void setUp() {
        router = new MiddlewareRouter();
    }
    
    // ========== Basic Routing Tests ==========
    
    @Test
    public void testExactRouting() {
        router.register("/users", "UsersHandler");
        router.register("/products", "ProductsHandler");
        
        assertEquals("UsersHandler", router.route("/users"));
        assertEquals("ProductsHandler", router.route("/products"));
        assertNull(router.route("/notfound"));
    }
    
    @Test
    public void testRootPath() {
        router.register("/", "HomeHandler");
        
        assertEquals("HomeHandler", router.route("/"));
        assertNull(router.route("/users"));
    }
    
    @Test
    public void testNullPath() {
        router.register("/users", "UsersHandler");
        
        assertNull(router.route(null));
    }
    
    @Test
    public void testEmptyRouter() {
        assertNull(router.route("/users"));
        assertEquals(0, router.getRouteCount());
    }
    
    // ========== Wildcard Tests (Scale Up 1) ==========
    
    @Test
    public void testWildcardMatching() {
        router.register("/api/*", "ApiHandler");
        
        assertEquals("ApiHandler", router.route("/api/users"));
        assertEquals("ApiHandler", router.route("/api/v1/users"));
        assertEquals("ApiHandler", router.route("/api/anything"));
        assertNull(router.route("/users"));
    }
    
    @Test
    public void testMultipleWildcards() {
        router.register("/api/*", "ApiHandler");
        router.register("/static/*", "StaticHandler");
        
        assertEquals("ApiHandler", router.route("/api/users"));
        assertEquals("StaticHandler", router.route("/static/css/style.css"));
    }
    
    @Test
    public void testWildcardVsExact() {
        router.register("/api/*", "WildcardHandler");
        router.register("/api/users", "ExactHandler");
        
        // Exact should match first (higher priority)
        assertEquals("ExactHandler", router.route("/api/users"));
        assertEquals("WildcardHandler", router.route("/api/products"));
    }
    
    // ========== Path Parameter Tests (Scale Up 2) ==========
    
    @Test
    public void testSinglePathParam() {
        router.register("/users/:id", "UserHandler");
        
        RouteMatch match = router.routeWithParams("/users/123");
        assertNotNull(match);
        assertEquals("UserHandler", match.getHandler());
        assertEquals("123", match.getPathParam("id"));
    }
    
    @Test
    public void testMultiplePathParams() {
        router.register("/users/:userId/posts/:postId", "PostHandler");
        
        RouteMatch match = router.routeWithParams("/users/alice/posts/hello");
        assertNotNull(match);
        assertEquals("PostHandler", match.getHandler());
        assertEquals("alice", match.getPathParam("userId"));
        assertEquals("hello", match.getPathParam("postId"));
    }
    
    @Test
    public void testPathParamWithSpecialChars() {
        router.register("/products/:id", "ProductHandler");
        
        RouteMatch match = router.routeWithParams("/products/laptop-pro-2024");
        assertNotNull(match);
        assertEquals("laptop-pro-2024", match.getPathParam("id"));
    }
    
    @Test
    public void testPathParamNoMatch() {
        router.register("/users/:id", "UserHandler");
        
        assertNull(router.routeWithParams("/users"));
        assertNull(router.routeWithParams("/users/123/extra"));
    }
    
    @Test
    public void testRouteWithoutParams() {
        router.register("/users", "UsersHandler");
        
        RouteMatch match = router.routeWithParams("/users");
        assertNotNull(match);
        assertEquals("UsersHandler", match.getHandler());
        assertTrue(match.getPathParams().isEmpty());
    }
    
    // ========== Priority Ordering Tests ==========
    
    @Test
    public void testPriorityOrdering() {
        router.register("/api/*", "WildcardHandler");
        router.register("/api/users", "ExactHandler");
        router.register("/api/:version/users", "ParamHandler");
        
        // Most specific should match first
        assertEquals("ExactHandler", router.route("/api/users"));
        assertEquals("ParamHandler", router.route("/api/v1/users"));
        assertEquals("WildcardHandler", router.route("/api/anything"));
    }
    
    @Test
    public void testExplicitPriority() {
        router.register("/users", "LowPriority", 100);
        router.register("/users", "HighPriority", 10);
        
        // Higher priority (lower number) should match first
        assertEquals("HighPriority", router.route("/users"));
    }
    
    @Test
    public void testAutoSortDisabled() {
        router.setAutoSort(false);
        router.register("/api/*", "WildcardHandler");
        router.register("/api/users", "ExactHandler");
        
        // Without auto-sort, first registered wins
        assertEquals("WildcardHandler", router.route("/api/users"));
    }
    
    // ========== Route Management Tests ==========
    
    @Test
    public void testRemoveRoute() {
        router.register("/users", "UsersHandler");
        router.register("/products", "ProductsHandler");
        
        assertEquals(2, router.getRouteCount());
        
        int removed = router.removeRoute("/users");
        assertEquals(1, removed);
        assertEquals(1, router.getRouteCount());
        
        assertNull(router.route("/users"));
        assertEquals("ProductsHandler", router.route("/products"));
    }
    
    @Test
    public void testRemoveNonExistentRoute() {
        router.register("/users", "UsersHandler");
        
        int removed = router.removeRoute("/notfound");
        assertEquals(0, removed);
        assertEquals(1, router.getRouteCount());
    }
    
    @Test
    public void testClear() {
        router.register("/users", "UsersHandler");
        router.register("/products", "ProductsHandler");
        
        router.clear();
        
        assertEquals(0, router.getRouteCount());
        assertNull(router.route("/users"));
    }
    
    @Test
    public void testGetRoutes() {
        router.register("/users", "UsersHandler");
        router.register("/products", "ProductsHandler");
        
        List<Route> routes = router.getRoutes();
        assertEquals(2, routes.size());
        
        // Modifying returned list shouldn't affect router
        routes.clear();
        assertEquals(2, router.getRouteCount());
    }
    
    // ========== Statistics Tests ==========
    
    @Test
    public void testStatistics() {
        router.register("/users", "ExactHandler");
        router.register("/api/*", "WildcardHandler");
        router.register("/users/:id", "ParamHandler");
        
        String stats = router.getStatistics();
        assertTrue(stats.contains("Total routes: 3"));
        assertTrue(stats.contains("Exact: 1"));
        assertTrue(stats.contains("Wildcard: 1"));
        assertTrue(stats.contains("Path params: 1"));
    }
    
    // ========== Complex Scenarios ==========
    
    @Test
    public void testComplexRouting() {
        router.register("/", "HomeHandler");
        router.register("/api/v1/users", "V1UsersHandler");
        router.register("/api/:version/users/:id", "VersionedUserHandler");
        router.register("/api/*", "ApiCatchAll");
        
        assertEquals("HomeHandler", router.route("/"));
        assertEquals("V1UsersHandler", router.route("/api/v1/users"));
        
        RouteMatch match = router.routeWithParams("/api/v2/users/123");
        assertEquals("VersionedUserHandler", match.getHandler());
        assertEquals("v2", match.getPathParam("version"));
        assertEquals("123", match.getPathParam("id"));
        
        assertEquals("ApiCatchAll", router.route("/api/anything/else"));
    }
    
    @Test
    public void testNestedPaths() {
        router.register("/a/b/c", "Handler1");
        router.register("/a/b/:id", "Handler2");
        router.register("/a/:x/c", "Handler3");
        
        assertEquals("Handler1", router.route("/a/b/c"));
        
        RouteMatch match2 = router.routeWithParams("/a/b/123");
        assertEquals("Handler2", match2.getHandler());
        assertEquals("123", match2.getPathParam("id"));
        
        RouteMatch match3 = router.routeWithParams("/a/xyz/c");
        assertEquals("Handler3", match3.getHandler());
        assertEquals("xyz", match3.getPathParam("x"));
    }
    
    // ========== Concurrent Operations ==========
    
    @Test
    public void testConcurrentRegistration() throws InterruptedException {
        int numThreads = 10;
        int routesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < routesPerThread; j++) {
                        router.register("/thread" + threadId + "/route" + j, "Handler" + threadId + "_" + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(numThreads * routesPerThread, router.getRouteCount());
    }
    
    @Test
    public void testConcurrentRouting() throws InterruptedException {
        router.register("/users/:id", "UserHandler");
        router.register("/products/:id", "ProductHandler");
        
        int numThreads = 10;
        int requestsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        String path = (j % 2 == 0) ? "/users/123" : "/products/456";
                        RouteMatch match = router.routeWithParams(path);
                        if (match != null) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(numThreads * requestsPerThread, successCount.get());
    }
    
    // ========== Edge Cases ==========
    
    @Test
    public void testTrailingSlash() {
        router.register("/users", "UsersHandler");
        
        assertEquals("UsersHandler", router.route("/users"));
        // Trailing slash is different path
        assertNull(router.route("/users/"));
    }
    
    @Test
    public void testCaseSensitive() {
        router.register("/Users", "Handler1");
        router.register("/users", "Handler2");
        
        assertEquals("Handler1", router.route("/Users"));
        assertEquals("Handler2", router.route("/users"));
    }
    
    @Test
    public void testDuplicateRegistration() {
        router.register("/users", "Handler1");
        router.register("/users", "Handler2");
        
        // Both routes exist, first one (higher priority) matches
        assertEquals("Handler1", router.route("/users"));
        assertEquals(2, router.getRouteCount());
    }
}

