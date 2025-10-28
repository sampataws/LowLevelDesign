# File System - Terminal Usage Guide

This guide shows you how to use the File System implementation through the interactive terminal shell, which supports Linux-style commands.

---

## 🚀 Quick Start

### 1. Start the Interactive Shell

```bash
cd file-system-lld
mvn exec:java -Dexec.mainClass="com.filesystem.FileSystemDemo"
```

You'll see:
```
============================================================
File System Demo - Interactive Shell
============================================================
Type 'help' for available commands, 'exit' to quit

/ $
```

### 2. Get Help

```bash
/ $ help
```

---

## 📖 Command Reference

### Directory Operations

#### `mkdir` - Create Directory

**Syntax:**
```bash
mkdir <path>           # Create single directory
mkdir <path> -p        # Create with parent directories
```

**Examples:**
```bash
/ $ mkdir /home
Directory created: /home

/ $ mkdir /home/user/documents -p
Directory created: /home/user/documents

/ $ mkdir projects
Directory created: /projects
```

---

#### `ls` - List Directory Contents

**Syntax:**
```bash
ls [path]              # List files and directories
```

**Examples:**
```bash
/ $ ls
home
projects

/ $ ls /home/user
documents
notes.txt

/ $ ls                 # List current directory
file1.txt
file2.txt
```

---

#### `ll` - List with Details

**Syntax:**
```bash
ll [path]              # List with permissions, size, date
```

**Examples:**
```bash
/ $ ll /home
drwxr-xr-x        0 Oct 27 20:15 user/
-rw-r--r--     1024 Oct 27 20:16 notes.txt

/ $ ll
drwxr-xr-x        0 Oct 27 20:15 home/
drwxr-xr-x        0 Oct 27 20:15 projects/
```

**Output Format:**
```
[permissions] [size] [date] [time] [name]
```

---

#### `cd` - Change Directory

**Syntax:**
```bash
cd <path>              # Change to directory
cd ..                  # Go to parent directory
cd /                   # Go to root
cd                     # Go to root (default)
```

**Examples:**
```bash
/ $ cd /home
/home $ cd user
/home/user $ cd ..
/home $ cd /
/ $
```

---

#### `pwd` - Print Working Directory

**Syntax:**
```bash
pwd                    # Show current directory
```

**Examples:**
```bash
/home/user $ pwd
/home/user

/ $ pwd
/
```

---

#### `tree` - Display Directory Tree

**Syntax:**
```bash
tree [path]            # Show directory tree structure
```

**Examples:**
```bash
/ $ tree /home
└── home/
    ├── user/
    │   ├── documents/
    │   │   └── report.pdf
    │   └── notes.txt
    └── shared/
        └── data.txt

/ $ tree               # Tree of current directory
```

---

### File Operations

#### `touch` - Create Empty File

**Syntax:**
```bash
touch <path>           # Create new empty file
```

**Examples:**
```bash
/ $ touch /home/test.txt
File created: /home/test.txt

/home $ touch notes.txt
File created: notes.txt

/ $ touch file1.txt file2.txt file3.txt
# Note: Only creates first file (single argument)
```

---

#### `cat` - Display File Content

**Syntax:**
```bash
cat <path>             # Display entire file content
```

**Examples:**
```bash
/ $ cat /home/notes.txt
Hello, World!
This is a test file.

/home $ cat notes.txt
Meeting notes:
- Discuss project timeline
- Review budget
```

---

#### `echo` - Write to File

**Syntax:**
```bash
echo "text" > file     # Overwrite file with text
echo "text" >> file    # Append text to file
```

**Examples:**
```bash
/ $ echo "Hello, World!" > /home/test.txt
Content written to: /home/test.txt

/ $ echo "New line" >> /home/test.txt
Content written to: /home/test.txt

/ $ cat /home/test.txt
Hello, World!
New line

# Without redirection, just prints
/ $ echo "Hello"
Hello
```

---

#### `write` - Write to File (Overwrite)

**Syntax:**
```bash
write <path> <content>  # Overwrite file with content
```

