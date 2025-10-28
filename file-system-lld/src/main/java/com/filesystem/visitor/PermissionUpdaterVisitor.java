package com.filesystem.visitor;

import com.filesystem.core.File;
import com.filesystem.core.Directory;
import com.filesystem.core.FileSystemNode;
import com.filesystem.core.Permission;

/**
 * Visitor to recursively update permissions
 */
public class PermissionUpdaterVisitor implements NodeVisitor {
    private final Permission newPermission;
    private final boolean recursive;
    
    public PermissionUpdaterVisitor(Permission newPermission, boolean recursive) {
        this.newPermission = newPermission;
        this.recursive = recursive;
    }
    
    @Override
    public void visitFile(File file) {
        file.setPermission(newPermission.copy());
    }
    
    @Override
    public void visitDirectory(Directory directory) {
        directory.setPermission(newPermission.copy());
        
        if (recursive) {
            for (FileSystemNode child : directory.listChildren()) {
                child.accept(this);
            }
        }
    }
}

