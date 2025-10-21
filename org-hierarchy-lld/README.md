# Organization Hierarchy - Closest Common Group Finder

A production-ready system for finding the closest common parent group for employees in an organization hierarchy. Designed for the classic Atlassian interview question.

## 📋 Overview

This project implements a comprehensive organization hierarchy system that:
- Manages hierarchical group structures (groups can have subgroups)
- Tracks employees and their group memberships
- Finds the **closest common parent group** for any set of employees
- Supports **shared groups** (employees in multiple groups, groups with multiple parents)
- Handles **concurrent reads and writes** with thread-safe operations
- Works with both **hierarchical and flat** organization structures

## 🎯 The Problem

**Atlassian Interview Question:**

*"Design a system to find the closest common parent group for a set of employees in an organization hierarchy."*

**Example:**
```
        Atlassian
        /       \
   Engineering  Sales
    /      \
 Backend  Frontend

Employees:
- Alice, Bob → Backend
- Carol → Frontend
- David → Sales

Query: getClosestCommonGroup([Alice, Carol])
Answer: Engineering (closest common parent)

Query: getClosestCommonGroup([Alice, David])
Answer: Atlassian (closest common parent)
```

## 🏗️ Architecture

### Core Components

1. **Employee**: Represents an employee
   - Employee ID, name, email
   - Immutable data class

2. **Group**: Represents a group in the hierarchy
   - Group ID, name
   - Set of employee IDs
   - Set of child group IDs
   - Set of parent group IDs (supports multiple parents)
   - Thread-safe collections

3. **OrgHierarchy**: Main system managing everything
   - Employee and group storage
   - Employee-to-groups mapping
   - Closest common group finding (LCA algorithm)
   - Dynamic hierarchy updates
   - Thread-safe with ReadWriteLock

### Data Structure

```
OrgHierarchy
├── employees: ConcurrentHashMap<String, Employee>
├── groups: ConcurrentHashMap<String, Group>
├── employeeToGroupsMap: ConcurrentHashMap<String, Set<String>>
└── lock: ReadWriteLock
```

## 🚀 Quick Start

### Build and Run

```bash
cd org-hierarchy-lld
mvn clean compile
```

### Run Demo

```bash
mvn exec:java -Dexec.mainClass="com.orghierarchy.DriverApplication"
```

### Run Tests

```bash
mvn test
```

## 📝 Usage Examples

### Basic Usage

```java
OrgHierarchy org = new OrgHierarchy();

// Create employees
Employee alice = new Employee("E001", "Alice", "alice@atlassian.com");
Employee bob = new Employee("E002", "Bob", "bob@atlassian.com");

org.addEmployee(alice);
org.addEmployee(bob);

// Create hierarchy
Group atlassian = new Group("G1", "Atlassian");
Group engineering = new Group("G2", "Engineering");
Group backend = new Group("G3", "Backend");

org.addGroup(atlassian);
org.addGroup(engineering);
org.addGroup(backend);

// Build hierarchy: Backend -> Engineering -> Atlassian
org.addGroupToGroup("G2", "G1");
org.addGroupToGroup("G3", "G2");

// Add employees to groups
org.addEmployeeToGroup("E001", "G3");
org.addEmployeeToGroup("E002", "G3");

// Find closest common group
Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002"));
Group common = org.getClosestCommonGroup(employees);

System.out.println("Closest common group: " + common.getName());
// Output: Backend
```

### Shared Groups (Employee in Multiple Groups)

```java
// Alice is in both Backend and Platform teams
org.addEmployeeToGroup("E001", "G3"); // Backend
org.addEmployeeToGroup("E001", "G4"); // Platform

Set<String> groups = org.getGroupsForEmployee("E001");
// Returns: [G3, G4]

// Find common group with another employee
Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002"));
Group common = org.getClosestCommonGroup(employees);
// Returns the closest group that contains both
```

### Dynamic Updates

```java
// Move employee from one group to another
org.moveEmployeeToGroup("E001", "G3", "G4");

// Add new group relationship
org.addGroupToGroup("G5", "G2"); // G5 becomes child of G2

// Remove employee from group
org.removeEmployeeFromGroup("E001", "G3");
```

### Concurrent Operations

```java
// Thread 1: Reading
Thread reader = new Thread(() -> {
    Set<String> emps = new HashSet<>(Arrays.asList("E001", "E002"));
    Group result = org.getClosestCommonGroup(emps);
    System.out.println("Common group: " + result.getName());
});

// Thread 2: Writing
Thread writer = new Thread(() -> {
    org.moveEmployeeToGroup("E003", "G4", "G5");
});

reader.start();
writer.start();

// ReadWriteLock ensures:
// - Multiple readers can run concurrently
// - Writers have exclusive access
// - Readers always see latest state
```

## 🎯 Algorithm: Finding Closest Common Group

### Approach: Lowest Common Ancestor (LCA) in DAG

**Step 1:** Get all groups for each employee
```
Employee E1 → [G3, G4]
Employee E2 → [G5]
```

**Step 2:** Find intersection of direct groups
```
Intersection([G3, G4], [G5]) = ∅
```

**Step 3:** If no common direct group, use BFS to find ancestors
```
Ancestors(E1) = [G3, G4, G2, G1]
Ancestors(E2) = [G5, G2, G1]
Common = [G2, G1]
```

**Step 4:** Return ancestor with minimum depth (closest)
```
G2 (depth 2) < G1 (depth 3)
Return: G2
```

### Time Complexity

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Add Employee | O(1) | O(1) |
| Add Group | O(1) | O(1) |
| Add Employee to Group | O(1) | O(1) |
| Get Closest Common Group | O(N × M × D) | O(M × D) |
| Move Employee | O(1) | O(1) |

