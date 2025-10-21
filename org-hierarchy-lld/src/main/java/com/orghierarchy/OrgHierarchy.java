package com.orghierarchy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main organization hierarchy system that manages groups and employees.
 * Supports finding the closest common parent group for a set of employees.
 * Thread-safe with ReadWriteLock for efficient concurrent reads and writes.
 */
public class OrgHierarchy {
    
    private final ConcurrentHashMap<String, Employee> employees;
    private final ConcurrentHashMap<String, Group> groups;
    private final ConcurrentHashMap<String, Set<String>> employeeToGroupsMap; // Employee can be in multiple groups
    private final ReadWriteLock lock;
    
    public OrgHierarchy() {
        this.employees = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
        this.employeeToGroupsMap = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Adds an employee to the system.
     */
    public void addEmployee(Employee employee) {
        lock.writeLock().lock();
        try {
            employees.put(employee.getEmployeeId(), employee);
            employeeToGroupsMap.putIfAbsent(employee.getEmployeeId(), ConcurrentHashMap.newKeySet());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Creates a new group in the hierarchy.
     */
    public void addGroup(Group group) {
        lock.writeLock().lock();
        try {
            groups.put(group.getGroupId(), group);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Adds an employee to a group.
     */
    public void addEmployeeToGroup(String employeeId, String groupId) {
        lock.writeLock().lock();
        try {
            if (!employees.containsKey(employeeId)) {
                throw new IllegalArgumentException("Employee not found: " + employeeId);
            }
            if (!groups.containsKey(groupId)) {
                throw new IllegalArgumentException("Group not found: " + groupId);
            }
            
            Group group = groups.get(groupId);
            group.addEmployee(employeeId);
            
            employeeToGroupsMap.get(employeeId).add(groupId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Establishes parent-child relationship between groups.
     */
    public void addGroupToGroup(String childGroupId, String parentGroupId) {
        lock.writeLock().lock();
        try {
            if (!groups.containsKey(childGroupId)) {
                throw new IllegalArgumentException("Child group not found: " + childGroupId);
            }
            if (!groups.containsKey(parentGroupId)) {
                throw new IllegalArgumentException("Parent group not found: " + parentGroupId);
            }
            
            // Check for cycles
            if (wouldCreateCycle(childGroupId, parentGroupId)) {
                throw new IllegalArgumentException("Adding this relationship would create a cycle");
            }
            
            Group childGroup = groups.get(childGroupId);
            Group parentGroup = groups.get(parentGroupId);
            
            parentGroup.addChildGroup(childGroupId);
            childGroup.addParentGroup(parentGroupId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Removes an employee from a group.
     */
    public void removeEmployeeFromGroup(String employeeId, String groupId) {
        lock.writeLock().lock();
        try {
            if (groups.containsKey(groupId)) {
                groups.get(groupId).removeEmployee(employeeId);
            }
            if (employeeToGroupsMap.containsKey(employeeId)) {
                employeeToGroupsMap.get(employeeId).remove(groupId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes parent-child relationship between groups.
     */
    public void removeGroupFromGroup(String childGroupId, String parentGroupId) {
        lock.writeLock().lock();
        try {
            if (groups.containsKey(childGroupId)) {
                groups.get(childGroupId).removeParentGroup(parentGroupId);
            }
            if (groups.containsKey(parentGroupId)) {
                groups.get(parentGroupId).removeChildGroup(childGroupId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Moves an employee from one group to another.
     */
    public void moveEmployeeToGroup(String employeeId, String fromGroupId, String toGroupId) {
        lock.writeLock().lock();
        try {
            removeEmployeeFromGroup(employeeId, fromGroupId);
            addEmployeeToGroup(employeeId, toGroupId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Moves a group from one parent to another.
     */
    public void moveGroupToGroup(String groupId, String fromParentId, String toParentId) {
        lock.writeLock().lock();
        try {
            removeGroupFromGroup(groupId, fromParentId);
            addGroupToGroup(groupId, toParentId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ==================== READ OPERATIONS ====================
    
    /**
     * Finds the closest common parent group for a set of employees.
     * Uses BFS to find the lowest common ancestor in the hierarchy.
     */
    public Group getClosestCommonGroup(Set<String> employeeIds) {
        lock.readLock().lock();
        try {
            if (employeeIds == null || employeeIds.isEmpty()) {
                throw new IllegalArgumentException("Employee IDs cannot be null or empty");
            }
            
            // Validate all employees exist
            for (String empId : employeeIds) {
                if (!employees.containsKey(empId)) {
                    throw new IllegalArgumentException("Employee not found: " + empId);
                }
            }
            
            // Get all groups for each employee
            List<Set<String>> employeeGroupSets = new ArrayList<>();
            for (String empId : employeeIds) {
                Set<String> empGroups = employeeToGroupsMap.get(empId);
                if (empGroups == null || empGroups.isEmpty()) {
                    throw new IllegalArgumentException("Employee not in any group: " + empId);
                }
                employeeGroupSets.add(empGroups);
            }
            
            // Find common groups at each level using BFS
            return findLowestCommonAncestor(employeeGroupSets);
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all groups an employee belongs to.
     */
    public Set<String> getGroupsForEmployee(String employeeId) {
        lock.readLock().lock();
        try {
            Set<String> groups = employeeToGroupsMap.get(employeeId);
            return groups != null ? new HashSet<>(groups) : new HashSet<>();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all employees in a group (direct members only).
     */
    public Set<String> getEmployeesInGroup(String groupId) {
        lock.readLock().lock();
        try {
            Group group = groups.get(groupId);
            return group != null ? group.getEmployeeIds() : new HashSet<>();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all employees in a group and its subgroups (recursive).
     */
    public Set<String> getAllEmployeesInGroupHierarchy(String groupId) {
        lock.readLock().lock();
        try {
            Set<String> allEmployees = new HashSet<>();
            Set<String> visited = new HashSet<>();
            collectEmployeesRecursive(groupId, allEmployees, visited);
            return allEmployees;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public Group getGroup(String groupId) {
        lock.readLock().lock();
        try {
            return groups.get(groupId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public Employee getEmployee(String employeeId) {
        lock.readLock().lock();
        try {
            return employees.get(employeeId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Finds the lowest common ancestor using BFS level-by-level traversal.
     */
    private Group findLowestCommonAncestor(List<Set<String>> employeeGroupSets) {
        // Start with direct groups of all employees
        Set<String> commonGroups = new HashSet<>(employeeGroupSets.get(0));
        for (int i = 1; i < employeeGroupSets.size(); i++) {
            commonGroups.retainAll(employeeGroupSets.get(i));
        }
        
        // If there's a common direct group, return the one with minimum depth
        if (!commonGroups.isEmpty()) {
            return findGroupWithMinimumDepth(commonGroups);
        }
        
        // BFS to find common ancestor
        Map<String, Set<String>> ancestorsMap = new HashMap<>();
        for (Set<String> groupSet : employeeGroupSets) {
            Set<String> ancestors = new HashSet<>();
            for (String groupId : groupSet) {
                collectAncestors(groupId, ancestors);
            }
            ancestorsMap.put(UUID.randomUUID().toString(), ancestors);
        }
        
        // Find common ancestors
        Set<String> commonAncestors = new HashSet<>(ancestorsMap.values().iterator().next());
        for (Set<String> ancestors : ancestorsMap.values()) {
            commonAncestors.retainAll(ancestors);
        }
        
        if (commonAncestors.isEmpty()) {
            return null; // No common ancestor
        }
        
        // Return the ancestor with minimum depth (closest to employees)
        return findGroupWithMinimumDepth(commonAncestors);
    }
    
    /**
     * Collects all ancestors of a group using BFS.
     */
    private void collectAncestors(String groupId, Set<String> ancestors) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(groupId);
        visited.add(groupId);
        ancestors.add(groupId);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            Group group = groups.get(current);
            
            if (group != null) {
                for (String parentId : group.getParentGroupIds()) {
                    if (!visited.contains(parentId)) {
                        visited.add(parentId);
                        ancestors.add(parentId);
                        queue.offer(parentId);
                    }
                }
            }
        }
    }
    
    /**
     * Finds the group with maximum depth (closest to leaves, furthest from root).
     * This gives us the "closest" common group.
     */
    private Group findGroupWithMinimumDepth(Set<String> groupIds) {
        String maxDepthGroupId = null;
        int maxDepth = Integer.MIN_VALUE;

        for (String groupId : groupIds) {
            int depth = calculateDepthFromRoot(groupId);
            if (depth > maxDepth) {
                maxDepth = depth;
                maxDepthGroupId = groupId;
            }
        }

        return maxDepthGroupId != null ? groups.get(maxDepthGroupId) : null;
    }

    /**
     * Calculates the depth of a group (distance from root).
     * Higher depth = further from root = closer to leaves.
     */
    private int calculateDepthFromRoot(String groupId) {
        Group group = groups.get(groupId);
        if (group == null || group.isRootGroup()) {
            return 0;
        }

        int maxDepth = 0;
        for (String parentId : group.getParentGroupIds()) {
            maxDepth = Math.max(maxDepth, calculateDepthFromRoot(parentId) + 1);
        }

        return maxDepth;
    }
    
    /**
     * Collects all employees in a group hierarchy recursively.
     */
    private void collectEmployeesRecursive(String groupId, Set<String> allEmployees, Set<String> visited) {
        if (visited.contains(groupId)) {
            return;
        }
        
        visited.add(groupId);
        Group group = groups.get(groupId);
        
        if (group != null) {
            allEmployees.addAll(group.getEmployeeIds());
            
            for (String childId : group.getChildGroupIds()) {
                collectEmployeesRecursive(childId, allEmployees, visited);
            }
        }
    }
    
    /**
     * Checks if adding a parent-child relationship would create a cycle.
     */
    private boolean wouldCreateCycle(String childGroupId, String parentGroupId) {
        // If parent is a descendant of child, it would create a cycle
        Set<String> descendants = new HashSet<>();
        collectDescendants(childGroupId, descendants);
        return descendants.contains(parentGroupId);
    }
    
    /**
     * Collects all descendants of a group.
     */
    private void collectDescendants(String groupId, Set<String> descendants) {
        Group group = groups.get(groupId);
        if (group == null) {
            return;
        }
        
        for (String childId : group.getChildGroupIds()) {
            if (!descendants.contains(childId)) {
                descendants.add(childId);
                collectDescendants(childId, descendants);
            }
        }
    }
}

