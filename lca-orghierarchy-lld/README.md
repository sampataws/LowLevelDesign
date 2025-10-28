# LCA Organization Hierarchy

Find the **Lowest Common Ancestor (LCA)** of employees in an organizational tree structure.

---

## 🎯 What Does This Do?

Given an organization hierarchy and multiple employees, this system finds the **smallest common group** (team/department) that contains all of them.

### Example

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \           |
            API     Infra     Carol
             |        |
          Alice     Bob
```

**Query:** What's the common group for Alice and Bob?  
**Answer:** Backend (the smallest team containing both)

**Query:** What's the common group for Alice and Carol?  
**Answer:** Engineering (they're in different sub-departments)

---

## 🚀 Quick Start

### Run the Demo

```bash
cd lca-orghierarchy-lld
mvn clean compile
mvn exec:java -Dexec.mainClass="DemoLCA"
```

**Output:**
```
Common group (Alice, Bob): Backend
Common group (Alice, Carol): Engineering
Common group (Alice): API
```

### Basic Usage

```java
OrganizationHierarchy org = new OrganizationHierarchy();

// Build organization tree
Group engineering = new Group("Engineering");
Group backend = new Group("Backend");
Group api = new Group("API");

engineering.addSubGroup(backend);
backend.addSubGroup(api);

// Register employees
org.registerEmployee(api, "Alice");
org.registerEmployee(backend, "Bob");

// Find common group
Group common = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob")
);

System.out.println(common);  // Output: Backend
```

---

## 📚 Documentation

### Core Documentation
- **[Complete Explanation](LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md)** - Comprehensive guide with diagrams
  - What is LCA?
  - Architecture and design
  - Algorithm explanation
  - Time/space complexity
  - Real-world applications

### Visual Learning
- **[Visual Examples](VISUAL_EXAMPLES.md)** - Step-by-step visual walkthroughs
  - 9 detailed examples with diagrams
  - Algorithm trace visualizations
  - Edge cases illustrated
  - Performance patterns

### Practical Usage
- **[Usage Examples](USAGE_EXAMPLES.md)** - Real-world code examples
  - Meeting scheduler
  - Access control system
  - Cost center allocation
  - Reporting structure
  - Dynamic organization changes

### Testing
- **[Test Documentation](TEST_DOCUMENTATION.md)** - Complete test suite documentation
  - 50 comprehensive tests
  - 100% passing rate
  - Test coverage details
  - Running tests guide

---

## 🏗️ Architecture

### Class Structure

```
OrganizationHierarchy
├── employeeToGroup: Map<String, Group>  (O(1) lookup)
├── root: Group
└── Methods:
    ├── registerEmployee(group, employee)
    ├── getCommonGroupForEmployees(employees): Group
    └── findLCA(groupA, groupB): Group

Group
├── name: String
├── parent: Group
├── subGroups: List<Group>
├── employees: List<String>
└── Methods:
    ├── addSubGroup(child)
    └── addEmployee(employee)
```

### How It Works

1. **Build Tree:** Create groups and link them with parent-child relationships
2. **Register Employees:** Map each employee to their group
3. **Find LCA:** Use ancestor tracking algorithm to find common group

---

## 🎨 Visual Example

### Organization Structure

```
                   ┌────────────────────────────┐
                   │        Engineering         │
                   └────────────────────────────┘
                           /             \
                          /               \
           ┌────────────────────┐     ┌──────────────────┐
           │      Backend       │     │     Frontend     │
           └────────────────────┘     └──────────────────┘
                 /        \                    │
                /          \                   │
     ┌────────────────┐  ┌────────────────┐   │
     │      API       │  │     Infra      │   │
     └────────────────┘  └────────────────┘   │
           │                     │             │
         Alice                 Bob          Carol
```

### LCA Queries

| Query | Result | Explanation |
|-------|--------|-------------|
| `LCA(Alice, Bob)` | Backend | Both under Backend |
| `LCA(Alice, Carol)` | Engineering | Different branches |
| `LCA(Bob, Carol)` | Engineering | Different branches |
| `LCA(Alice, Bob, Carol)` | Engineering | All three together |

---

## 🧮 Algorithm

### LCA Algorithm (Two Groups)

```java
private Group findLCA(Group a, Group b) {
    // Phase 1: Collect all ancestors of 'a'
    Set<Group> ancestors = new HashSet<>();
    while (a != null) {
        ancestors.add(a);
        a = a.parent;
    }
    
    // Phase 2: Find first common ancestor in 'b's chain
    while (b != null) {
        if (ancestors.contains(b)) return b;
        b = b.parent;
    }
    
    return null;
}
```

### Visual Trace

```
Finding LCA(API, Infra):

Step 1: Collect API's ancestors
API → Backend → Engineering → null
Ancestors: {API, Backend, Engineering}

Step 2: Check Infra's chain
Infra → not in set
Backend → in set! ✓

