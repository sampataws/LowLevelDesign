# Interview Strategy: Organization Hierarchy - Closest Common Group

## 🎯 **The Problem Statement**

**Atlassian Interview Question:**

*"Design a system to find the closest common parent group for a set of employees in an organization hierarchy."*

**Follow-up Questions:**
- a) Basic hierarchy with groups and subgroups
- b) Shared groups (employees in multiple groups, groups with multiple parents)
- c) Concurrent reads/writes with dynamic updates
- d) Flat hierarchy (single level, no subgroups)

---

## **The 45-60 Minute Strategy**

### **Phase 1: First 15 Minutes - Clarify & Design**

#### **Step 1: Clarify Requirements (5 minutes)**

**What to say:**
```
"Let me confirm the requirements:

1. Organization has hierarchical groups (groups can have subgroups)
2. Employees belong to groups
3. Find the CLOSEST common parent group for a set of employees
4. Handle shared groups (employee in multiple groups)
5. Support concurrent reads/writes
6. Also support flat hierarchy

Questions:
- Can a group have multiple parents? (Yes - shared groups)
- Can an employee be in multiple groups? (Yes)
- What if there's no common group? (Return null)
- Performance requirements? (Optimize for reads)
- How many employees/groups? (Scalable design)"
```

#### **Step 2: Draw the Design (5 minutes)**

Draw on whiteboard:
```
Employee (id, name, email)
    ↓
Group (id, name, employees[], children[], parents[])
    ↓
OrgHierarchy
    - employees: Map<id, Employee>
    - groups: Map<id, Group>
    - employeeToGroups: Map<empId, Set<groupId>>
    ↓
getClosestCommonGroup(Set<employeeIds>) → Group
```

**Say:** *"I'll use BFS to find the lowest common ancestor. For thread safety, I'll use ReadWriteLock."*

#### **Step 3: Discuss Algorithm (5 minutes)**

**Algorithm:**
```
1. Get all groups for each employee
2. Find intersection of direct groups
3. If common direct group exists → return it
4. Otherwise, BFS upward to find common ancestor
5. Return the ancestor with minimum depth (closest)
```

**Say:** *"This is essentially finding the Lowest Common Ancestor (LCA) in a DAG (Directed Acyclic Graph)."*

---

### **Phase 2: Next 20 Minutes - Core Implementation**

#### **Build in This Order:**

**1. Employee class (2 minutes)**
```java
public class Employee {
    private final String employeeId;
    private final String name;
    private final String email;
    
    public Employee(String employeeId, String name, String email) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
    }
    
    // Getters, equals, hashCode
}
```

**2. Group class (3 minutes)**
```java
public class Group {
    private final String groupId;
    private final String name;
    private final Set<String> employeeIds;
    private final Set<String> childGroupIds;
    private final Set<String> parentGroupIds; // For shared groups
    
    public Group(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
        this.employeeIds = ConcurrentHashMap.newKeySet();
        this.childGroupIds = ConcurrentHashMap.newKeySet();
        this.parentGroupIds = ConcurrentHashMap.newKeySet();
    }
    
    public void addEmployee(String empId) { employeeIds.add(empId); }
    public void addChildGroup(String childId) { childGroupIds.add(childId); }
    public void addParentGroup(String parentId) { parentGroupIds.add(parentId); }
    
    // Getters
}
```

**Say:** *"Using ConcurrentHashMap.newKeySet() for thread-safe collections. Supporting multiple parents for shared groups."*

**3. OrgHierarchy - Basic Structure (5 minutes)**
```java
public class OrgHierarchy {
    private final ConcurrentHashMap<String, Employee> employees;
    private final ConcurrentHashMap<String, Group> groups;
    private final ConcurrentHashMap<String, Set<String>> employeeToGroupsMap;
    private final ReadWriteLock lock;
    
    public OrgHierarchy() {
        this.employees = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
        this.employeeToGroupsMap = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    public void addEmployee(Employee emp) {
        lock.writeLock().lock();
        try {
            employees.put(emp.getEmployeeId(), emp);
            employeeToGroupsMap.putIfAbsent(emp.getEmployeeId(), 
                ConcurrentHashMap.newKeySet());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void addEmployeeToGroup(String empId, String groupId) {
        lock.writeLock().lock();
        try {
            groups.get(groupId).addEmployee(empId);
            employeeToGroupsMap.get(empId).add(groupId);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

**Say:** *"Using ReadWriteLock for concurrent reads and exclusive writes. This allows multiple threads to read simultaneously."*

**4. Core Algorithm - getClosestCommonGroup (10 minutes)**
```java
public Group getClosestCommonGroup(Set<String> employeeIds) {
    lock.readLock().lock();
    try {
        // Get all groups for each employee
        List<Set<String>> employeeGroupSets = new ArrayList<>();
        for (String empId : employeeIds) {
            Set<String> empGroups = employeeToGroupsMap.get(empId);
            if (empGroups == null || empGroups.isEmpty()) {
                throw new IllegalArgumentException("Employee not in any group");
            }
            employeeGroupSets.add(empGroups);
        }
        
        // Find common direct groups
        Set<String> commonGroups = new HashSet<>(employeeGroupSets.get(0));
        for (int i = 1; i < employeeGroupSets.size(); i++) {
            commonGroups.retainAll(employeeGroupSets.get(i));
        }
        
        // If common direct group exists, return it
        if (!commonGroups.isEmpty()) {
            return findGroupWithMinimumDepth(commonGroups);
        }
        
        // BFS to find common ancestor
        return findLowestCommonAncestor(employeeGroupSets);
        
    } finally {
        lock.readLock().unlock();
    }
}

