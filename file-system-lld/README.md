# File System - Low Level Design

A comprehensive in-memory file system implementation in Java with thread-safe operations, Unix-style permissions, and support for standard file operations.

## 🌟 Features

- ✅ **Complete File Operations**: mkdir, ls, stat, create, read, write, move, rename, delete
- ✅ **Path Resolution**: Absolute and relative paths with `.` and `..` support
- ✅ **Permission System**: Unix-style permissions (rwx for owner/group/others)
- ✅ **Metadata Tracking**: Size, creation time, modification time
- ✅ **Thread Safety**: Per-path locks with deadlock prevention
- ✅ **Atomic Operations**: Atomic rename/move within directory tree
- ✅ **Efficient Lookups**: HashMap-based directory structure (O(1) access)

## 🏗️ Architecture

### Design Patterns Used

1. **Composite Pattern**: Unified interface for files and directories
2. **Visitor Pattern**: Extensible tree traversal operations
3. **Template Method**: Lock acquisition patterns
4. **Strategy Pattern**: Different write modes (append/overwrite)

### Core Components

```
FileSystem
├── Core
│   ├── FileSystemNode (abstract)
│   ├── Directory (composite)
│   ├── File (leaf)
│   ├── Permission
│   ├── FileStat
│   └── PathResolver
├── Lock
│   └── PathLockManager
└── Visitor
    ├── NodeVisitor (interface)
    ├── SizeCalculatorVisitor
    └── PermissionUpdaterVisitor
```

## 🚀 Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Build

```bash
cd file-system-lld
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Run Interactive Demo

```bash
mvn exec:java -Dexec.mainClass="com.filesystem.FileSystemDemo"
```

## 💻 Usage Examples

### Basic Operations

```java
FileSystem fs = new FileSystem();

// Create directory structure
fs.mkdir("/home/user/documents", true);

// Create and write to file
fs.createFile("/home/user/notes.txt");
fs.write("/home/user/notes.txt", "Hello, World!");

// Read file
String content = fs.read("/home/user/notes.txt");
System.out.println(content);  // Output: Hello, World!

// List directory
List<String> files = fs.ls("/home/user");
System.out.println(files);  // Output: [notes.txt, documents]
```

### Working with Permissions

```java
// Create file with default permissions
fs.createFile("/secure/secret.txt");

// Set read-only permissions
Permission readOnly = new Permission(0444);
fs.setPermission("/secure/secret.txt", readOnly);

// Get file statistics
FileStat stat = fs.stat("/secure/secret.txt");
System.out.println(stat);
```

### Path Resolution

```java
// Navigate with relative paths
fs.changeDirectory("/home/user");
fs.createFile("./documents/report.pdf");

// Use .. to go up
fs.createFile("../shared/data.txt");

// Verify
System.out.println(fs.exists("/home/shared/data.txt"));  // true
```

### Move and Rename

```java
// Move file to different directory
fs.move("/home/file.txt", "/backup/file.txt");

// Rename file in same directory
fs.rename("/home/old.txt", "new.txt");
```

### Delete Operations

```java
// Delete file
fs.delete("/home/file.txt");

// Delete empty directory
fs.delete("/home/emptydir");

// Delete non-empty directory (recursive)
fs.delete("/home/fulldir", true);
```

## 🎮 Interactive Demo

The project includes an interactive shell for exploring the file system:

```bash
$ mvn exec:java -Dexec.mainClass="com.filesystem.FileSystemDemo"

File System Demo - Interactive Shell
Type 'help' for available commands, 'exit' to quit

/ $ mkdir /home
Directory created: /home

/ $ cd /home
/home $ touch test.txt
File created: test.txt

/home $ echo "Hello, World!" > test.txt
Content written to: test.txt

/home $ cat test.txt
Hello, World!

/home $ ls
test.txt

/home $ stat test.txt
File: test.txt
  Type: file
  Size: 13 bytes
  Permissions: rw-r--r-- (0644)
  Created: 2024-12-25 10:30:00
  Modified: 2024-12-25 10:30:00
  Path: /home/test.txt
