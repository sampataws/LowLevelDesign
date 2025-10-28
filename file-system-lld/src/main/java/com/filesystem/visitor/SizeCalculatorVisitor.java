package com.filesystem.visitor;

import com.filesystem.core.File;
import com.filesystem.core.Directory;
import com.filesystem.core.FileSystemNode;

/**
 * Visitor to calculate total size of a directory tree
 */
public class SizeCalculatorVisitor implements NodeVisitor {
    private long totalSize = 0;
    
    @Override
    public void visitFile(File file) {
        totalSize += file.getSize();
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        for (FileSystemNode child : directory.listChildren()) {
            child.accept(this);
        }
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public void reset() {
        totalSize = 0;
    }
}

