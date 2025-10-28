package com.filesystem;

import com.filesystem.core.FileStat;
import com.filesystem.core.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for FileSystem implementation
 */
class FileSystemTest {
    
    private FileSystem fs;
    
    @BeforeEach
    void setUp() {
        fs = new FileSystem();
    }
    
    @Test
    @DisplayName("Test mkdir - create single directory")
    void testMkdir() {
        assertTrue(fs.mkdir("/home"));
        assertTrue(fs.exists("/home"));
        assertTrue(fs.isDirectory("/home"));
    }
    
    @Test
    @DisplayName("Test mkdir - create nested directories with parent flag")
    void testMkdirRecursive() {
        assertTrue(fs.mkdir("/home/user/documents", true));
        assertTrue(fs.exists("/home"));
        assertTrue(fs.exists("/home/user"));
        assertTrue(fs.exists("/home/user/documents"));
    }
    
    @Test
    @DisplayName("Test mkdir - fail without parent flag")
    void testMkdirWithoutParent() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.mkdir("/home/user/documents", false);
        });
    }
    
    @Test
    @DisplayName("Test mkdir - duplicate directory returns false")
    void testMkdirDuplicate() {
        assertTrue(fs.mkdir("/home"));
        assertFalse(fs.mkdir("/home"));
    }
    
    @Test
    @DisplayName("Test createFile - create new file")
    void testCreateFile() {
        fs.mkdir("/home", true);
        assertTrue(fs.createFile("/home/test.txt"));
        assertTrue(fs.exists("/home/test.txt"));
        assertTrue(fs.isFile("/home/test.txt"));
    }
    
    @Test
    @DisplayName("Test createFile - duplicate file returns false")
    void testCreateFileDuplicate() {
        fs.mkdir("/home", true);
        assertTrue(fs.createFile("/home/test.txt"));
        assertFalse(fs.createFile("/home/test.txt"));
    }
    
    @Test
    @DisplayName("Test write and read - overwrite mode")
    void testWriteAndRead() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        fs.write("/home/test.txt", "Hello, World!");
        assertEquals("Hello, World!", fs.read("/home/test.txt"));
        
        fs.write("/home/test.txt", "New content");
        assertEquals("New content", fs.read("/home/test.txt"));
    }
    
    @Test
    @DisplayName("Test write - append mode")
    void testAppend() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        fs.write("/home/test.txt", "Hello");
        fs.append("/home/test.txt", ", World!");
        
        assertEquals("Hello, World!", fs.read("/home/test.txt"));
    }
    
    @Test
    @DisplayName("Test ls - list directory contents")
    void testLs() {
        fs.mkdir("/home", true);
        fs.createFile("/home/file1.txt");
        fs.createFile("/home/file2.txt");
        fs.mkdir("/home/subdir");
        
        List<String> contents = fs.ls("/home");
        assertEquals(3, contents.size());
        assertTrue(contents.contains("file1.txt"));
        assertTrue(contents.contains("file2.txt"));
        assertTrue(contents.contains("subdir"));
    }
    
    @Test
    @DisplayName("Test ls - list file returns file name")
    void testLsFile() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        List<String> contents = fs.ls("/home/test.txt");
        assertEquals(1, contents.size());
        assertEquals("test.txt", contents.get(0));
    }
    
    @Test
    @DisplayName("Test stat - get file statistics")
    void testStat() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        fs.write("/home/test.txt", "Hello");
        
        FileStat stat = fs.stat("/home/test.txt");
        
        assertEquals("test.txt", stat.getName());
        assertEquals("/home/test.txt", stat.getPath());
        assertTrue(stat.isFile());
        assertFalse(stat.isDirectory());
        assertEquals(5, stat.getSize());
        assertNotNull(stat.getPermission());
    }
    
    @Test
    @DisplayName("Test move - move file to different directory")
    void testMove() {
        fs.mkdir("/home", true);
        fs.mkdir("/backup", true);
        fs.createFile("/home/test.txt");
        fs.write("/home/test.txt", "content");
        
        fs.move("/home/test.txt", "/backup/test.txt");
        
        assertFalse(fs.exists("/home/test.txt"));
        assertTrue(fs.exists("/backup/test.txt"));
        assertEquals("content", fs.read("/backup/test.txt"));
    }
    
    @Test
    @DisplayName("Test rename - rename file in same directory")
    void testRename() {
        fs.mkdir("/home", true);
        fs.createFile("/home/old.txt");
        fs.write("/home/old.txt", "content");
        
        fs.rename("/home/old.txt", "new.txt");
        
        assertFalse(fs.exists("/home/old.txt"));
        assertTrue(fs.exists("/home/new.txt"));
        assertEquals("content", fs.read("/home/new.txt"));
    }
    
    @Test
    @DisplayName("Test delete - delete file")
    void testDeleteFile() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        assertTrue(fs.delete("/home/test.txt"));
        assertFalse(fs.exists("/home/test.txt"));
    }
    
    @Test
    @DisplayName("Test delete - delete empty directory")
    void testDeleteEmptyDirectory() {
        fs.mkdir("/home", true);
        
        assertTrue(fs.delete("/home"));
        assertFalse(fs.exists("/home"));
    }
    
    @Test
    @DisplayName("Test delete - fail on non-empty directory without recursive flag")
    void testDeleteNonEmptyDirectory() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        assertThrows(IllegalArgumentException.class, () -> {
            fs.delete("/home", false);
        });
    }
    
    @Test
    @DisplayName("Test delete - recursive delete")
    void testDeleteRecursive() {
        fs.mkdir("/home/user/documents", true);
        fs.createFile("/home/user/documents/file.txt");
        fs.createFile("/home/user/file2.txt");
        
        assertTrue(fs.delete("/home", true));
        assertFalse(fs.exists("/home"));
    }
    
    @Test
    @DisplayName("Test permissions - set and get")
    void testPermissions() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        Permission newPerm = new Permission(0777);
        fs.setPermission("/home/test.txt", newPerm);
        
        Permission retrieved = fs.getPermission("/home/test.txt");
        assertEquals(0777, retrieved.getMode());
    }
    
    @Test
    @DisplayName("Test permissions - read permission check")
    void testReadPermission() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        fs.write("/home/test.txt", "content");
        
        // Remove read permission
        Permission noRead = new Permission(0200); // write only
        fs.setPermission("/home/test.txt", noRead);
        
        assertThrows(SecurityException.class, () -> {
            fs.read("/home/test.txt");
        });
    }
    
    @Test
    @DisplayName("Test permissions - write permission check")
    void testWritePermission() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        
        // Remove write permission
        Permission noWrite = new Permission(0400); // read only
        fs.setPermission("/home/test.txt", noWrite);
        
        assertThrows(SecurityException.class, () -> {
            fs.write("/home/test.txt", "content");
        });
    }
    
    @Test
    @DisplayName("Test path resolution - absolute path")
    void testAbsolutePath() {
        fs.mkdir("/home/user", true);
        fs.createFile("/home/user/test.txt");
        
        assertTrue(fs.exists("/home/user/test.txt"));
    }
    
    @Test
    @DisplayName("Test path resolution - relative path with current directory")
    void testRelativePath() {
        fs.mkdir("/home/user", true);
        fs.changeDirectory("/home");
        fs.createFile("user/test.txt");
        
        assertTrue(fs.exists("/home/user/test.txt"));
    }
    
    @Test
    @DisplayName("Test path resolution - dot notation")
    void testDotNotation() {
        fs.mkdir("/home/user", true);
        fs.createFile("/home/user/test.txt");
        
        assertTrue(fs.exists("/home/./user/./test.txt"));
    }
    
    @Test
    @DisplayName("Test path resolution - double dot notation")
    void testDoubleDotNotation() {
        fs.mkdir("/home/user/documents", true);
        fs.createFile("/home/user/test.txt");

        assertTrue(fs.exists("/home/user/documents/../test.txt"));
    }

    @Test
    @DisplayName("Test path resolution - prevent traversal beyond root")
    void testPreventRootTraversal() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.mkdir("/../etc");
        });
    }

    @Test
    @DisplayName("Test getSize - file size")
    void testGetSizeFile() {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");
        fs.write("/home/test.txt", "Hello, World!");

        assertEquals(13, fs.getSize("/home/test.txt"));
    }

    @Test
    @DisplayName("Test getSize - directory size (recursive)")
    void testGetSizeDirectory() {
        fs.mkdir("/home/user", true);
        fs.createFile("/home/user/file1.txt");
        fs.createFile("/home/user/file2.txt");
        fs.write("/home/user/file1.txt", "Hello");
        fs.write("/home/user/file2.txt", "World");

        assertEquals(10, fs.getSize("/home/user"));
    }

    @Test
    @DisplayName("Test changeDirectory - change working directory")
    void testChangeDirectory() {
        fs.mkdir("/home/user", true);

        assertEquals("/", fs.getCurrentWorkingDirectory());

        fs.changeDirectory("/home/user");
        assertEquals("/home/user", fs.getCurrentWorkingDirectory());

        fs.changeDirectory("..");
        assertEquals("/home", fs.getCurrentWorkingDirectory());
    }

    @Test
    @DisplayName("Test tree - print directory structure")
    void testTree() {
        fs.mkdir("/home/user/documents", true);
        fs.createFile("/home/user/test.txt");
        fs.createFile("/home/user/documents/file.txt");

        String tree = fs.tree("/home");
        assertNotNull(tree);
        assertTrue(tree.contains("user"));
        assertTrue(tree.contains("documents"));
        assertTrue(tree.contains("test.txt"));
    }

    @Test
    @DisplayName("Test concurrent operations - thread safety")
    void testConcurrentOperations() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        fs.mkdir("/concurrent", true);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String fileName = "/concurrent/file_" + threadId + "_" + j + ".txt";
                        fs.createFile(fileName);
                        fs.write(fileName, "Thread " + threadId + " operation " + j);
                        String content = fs.read(fileName);
                        if (content.contains("Thread " + threadId)) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread, successCount.get());

        List<String> files = fs.ls("/concurrent");
        assertEquals(threadCount * operationsPerThread, files.size());
    }

    @Test
    @DisplayName("Test concurrent move operations - no deadlock")
    void testConcurrentMoveNoDeadlock() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        fs.mkdir("/source", true);
        fs.mkdir("/dest", true);

        // Create files
        for (int i = 0; i < threadCount; i++) {
            fs.createFile("/source/file" + i + ".txt");
            fs.write("/source/file" + i + ".txt", "content" + i);
        }

        // Move files concurrently
        for (int i = 0; i < threadCount; i++) {
            final int fileId = i;
            executor.submit(() -> {
                try {
                    fs.move("/source/file" + fileId + ".txt", "/dest/file" + fileId + ".txt");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify all files moved
        for (int i = 0; i < threadCount; i++) {
            assertTrue(fs.exists("/dest/file" + i + ".txt"));
            assertFalse(fs.exists("/source/file" + i + ".txt"));
        }
    }

    @Test
    @DisplayName("Test edge case - move directory into itself should fail")
    void testMoveIntoItself() {
        fs.mkdir("/home/user", true);

        assertThrows(IllegalArgumentException.class, () -> {
            fs.move("/home", "/home/user/home");
        });
    }

    @Test
    @DisplayName("Test edge case - cannot delete root")
    void testCannotDeleteRoot() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.delete("/");
        });
    }

    @Test
    @DisplayName("Test edge case - cannot move root")
    void testCannotMoveRoot() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.move("/", "/newroot");
        });
    }

    @Test
    @DisplayName("Test lsDetailed - get detailed file information")
    void testLsDetailed() {
        fs.mkdir("/home", true);
        fs.createFile("/home/file1.txt");
        fs.createFile("/home/file2.txt");
        fs.write("/home/file1.txt", "Hello");

        List<FileStat> stats = fs.lsDetailed("/home");

        assertEquals(2, stats.size());

        FileStat file1Stat = stats.stream()
            .filter(s -> s.getName().equals("file1.txt"))
            .findFirst()
            .orElse(null);

        assertNotNull(file1Stat);
        assertEquals(5, file1Stat.getSize());
        assertTrue(file1Stat.isFile());
    }

    @Test
    @DisplayName("Test modification time updates")
    void testModificationTime() throws InterruptedException {
        fs.mkdir("/home", true);
        fs.createFile("/home/test.txt");

        FileStat stat1 = fs.stat("/home/test.txt");
        long mtime1 = stat1.getModificationTime();

        Thread.sleep(10); // Ensure time difference

        fs.write("/home/test.txt", "new content");

        FileStat stat2 = fs.stat("/home/test.txt");
        long mtime2 = stat2.getModificationTime();

        assertTrue(mtime2 > mtime1, "Modification time should be updated");
    }

    @Test
    @DisplayName("Test invalid path characters")
    void testInvalidPathCharacters() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.mkdir("/home/user<invalid>");
        });
    }

    @Test
    @DisplayName("Test empty path")
    void testEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            fs.mkdir("");
        });
    }
}
