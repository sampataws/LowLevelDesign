package com.orghierarchy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a group in the organization hierarchy.
 * A group can contain employees and have child groups.
 */
public class Group {
    
    private final String groupId;
    private final String name;
    private final Set<String> employeeIds;
    private final Set<String> childGroupIds;
    private final Set<String> parentGroupIds; // For handling multiple parents (shared groups)
    
    public Group(String groupId, String name) {
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("Group ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        this.groupId = groupId;
        this.name = name;
        this.employeeIds = ConcurrentHashMap.newKeySet();
        this.childGroupIds = ConcurrentHashMap.newKeySet();
        this.parentGroupIds = ConcurrentHashMap.newKeySet();
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getName() {
        return name;
    }
    
    public Set<String> getEmployeeIds() {
        return new HashSet<>(employeeIds);
    }
    
    public Set<String> getChildGroupIds() {
        return new HashSet<>(childGroupIds);
    }
    
    public Set<String> getParentGroupIds() {
        return new HashSet<>(parentGroupIds);
    }
    
    public void addEmployee(String employeeId) {
        employeeIds.add(employeeId);
    }
    
    public void removeEmployee(String employeeId) {
        employeeIds.remove(employeeId);
    }
    
    public boolean hasEmployee(String employeeId) {
        return employeeIds.contains(employeeId);
    }
    
    public void addChildGroup(String childGroupId) {
        childGroupIds.add(childGroupId);
    }
    
    public void removeChildGroup(String childGroupId) {
        childGroupIds.remove(childGroupId);
    }
    
    public void addParentGroup(String parentGroupId) {
        parentGroupIds.add(parentGroupId);
    }
    
    public void removeParentGroup(String parentGroupId) {
        parentGroupIds.remove(parentGroupId);
    }
    
    public boolean isLeafGroup() {
        return childGroupIds.isEmpty();
    }
    
    public boolean isRootGroup() {
        return parentGroupIds.isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupId.equals(group.groupId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }
    
    @Override
    public String toString() {
        return "Group{" +
                "id='" + groupId + '\'' +
                ", name='" + name + '\'' +
                ", employees=" + employeeIds.size() +
                ", children=" + childGroupIds.size() +
                ", parents=" + parentGroupIds.size() +
                '}';
    }
}

