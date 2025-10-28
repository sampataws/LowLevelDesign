package com.filesystem.core;

import com.filesystem.visitor.NodeVisitor;

/**
 * Represents a file in the file system
 */
public class File extends FileSystemNode {
    private StringBuilder content;
    
    public File(String name) {
        this(name, new Permission(Permission.DEFAULT_FILE_PERMISSION));
    }
    
    public File(String name, Permission permission) {
        super(name, permission);
        this.content = new StringBuilder();
    }
    
    /**
     * Read the entire content of the file
     */
    public String read() {
        if (!permission.canRead()) {
            throw new SecurityException("Permission denied: cannot read file " + name);
        }
        return content.toString();
    }
    
    /**
     * Write content to the file (overwrite mode)
     */
    public void write(String data, boolean append) {
        if (!permission.canWrite()) {
            throw new SecurityException("Permission denied: cannot write to file " + name);
        }
        
        if (append) {
            content.append(data);
        } else {
            content = new StringBuilder(data);
        }
        updateModificationTime();
    }
    
    /**
     * Overwrite the file content
     */
    public void overwrite(String data) {
        write(data, false);
    }
    
    /**
     * Append to the file content
     */
    public void append(String data) {
        write(data, true);
    }
    
    /**
     * Clear the file content
     */
    public void clear() {
        if (!permission.canWrite()) {
            throw new SecurityException("Permission denied: cannot write to file " + name);
        }
        content = new StringBuilder();
        updateModificationTime();
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public long getSize() {
        return content.length();
    }
    
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitFile(this);
    }
    
    public String getContent() {
        return content.toString();
    }
}

