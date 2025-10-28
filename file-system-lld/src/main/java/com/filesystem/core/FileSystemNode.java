package com.filesystem.core;

import com.filesystem.visitor.NodeVisitor;

/**
 * Abstract base class for file system nodes (Composite Pattern)
 * Represents both files and directories
 */
public abstract class FileSystemNode {
    protected String name;
    protected Permission permission;
    protected long creationTime;
    protected long modificationTime;
    protected FileSystemNode parent;
    
    public FileSystemNode(String name, Permission permission) {
        this.name = name;
        this.permission = permission;
        this.creationTime = System.currentTimeMillis();
        this.modificationTime = this.creationTime;
        this.parent = null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        updateModificationTime();
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    public long getCreationTime() {
        return creationTime;
    }
    
    public long getModificationTime() {
        return modificationTime;
    }
    
    public void updateModificationTime() {
        this.modificationTime = System.currentTimeMillis();
    }
    
    public FileSystemNode getParent() {
        return parent;
    }
    
    public void setParent(FileSystemNode parent) {
        this.parent = parent;
    }
    
    /**
     * Get the full path of this node
     */
    public String getFullPath() {
        if (parent == null) {
            return "/";
        }
        
        StringBuilder path = new StringBuilder();
        buildPath(path);
        return path.toString();
    }
    
    private void buildPath(StringBuilder sb) {
        if (parent != null && parent.parent != null) {
            parent.buildPath(sb);
            sb.append("/");
        } else if (parent != null) {
            sb.append("/");
        }
        sb.append(name);
    }
    
    /**
     * Check if this is a directory
     */
    public abstract boolean isDirectory();
    
    /**
     * Get the size of this node
     */
    public abstract long getSize();
    
    /**
     * Accept a visitor (Visitor Pattern)
     */
    public abstract void accept(NodeVisitor visitor);
    
    @Override
    public String toString() {
        return name;
    }
}