Where:
- N = number of employees in query
- M = number of groups
- D = depth of hierarchy

## 🔒 Thread Safety

### ReadWriteLock Strategy

**Read Operations** (Concurrent):
- `getClosestCommonGroup()`
- `getGroupsForEmployee()`
- `getEmployeesInGroup()`
- `getAllEmployeesInGroupHierarchy()`

**Write Operations** (Exclusive):
- `addEmployee()`
- `addGroup()`
- `addEmployeeToGroup()`
- `moveEmployeeToGroup()`
- `addGroupToGroup()`

**Benefits:**
- Multiple threads can read simultaneously
- Writes are serialized for consistency
- No stale data - reads always see latest state
- Better performance than synchronized for read-heavy workloads

## 🌟 Key Features

### 1. Shared Groups Support

**Employees in Multiple Groups:**
```java
org.addEmployeeToGroup("E001", "G3"); // Backend
org.addEmployeeToGroup("E001", "G4"); // Platform
// Alice is now in both Backend and Platform
```

**Groups with Multiple Parents:**
```java
org.addGroupToGroup("G6", "G2"); // Platform -> Engineering
org.addGroupToGroup("G6", "G3"); // Platform -> Sales
// Platform reports to both Engineering and Sales
```

### 2. Cycle Detection

```java
// Prevents cycles in hierarchy
org.addGroupToGroup("G2", "G1"); // Engineering -> Atlassian
org.addGroupToGroup("G1", "G2"); // Would create cycle!
// Throws: IllegalArgumentException("Would create cycle")
```

### 3. Flat Hierarchy Support

```java
// Single-level groups (no subgroups)
Group team1 = new Group("T1", "Team 1");
Group team2 = new Group("T2", "Team 2");

org.addEmployeeToGroup("E001", "T1");
org.addEmployeeToGroup("E002", "T2");

// No common group
Group common = org.getClosestCommonGroup(Arrays.asList("E001", "E002"));
// Returns: null
```

### 4. Dynamic Hierarchy Updates

All operations are thread-safe and reflect immediately:
- Add/remove employees
- Add/remove groups
- Move employees between groups
- Restructure group hierarchy

## 🧪 Testing

### Test Coverage

**OrgHierarchyTest**: 28 comprehensive tests
- Basic operations (add, get)
- Closest common group finding
- Shared groups scenarios
- Concurrent reads and writes
- Flat hierarchy support
- Cycle detection
- Error handling
- Edge cases

**Run Tests:**
```bash
mvn test
```

**All tests passing:** ✅

## 📊 Example Scenarios

### Scenario 1: Same Team
```
Backend: [Alice, Bob]
Query: [Alice, Bob]
Result: Backend
```

### Scenario 2: Same Department
```
Engineering
├── Backend: [Alice]
└── Frontend: [Carol]

Query: [Alice, Carol]
Result: Engineering
```

### Scenario 3: Different Departments
```
Atlassian
├── Engineering
│   └── Backend: [Alice]
└── Sales: [David]

Query: [Alice, David]
Result: Atlassian
```

### Scenario 4: Shared Employee
```
Backend: [Alice, Bob]
Platform: [Alice, Carol]

Query: [Alice, Carol]
Result: Platform (direct common group)
```

## 🎓 Interview Tips

### What to Highlight

1. **Algorithm**: LCA in DAG using BFS
2. **Thread Safety**: ReadWriteLock for concurrent access
3. **Shared Groups**: Multiple parents/memberships
4. **Cycle Detection**: Maintain DAG property
5. **Scalability**: Discuss caching, partitioning

### Common Follow-up Questions

**Q: How to handle very deep hierarchies?**
- Limit depth (e.g., max 10 levels)
- Cache ancestor paths
- Use skip pointers
- Precompute LCA table

**Q: How to scale for millions of employees?**
- Partition by department
- Use graph database (Neo4j)
- Cache common queries (Redis)
- Distributed locks (Zookeeper)

**Q: What if hierarchy changes frequently?**
- Current design handles it well
- For very high write volume: MVCC, event sourcing
- Versioned snapshots for consistency

## 🔧 Optimizations

### Current Implementation
- BFS for ancestor collection: O(M × D)
- Good for: Dynamic hierarchies, moderate scale

### Possible Optimizations

1. **Caching**: Cache ancestor paths
2. **Precomputation**: LCA table for static hierarchies
3. **Indexing**: Inverted index for employee-to-groups
4. **Compression**: Path compression for deep hierarchies
5. **Partitioning**: Shard by top-level groups

## 🌍 Real-World Applications

- **Atlassian**: Employee directory, team structure
- **Google**: Organization hierarchy, reporting structure
- **Microsoft**: Department management
- **Slack**: Workspace and channel hierarchy
- **GitHub**: Organization and team management

## 📚 Design Patterns Used

1. **Facade**: OrgHierarchy simplifies complex operations
2. **Repository**: Centralized data storage
3. **Strategy**: Pluggable LCA algorithms
4. **Observer**: Could add listeners for hierarchy changes
5. **Immutable**: Employee class is immutable

## 🎯 Learning Outcomes

After studying this project, you'll understand:
- Graph algorithms (LCA in DAG)
- Thread-safe concurrent systems
- ReadWriteLock usage
- Cycle detection in graphs
- BFS/DFS traversal
- System design for hierarchies

## 📖 Further Reading

- [Lowest Common Ancestor](https://en.wikipedia.org/wiki/Lowest_common_ancestor)
- [Directed Acyclic Graph](https://en.wikipedia.org/wiki/Directed_acyclic_graph)
- [ReadWriteLock](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReadWriteLock.html)

## 🤝 Contributing

This is an educational project for interview preparation. Suggestions welcome!

## 📄 License

MIT License - Free to use for learning and interview preparation.

