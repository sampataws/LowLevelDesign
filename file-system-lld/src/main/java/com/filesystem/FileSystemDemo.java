package com.filesystem;

import com.filesystem.core.FileStat;
import com.filesystem.core.Permission;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive demo of the FileSystem implementation
 */
public class FileSystemDemo {
    
    private final FileSystem fs;
    private final Scanner scanner;
    
    public FileSystemDemo() {
        this.fs = new FileSystem();
        this.scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("=".repeat(60));
        System.out.println("File System Demo - Interactive Shell");
        System.out.println("=".repeat(60));
        System.out.println("Type 'help' for available commands, 'exit' to quit\n");
        
        while (true) {
            System.out.print(fs.getCurrentWorkingDirectory() + " $ ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";
            
            try {
                if (command.equals("exit") || command.equals("quit")) {
                    System.out.println("Goodbye!");
                    break;
                } else if (command.equals("help")) {
                    printHelp();
                } else {
                    executeCommand(command, args);
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private void executeCommand(String command, String args) {
        switch (command) {
            case "mkdir":
                handleMkdir(args);
                break;
            case "touch":
                handleTouch(args);
                break;
            case "ls":
                handleLs(args);
                break;
            case "ll":
                handleLl(args);
                break;
            case "cat":
                handleCat(args);
                break;
            case "echo":
                handleEcho(args);
                break;
            case "write":
                handleWrite(args);
                break;
            case "append":
                handleAppend(args);
                break;
            case "mv":
                handleMv(args);
                break;
            case "rm":
                handleRm(args);
                break;
            case "cd":
                handleCd(args);
                break;
            case "pwd":
                handlePwd();
                break;
            case "stat":
                handleStat(args);
                break;
            case "chmod":
                handleChmod(args);
                break;
            case "tree":
                handleTree(args);
                break;
            case "du":
                handleDu(args);
                break;
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Type 'help' for available commands");
        }
    }
    
    private void handleMkdir(String path) {
        if (path.isEmpty()) {
            System.out.println("Usage: mkdir <path> [-p]");
            return;
        }
        
        boolean createParents = path.contains("-p");
        path = path.replace("-p", "").trim();
        
        if (fs.mkdir(path, createParents)) {
            System.out.println("Directory created: " + path);
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }
    
    private void handleTouch(String path) {
        if (path.isEmpty()) {
            System.out.println("Usage: touch <path>");
            return;
        }
        
        if (fs.createFile(path)) {
            System.out.println("File created: " + path);
        } else {
            System.out.println("File already exists: " + path);
        }
    }
    
    private void handleLs(String path) {
        if (path.isEmpty()) {
            path = ".";
        }
        
        List<String> contents = fs.ls(path);
        for (String item : contents) {
            System.out.println(item);
        }
    }
    
    private void handleLl(String path) {
        if (path.isEmpty()) {
            path = ".";
        }
        
        List<FileStat> stats = fs.lsDetailed(path);
        for (FileStat stat : stats) {
            System.out.println(stat.toCompactString());
        }
    }
    
    private void handleCat(String path) {
        if (path.isEmpty()) {
            System.out.println("Usage: cat <path>");
            return;
        }
        
        String content = fs.read(path);
        System.out.println(content);
    }
    
    private void handleEcho(String args) {
        // Format: echo "content" > file  or  echo "content" >> file
        if (!args.contains(">")) {
            System.out.println(args);
            return;
        }
        
        boolean append = args.contains(">>");
        String[] parts = args.split(append ? ">>" : ">", 2);
        
        if (parts.length != 2) {
            System.out.println("Usage: echo \"content\" > file  or  echo \"content\" >> file");
            return;
        }
        
        String content = parts[0].trim().replaceAll("^\"|\"$", "");
        String path = parts[1].trim();
        
        fs.write(path, content, append);
        System.out.println("Content written to: " + path);
    }
    
    private void handleWrite(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length != 2) {
            System.out.println("Usage: write <path> <content>");
            return;
        }
        
        fs.write(parts[0], parts[1], false);
        System.out.println("Content written to: " + parts[0]);
    }
    
    private void handleAppend(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length != 2) {
            System.out.println("Usage: append <path> <content>");
            return;
        }
        
        fs.append(parts[0], parts[1]);
        System.out.println("Content appended to: " + parts[0]);
    }
    
    private void handleMv(String args) {
        String[] parts = args.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Usage: mv <source> <destination>");
            return;
        }
        
        fs.move(parts[0], parts[1]);
        System.out.println("Moved: " + parts[0] + " -> " + parts[1]);
    }
    
    private void handleRm(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: rm <path> [-r]");
            return;
        }
        
        boolean recursive = args.contains("-r");
        String path = args.replace("-r", "").trim();
        
        if (fs.delete(path, recursive)) {
            System.out.println("Deleted: " + path);
        } else {
            System.out.println("Not found: " + path);
        }
    }
    
    private void handleCd(String path) {
        if (path.isEmpty()) {
            path = "/";
        }
        
        fs.changeDirectory(path);
    }
    
    private void handlePwd() {
        System.out.println(fs.getCurrentWorkingDirectory());
    }
    
    private void handleStat(String path) {
        if (path.isEmpty()) {
            System.out.println("Usage: stat <path>");
            return;
        }
        
        FileStat stat = fs.stat(path);
        System.out.println(stat);
    }
    
    private void handleChmod(String args) {
        String[] parts = args.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Usage: chmod <mode> <path>");
            System.out.println("Example: chmod 755 /home/file.txt");
            return;
        }
        
        try {
            int mode = Integer.parseInt(parts[0], 8);
            Permission perm = new Permission(mode);
            fs.setPermission(parts[1], perm);
            System.out.println("Permissions changed: " + parts[1] + " -> " + perm);
        } catch (NumberFormatException e) {
            System.out.println("Invalid mode: " + parts[0]);
        }
    }
    
    private void handleTree(String path) {
        if (path.isEmpty()) {
            path = ".";
        }
        
        System.out.println(fs.tree(path));
    }
    
    private void handleDu(String path) {
        if (path.isEmpty()) {
            path = ".";
        }
        
        long size = fs.getSize(path);
        System.out.println(size + " bytes\t" + path);
    }
    
    private void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  mkdir <path> [-p]       - Create directory (-p for parents)");
        System.out.println("  touch <path>            - Create empty file");
        System.out.println("  ls [path]               - List directory contents");
        System.out.println("  ll [path]               - List with details");
        System.out.println("  cat <path>              - Display file content");
        System.out.println("  echo \"text\" > file      - Write to file (overwrite)");
        System.out.println("  echo \"text\" >> file     - Append to file");
        System.out.println("  write <path> <content>  - Write to file");
        System.out.println("  append <path> <content> - Append to file");
        System.out.println("  mv <src> <dest>         - Move/rename file or directory");
        System.out.println("  rm <path> [-r]          - Delete file/directory (-r for recursive)");
        System.out.println("  cd <path>               - Change directory");
        System.out.println("  pwd                     - Print working directory");
        System.out.println("  stat <path>             - Show file/directory statistics");
        System.out.println("  chmod <mode> <path>     - Change permissions (e.g., chmod 755 file)");
        System.out.println("  tree [path]             - Display directory tree");
        System.out.println("  du [path]               - Show disk usage");
        System.out.println("  help                    - Show this help message");
        System.out.println("  exit                    - Exit the program");
        System.out.println();
    }
    
    public static void main(String[] args) {
        FileSystemDemo demo = new FileSystemDemo();
        demo.run();
    }
}

