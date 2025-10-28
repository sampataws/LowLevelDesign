package com.filesystem.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents file/directory statistics (similar to Unix stat)
 */
public class FileStat {
    private final String name;
    private final String path;
    private final boolean isDirectory;
    private final long size;
    private final Permission permission;
    private final long creationTime;
    private final long modificationTime;
    
    public FileStat(FileSystemNode node) {
        this.name = node.getName();
        this.path = node.getFullPath();
        this.isDirectory = node.isDirectory();
        this.size = node.getSize();
        this.permission = node.getPermission();
        this.creationTime = node.getCreationTime();
        this.modificationTime = node.getModificationTime();
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isDirectory() {
        return isDirectory;
    }
    
    public boolean isFile() {
        return !isDirectory;
    }
    
    public long getSize() {
        return size;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public long getCreationTime() {
        return creationTime;
    }
    
    public long getModificationTime() {
        return modificationTime;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return String.format(
            "File: %s\n" +
            "  Type: %s\n" +
            "  Size: %d bytes\n" +
            "  Permissions: %s (%04o)\n" +
            "  Created: %s\n" +
            "  Modified: %s\n" +
            "  Path: %s",
            name,
            isDirectory ? "directory" : "file",
            size,
            permission.toString(),
            permission.getMode(),
            sdf.format(new Date(creationTime)),
            sdf.format(new Date(modificationTime)),
            path
        );
    }
    
    /**
     * Get a compact single-line representation (like ls -l)
     */
    public String toCompactString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
        return String.format("%s %s %8d %s %s",
            isDirectory ? "d" : "-",
            permission.toString(),
            size,
            sdf.format(new Date(modificationTime)),
            name
        );
    }
}