private Group findLowestCommonAncestor(List<Set<String>> employeeGroupSets) {
    // Collect all ancestors for each employee's groups
    Map<String, Set<String>> ancestorsMap = new HashMap<>();
    for (Set<String> groupSet : employeeGroupSets) {
        Set<String> ancestors = new HashSet<>();
        for (String groupId : groupSet) {
            collectAncestors(groupId, ancestors);
        }
        ancestorsMap.put(UUID.randomUUID().toString(), ancestors);
    }
    
    // Find common ancestors
    Set<String> commonAncestors = new HashSet<>(
        ancestorsMap.values().iterator().next());
    for (Set<String> ancestors : ancestorsMap.values()) {
        commonAncestors.retainAll(ancestors);
    }
    
    if (commonAncestors.isEmpty()) {
        return null;
    }
    
    // Return ancestor with minimum depth
    return findGroupWithMinimumDepth(commonAncestors);
}

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
```

**Say:** *"Using BFS to collect all ancestors, then finding intersection. Returning the one with minimum depth for 'closest' group."*

---

### **Phase 3: Next 10 Minutes - Demo & Enhancements**

**Create Demo (5 minutes)**
```java
public static void main(String[] args) {
    OrgHierarchy org = new OrgHierarchy();
    
    // Create hierarchy
    Employee alice = new Employee("E1", "Alice", "alice@co.com");
    Employee bob = new Employee("E2", "Bob", "bob@co.com");
    
    org.addEmployee(alice);
    org.addEmployee(bob);
    
    Group atlassian = new Group("G1", "Atlassian");
    Group engineering = new Group("G2", "Engineering");
    Group backend = new Group("G3", "Backend");
    
    org.addGroup(atlassian);
    org.addGroup(engineering);
    org.addGroup(backend);
    
    org.addGroupToGroup("G2", "G1");
    org.addGroupToGroup("G3", "G2");
    
    org.addEmployeeToGroup("E1", "G3");
    org.addEmployeeToGroup("E2", "G3");
    
    // Find common group
    Set<String> employees = new HashSet<>(Arrays.asList("E1", "E2"));
    Group common = org.getClosestCommonGroup(employees);
    
    System.out.println("Closest common group: " + common.getName());
    // Output: Backend
}
```

**RUN IT!** Show it works.

**Add Dynamic Updates (5 minutes)**
```java
public void moveEmployeeToGroup(String empId, String fromGroupId, String toGroupId) {
    lock.writeLock().lock();
    try {
        removeEmployeeFromGroup(empId, fromGroupId);
        addEmployeeToGroup(empId, toGroupId);
    } finally {
        lock.writeLock().unlock();
    }
}

public void addGroupToGroup(String childId, String parentId) {
    lock.writeLock().lock();
    try {
        // Check for cycles
        if (wouldCreateCycle(childId, parentId)) {
            throw new IllegalArgumentException("Would create cycle");
        }
        
        groups.get(parentId).addChildGroup(childId);
        groups.get(childId).addParentGroup(parentId);
    } finally {
        lock.writeLock().unlock();
    }
}
```

---

### **Phase 4: Final 10 Minutes - Handle Follow-ups**

#### **Question (b): Shared Groups**

**Say:**
```
"Already handled! 
- Employee can be in multiple groups: employeeToGroupsMap is Set<String>
- Group can have multiple parents: parentGroupIds is Set<String>
- Algorithm finds intersection of all possible paths
- Returns ONE closest group using minimum depth"
```

#### **Question (c): Concurrent Reads/Writes**

**Say:**
```
"Using ReadWriteLock:
- Multiple threads can read simultaneously (getClosestCommonGroup)
- Writes are exclusive (addEmployee, moveEmployee, etc.)
- Always reflects latest state because:
  * Reads acquire read lock
  * Writes acquire write lock
  * No stale data due to lock ordering
  
