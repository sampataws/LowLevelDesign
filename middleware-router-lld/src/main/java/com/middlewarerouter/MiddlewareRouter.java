package com.middlewarerouter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe middleware router for web services.
 * 
 * Features:
 * - Exact path matching
 * - Wildcard matching (*)
 * - Path parameter extraction (:param)
 * - Priority-based route ordering
 * - Thread-safe route registration and matching
 * 
 * Examples:
 * - Exact: "/users" matches "/users" only
 * - Wildcard: "/api/*" matches "/api/anything"
 * - Path params: "/users/:id" matches "/users/123" and extracts id=123
 * - Combined: "/api/:version/users/:id" matches "/api/v1/users/123"
 */
public class MiddlewareRouter {
    
    private final List<Route> routes;
    private final ReadWriteLock lock;
    private boolean autoSort;
    
    public MiddlewareRouter() {
        this.routes = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.autoSort = true;
    }
    
    /**
     * Sets whether routes should be automatically sorted by priority.
     * Default is true.
     * 
     * @param autoSort true to auto-sort, false otherwise
     */
    public void setAutoSort(boolean autoSort) {
        lock.writeLock().lock();
        try {
            this.autoSort = autoSort;
            if (autoSort) {
                sortRoutes();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Registers a route with the router.
     * 
     * @param pattern the path pattern (e.g., "/users", "/users/*", "/users/:id")
     * @param handler the handler string to return
     */
    public void register(String pattern, String handler) {
        lock.writeLock().lock();
        try {
            Route route = new Route(pattern, handler);
            routes.add(route);
            if (autoSort) {
                sortRoutes();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Registers a route with explicit priority.
     * Lower priority numbers = higher priority.
     * 
     * @param pattern the path pattern
     * @param handler the handler string
     * @param priority the priority (lower = higher priority)
     */
    public void register(String pattern, String handler, int priority) {
        lock.writeLock().lock();
        try {
            Route route = new Route(pattern, handler, priority);
            routes.add(route);
            if (autoSort) {
                sortRoutes();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Routes a path and returns the handler string.
     * 
     * @param path the path to route
     * @return the handler string, or null if no match
     */
    public String route(String path) {
        RouteMatch match = routeWithParams(path);
        return match != null ? match.getHandler() : null;
    }
    
    /**
     * Routes a path and returns the full match with path parameters.
     * 
     * @param path the path to route
     * @return the route match, or null if no match
     */
    public RouteMatch routeWithParams(String path) {
        if (path == null) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            for (Route route : routes) {
                if (route.matches(path)) {
                    Map<String, String> pathParams = route.extractPathParams(path);
                    return new RouteMatch(route.getHandler(), pathParams, route);
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Removes all routes matching the given pattern.
     *
     * @param pattern the pattern to remove
     * @return the number of routes removed
     */
    public int removeRoute(String pattern) {
        lock.writeLock().lock();
        try {
            int initialSize = routes.size();
            routes.removeIf(route -> route.getPattern().equals(pattern));
            return initialSize - routes.size();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Clears all routes.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            routes.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the number of registered routes.
     * 
     * @return the route count
     */
    public int getRouteCount() {
        lock.readLock().lock();
        try {
            return routes.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all registered routes (copy).
     * 
     * @return list of routes
     */
    public List<Route> getRoutes() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(routes);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Sorts routes by priority (lower priority number = higher priority).
     * More specific routes should have lower priority numbers.
     */
    private void sortRoutes() {
        routes.sort(Comparator.comparingInt(Route::getPriority));
    }
    
    /**
     * Gets statistics about the router.
     * 
     * @return statistics string
     */
    public String getStatistics() {
        lock.readLock().lock();
        try {
            long exactRoutes = routes.stream()
                .filter(r -> !r.hasWildcard() && !r.hasPathParams())
                .count();
            long wildcardRoutes = routes.stream()
                .filter(Route::hasWildcard)
                .count();
            long paramRoutes = routes.stream()
                .filter(Route::hasPathParams)
                .count();
            
            return String.format(
                "Total routes: %d (Exact: %d, Wildcard: %d, Path params: %d)",
                routes.size(), exactRoutes, wildcardRoutes, paramRoutes
            );
        } finally {
            lock.readLock().unlock();
        }
    }
}

