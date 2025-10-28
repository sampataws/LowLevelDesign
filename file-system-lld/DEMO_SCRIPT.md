# File System - Interactive Demo Script

This is a step-by-step demo script you can follow to explore the file system's capabilities.

---

## 🎬 Demo Script

### Step 1: Start the Shell

```bash
cd file-system-lld
mvn exec:java -Dexec.mainClass="com.filesystem.FileSystemDemo"
```

---

### Step 2: Basic Navigation

```bash
# Check where you are
/ $ pwd
/

# See what's in root (empty initially)
/ $ ls
```

---

### Step 3: Create Directory Structure

```bash
# Create a home directory
/ $ mkdir /home
Directory created: /home

# Create nested directories
/ $ mkdir /home/user/documents -p
Directory created: /home/user/documents

# Create more directories
/ $ mkdir /home/user/projects -p
Directory created: /home/user/projects

# Create a backup directory
/ $ mkdir /backup
Directory created: /backup

# View the structure
/ $ tree
└── /
    ├── backup/
    └── home/
        └── user/
            ├── documents/
            └── projects/
```

---

### Step 4: Navigate Directories

```bash
# Change to home directory
/ $ cd /home
/home $ pwd
/home

# Go to user directory
/home $ cd user
/home/user $ pwd
/home/user

# List contents
/home/user $ ls
documents
projects

# Go back to parent
/home/user $ cd ..
/home $ pwd
/home

# Go to root
/home $ cd /
/ $ pwd
/
```

---

### Step 5: Create and Edit Files

```bash
# Navigate to documents
/ $ cd /home/user/documents
/home/user/documents $ pwd
/home/user/documents

# Create a file
/home/user/documents $ touch notes.txt
File created: notes.txt

# Write content
/home/user/documents $ echo "Meeting Notes" > notes.txt
Content written to: notes.txt

# Append more content
/home/user/documents $ echo "- Discuss Q4 goals" >> notes.txt
Content written to: notes.txt

/home/user/documents $ echo "- Review budget" >> notes.txt
Content written to: notes.txt

/home/user/documents $ echo "- Plan team event" >> notes.txt
Content written to: notes.txt

# Read the file
/home/user/documents $ cat notes.txt
Meeting Notes
- Discuss Q4 goals
- Review budget
- Plan team event
```

---

### Step 6: Create More Files

```bash
# Create a report
/home/user/documents $ touch report.txt
File created: report.txt

/home/user/documents $ echo "Q4 Report" > report.txt
Content written to: report.txt

/home/user/documents $ echo "Revenue: $1M" >> report.txt
Content written to: report.txt

# Create a TODO list
/home/user/documents $ touch todo.txt
File created: todo.txt

/home/user/documents $ write todo.txt "TODO List"
Content written to: todo.txt

/home/user/documents $ append todo.txt "1. Review code"
Content appended to: todo.txt

/home/user/documents $ append todo.txt "2. Write tests"
Content appended to: todo.txt

# List all files
/home/user/documents $ ls
notes.txt
report.txt
todo.txt

# List with details
/home/user/documents $ ll
-rw-r--r--       67 Oct 27 20:30 notes.txt
-rw-r--r--       24 Oct 27 20:31 report.txt
-rw-r--r--       38 Oct 27 20:32 todo.txt
```

---

### Step 7: Working with Projects

```bash
# Go to projects directory
/home/user/documents $ cd ../projects
/home/user/projects $ pwd
/home/user/projects

# Create project structure
/home/user/projects $ mkdir java-app -p
Directory created: java-app

/home/user/projects $ cd java-app
/home/user/projects/java-app $ mkdir src test
Directory created: src

/home/user/projects/java-app $ mkdir test
Directory created: test

# Create source files
/home/user/projects/java-app $ touch src/Main.java
File created: src/Main.java

/home/user/projects/java-app $ echo "public class Main {}" > src/Main.java
Content written to: src/Main.java

/home/user/projects/java-app $ touch test/MainTest.java
File created: test/MainTest.java

# View structure
/home/user/projects/java-app $ tree
└── java-app/
    ├── src/
    │   └── Main.java
    └── test/
        └── MainTest.java

# Go back to projects
/home/user/projects/java-app $ cd ..
/home/user/projects $ ls
java-app
```

---

### Step 8: File Statistics

```bash
# Get detailed info about a file
/home/user/projects $ stat ../documents/notes.txt
File: notes.txt
  Type: file
  Size: 67 bytes
  Permissions: rw-r--r-- (0644)
  Created: 2024-10-27 20:30:15
  Modified: 2024-10-27 20:30:45
  Path: /home/user/documents/notes.txt

# Get directory info
/home/user/projects $ stat .
File: projects
  Type: directory
  Size: 0 bytes
  Permissions: rwxr-xr-x (0755)
  Created: 2024-10-27 20:25:00
  Modified: 2024-10-27 20:35:00
  Path: /home/user/projects

# Check disk usage
/home/user/projects $ du .
129 bytes	/home/user/projects

/home/user/projects $ du /home
258 bytes	/home
```

---

### Step 9: Move and Rename Files

```bash
# Go to documents
/home/user/projects $ cd ../documents
/home/user/documents $ ls
notes.txt
report.txt
todo.txt

# Rename a file
/home/user/documents $ mv todo.txt tasks.txt
Moved: todo.txt -> tasks.txt

/home/user/documents $ ls
notes.txt
report.txt
tasks.txt

# Move file to backup
/home/user/documents $ mv report.txt /backup/q4-report.txt
Moved: report.txt -> /backup/q4-report.txt

/home/user/documents $ ls
notes.txt
tasks.txt

# Check backup
/home/user/documents $ ls /backup
q4-report.txt
```

