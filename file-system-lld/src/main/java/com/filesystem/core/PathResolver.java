package com.filesystem.core;

import java.util.*;

/**
 * Handles path resolution and normalization
 * Supports absolute and relative paths with . and ..
 */
public class PathResolver {
    
    /**
     * Normalize a path by resolving . and .. components
     * 
     * @param path The path to normalize
     * @return Normalized absolute path
     */
    public static String normalize(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        // Check for invalid characters
        if (path.contains("\0")) {
            throw new IllegalArgumentException("Path contains null character");
        }
        
        boolean isAbsolute = path.startsWith("/");
        String[] parts = path.split("/");
        
        Deque<String> stack = new ArrayDeque<>();
        
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                // Skip empty parts and current directory
                continue;
            } else if (part.equals("..")) {
                // Go up one level
                if (!stack.isEmpty()) {
                    stack.pop();
                } else {
                    // Cannot traverse beyond root for both absolute and relative paths
                    throw new IllegalArgumentException("Path traversal beyond root: " + path);
                }
            } else {
                // Regular directory/file name
                validatePathComponent(part);
                stack.push(part);
            }
        }
        
        // Build normalized path
        if (stack.isEmpty()) {
            return "/";
        }
        
        StringBuilder normalized = new StringBuilder();
        List<String> components = new ArrayList<>(stack);
        Collections.reverse(components);
        
        for (String component : components) {
            normalized.append("/").append(component);
        }
        
        return normalized.toString();
    }
    
    /**
     * Resolve a relative path against a base path
     * 
     * @param basePath The base path (must be absolute)
     * @param relativePath The relative path
     * @return Resolved absolute path
     */
    public static String resolve(String basePath, String relativePath) {
        if (relativePath.startsWith("/")) {
            // Already absolute
            return normalize(relativePath);
        }
        
        // Combine base and relative paths
        String combined = basePath.endsWith("/") 
            ? basePath + relativePath 
            : basePath + "/" + relativePath;
        
        return normalize(combined);
    }
    
    /**
     * Split a path into components
     * 
     * @param path The path to split
     * @return List of path components (excluding empty strings)
     */
    public static List<String> split(String path) {
        String normalized = normalize(path);
        if (normalized.equals("/")) {
            return new ArrayList<>();
        }
        
        String[] parts = normalized.substring(1).split("/");
        return Arrays.asList(parts);
    }
    
    /**
     * Get the parent path
     * 
     * @param path The path
     * @return Parent path, or "/" if at root
     */
    public static String getParent(String path) {
        String normalized = normalize(path);
        if (normalized.equals("/")) {
            return "/";
        }
        
        int lastSlash = normalized.lastIndexOf('/');
        if (lastSlash == 0) {
            return "/";
        }
        
        return normalized.substring(0, lastSlash);
    }
    
    /**
     * Get the file/directory name from a path
     * 
     * @param path The path
     * @return The name component
     */
    public static String getName(String path) {
        String normalized = normalize(path);
        if (normalized.equals("/")) {
            return "/";
        }
        
        int lastSlash = normalized.lastIndexOf('/');
        return normalized.substring(lastSlash + 1);
    }
    
    /**
     * Validate a single path component (file or directory name)
     * 
     * @param component The component to validate
     */
    private static void validatePathComponent(String component) {
        if (component.isEmpty()) {
            throw new IllegalArgumentException("Path component cannot be empty");
        }
        
        // Check for invalid characters
        char[] invalid = {'/', '\0', '<', '>', ':', '"', '|', '?', '*'};
        for (char c : invalid) {
            if (component.indexOf(c) >= 0) {
                throw new IllegalArgumentException(
                    "Path component contains invalid character: " + c);
            }
        }
        
        // Check for reserved names (Windows-style, but good practice)
        String upper = component.toUpperCase();
        if (upper.equals("CON") || upper.equals("PRN") || upper.equals("AUX") || 
            upper.equals("NUL") || upper.matches("COM[1-9]") || upper.matches("LPT[1-9]")) {
            throw new IllegalArgumentException("Path component uses reserved name: " + component);
        }
    }
    
    /**
     * Check if a path is absolute
     * 
     * @param path The path to check
     * @return true if absolute, false otherwise
     */
    public static boolean isAbsolute(String path) {
        return path != null && path.startsWith("/");
    }
    
    /**
     * Join multiple path components
     * 
     * @param components Path components to join
     * @return Joined path
     */
    public static String join(String... components) {
        if (components.length == 0) {
            return "/";
        }
        
        StringBuilder path = new StringBuilder();
        for (String component : components) {
            if (component == null || component.isEmpty()) {
                continue;
            }
            
            if (path.length() > 0 && !path.toString().endsWith("/")) {
                path.append("/");
            }
            path.append(component);
        }
        
        return normalize(path.toString());
    }
}

