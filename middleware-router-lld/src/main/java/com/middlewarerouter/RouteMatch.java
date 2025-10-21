package com.middlewarerouter;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a successful route match with handler and path parameters.
 */
public class RouteMatch {
    private final String handler;
    private final Map<String, String> pathParams;
    private final Route route;
    
    public RouteMatch(String handler, Map<String, String> pathParams, Route route) {
        this.handler = handler;
        this.pathParams = pathParams != null ? pathParams : Collections.emptyMap();
        this.route = route;
    }
    
    public String getHandler() {
        return handler;
    }
    
    public Map<String, String> getPathParams() {
        return Collections.unmodifiableMap(pathParams);
    }
    
    public Route getRoute() {
        return route;
    }
    
    /**
     * Gets a path parameter value.
     * 
     * @param name the parameter name
     * @return the parameter value, or null if not found
     */
    public String getPathParam(String name) {
        return pathParams.get(name);
    }
    
    /**
     * Checks if a path parameter exists.
     * 
     * @param name the parameter name
     * @return true if exists, false otherwise
     */
    public boolean hasPathParam(String name) {
        return pathParams.containsKey(name);
    }
    
    @Override
    public String toString() {
        if (pathParams.isEmpty()) {
            return String.format("RouteMatch{handler='%s'}", handler);
        }
        return String.format("RouteMatch{handler='%s', pathParams=%s}", handler, pathParams);
    }
}

