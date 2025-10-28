package com.filesystem.core;

import com.filesystem.visitor.NodeVisitor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a directory in the file system (Composite Pattern)
 */
public class Directory extends FileSystemNode {
    private final Map<String, FileSystemNode> children;
    
    public Directory(String name) {
        this(name, new Permission(Permission.DEFAULT_DIR_PERMISSION));
    }
    
    public Directory(String name, Permission permission) {
        super(name, permission);
        this.children = new ConcurrentHashMap<>();
    }
    
    /**
     * Add a child node to this directory
     */
    public void addChild(FileSystemNode node) {
        if (!permission.canWrite()) {
            throw new SecurityException("Permission denied: cannot write to directory " + name);
        }
        
        if (children.containsKey(node.getName())) {
            throw new IllegalArgumentException("Node with name '" + node.getName() + "' already exists");
        }
        
        children.put(node.getName(), node);
        node.setParent(this);
        updateModificationTime();
    }
    
    /**
     * Remove a child node from this directory
     */
    public FileSystemNode removeChild(String name) {
        if (!permission.canWrite()) {
            throw new SecurityException("Permission denied: cannot write to directory " + this.name);
        }
        
        FileSystemNode node = children.remove(name);
        if (node != null) {
            node.setParent(null);
            updateModificationTime();
        }
        return node;
    }
    
    /**
     * Get a child node by name
     */
    public FileSystemNode getChild(String name) {
        if (!permission.canRead()) {
            throw new SecurityException("Permission denied: cannot read directory " + this.name);
        }
        return children.get(name);
    }
    
    /**
     * Check if a child exists
     */
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
    
    /**
     * List all children
     */
    public List<FileSystemNode> listChildren() {
        if (!permission.canRead()) {
            throw new SecurityException("Permission denied: cannot read directory " + name);
        }
        return new ArrayList<>(children.values());
    }
    
    /**
     * Get children names
     */
    public Set<String> getChildrenNames() {
        if (!permission.canRead()) {
            throw new SecurityException("Permission denied: cannot read directory " + name);
        }
        return new HashSet<>(children.keySet());
    }
    
    /**
     * Check if directory is empty
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }
    
    /**
     * Get number of children
     */
    public int getChildCount() {
        return children.size();
    }
    
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    @Override
    public long getSize() {
        long totalSize = 0;
        for (FileSystemNode child : children.values()) {
            totalSize += child.getSize();
        }
        return totalSize;
    }
    
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitDirectory(this);
    }
}

