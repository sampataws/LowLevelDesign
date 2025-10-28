package com.filesystem.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;

/**
 * Manages per-path locks for thread-safe file system operations
 * Uses consistent lock ordering to prevent deadlocks
 */
public class PathLockManager {
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks;
    
    public PathLockManager() {
        this.locks = new ConcurrentHashMap<>();
    }
    
    /**
     * Get or create a lock for a path
     */
    private ReentrantReadWriteLock getLock(String path) {
        return locks.computeIfAbsent(path, k -> new ReentrantReadWriteLock());
    }
    
    /**
     * Acquire read lock for a single path
     */
    public void acquireReadLock(String path) {
        getLock(path).readLock().lock();
    }
    
    /**
     * Release read lock for a single path
     */
    public void releaseReadLock(String path) {
        ReentrantReadWriteLock lock = locks.get(path);
        if (lock != null) {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Acquire write lock for a single path
     */
    public void acquireWriteLock(String path) {
        getLock(path).writeLock().lock();
    }
    
    /**
     * Release write lock for a single path
     */
    public void releaseWriteLock(String path) {
        ReentrantReadWriteLock lock = locks.get(path);
        if (lock != null) {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Acquire write locks for multiple paths in consistent order
     * This prevents deadlocks by always acquiring locks in sorted order
     */
    public void acquireWriteLocks(String... paths) {
        // Sort paths to ensure consistent lock ordering
        String[] sortedPaths = Arrays.copyOf(paths, paths.length);
        Arrays.sort(sortedPaths);
        
        for (String path : sortedPaths) {
            acquireWriteLock(path);
        }
    }
    
    /**
     * Release write locks for multiple paths
     */
    public void releaseWriteLocks(String... paths) {
        // Release in reverse order of acquisition
        String[] sortedPaths = Arrays.copyOf(paths, paths.length);
        Arrays.sort(sortedPaths);
        
        for (int i = sortedPaths.length - 1; i >= 0; i--) {
            releaseWriteLock(sortedPaths[i]);
        }
    }
    
    /**
     * Execute an operation with read lock
     */
    public <T> T withReadLock(String path, LockOperation<T> operation) {
        acquireReadLock(path);
        try {
            return operation.execute();
        } finally {
            releaseReadLock(path);
        }
    }
    
    /**
     * Execute an operation with write lock
     */
    public <T> T withWriteLock(String path, LockOperation<T> operation) {
        acquireWriteLock(path);
        try {
            return operation.execute();
        } finally {
            releaseWriteLock(path);
        }
    }
    
    /**
     * Execute an operation with multiple write locks
     */
    public <T> T withWriteLocks(LockOperation<T> operation, String... paths) {
        acquireWriteLocks(paths);
        try {
            return operation.execute();
        } finally {
            releaseWriteLocks(paths);
        }
    }
    
    /**
     * Functional interface for lock operations
     */
    @FunctionalInterface
    public interface LockOperation<T> {
        T execute();
    }
    
    /**
     * Clean up unused locks (optional optimization)
     */
    public void cleanup() {
        locks.entrySet().removeIf(entry -> {
            ReentrantReadWriteLock lock = entry.getValue();
            return !lock.isWriteLocked() && lock.getReadLockCount() == 0;
        });
    }
}

