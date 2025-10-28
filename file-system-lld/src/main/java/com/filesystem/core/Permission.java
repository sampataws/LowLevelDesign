package com.filesystem.core;

/**
 * Represents file/directory permissions
 * Supports read, write, execute permissions for owner, group, and others
 */
public class Permission {
    private int mode; // Unix-style permission bits
    
    // Permission bits
    public static final int OWNER_READ = 0400;
    public static final int OWNER_WRITE = 0200;
    public static final int OWNER_EXECUTE = 0100;
    public static final int GROUP_READ = 0040;
    public static final int GROUP_WRITE = 0020;
    public static final int GROUP_EXECUTE = 0010;
    public static final int OTHERS_READ = 0004;
    public static final int OTHERS_WRITE = 0002;
    public static final int OTHERS_EXECUTE = 0001;
    
    // Default permissions
    public static final int DEFAULT_FILE_PERMISSION = 0644; // rw-r--r--
    public static final int DEFAULT_DIR_PERMISSION = 0755;  // rwxr-xr-x
    
    public Permission(int mode) {
        this.mode = mode;
    }
    
    public Permission() {
        this(DEFAULT_FILE_PERMISSION);
    }
    
    public int getMode() {
        return mode;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public boolean canRead() {
        return (mode & OWNER_READ) != 0;
    }
    
    public boolean canWrite() {
        return (mode & OWNER_WRITE) != 0;
    }
    
    public boolean canExecute() {
        return (mode & OWNER_EXECUTE) != 0;
    }
    
    public Permission copy() {
        return new Permission(this.mode);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Owner permissions
        sb.append((mode & OWNER_READ) != 0 ? 'r' : '-');
        sb.append((mode & OWNER_WRITE) != 0 ? 'w' : '-');
        sb.append((mode & OWNER_EXECUTE) != 0 ? 'x' : '-');
        
        // Group permissions
        sb.append((mode & GROUP_READ) != 0 ? 'r' : '-');
        sb.append((mode & GROUP_WRITE) != 0 ? 'w' : '-');
        sb.append((mode & GROUP_EXECUTE) != 0 ? 'x' : '-');
        
        // Others permissions
        sb.append((mode & OTHERS_READ) != 0 ? 'r' : '-');
        sb.append((mode & OTHERS_WRITE) != 0 ? 'w' : '-');
        sb.append((mode & OTHERS_EXECUTE) != 0 ? 'x' : '-');
        
        return sb.toString();
    }
}

