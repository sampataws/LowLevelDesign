package com.middlewarerouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a route with path pattern and handler.
 */
public class Route {
    private final String pattern;
    private final String handler;
    private final int priority;
    private final boolean hasWildcard;
    private final boolean hasPathParams;
    private final List<String> pathParamNames;
    private final Pattern compiledPattern;
    
    /**
     * Creates a new route.
     *
     * @param pattern the path pattern (e.g., "/users/*", "/users/:id")
     * @param handler the handler string to return
     * @param priority the priority (lower = higher priority)
     */
    public Route(String pattern, String handler, int priority) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        this.pattern = pattern;
        this.handler = handler;
        this.priority = priority;
        this.hasWildcard = pattern.contains("*");
        this.hasPathParams = pattern.contains(":");
        this.pathParamNames = new ArrayList<>();
        this.compiledPattern = compilePattern(pattern);
    }

    /**
     * Creates a new route with default priority based on specificity.
     */
    public Route(String pattern, String handler) {
        this(pattern, handler, pattern != null ? calculateDefaultPriority(pattern) : 0);
    }
    
    /**
     * Compiles the pattern into a regex pattern.
     */
    private Pattern compilePattern(String pattern) {
        String regex = pattern;
        
        // Extract path parameter names
        Pattern paramPattern = Pattern.compile(":([a-zA-Z][a-zA-Z0-9_]*)");
        Matcher matcher = paramPattern.matcher(pattern);
        while (matcher.find()) {
            pathParamNames.add(matcher.group(1));
        }
        
        // Convert path params to regex groups
        regex = regex.replaceAll(":([a-zA-Z][a-zA-Z0-9_]*)", "([^/]+)");
        
        // Convert wildcards to regex
        regex = regex.replace("*", ".*");
        
        // Exact match
        regex = "^" + regex + "$";
        
        return Pattern.compile(regex);
    }
    
    /**
     * Calculates default priority based on pattern specificity.
     * More specific patterns get lower priority numbers (higher priority).
     */
    private static int calculateDefaultPriority(String pattern) {
        int priority = 0;
        
        // Exact paths have highest priority
        if (!pattern.contains("*") && !pattern.contains(":")) {
            return 0;
        }
        
        // Path params have medium priority
        if (pattern.contains(":")) {
            priority = 100;
            // More specific path params get higher priority
            priority -= pattern.split("/").length * 10;
        }
        
        // Wildcards have lowest priority
        if (pattern.contains("*")) {
            priority = 200;
            // More specific wildcards get higher priority
            int wildcardIndex = pattern.indexOf("*");
            priority -= wildcardIndex;
        }
        
        return priority;
    }
    
    /**
     * Checks if this route matches the given path.
     * 
     * @param path the path to match
     * @return true if matches, false otherwise
     */
    public boolean matches(String path) {
        return compiledPattern.matcher(path).matches();
    }
    
    /**
     * Extracts path parameters from the given path.
     * 
     * @param path the path to extract from
     * @return map of parameter names to values, or empty map if no params
     */
    public Map<String, String> extractPathParams(String path) {
        Map<String, String> params = new HashMap<>();
        
        if (!hasPathParams) {
            return params;
        }
        
        Matcher matcher = compiledPattern.matcher(path);
        if (matcher.matches()) {
            for (int i = 0; i < pathParamNames.size(); i++) {
                params.put(pathParamNames.get(i), matcher.group(i + 1));
            }
        }
        
        return params;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public String getHandler() {
        return handler;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public boolean hasWildcard() {
        return hasWildcard;
    }
    
    public boolean hasPathParams() {
        return hasPathParams;
    }
    
    public List<String> getPathParamNames() {
        return new ArrayList<>(pathParamNames);
    }
    
    @Override
    public String toString() {
        return String.format("Route{pattern='%s', handler='%s', priority=%d}", 
            pattern, handler, priority);
    }
}