For higher performance:
- Could use optimistic locking with versioning
- Cache common queries with invalidation
- Use lock-free data structures for reads"
```

#### **Question (d): Flat Hierarchy**

**Say:**
```
"Already supported!
- If employees are in same group → returns that group
- If employees in different groups with no common parent → returns null
- If employee in multiple groups → finds common group

Example:
Team A: [Alice, Bob]
Team B: [Carol]
Alice also in Team B

getClosestCommonGroup([Alice, Carol]) → Team B
getClosestCommonGroup([Bob, Carol]) → null (no common group)"
```

---

## 💬 **Common Interview Questions & Answers**

### **Q: "What's the time complexity?"**
```
A: "For N employees, M groups, D depth:
- getClosestCommonGroup: O(N * M * D) worst case
  * N employees to process
  * M groups to traverse per employee
  * D depth for BFS
  
Optimizations:
- Cache ancestor paths: O(1) lookup
- Use Union-Find for static hierarchies
- Precompute LCA for common queries"
```

### **Q: "How do you handle cycles?"**
```
A: "Prevent cycles during insertion:
- Before adding parent-child relationship
- Check if parent is descendant of child
- Use DFS to collect all descendants
- If parent in descendants → reject

This ensures DAG property is maintained."
```

### **Q: "What if hierarchy changes frequently?"**
```
A: "Current design handles it well:
- ReadWriteLock allows concurrent reads during stable periods
- Writes are serialized but fast (O(1) for most operations)
- No caching means always fresh data

For very high write volume:
- Use versioned snapshots
- MVCC (Multi-Version Concurrency Control)
- Event sourcing with materialized views"
```

### **Q: "How would you scale this?"**
```
A: "For distributed system:
1. Partition by top-level groups (departments)
2. Store in graph database (Neo4j)
3. Cache common queries in Redis
4. Use distributed locks (Zookeeper)
5. Replicate read-only copies
6. Eventual consistency for non-critical updates"
```

### **Q: "What about very deep hierarchies?"**
```
A: "Optimizations:
1. Limit depth (e.g., max 10 levels)
2. Cache ancestor paths
3. Use skip pointers (like skip lists)
4. Precompute LCA table
5. Compress paths (path compression)"
```

---

## ⏱️ **Time Management**

| Time | What to Have | Status |
|------|--------------|--------|
| **5 min** | Requirements clarified | ✅ |
| **10 min** | Design drawn | ✅ |
| **15 min** | Employee & Group classes | ✅ |
| **25 min** | OrgHierarchy basic structure | ✅ |
| **35 min** | getClosestCommonGroup working | ✅ DEMO |
| **40 min** | Demo running | ✅ DEMO |
| **50 min** | Dynamic updates + concurrency | ✅ |
| **60 min** | All follow-ups answered | ✅ |

---

## 🎯 **Priority Order**

### 1. ✅ **Must Have** (First 35 min)
- Employee, Group classes
- OrgHierarchy with basic operations
- getClosestCommonGroup algorithm
- Demo working

### 2. ✅ **Should Have** (Next 15 min)
- Thread safety (ReadWriteLock)
- Dynamic updates (move, add, remove)
- Cycle detection
- Error handling

### 3. ✅ **Nice to Have** (Next 10 min)
- Shared groups support
- Flat hierarchy support
- Comprehensive demo
- Performance discussion

---

## 🚀 **Quick Start Checklist**

```
☐ 1. Employee class (2 min)
☐ 2. Group class (3 min)
☐ 3. OrgHierarchy structure (5 min)
☐ 4. addEmployee, addGroup (3 min)
☐ 5. addEmployeeToGroup (2 min)
☐ 6. getClosestCommonGroup (10 min)
☐ 7. Demo main() (5 min)
☐ 8. RUN IT! ✅
☐ 9. Add ReadWriteLock (5 min)
☐ 10. Add dynamic updates (5 min)
☐ 11. Discuss follow-ups (10 min)
```

**Total: ~50 minutes for full solution**

---

## 🎓 **Final Tips**

### **DO:**
✅ Clarify shared groups upfront
✅ Explain LCA algorithm clearly
✅ Demo frequently
✅ Discuss thread safety
✅ Handle all 4 parts (a, b, c, d)

### **DON'T:**
❌ Forget about multiple parents
❌ Ignore cycle detection
❌ Skip thread safety discussion
❌ Forget flat hierarchy case

### **Remember:**
> **This is a graph problem (DAG), not just a tree!** 🎯

**Good luck with your Atlassian interview!** 🚀