**Examples:**
```bash
/ $ write /home/test.txt "New content"
Content written to: /home/test.txt

/ $ write notes.txt "Meeting at 3pm"
Content written to: notes.txt
```

---

#### `append` - Append to File

**Syntax:**
```bash
append <path> <content>  # Append content to file
```

**Examples:**
```bash
/ $ append /home/test.txt "Additional line"
Content appended to: /home/test.txt

/ $ append notes.txt "- Action item"
Content appended to: notes.txt
```

---

### Move/Rename Operations

#### `mv` - Move or Rename

**Syntax:**
```bash
mv <source> <destination>  # Move or rename file/directory
```

**Examples:**
```bash
# Rename file in same directory
/ $ mv /home/old.txt /home/new.txt
Moved: /home/old.txt -> /home/new.txt

# Move file to different directory
/ $ mv /home/file.txt /backup/file.txt
Moved: /home/file.txt -> /backup/file.txt

# Move directory
/ $ mv /home/docs /home/documents
Moved: /home/docs -> /home/documents

# Relative paths
/home $ mv old.txt new.txt
Moved: old.txt -> new.txt
```

---

### Delete Operations

#### `rm` - Remove File or Directory

**Syntax:**
```bash
rm <path>              # Delete file or empty directory
rm <path> -r           # Delete directory recursively
```

**Examples:**
```bash
# Delete file
/ $ rm /home/test.txt
Deleted: /home/test.txt

# Delete empty directory
/ $ rm /home/emptydir
Deleted: /home/emptydir

# Delete non-empty directory (recursive)
/ $ rm /home/documents -r
Deleted: /home/documents

# Delete with flag at end
/ $ rm -r /home/projects
Deleted: /home/projects
```

---

### Information Commands

#### `stat` - Show File Statistics

**Syntax:**
```bash
stat <path>            # Show detailed file/directory info
```

**Examples:**
```bash
/ $ stat /home/test.txt
File: test.txt
  Type: file
  Size: 1024 bytes
  Permissions: rw-r--r-- (0644)
  Created: 2024-10-27 20:15:30
  Modified: 2024-10-27 20:16:45
  Path: /home/test.txt

/ $ stat /home
File: home
  Type: directory
  Size: 0 bytes
  Permissions: rwxr-xr-x (0755)
  Created: 2024-10-27 20:15:00
  Modified: 2024-10-27 20:15:00
  Path: /home
```

---

#### `du` - Disk Usage

**Syntax:**
```bash
du [path]              # Show size of file/directory
```

**Examples:**
```bash
/ $ du /home
4096 bytes	/home

/ $ du /home/test.txt
1024 bytes	/home/test.txt

/home $ du
4096 bytes	/home
```

---

### Permission Commands

#### `chmod` - Change Permissions

**Syntax:**
```bash
chmod <mode> <path>    # Change file/directory permissions
```

**Mode Format:** Octal notation (e.g., 755, 644)

**Examples:**
```bash
# Make file read-only
/ $ chmod 444 /home/test.txt
Permissions changed: /home/test.txt -> r--r--r--

# Make file executable
/ $ chmod 755 /home/script.sh
Permissions changed: /home/script.sh -> rwxr-xr-x

# Standard file permissions
/ $ chmod 644 /home/file.txt
Permissions changed: /home/file.txt -> rw-r--r--

# Directory permissions
/ $ chmod 755 /home/documents
Permissions changed: /home/documents -> rwxr-xr-x
```

**Common Permission Modes:**
```
644 (rw-r--r--) - Standard file (owner can write, others read)
755 (rwxr-xr-x) - Standard directory or executable
777 (rwxrwxrwx) - Full permissions for everyone
400 (r--------) - Read-only for owner
600 (rw-------) - Read-write for owner only
```

---

## 🎯 Complete Usage Examples

### Example 1: Basic File Management

```bash
/ $ mkdir /home -p
Directory created: /home

/ $ cd /home
/home $ touch notes.txt
File created: notes.txt

/home $ echo "Meeting notes" > notes.txt
Content written to: notes.txt

/home $ echo "- Item 1" >> notes.txt
Content written to: notes.txt

/home $ cat notes.txt
Meeting notes
- Item 1

/home $ ls
notes.txt

/home $ stat notes.txt
File: notes.txt
  Type: file
  Size: 24 bytes
  Permissions: rw-r--r-- (0644)
  ...
```

