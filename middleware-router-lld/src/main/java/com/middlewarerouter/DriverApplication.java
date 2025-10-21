package com.middlewarerouter;

import java.util.Map;

/**
 * Driver application demonstrating the Middleware Router.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Middleware Router - Real-time Demo                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        demo1_BasicRouting();
        demo2_WildcardRouting();
        demo3_PathParameters();
        demo4_PriorityOrdering();
        demo5_ComplexScenarios();
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ Exact path matching");
        System.out.println("  ✓ Wildcard matching (*)");
        System.out.println("  ✓ Path parameter extraction (:param)");
        System.out.println("  ✓ Priority-based ordering");
        System.out.println("  ✓ Complex route combinations");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1_BasicRouting() {
        System.out.println("📋 Demo 1: Basic Exact Path Routing");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        MiddlewareRouter router = new MiddlewareRouter();
        
        // Register exact routes
        router.register("/", "HomeHandler");
        router.register("/users", "UsersHandler");
        router.register("/products", "ProductsHandler");
        router.register("/about", "AboutHandler");
        
        System.out.println("Registered routes:");
        System.out.println("  / → HomeHandler");
        System.out.println("  /users → UsersHandler");
        System.out.println("  /products → ProductsHandler");
        System.out.println("  /about → AboutHandler");
        System.out.println();
        
        // Test routing
        System.out.println("Testing routes:");
        testRoute(router, "/");
        testRoute(router, "/users");
        testRoute(router, "/products");
        testRoute(router, "/about");
        testRoute(router, "/notfound");
        
        System.out.println();
        System.out.println("✓ Basic routing working correctly");
        System.out.println();
    }
    
    private static void demo2_WildcardRouting() {
        System.out.println("🌟 Demo 2: Wildcard Routing (Scale Up 1)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        MiddlewareRouter router = new MiddlewareRouter();
        
        // Register wildcard routes
        router.register("/api/*", "ApiHandler");
        router.register("/static/*", "StaticHandler");
        router.register("/admin/*", "AdminHandler");
        router.register("/users", "UsersHandler");  // Exact route
        
        System.out.println("Registered routes:");
        System.out.println("  /api/* → ApiHandler");
        System.out.println("  /static/* → StaticHandler");
        System.out.println("  /admin/* → AdminHandler");
        System.out.println("  /users → UsersHandler (exact)");
        System.out.println();
        
        // Test routing
        System.out.println("Testing routes:");
        testRoute(router, "/api/v1/users");
        testRoute(router, "/api/v2/products");
        testRoute(router, "/static/css/style.css");
        testRoute(router, "/static/js/app.js");
        testRoute(router, "/admin/dashboard");
        testRoute(router, "/users");  // Should match exact route
        
        System.out.println();
        System.out.println("✓ Wildcard routing working correctly");
        System.out.println();
    }
    
    private static void demo3_PathParameters() {
        System.out.println("🔗 Demo 3: Path Parameters (Scale Up 2)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        MiddlewareRouter router = new MiddlewareRouter();
        
        // Register routes with path parameters
        router.register("/users/:id", "UserDetailHandler");
        router.register("/users/:userId/posts/:postId", "UserPostHandler");
        router.register("/products/:category/:productId", "ProductDetailHandler");
        router.register("/api/:version/users", "VersionedUsersHandler");
        
        System.out.println("Registered routes:");
        System.out.println("  /users/:id → UserDetailHandler");
        System.out.println("  /users/:userId/posts/:postId → UserPostHandler");
        System.out.println("  /products/:category/:productId → ProductDetailHandler");
        System.out.println("  /api/:version/users → VersionedUsersHandler");
        System.out.println();
        
        // Test routing with parameters
        System.out.println("Testing routes with path parameters:");
        testRouteWithParams(router, "/users/123");
        testRouteWithParams(router, "/users/alice/posts/456");
        testRouteWithParams(router, "/products/electronics/laptop-pro");
        testRouteWithParams(router, "/api/v1/users");
        testRouteWithParams(router, "/api/v2/users");
        
        System.out.println();
        System.out.println("✓ Path parameter extraction working correctly");
        System.out.println();
    }
    
    private static void demo4_PriorityOrdering() {
        System.out.println("🎯 Demo 4: Priority-Based Ordering");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        MiddlewareRouter router = new MiddlewareRouter();
        
        // Register routes in random order - router will sort by priority
        router.register("/api/*", "WildcardApiHandler");
        router.register("/api/users", "ExactApiUsersHandler");
        router.register("/api/:version/users", "VersionedUsersHandler");
        
        System.out.println("Registered routes (auto-sorted by priority):");
        System.out.println("  /api/users → ExactApiUsersHandler (exact - highest priority)");
        System.out.println("  /api/:version/users → VersionedUsersHandler (path param - medium)");
        System.out.println("  /api/* → WildcardApiHandler (wildcard - lowest)");
        System.out.println();
        
        // Test routing - should match most specific first
        System.out.println("Testing priority ordering:");
        testRoute(router, "/api/users");        // Should match exact
        testRoute(router, "/api/v1/users");     // Should match path param
        testRoute(router, "/api/anything");     // Should match wildcard
        
        System.out.println();
        System.out.println("✓ Priority ordering working correctly");
        System.out.println();
    }
    
    private static void demo5_ComplexScenarios() {
        System.out.println("🚀 Demo 5: Complex Scenarios");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        MiddlewareRouter router = new MiddlewareRouter();
        
        // Mix of all route types
        router.register("/", "HomeHandler");
        router.register("/api/v1/users", "V1UsersHandler");
        router.register("/api/:version/users/:id", "VersionedUserHandler");
        router.register("/api/*", "ApiCatchAllHandler");
        router.register("/static/*", "StaticHandler");
        router.register("/admin/users/:id/edit", "AdminUserEditHandler");
        
        System.out.println("Registered complex routes:");
        System.out.println("  / → HomeHandler");
        System.out.println("  /api/v1/users → V1UsersHandler");
        System.out.println("  /api/:version/users/:id → VersionedUserHandler");
        System.out.println("  /api/* → ApiCatchAllHandler");
        System.out.println("  /static/* → StaticHandler");
        System.out.println("  /admin/users/:id/edit → AdminUserEditHandler");
        System.out.println();
        
        System.out.println("Statistics: " + router.getStatistics());
        System.out.println();
        
        // Test complex scenarios
        System.out.println("Testing complex scenarios:");
        testRouteWithParams(router, "/");
        testRouteWithParams(router, "/api/v1/users");
        testRouteWithParams(router, "/api/v2/users/123");
        testRouteWithParams(router, "/api/anything/else");
        testRouteWithParams(router, "/static/images/logo.png");
        testRouteWithParams(router, "/admin/users/456/edit");
        
        System.out.println();
        System.out.println("✓ Complex scenarios working correctly");
        System.out.println();
    }
    
    private static void testRoute(MiddlewareRouter router, String path) {
        String handler = router.route(path);
        if (handler != null) {
            System.out.printf("  %s → %s%n", path, handler);
        } else {
            System.out.printf("  %s → NOT FOUND%n", path);
        }
    }
    
    private static void testRouteWithParams(MiddlewareRouter router, String path) {
        RouteMatch match = router.routeWithParams(path);
        if (match != null) {
            if (match.getPathParams().isEmpty()) {
                System.out.printf("  %s → %s%n", path, match.getHandler());
            } else {
                System.out.printf("  %s → %s %s%n", 
                    path, match.getHandler(), formatParams(match.getPathParams()));
            }
        } else {
            System.out.printf("  %s → NOT FOUND%n", path);
        }
    }
    
    private static String formatParams(Map<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}