---

### Step 10: Copy Using Read/Write

```bash
# Read a file
/home/user/documents $ cat notes.txt
Meeting Notes
- Discuss Q4 goals
- Review budget
- Plan team event

# "Copy" by reading and writing to new location
# (Note: No direct copy command, but you can simulate it)
/home/user/documents $ cat notes.txt
# Copy the output and write to new file
/home/user/documents $ echo "Meeting Notes" > /backup/notes-backup.txt
Content written to: /backup/notes-backup.txt

# Verify
/home/user/documents $ ls /backup
notes-backup.txt
q4-report.txt
```

---

### Step 11: Permissions

```bash
# Create a secure file
/home/user/documents $ cd /home/user
/home/user $ mkdir secure
Directory created: secure

/home/user $ touch secure/password.txt
File created: secure/password.txt

/home/user $ echo "secret123" > secure/password.txt
Content written to: secure/password.txt

# Check current permissions
/home/user $ stat secure/password.txt
File: password.txt
  Type: file
  Size: 10 bytes
  Permissions: rw-r--r-- (0644)
  ...

# Make it read-only
/home/user $ chmod 444 secure/password.txt
Permissions changed: secure/password.txt -> r--r--r--

# Try to write (will fail)
/home/user $ echo "newpass" > secure/password.txt
Error: Permission denied

# Make it owner-only read-write
/home/user $ chmod 600 secure/password.txt
Permissions changed: secure/password.txt -> rw-------

# Now you can write
/home/user $ echo "newsecret456" > secure/password.txt
Content written to: secure/password.txt
```

---

### Step 12: Delete Operations

```bash
# Create a temp directory with files
/home/user $ mkdir temp
Directory created: temp

/home/user $ touch temp/file1.txt temp/file2.txt
File created: temp/file1.txt

/home/user $ touch temp/file2.txt
File created: temp/file2.txt

/home/user $ ls temp
file1.txt
file2.txt

# Try to delete non-empty directory (will fail)
/home/user $ rm temp
Error: Directory not empty

# Delete with recursive flag
/home/user $ rm temp -r
Deleted: temp

# Delete a single file
/home/user $ rm secure/password.txt
Deleted: secure/password.txt

# Delete empty directory
/home/user $ rm secure
Deleted: secure
```

---

### Step 13: View Complete Structure

```bash
# Go to root
/home/user $ cd /
/ $ pwd
/

# View entire tree
/ $ tree
└── /
    ├── backup/
    │   ├── notes-backup.txt
    │   └── q4-report.txt
    └── home/
        └── user/
            ├── documents/
            │   ├── notes.txt
            │   └── tasks.txt
            └── projects/
                └── java-app/
                    ├── src/
                    │   └── Main.java
                    └── test/
                        └── MainTest.java

# List root
/ $ ls
backup
home

# Detailed list
/ $ ll
drwxr-xr-x        0 Oct 27 20:35 backup/
drwxr-xr-x        0 Oct 27 20:25 home/
```

---

### Step 14: Relative Path Navigation

```bash
# Navigate using relative paths
/ $ cd home/user/documents
/home/user/documents $ pwd
/home/user/documents

# Go up one level
/home/user/documents $ cd ..
/home/user $ pwd
/home/user

# Go to sibling directory
/home/user $ cd ./projects
/home/user/projects $ pwd
/home/user/projects

# Go up two levels
/home/user/projects $ cd ../..
/home $ pwd
/home

# Use . for current directory
/home $ cd ./user/documents
/home/user/documents $ pwd
/home/user/documents
```

---

### Step 15: Advanced Operations

```bash
# Create a complex structure
/ $ mkdir /data/logs/2024/october -p
Directory created: /data/logs/2024/october

/ $ cd /data/logs/2024/october
/data/logs/2024/october $ touch app.log error.log access.log
File created: app.log

/data/logs/2024/october $ touch error.log
File created: error.log

/data/logs/2024/october $ touch access.log
File created: access.log

# Add content to logs
/data/logs/2024/october $ echo "[INFO] Application started" > app.log
Content written to: app.log

/data/logs/2024/october $ echo "[ERROR] Connection failed" > error.log
Content written to: error.log

/data/logs/2024/october $ echo "127.0.0.1 - GET /" > access.log
Content written to: access.log

# View tree from data
/data/logs/2024/october $ cd /data
/data $ tree
└── data/
    └── logs/
        └── 2024/
            └── october/
                ├── access.log
                ├── app.log
                └── error.log

# Check total size
/data $ du
150 bytes	/data
```

---

### Step 16: Exit

```bash
/ $ exit
Goodbye!
```

---

## 🎯 Quick Command Cheat Sheet

```bash
# Navigation
pwd                    # Print working directory
cd <path>              # Change directory
cd ..                  # Go to parent
cd /                   # Go to root

# Listing
ls [path]              # List contents
ll [path]              # List with details
tree [path]            # Show tree structure

# Directories
mkdir <path>           # Create directory
mkdir <path> -p        # Create with parents
rm <path> -r           # Delete directory recursively

# Files
touch <path>           # Create file
cat <path>             # Display content
echo "text" > file     # Write (overwrite)
echo "text" >> file    # Append
rm <path>              # Delete file

# Move/Rename
mv <src> <dest>        # Move or rename

# Information
stat <path>            # File statistics
du [path]              # Disk usage

# Permissions
chmod <mode> <path>    # Change permissions

# Help & Exit
help                   # Show help
exit                   # Exit shell
```

---

**Have fun exploring!** 🚀