```

### Available Commands

| Command | Description | Example |
|---------|-------------|---------|
| `mkdir <path> [-p]` | Create directory | `mkdir /home/user -p` |
| `touch <path>` | Create file | `touch /home/file.txt` |
| `ls [path]` | List directory | `ls /home` |
| `ll [path]` | List with details | `ll /home` |
| `cat <path>` | Display file content | `cat /home/file.txt` |
| `echo "text" > file` | Write to file | `echo "Hello" > file.txt` |
| `echo "text" >> file` | Append to file | `echo "World" >> file.txt` |
| `mv <src> <dest>` | Move/rename | `mv old.txt new.txt` |
| `rm <path> [-r]` | Delete | `rm /home/dir -r` |
| `cd <path>` | Change directory | `cd /home` |
| `pwd` | Print working directory | `pwd` |
| `stat <path>` | Show statistics | `stat file.txt` |
| `chmod <mode> <path>` | Change permissions | `chmod 755 file.txt` |
| `tree [path]` | Display tree | `tree /home` |
| `du [path]` | Show disk usage | `du /home` |

## 🧪 Testing

The project includes comprehensive tests:

- **Unit Tests**: Individual operation testing
- **Integration Tests**: Complex workflow testing
- **Concurrency Tests**: Thread-safety verification
- **Edge Case Tests**: Boundary condition testing

Run specific test class:
```bash
mvn test -Dtest=FileSystemTest
```

Run with coverage:
```bash
mvn clean test jacoco:report
```

## 📊 Performance

### Time Complexity

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| mkdir | O(d) | d = path depth |
| createFile | O(d) | d = path depth |
| ls | O(n) | n = children count |
| read/write | O(d + c) | c = content size |
| move | O(d₁ + d₂) | depths of source/dest |
| delete | O(d + n) | n = nodes in subtree |

### Space Complexity

- Overall: O(n) where n = total nodes
- Directory: O(c) where c = number of children
- File: O(s) where s = content size

## 🔒 Thread Safety

- **Per-path read-write locks** for fine-grained concurrency
- **Consistent lock ordering** to prevent deadlocks
- **Atomic operations** for move/rename
- **ConcurrentHashMap** for directory children

### Concurrency Example

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 100; i++) {
    final int id = i;
    executor.submit(() -> {
        fs.createFile("/shared/file" + id + ".txt");
        fs.write("/shared/file" + id + ".txt", "Data " + id);
    });
}

executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

// All 100 files created successfully
System.out.println("Files: " + fs.ls("/shared").size());  // 100
```

## 📚 Documentation

- **[Design Document](FILE_SYSTEM_DESIGN.md)**: Comprehensive design documentation (1234 lines)
- **[API Reference](FILE_SYSTEM_DESIGN.md#api-documentation)**: Detailed API documentation
- **[Terminal Usage Guide](TERMINAL_USAGE_GUIDE.md)**: Complete guide to using Linux-style commands
- **[Demo Script](DEMO_SCRIPT.md)**: Step-by-step interactive demo
- **[Implementation Summary](IMPLEMENTATION_SUMMARY.md)**: Project summary and test results
- **[Code Examples](FILE_SYSTEM_DESIGN.md#examples)**: Programmatic usage examples

## 🛠️ Project Structure

```
file-system-lld/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── filesystem/
│   │               ├── FileSystem.java
│   │               ├── FileSystemDemo.java
│   │               ├── core/
│   │               │   ├── FileSystemNode.java
│   │               │   ├── Directory.java
│   │               │   ├── File.java
│   │               │   ├── Permission.java
│   │               │   ├── FileStat.java
│   │               │   └── PathResolver.java
│   │               ├── lock/
│   │               │   └── PathLockManager.java
│   │               └── visitor/
│   │                   ├── NodeVisitor.java
│   │                   ├── SizeCalculatorVisitor.java
│   │                   └── PermissionUpdaterVisitor.java
│   └── test/
│       └── java/
│           └── com/
│               └── filesystem/
│                   └── FileSystemTest.java
├── FILE_SYSTEM_DESIGN.md
├── README.md
└── pom.xml
```

## 🔮 Future Enhancements

- [ ] Symbolic links support
- [ ] File watching/notification system
- [ ] Quota management
- [ ] Journaling for crash recovery
- [ ] Persistence (save/load from disk)
- [ ] Search functionality
- [ ] File compression
- [ ] Access control lists (ACL)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

Created as a low-level design exercise demonstrating:
- Object-oriented design principles
- Design patterns in practice
- Concurrent programming
- File system concepts

---

**Note**: This is an in-memory file system for educational purposes. For production use, consider using established file system libraries or frameworks.

