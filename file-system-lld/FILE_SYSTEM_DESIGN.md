# File System - Low Level Design

## 📋 Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [Architecture](#architecture)
4. [Design Patterns](#design-patterns)
5. [Core Components](#core-components)
6. [Thread Safety](#thread-safety)
7. [API Documentation](#api-documentation)
8. [Examples](#examples)
9. [Performance Analysis](#performance-analysis)

## 🎯 Overview

This is a comprehensive in-memory file system implementation that supports standard file operations with thread-safe concurrent access. The design follows SOLID principles and incorporates multiple design patterns for extensibility and maintainability.

### Key Features

✅ **Complete File Operations**: mkdir, ls, stat, create, read, write, move, rename, delete  
✅ **Path Resolution**: Absolute and relative paths with `.` and `..` support  
✅ **Permission System**: Unix-style permissions (rwx for owner/group/others)  
✅ **Metadata Tracking**: Size, creation time, modification time  
✅ **Thread Safety**: Per-path locks with deadlock prevention  
✅ **Atomic Operations**: Atomic rename/move within directory tree  
✅ **Efficient Lookups**: HashMap-based directory structure  

## 📝 Requirements

### Functional Requirements

| Requirement | Description | Status |
|-------------|-------------|--------|
| **mkdir** | Create directories with optional parent creation | ✅ |
| **ls** | List directory contents | ✅ |
| **stat** | Get file/directory metadata | ✅ |
| **create** | Create new files | ✅ |
| **read** | Read file content | ✅ |
| **write** | Write to files (append/overwrite modes) | ✅ |
| **move/rename** | Move or rename files/directories | ✅ |
| **delete** | Delete files/directories (recursive option) | ✅ |
| **Path Resolution** | Handle absolute/relative paths with . and .. | ✅ |
| **Permissions** | Set/get Unix-style permissions | ✅ |
| **Metadata** | Track size, ctime, mtime | ✅ |

### Non-Functional Requirements

| Requirement | Implementation | Status |
|-------------|----------------|--------|
| **Thread Safety** | Per-path read-write locks | ✅ |
| **Atomic Rename** | Consistent lock ordering | ✅ |
| **Efficient Lookups** | HashMap for O(1) directory access | ✅ |
| **Path Validation** | Normalization and security checks | ✅ |
| **Deadlock Prevention** | Sorted lock acquisition | ✅ |

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     FileSystem API                          │
│  (mkdir, ls, stat, create, read, write, move, delete)      │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
┌──────────────┐ ┌──────────┐ ┌─────────────┐
│ PathResolver │ │  Locks   │ │  Visitors   │
│  (normalize) │ │ (thread  │ │ (traversal) │
│              │ │  safety) │ │             │
└──────────────┘ └──────────┘ └─────────────┘
        │            │            │
        └────────────┼────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │   File System Tree     │
        │   (Composite Pattern)  │
        └────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────┐         ┌──────────────┐
│  Directory   │         │     File     │
│  (Composite) │◄────────┤    (Leaf)    │
│              │ parent  │              │
└──────────────┘         └──────────────┘
```

### Class Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    FileSystemNode                           │
│  (Abstract Base Class)                                      │
├─────────────────────────────────────────────────────────────┤
│ - name: String                                              │
│ - permission: Permission                                    │
│ - creationTime: long                                        │
│ - modificationTime: long                                    │
│ - parent: FileSystemNode                                    │
├─────────────────────────────────────────────────────────────┤
│ + isDirectory(): boolean                                    │
│ + getSize(): long                                           │
│ + accept(visitor: NodeVisitor): void                        │
│ + getFullPath(): String                                     │
└─────────────────────────────────────────────────────────────┘
                          △
                          │
                ┌─────────┴─────────┐
                │                   │
┌───────────────▼──────┐  ┌─────────▼────────────┐
│      Directory       │  │        File          │
├──────────────────────┤  ├──────────────────────┤
│ - children: Map      │  │ - content: String    │
├──────────────────────┤  ├──────────────────────┤
│ + addChild()         │  │ + read(): String     │
│ + removeChild()      │  │ + write()            │
│ + getChild()         │  │ + append()           │
│ + listChildren()     │  │ + overwrite()        │
└──────────────────────┘  └──────────────────────┘
```

## 🎨 Design Patterns

### 1. Composite Pattern

**Purpose**: Treat files and directories uniformly

```
FileSystemNode (Component)
    ├── Directory (Composite)
    │   └── children: Map<String, FileSystemNode>
    └── File (Leaf)
        └── content: String
```

**Benefits**:
- Uniform interface for files and directories
- Easy tree traversal
- Simplified client code

### 2. Visitor Pattern

**Purpose**: Perform operations on the file system tree without modifying node classes

```java
interface NodeVisitor {
    void visitFile(File file);
    void visitDirectory(Directory directory);
}

// Implementations:
- SizeCalculatorVisitor: Calculate total size
- PermissionUpdaterVisitor: Update permissions recursively
```

**Benefits**:
- Separation of concerns
- Easy to add new operations
- No modification to node classes

### 3. Strategy Pattern (Implicit)

**Purpose**: Different write strategies (append vs overwrite)

```java
// Explicit mode parameter
file.write(content, append=true);   // Append strategy
file.write(content, append=false);  // Overwrite strategy
```

### 4. Template Method Pattern

**Purpose**: Lock acquisition template

```java
public <T> T withWriteLock(String path, LockOperation<T> operation) {
    acquireWriteLock(path);
    try {
        return operation.execute();
    } finally {
        releaseWriteLock(path);
    }
}
```

### 5. Singleton Pattern (Implicit)

**Purpose**: Single file system instance per application

```java
FileSystem fs = new FileSystem();  // Typically one instance
```

## 🔧 Core Components

### 1. FileSystemNode (Abstract Base)

**Responsibilities**:
- Store common metadata (name, permissions, timestamps)
- Provide abstract methods for polymorphic behavior
- Support visitor pattern

**Key Methods**:
```java
abstract boolean isDirectory()
abstract long getSize()
abstract void accept(NodeVisitor visitor)
String getFullPath()
```

### 2. Directory (Composite)

**Responsibilities**:
- Manage child nodes (files and subdirectories)
- Provide efficient lookup via HashMap
- Enforce permissions on operations

**Data Structure**:
```java
Map<String, FileSystemNode> children = new ConcurrentHashMap<>();
```

**Time Complexity**:
- Add child: O(1)
- Remove child: O(1)
- Get child: O(1)
- List children: O(n)

### 3. File (Leaf)

**Responsibilities**:
- Store file content
- Support read/write operations
- Enforce read/write permissions

**Data Structure**:
```java
StringBuilder content;  // Mutable for efficient append
```

### 4. PathResolver

**Responsibilities**:
- Normalize paths (resolve `.` and `..`)
- Validate path components
- Prevent directory traversal attacks
- Convert relative to absolute paths

**Key Methods**:
```java
String normalize(String path)
String resolve(String basePath, String relativePath)
List<String> split(String path)
String getParent(String path)
String getName(String path)
```

**Path Normalization Examples**:
```
/home/./user/../documents  →  /home/documents
/home//user///file.txt     →  /home/user/file.txt
/../etc                    →  Error (traversal beyond root)
```

### 5. PathLockManager

**Responsibilities**:
- Manage per-path read-write locks
- Prevent deadlocks via consistent lock ordering
- Provide lock acquisition templates

**Lock Strategy**:
```java
// Single path
acquireWriteLock(path)

// Multiple paths (sorted to prevent deadlock)
acquireWriteLocks(path1, path2, path3)
```

**Deadlock Prevention**:
```
Thread 1: lock(/a) → lock(/b)  ✅
Thread 2: lock(/b) → lock(/a)  ❌ Deadlock!

Solution: Always acquire locks in sorted order
Thread 1: lock(/a) → lock(/b)  ✅
Thread 2: lock(/a) → lock(/b)  ✅ No deadlock!
```

### 6. Permission

**Responsibilities**:
- Store Unix-style permission bits
- Provide permission checks
- Support permission display

**Permission Bits**:
```
Owner  Group  Others
rwx    rwx    rwx
421    421    421

Example: 755 = rwxr-xr-x
  Owner: 7 (4+2+1) = rwx
  Group: 5 (4+0+1) = r-x
  Others: 5 (4+0+1) = r-x
```

### 7. FileStat

**Responsibilities**:
- Provide file/directory metadata
- Format statistics for display
- Support both detailed and compact views

**Metadata**:
- Name, path, type (file/directory)
- Size (bytes)
- Permissions
- Creation time, modification time

## 🔒 Thread Safety

### Locking Strategy

**Read-Write Locks**:
- Multiple readers can access simultaneously
- Writers have exclusive access
- Readers block writers, writers block everyone

**Per-Path Locking**:
```java
// Different paths can be accessed concurrently
Thread 1: read(/home/file1.txt)   ✅ Concurrent
Thread 2: read(/home/file2.txt)   ✅ Concurrent

// Same path requires synchronization
Thread 1: write(/home/file.txt)   ✅ Exclusive
Thread 2: read(/home/file.txt)    ⏳ Waits
```

### Deadlock Prevention

**Problem**: Circular wait on locks
```
Thread 1: lock(A) → lock(B)
Thread 2: lock(B) → lock(A)
Result: Deadlock! ❌
```

**Solution**: Consistent lock ordering
```java
public void acquireWriteLocks(String... paths) {
    // Sort paths to ensure consistent order
    String[] sortedPaths = Arrays.copyOf(paths, paths.length);
    Arrays.sort(sortedPaths);
    
    for (String path : sortedPaths) {
        acquireWriteLock(path);
    }
}
```

**Example**:
```java
// Move operation locks both source and destination
fs.move("/home/file.txt", "/backup/file.txt");

// Internally:
// 1. Sort: ["/backup/file.txt", "/home/file.txt"]
// 2. Lock in order: /backup/file.txt → /home/file.txt
// 3. Perform move
// 4. Unlock in reverse order
```

### Atomic Operations

**Atomic Rename/Move**:
```java
lockManager.withWriteLocks(() -> {
    // All operations within this block are atomic
    sourceParent.removeChild(node);
    node.setName(newName);
    destParent.addChild(node);
    return null;
}, sourcePath, destPath);
```

**Benefits**:
- No partial state visible to other threads
- Either complete success or complete failure
- Consistent file system state

## 📚 API Documentation

### Directory Operations

#### mkdir(path, createParents)
Create a directory at the specified path.

```java
// Create single directory
fs.mkdir("/home");

// Create with parents
fs.mkdir("/home/user/documents", true);
```

**Parameters**:
- `path`: Directory path (absolute or relative)
- `createParents`: If true, create parent directories as needed

**Returns**: `boolean` - true if created, false if already exists

**Throws**:
- `IllegalArgumentException` - If parent doesn't exist (when createParents=false)
- `IllegalArgumentException` - Invalid path

---

#### ls(path)
List contents of a directory.

```java
List<String> files = fs.ls("/home");
// Returns: ["file1.txt", "file2.txt", "subdir"]
```

**Returns**: `List<String>` - Names of files and directories

**Throws**:
- `IllegalArgumentException` - If path doesn't exist
- `SecurityException` - If no read permission

---

#### lsDetailed(path)
List contents with detailed metadata.

```java
List<FileStat> stats = fs.lsDetailed("/home");
for (FileStat stat : stats) {
    System.out.println(stat.toCompactString());
}
// Output:
// -rw-r--r--     1024 Dec 25 10:30 file1.txt
// drwxr-xr-x        0 Dec 25 10:31 subdir/
```

**Returns**: `List<FileStat>` - Detailed file statistics

---

### File Operations

#### createFile(path)
Create a new empty file.

```java
fs.createFile("/home/test.txt");
```

**Returns**: `boolean` - true if created, false if already exists

**Throws**:
- `IllegalArgumentException` - If parent directory doesn't exist
- `SecurityException` - If no write permission on parent

---

#### read(path)
Read entire file content.

```java
String content = fs.read("/home/test.txt");
```

**Returns**: `String` - File content

**Throws**:
- `IllegalArgumentException` - If file doesn't exist or is a directory
- `SecurityException` - If no read permission

---

#### write(path, content, append)
Write content to a file.

```java
// Overwrite
fs.write("/home/test.txt", "Hello, World!", false);

// Append
fs.write("/home/test.txt", "\nNew line", true);

// Convenience methods
fs.overwrite("/home/test.txt", "New content");
fs.append("/home/test.txt", "More content");
```

**Parameters**:
- `path`: File path
- `content`: Content to write
- `append`: If true, append; if false, overwrite

**Throws**:
- `IllegalArgumentException` - If file doesn't exist or is a directory
- `SecurityException` - If no write permission

---

### Move/Rename Operations

#### move(sourcePath, destPath)
Move or rename a file/directory atomically.

```java
// Move to different directory
fs.move("/home/file.txt", "/backup/file.txt");

// Rename in same directory
fs.move("/home/old.txt", "/home/new.txt");
```

**Atomic**: Yes - uses consistent lock ordering

**Throws**:
- `IllegalArgumentException` - If source doesn't exist
- `IllegalArgumentException` - If destination already exists
- `IllegalArgumentException` - If moving directory into itself
- `IllegalArgumentException` - Cannot move root

---

#### rename(path, newName)
Rename a file/directory (convenience method).

```java
fs.rename("/home/old.txt", "new.txt");
// Equivalent to: fs.move("/home/old.txt", "/home/new.txt")
```

---

### Delete Operations

#### delete(path, recursive)
Delete a file or directory.

```java
// Delete file
fs.delete("/home/file.txt");

// Delete empty directory
fs.delete("/home/emptydir");

// Delete non-empty directory (recursive)
fs.delete("/home/fulldir", true);
```

**Parameters**:
- `path`: Path to delete
- `recursive`: If true, delete non-empty directories

**Returns**: `boolean` - true if deleted, false if not found

**Throws**:
- `IllegalArgumentException` - If directory not empty (when recursive=false)
- `IllegalArgumentException` - Cannot delete root

---

### Metadata Operations

#### stat(path)
Get detailed file/directory statistics.

```java
FileStat stat = fs.stat("/home/test.txt");
System.out.println(stat);

// Output:
// File: test.txt
//   Type: file
//   Size: 1024 bytes
//   Permissions: rw-r--r-- (0644)
//   Created: 2024-12-25 10:30:00
//   Modified: 2024-12-25 10:35:00
//   Path: /home/test.txt
```

**Returns**: `FileStat` - File statistics object

---

#### getSize(path)
Get total size of file or directory tree.

```java
long size = fs.getSize("/home");
// For directories, recursively calculates total size
```

**Returns**: `long` - Size in bytes

---

### Permission Operations

#### setPermission(path, permission)
Set file/directory permissions.

```java
Permission perm = new Permission(0755);
fs.setPermission("/home/script.sh", perm);
```

---

#### getPermission(path)
Get file/directory permissions.

```java
Permission perm = fs.getPermission("/home/file.txt");
System.out.println(perm);  // Output: rw-r--r--
```

---

### Navigation Operations

#### changeDirectory(path)
Change current working directory.

```java
fs.changeDirectory("/home/user");
System.out.println(fs.getCurrentWorkingDirectory());
// Output: /home/user

fs.changeDirectory("..");
System.out.println(fs.getCurrentWorkingDirectory());
// Output: /home
```

---

#### getCurrentWorkingDirectory()
Get current working directory.

```java
String cwd = fs.getCurrentWorkingDirectory();
```

---

### Utility Operations

#### exists(path)
Check if path exists.

```java
if (fs.exists("/home/file.txt")) {
    // File exists
}
```

---

#### isDirectory(path)
Check if path is a directory.

```java
if (fs.isDirectory("/home")) {
    // It's a directory
}
```

---

#### isFile(path)
Check if path is a file.

```java
if (fs.isFile("/home/test.txt")) {
    // It's a file
}
```

---

#### tree(path)
Display directory tree structure.

```java
System.out.println(fs.tree("/home"));

// Output:
// └── home/
//     ├── user/
//     │   ├── documents/
//     │   │   └── file.txt
//     │   └── test.txt
//     └── file2.txt
```

## 💡 Examples

### Example 1: Basic File Operations

```java
FileSystem fs = new FileSystem();

// Create directory structure
fs.mkdir("/home/user/documents", true);

// Create and write to file
fs.createFile("/home/user/notes.txt");
fs.write("/home/user/notes.txt", "Meeting notes:\n");
fs.append("/home/user/notes.txt", "- Discuss project timeline\n");
fs.append("/home/user/notes.txt", "- Review budget\n");

// Read file
String content = fs.read("/home/user/notes.txt");
System.out.println(content);

// List directory
List<String> files = fs.ls("/home/user");
System.out.println("Files: " + files);
```

### Example 2: Working with Permissions

```java
FileSystem fs = new FileSystem();

// Create file with default permissions
fs.mkdir("/secure", true);
fs.createFile("/secure/secret.txt");
fs.write("/secure/secret.txt", "Confidential data");

// Check current permissions
Permission perm = fs.getPermission("/secure/secret.txt");
System.out.println("Current: " + perm);  // rw-r--r--

// Make file read-only
Permission readOnly = new Permission(0444);
fs.setPermission("/secure/secret.txt", readOnly);

// Try to write (will fail)
try {
    fs.write("/secure/secret.txt", "New data");
} catch (SecurityException e) {
    System.out.println("Write denied: " + e.getMessage());
}
```

### Example 3: Path Resolution

```java
FileSystem fs = new FileSystem();

// Create structure
fs.mkdir("/home/user/projects/java", true);
fs.createFile("/home/user/projects/java/Main.java");

// Navigate with relative paths
fs.changeDirectory("/home/user");
fs.createFile("./projects/java/Utils.java");

// Use .. to go up
fs.createFile("./projects/../README.md");

// Verify
System.out.println(fs.exists("/home/user/README.md"));  // true
System.out.println(fs.exists("/home/user/projects/java/Utils.java"));  // true
```

### Example 4: Concurrent Access

```java
FileSystem fs = new FileSystem();
fs.mkdir("/shared", true);

// Multiple threads writing to different files
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 10; i++) {
    final int threadId = i;
    executor.submit(() -> {
        String file = "/shared/file" + threadId + ".txt";
        fs.createFile(file);
        fs.write(file, "Data from thread " + threadId);
    });
}

executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

// All files created successfully
System.out.println("Files created: " + fs.ls("/shared").size());
```

### Example 5: Tree Traversal with Visitor

```java
FileSystem fs = new FileSystem();

// Create structure
fs.mkdir("/project/src/main/java", true);
fs.mkdir("/project/src/test/java", true);
fs.createFile("/project/src/main/java/App.java");
fs.createFile("/project/src/test/java/AppTest.java");
fs.write("/project/src/main/java/App.java", "public class App {}");

// Calculate total size
long totalSize = fs.getSize("/project");
System.out.println("Total size: " + totalSize + " bytes");

// Display tree
System.out.println(fs.tree("/project"));
```

## ⚡ Performance Analysis

### Time Complexity

| Operation | Average Case | Worst Case | Notes |
|-----------|-------------|------------|-------|
| **mkdir** | O(d) | O(d) | d = depth of path |
| **createFile** | O(d) | O(d) | d = depth of path |
| **ls** | O(n) | O(n) | n = number of children |
| **stat** | O(d) | O(d) | d = depth of path |
| **read** | O(d + c) | O(d + c) | c = content size |
| **write** | O(d + c) | O(d + c) | c = content size |
| **move** | O(d₁ + d₂) | O(d₁ + d₂) | d₁, d₂ = depths of source/dest |
| **delete** | O(d + n) | O(d + n) | n = nodes in subtree (recursive) |
| **getSize** | O(n) | O(n) | n = nodes in subtree |
| **exists** | O(d) | O(d) | d = depth of path |

**Path Navigation**: O(d) where d is the depth
- Each component requires one HashMap lookup: O(1)
- Total: O(d) for d components

**Directory Lookup**: O(1)
- HashMap-based children storage
- Constant time access by name

### Space Complexity

| Component | Space | Notes |
|-----------|-------|-------|
| **Overall** | O(n) | n = total nodes |
| **Directory** | O(c) | c = number of children |
| **File** | O(s) | s = content size |
| **Locks** | O(p) | p = number of paths with active locks |
| **Path Cache** | O(1) | No caching implemented |

### Optimization Opportunities

1. **Path Caching**: Cache frequently accessed paths
   ```java
   Map<String, FileSystemNode> pathCache = new LRUCache<>(1000);
   ```

2. **Lazy Size Calculation**: Cache directory sizes, invalidate on change
   ```java
   class Directory {
       private Long cachedSize = null;

       public long getSize() {
           if (cachedSize == null) {
               cachedSize = calculateSize();
           }
           return cachedSize;
       }
   }
   ```

3. **Lock Striping**: Reduce lock contention
   ```java
   // Instead of per-path locks, use lock striping
   ReentrantReadWriteLock[] locks = new ReentrantReadWriteLock[256];
   int lockIndex = path.hashCode() % 256;
   ```

4. **Batch Operations**: Reduce lock overhead
   ```java
   fs.batchWrite(List<WriteOperation> operations);
   ```

## 🎨 Detailed Diagrams

### File System Tree Structure

```
                    Root (/)
                      │
        ┌─────────────┼─────────────┐
        │             │             │
      home/         etc/          tmp/
        │
    ┌───┴───┐
    │       │
  user/   shared/
    │
┌───┼───┐
│   │   │
docs/ projects/ notes.txt
│
├── report.pdf
└── memo.txt
```

### Operation Flow: mkdir("/home/user/docs", true)

```
┌─────────────────────────────────────────────────────────┐
│ 1. Resolve Path                                         │
│    Input: "/home/user/docs"                             │
│    Output: Normalized absolute path                     │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 2. Acquire Write Lock                                   │
│    Lock: "/home/user/docs"                              │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 3. Check if Path Exists                                 │
│    Navigate: / → home → user → docs                     │
│    Result: Not found                                    │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 4. Create Parent Directories (recursive flag = true)    │
│    Create: /home (if not exists)                        │
│    Create: /home/user (if not exists)                   │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 5. Create Target Directory                              │
│    parent = navigate("/home/user")                      │
│    newDir = new Directory("docs")                       │
│    parent.addChild(newDir)                              │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 6. Release Lock                                         │
│    Unlock: "/home/user/docs"                            │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
           Success
```

### Operation Flow: move("/home/file.txt", "/backup/file.txt")

```
┌─────────────────────────────────────────────────────────┐
│ 1. Resolve Paths                                        │
│    Source: "/home/file.txt"                             │
│    Dest: "/backup/file.txt"                             │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 2. Validate Operation                                   │
│    ✓ Source exists                                      │
│    ✓ Dest doesn't exist                                 │
│    ✓ Not moving into itself                             │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 3. Acquire Locks (sorted order)                         │
│    Sort: ["/backup/file.txt", "/home/file.txt"]         │
│    Lock: "/backup/file.txt"                             │
│    Lock: "/home/file.txt"                               │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 4. Atomic Move Operation                                │
│    sourceParent.removeChild("file.txt")                 │
│    node.setName("file.txt")                             │
│    destParent.addChild(node)                            │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│ 5. Release Locks (reverse order)                        │
│    Unlock: "/home/file.txt"                             │
│    Unlock: "/backup/file.txt"                           │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
           Success
```

### Concurrent Access Scenario

```
Time →

Thread 1: read(/home/file1.txt)
          │
          ├─ Acquire Read Lock (/home/file1.txt)
          │  │
          │  ├─ Read content
          │  │
          │  └─ Release Read Lock
          │
          └─ Done

Thread 2: read(/home/file1.txt)
          │
          ├─ Acquire Read Lock (/home/file1.txt)  ← Concurrent!
          │  │
          │  ├─ Read content
          │  │
          │  └─ Release Read Lock
          │
          └─ Done

Thread 3: write(/home/file1.txt)
          │
          ├─ Acquire Write Lock (/home/file1.txt)  ← Waits for readers
          │  │
          │  ├─ Write content
          │  │
          │  └─ Release Write Lock
          │
          └─ Done

Thread 4: read(/home/file2.txt)
          │
          ├─ Acquire Read Lock (/home/file2.txt)  ← Different path, concurrent!
          │  │
          │  ├─ Read content
          │  │
          │  └─ Release Read Lock
          │
          └─ Done
```

## 🔍 Design Decisions

### 1. Why HashMap for Directory Children?

**Decision**: Use `ConcurrentHashMap<String, FileSystemNode>`

**Alternatives Considered**:
- TreeMap: O(log n) lookup, sorted order
- ArrayList: O(n) lookup, insertion order
- LinkedHashMap: O(1) lookup, insertion order

**Rationale**:
- O(1) lookup is critical for path navigation
- ConcurrentHashMap provides thread-safe iteration
- No need for sorted order in most use cases
- Memory overhead acceptable for performance gain

### 2. Why Doubly-Linked Parent References?

**Decision**: Each node has a `parent` reference

**Alternatives Considered**:
- No parent reference: Navigate from root each time
- Path string: Store full path in each node

**Rationale**:
- Enables efficient `getFullPath()` traversal
- Simplifies move operations (update parent reference)
- Small memory overhead (one reference per node)
- Avoids path string synchronization issues

### 3. Why Per-Path Locks Instead of Global Lock?

**Decision**: Fine-grained per-path read-write locks

**Alternatives Considered**:
- Global lock: Simple but poor concurrency
- Per-directory lock: Better but still coarse
- Lock-free: Complex, hard to guarantee consistency

**Rationale**:
- Maximizes concurrency for independent operations
- Read-write locks allow multiple readers
- Acceptable memory overhead
- Deadlock prevention via sorted acquisition

### 4. Why StringBuilder for File Content?

**Decision**: Use `StringBuilder` instead of `String`

**Alternatives Considered**:
- String: Immutable, creates new object on append
- byte[]: More memory efficient, harder to use
- Custom buffer: More control, more complexity

**Rationale**:
- Efficient append operations (common use case)
- Mutable for in-place updates
- Easy conversion to String for reading
- Acceptable for in-memory file system

### 5. Why Visitor Pattern for Traversal?

**Decision**: Implement visitor pattern for tree operations

**Alternatives Considered**:
- Methods in node classes: Violates SRP
- Utility classes with instanceof: Not extensible
- Callbacks: Less type-safe

**Rationale**:
- Separation of concerns (traversal vs. node logic)
- Easy to add new operations without modifying nodes
- Type-safe, compile-time checked
- Standard pattern for tree structures

## 🚀 Future Enhancements

### 1. Symbolic Links
```java
class SymbolicLink extends FileSystemNode {
    private String targetPath;

    public FileSystemNode resolve() {
        return navigateToNode(targetPath);
    }
}
```

### 2. File Watching
```java
interface FileWatcher {
    void onFileCreated(String path);
    void onFileModified(String path);
    void onFileDeleted(String path);
}

fs.watch("/home", watcher);
```

### 3. Quota Management
```java
class Directory {
    private long quotaBytes;

    public void setQuota(long bytes) {
        this.quotaBytes = bytes;
    }

    public boolean hasQuota() {
        return getSize() < quotaBytes;
    }
}
```

### 4. Journaling
```java
interface FileSystemJournal {
    void logOperation(Operation op);
    void replay();
    void checkpoint();
}
```

### 5. Persistence
```java
interface FileSystemPersistence {
    void save(OutputStream out);
    void load(InputStream in);
}
```

## 📊 Testing Strategy

### Unit Tests
- ✅ Individual operations (mkdir, create, read, write, etc.)
- ✅ Path resolution and normalization
- ✅ Permission checks
- ✅ Edge cases (root, empty paths, invalid characters)

### Integration Tests
- ✅ Complex workflows (create structure, move files, delete)
- ✅ Concurrent operations
- ✅ Deadlock prevention

### Performance Tests
- Load testing with large directory trees
- Concurrent access benchmarks
- Memory usage profiling

### Security Tests
- Path traversal prevention
- Permission enforcement
- Invalid input handling

## 📖 References

- **Design Patterns**: Gang of Four (Composite, Visitor, Template Method)
- **Concurrency**: Java Concurrency in Practice (Brian Goetz)
- **File Systems**: Operating System Concepts (Silberschatz)
- **Unix Permissions**: POSIX standards

## 🎓 Key Takeaways

1. **Composite Pattern** is ideal for hierarchical structures like file systems
2. **Visitor Pattern** enables extensible tree operations without modifying nodes
3. **Per-path locking** with consistent ordering prevents deadlocks
4. **Read-write locks** maximize concurrency for read-heavy workloads
5. **Path normalization** is critical for security and correctness
6. **HashMap-based directories** provide O(1) lookup performance
7. **Atomic operations** require careful lock management
8. **Permission checks** should be enforced at the node level

---

**Author**: File System LLD Implementation
**Version**: 1.0
**Last Updated**: 2024

