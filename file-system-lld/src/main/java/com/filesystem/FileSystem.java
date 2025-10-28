package com.filesystem;

import com.filesystem.core.*;
import com.filesystem.lock.PathLockManager;
import com.filesystem.visitor.SizeCalculatorVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main FileSystem implementation with thread-safe operations
 * 
 * Features:
 * - mkdir/ls/stat operations
 * - create/read/write (append|overwrite) files
 * - move/rename and delete
 * - resolve absolute/relative paths with . and ..
 * - set/get permissions
 * - maintain size/ctime/mtime
 * - Thread-safe operations via per-path locks
 * - Atomic rename within directory tree
 */
public class FileSystem {
    private final Directory root;
    private final PathLockManager lockManager;
    private String currentWorkingDirectory;
    
    public FileSystem() {
        this.root = new Directory("");
        this.lockManager = new PathLockManager();
        this.currentWorkingDirectory = "/";
    }
    
    /**
     * Create a directory at the given path
     * 
     * @param path Path where directory should be created
     * @param createParents If true, create parent directories as needed
     * @return true if created, false if already exists
     */
    public boolean mkdir(String path, boolean createParents) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withWriteLock(normalizedPath, () -> {
            if (createParents) {
                return mkdirRecursive(normalizedPath);
            } else {
                return mkdirSingle(normalizedPath);
            }
        });
    }
    
    /**
     * Create a directory (non-recursive)
     */
    public boolean mkdir(String path) {
        return mkdir(path, false);
    }
    
    private boolean mkdirSingle(String path) {
        String parentPath = PathResolver.getParent(path);
        String dirName = PathResolver.getName(path);
        
        Directory parent = (Directory) navigateToNode(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Parent directory does not exist: " + parentPath);
        }
        
        if (parent.hasChild(dirName)) {
            return false; // Already exists
        }
        
        Directory newDir = new Directory(dirName);
        parent.addChild(newDir);
        return true;
    }
    
    private boolean mkdirRecursive(String path) {
        if (path.equals("/")) {
            return false; // Root already exists
        }
        
        FileSystemNode existing = navigateToNode(path);
        if (existing != null) {
            return existing.isDirectory(); // Already exists
        }
        
        // Create parent first
        String parentPath = PathResolver.getParent(path);
        if (!parentPath.equals("/")) {
            mkdirRecursive(parentPath);
        }
        
        return mkdirSingle(path);
    }
    
    /**
     * List contents of a directory
     * 
     * @param path Directory path
     * @return List of file/directory names
     */
    public List<String> ls(String path) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            
            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }
            
            if (!node.isDirectory()) {
                // If it's a file, return just the file name
                return Collections.singletonList(node.getName());
            }
            
            Directory dir = (Directory) node;
            return new ArrayList<>(dir.getChildrenNames());
        });
    }
    
    /**
     * List contents with detailed information
     */
    public List<FileStat> lsDetailed(String path) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            
            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }
            
            if (!node.isDirectory()) {
                return Collections.singletonList(new FileStat(node));
            }
            
            Directory dir = (Directory) node;
            return dir.listChildren().stream()
                    .map(FileStat::new)
                    .collect(Collectors.toList());
        });
    }
    
    /**
     * Get file/directory statistics
     * 
     * @param path Path to file or directory
     * @return FileStat object with metadata
     */
    public FileStat stat(String path) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            
            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }
            
            return new FileStat(node);
        });
    }
    
    /**
     * Create a new file
     * 
     * @param path File path
     * @return true if created, false if already exists
     */
    public boolean createFile(String path) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withWriteLock(normalizedPath, () -> {
            String parentPath = PathResolver.getParent(normalizedPath);
            String fileName = PathResolver.getName(normalizedPath);
            
            Directory parent = (Directory) navigateToNode(parentPath);
            if (parent == null) {
                throw new IllegalArgumentException("Parent directory does not exist: " + parentPath);
            }
            
            if (parent.hasChild(fileName)) {
                return false; // Already exists
            }
            
            File newFile = new File(fileName);
            parent.addChild(newFile);
            return true;
        });
    }
    
    /**
     * Read file content
     * 
     * @param path File path
     * @return File content as string
     */
    public String read(String path) {
        String normalizedPath = resolvePath(path);
        
        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            
            if (node == null) {
                throw new IllegalArgumentException("File does not exist: " + normalizedPath);
            }
            
            if (node.isDirectory()) {
                throw new IllegalArgumentException("Cannot read a directory: " + normalizedPath);
            }
            
            File file = (File) node;
            return file.read();
        });
    }
    
    /**
     * Write to file (overwrite mode)
     * 
     * @param path File path
     * @param content Content to write
     */
    public void write(String path, String content) {
        write(path, content, false);
    }
    
    /**
     * Write to file with explicit mode
     * 
     * @param path File path
     * @param content Content to write
     * @param append If true, append; if false, overwrite
     */
    public void write(String path, String content, boolean append) {
        String normalizedPath = resolvePath(path);
        
        lockManager.withWriteLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            
            if (node == null) {
                throw new IllegalArgumentException("File does not exist: " + normalizedPath);
            }
            
            if (node.isDirectory()) {
                throw new IllegalArgumentException("Cannot write to a directory: " + normalizedPath);
            }
            
            File file = (File) node;
            file.write(content, append);
            return null;
        });
    }
    
    /**
     * Append to file
     */
    public void append(String path, String content) {
        write(path, content, true);
    }
    
    /**
     * Overwrite file
     */
    public void overwrite(String path, String content) {
        write(path, content, false);
    }
    
    /**
     * Move/rename a file or directory (atomic within directory tree)
     * 
     * @param sourcePath Source path
     * @param destPath Destination path
     */
    public void move(String sourcePath, String destPath) {
        String normalizedSource = resolvePath(sourcePath);
        String normalizedDest = resolvePath(destPath);
        
        if (normalizedSource.equals("/")) {
            throw new IllegalArgumentException("Cannot move root directory");
        }
        
        if (normalizedDest.startsWith(normalizedSource + "/")) {
            throw new IllegalArgumentException("Cannot move directory into itself");
        }
        
        // Acquire locks in consistent order to prevent deadlocks
        lockManager.withWriteLocks(() -> {
            FileSystemNode sourceNode = navigateToNode(normalizedSource);
            if (sourceNode == null) {
                throw new IllegalArgumentException("Source does not exist: " + normalizedSource);
            }
            
            String sourceParentPath = PathResolver.getParent(normalizedSource);
            String destParentPath = PathResolver.getParent(normalizedDest);
            String destName = PathResolver.getName(normalizedDest);
            
            Directory sourceParent = (Directory) navigateToNode(sourceParentPath);
            Directory destParent = (Directory) navigateToNode(destParentPath);
            
            if (destParent == null) {
                throw new IllegalArgumentException("Destination parent does not exist: " + destParentPath);
            }
            
            if (destParent.hasChild(destName)) {
                throw new IllegalArgumentException("Destination already exists: " + normalizedDest);
            }
            
            // Atomic move: remove from source, add to destination
            sourceParent.removeChild(sourceNode.getName());
            sourceNode.setName(destName);
            destParent.addChild(sourceNode);
            
            return null;
        }, normalizedSource, normalizedDest);
    }

    /**
     * Rename a file or directory (shorthand for move within same directory)
     */
    public void rename(String path, String newName) {
        String normalizedPath = resolvePath(path);
        String parentPath = PathResolver.getParent(normalizedPath);
        String newPath = PathResolver.join(parentPath, newName);
        move(normalizedPath, newPath);
    }

    /**
     * Delete a file or directory
     *
     * @param path Path to delete
     * @param recursive If true, delete non-empty directories recursively
     * @return true if deleted, false if not found
     */
    public boolean delete(String path, boolean recursive) {
        String normalizedPath = resolvePath(path);

        if (normalizedPath.equals("/")) {
            throw new IllegalArgumentException("Cannot delete root directory");
        }

        return lockManager.withWriteLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                return false; // Not found
            }

            if (node.isDirectory()) {
                Directory dir = (Directory) node;
                if (!dir.isEmpty() && !recursive) {
                    throw new IllegalArgumentException(
                        "Directory is not empty. Use recursive=true to delete: " + normalizedPath);
                }
            }

            String parentPath = PathResolver.getParent(normalizedPath);
            Directory parent = (Directory) navigateToNode(parentPath);

            parent.removeChild(node.getName());
            return true;
        });
    }

    /**
     * Delete a file or empty directory
     */
    public boolean delete(String path) {
        return delete(path, false);
    }

    /**
     * Set permissions on a file or directory
     *
     * @param path Path to file/directory
     * @param permission New permission
     */
    public void setPermission(String path, Permission permission) {
        String normalizedPath = resolvePath(path);

        lockManager.withWriteLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }

            node.setPermission(permission);
            return null;
        });
    }

    /**
     * Get permissions of a file or directory
     *
     * @param path Path to file/directory
     * @return Permission object
     */
    public Permission getPermission(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }

            return node.getPermission();
        });
    }

    /**
     * Check if a path exists
     *
     * @param path Path to check
     * @return true if exists, false otherwise
     */
    public boolean exists(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            return navigateToNode(normalizedPath) != null;
        });
    }

    /**
     * Check if path is a directory
     */
    public boolean isDirectory(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            return node != null && node.isDirectory();
        });
    }

    /**
     * Check if path is a file
     */
    public boolean isFile(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);
            return node != null && !node.isDirectory();
        });
    }

    /**
     * Get current working directory
     */
    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    /**
     * Change current working directory
     */
    public void changeDirectory(String path) {
        String normalizedPath = resolvePath(path);

        lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                throw new IllegalArgumentException("Directory does not exist: " + normalizedPath);
            }

            if (!node.isDirectory()) {
                throw new IllegalArgumentException("Not a directory: " + normalizedPath);
            }

            currentWorkingDirectory = normalizedPath;
            return null;
        });
    }

    /**
     * Resolve a path (handle relative paths and normalization)
     */
    private String resolvePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        if (PathResolver.isAbsolute(path)) {
            return PathResolver.normalize(path);
        } else {
            return PathResolver.resolve(currentWorkingDirectory, path);
        }
    }

    /**
     * Navigate to a node given a path
     *
     * @param path Normalized absolute path
     * @return FileSystemNode or null if not found
     */
    private FileSystemNode navigateToNode(String path) {
        if (path.equals("/")) {
            return root;
        }

        List<String> components = PathResolver.split(path);
        FileSystemNode current = root;

        for (String component : components) {
            if (!current.isDirectory()) {
                return null; // Path component is not a directory
            }

            Directory dir = (Directory) current;
            current = dir.getChild(component);

            if (current == null) {
                return null; // Component not found
            }
        }

        return current;
    }

    /**
     * Get total size of a directory tree
     */
    public long getSize(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }

            if (!node.isDirectory()) {
                return node.getSize();
            }

            SizeCalculatorVisitor visitor = new SizeCalculatorVisitor();
            node.accept(visitor);
            return visitor.getTotalSize();
        });
    }

    /**
     * Print directory tree structure
     */
    public String tree(String path) {
        String normalizedPath = resolvePath(path);

        return lockManager.withReadLock(normalizedPath, () -> {
            FileSystemNode node = navigateToNode(normalizedPath);

            if (node == null) {
                throw new IllegalArgumentException("Path does not exist: " + normalizedPath);
            }

            StringBuilder sb = new StringBuilder();
            printTree(node, "", true, sb);
            return sb.toString();
        });
    }

    private void printTree(FileSystemNode node, String prefix, boolean isLast, StringBuilder sb) {
        sb.append(prefix);
        sb.append(isLast ? "└── " : "├── ");
        sb.append(node.getName());

        if (node.isDirectory()) {
            sb.append("/");
        }

        sb.append("\n");

        if (node.isDirectory()) {
            Directory dir = (Directory) node;
            List<FileSystemNode> children = dir.listChildren();

            for (int i = 0; i < children.size(); i++) {
                FileSystemNode child = children.get(i);
                boolean childIsLast = (i == children.size() - 1);
                String newPrefix = prefix + (isLast ? "    " : "│   ");
                printTree(child, newPrefix, childIsLast, sb);
            }
        }
    }
}