### Example 2: Directory Structure

```bash
/ $ mkdir /projects/java/src -p
Directory created: /projects/java/src

/ $ mkdir /projects/java/test -p
Directory created: /projects/java/test

/ $ cd /projects/java
/projects/java $ touch src/Main.java
File created: src/Main.java

/projects/java $ touch test/MainTest.java
File created: test/MainTest.java

/projects/java $ tree
└── java/
    ├── src/
    │   └── Main.java
    └── test/
        └── MainTest.java

/projects/java $ ls
src
test
```

### Example 3: Moving and Renaming

```bash
/ $ mkdir /home/documents -p
Directory created: /home/documents

/ $ touch /home/draft.txt
File created: /home/draft.txt

/ $ echo "Draft content" > /home/draft.txt
Content written to: /home/draft.txt

# Rename file
/ $ mv /home/draft.txt /home/final.txt
Moved: /home/draft.txt -> /home/final.txt

# Move to directory
/ $ mv /home/final.txt /home/documents/report.txt
Moved: /home/final.txt -> /home/documents/report.txt

/ $ ls /home/documents
report.txt
```

### Example 4: Working with Relative Paths

```bash
/ $ mkdir /home/user/projects -p
Directory created: /home/user/projects

/ $ cd /home/user
/home/user $ mkdir documents
Directory created: documents

/home/user $ touch ./projects/file.txt
File created: ./projects/file.txt

/home/user $ cd projects
/home/user/projects $ touch ../documents/notes.txt
File created: ../documents/notes.txt

/home/user/projects $ cd ..
/home/user $ ls
documents
projects

/home/user $ ls documents
notes.txt
```

### Example 5: Permissions

```bash
/ $ mkdir /secure -p
Directory created: /secure

/ $ touch /secure/secret.txt
File created: /secure/secret.txt

/ $ echo "Confidential data" > /secure/secret.txt
Content written to: /secure/secret.txt

# Make read-only
/ $ chmod 444 /secure/secret.txt
Permissions changed: /secure/secret.txt -> r--r--r--

# Try to write (will fail with permission error)
/ $ echo "New data" > /secure/secret.txt
Error: Permission denied

# Make writable again
/ $ chmod 644 /secure/secret.txt
Permissions changed: /secure/secret.txt -> rw-r--r--

/ $ echo "New data" > /secure/secret.txt
Content written to: /secure/secret.txt
```

---

## 🔧 Tips and Tricks

### 1. Use Tab Completion (Not Implemented)
Currently, you need to type full paths. Future enhancement could add tab completion.

### 2. Path Shortcuts
```bash
.   # Current directory
..  # Parent directory
/   # Root directory
```

### 3. Combine Commands
```bash
/ $ mkdir /home/user -p
/ $ cd /home/user
/home/user $ touch file1.txt file2.txt file3.txt
/home/user $ ls
```

### 4. Check Before Delete
```bash
/ $ ls /home/documents
file1.txt
file2.txt

/ $ rm /home/documents -r
Deleted: /home/documents
```

### 5. Use `tree` for Overview
```bash
/ $ tree
└── /
    ├── home/
    │   ├── user/
    │   │   └── notes.txt
    │   └── shared/
    └── projects/
```

---

## ⚠️ Important Notes

1. **No Undo**: Deleted files cannot be recovered
2. **Case Sensitive**: `/Home` and `/home` are different
3. **No Wildcards**: `rm *.txt` not supported (yet)
4. **Single Arguments**: Most commands take one path at a time
5. **Permissions**: Some operations require appropriate permissions

---

## 🚪 Exit the Shell

```bash
/ $ exit
Goodbye!
```

Or:
```bash
/ $ quit
Goodbye!
```

---

## 📚 Additional Resources

- **README.md** - Quick start guide
- **FILE_SYSTEM_DESIGN.md** - Comprehensive design documentation
- **IMPLEMENTATION_SUMMARY.md** - Project summary

---

**Enjoy exploring the file system!** 🎉