Result: Backend
```

---

## ⚡ Performance

### Time Complexity

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| `registerEmployee` | O(1) | HashMap insertion |
| `findLCA(a, b)` | O(h) | h = tree height |
| `getCommonGroup(n employees)` | O(n × h) | n LCA operations |

### Space Complexity

| Component | Complexity |
|-----------|-----------|
| Employee map | O(e) where e = employees |
| Ancestor set | O(h) where h = height |
| Overall | O(e + h) |

### Typical Performance

For a typical corporate hierarchy (3-5 levels):
- **LCA query:** O(1) to O(5) ≈ constant time
- **Very fast** for practical use cases

---

## 💡 Real-World Applications

### 1. Meeting Scheduling
Find the smallest team that needs to be involved in a meeting with multiple attendees.

### 2. Access Control
Determine if users share a common security group for collaboration.

### 3. Cost Center Allocation
Identify which department should be charged for a multi-person project.

### 4. Reporting Structure
Find the common manager for multiple employees.

### 5. Resource Planning
Determine the smallest organizational unit that can handle a task.

---

## 📖 Code Examples

### Example 1: Basic Company

```java
OrganizationHierarchy org = new OrganizationHierarchy();

Group company = new Group("TechCorp");
Group engineering = new Group("Engineering");
Group sales = new Group("Sales");

company.addSubGroup(engineering);
company.addSubGroup(sales);

org.registerEmployee(engineering, "Alice");
org.registerEmployee(sales, "Bob");

Group common = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob")
);
// Result: TechCorp
```

### Example 2: Multi-Level Hierarchy

```java
Group engineering = new Group("Engineering");
Group backend = new Group("Backend");
Group api = new Group("API");
Group database = new Group("Database");

engineering.addSubGroup(backend);
backend.addSubGroup(api);
backend.addSubGroup(database);

org.registerEmployee(api, "Alice");
org.registerEmployee(database, "Bob");

Group common = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob")
);
// Result: Backend
```

### Example 3: Multiple Employees

```java
org.registerEmployee(api, "Alice");
org.registerEmployee(database, "Bob");
org.registerEmployee(frontend, "Carol");

Group common = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob", "Carol")
);
// Result: Engineering (assuming frontend is also under engineering)
```

---

## 🔧 Building and Running

### Compile

```bash
mvn clean compile
```

### Run Demo

```bash
mvn exec:java -Dexec.mainClass="DemoLCA"
```

### Run Tests

```bash
mvn test
```

**Test Results:**
```
Tests run: 50, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 📁 Project Structure

```
lca-orghierarchy-lld/
├── src/
│   ├── main/java/
│   │   ├── Group.java                           # Group/team representation
│   │   ├── OrganizationHierarchy.java           # Main LCA logic
│   │   └── DemoLCA.java                         # Demo application
│   └── test/java/
│       ├── OrganizationHierarchyTest.java       # LCA tests (22 tests)
│       └── GroupTest.java                       # Group tests (28 tests)
├── pom.xml                                      # Maven configuration
├── README.md                                    # This file
├── LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md      # Complete guide
├── VISUAL_EXAMPLES.md                           # Visual walkthroughs
├── USAGE_EXAMPLES.md                            # Practical examples
└── TEST_DOCUMENTATION.md                        # Test documentation
```

---

## 🎓 Key Concepts

### What is LCA?
The **Lowest Common Ancestor** is the deepest node in a tree that has both nodes as descendants.

### Why Use LCA?
- Find smallest common organizational unit
- Efficient queries (O(h) where h is typically small)
- Natural fit for hierarchical structures

### How Does It Work?
1. Collect all ancestors of first node
2. Traverse second node's ancestors
3. Return first common ancestor found

---

## 🌟 Features

- ✅ Simple and intuitive API
- ✅ Efficient O(h) LCA algorithm
- ✅ Support for multiple employees
- ✅ Handles edge cases (null, single employee)
- ✅ Clean object-oriented design
- ✅ Well-documented with examples
- ✅ Comprehensive test suite (50 tests, 100% passing)

---

## 📚 Learn More

1. **Start with:** [LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md](LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md)
   - Understand the problem and solution
   - See architecture and algorithm

2. **Visualize:** [VISUAL_EXAMPLES.md](VISUAL_EXAMPLES.md)
   - Step-by-step visual examples
   - Algorithm traces

3. **Practice:** [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md)
   - Real-world code examples
   - Common patterns

---

## 🎯 Quick Reference

### Create Organization
```java
Group root = new Group("Company");
Group child = new Group("Department");
root.addSubGroup(child);
```

### Register Employee
```java
org.registerEmployee(group, "EmployeeName");
```

### Find Common Group
```java
Group common = org.getCommonGroupForEmployees(
    Arrays.asList("Employee1", "Employee2")
);
```

---

**Ready to find common ancestors in your organization!** 🚀

For detailed explanations, see [LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md](LCA_ORGANIZATION_HIERARCHY_EXPLAINED.md)

