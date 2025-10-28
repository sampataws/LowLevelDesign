package com.filesystem.visitor;

import com.filesystem.core.File;
import com.filesystem.core.Directory;

/**
 * Visitor interface for traversing file system nodes (Visitor Pattern)
 */
public interface NodeVisitor {
    void visitFile(File file);
    void visitDirectory(Directory directory);
}

