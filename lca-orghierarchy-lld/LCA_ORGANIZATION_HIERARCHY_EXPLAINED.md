# LCA Organization Hierarchy - Complete Explanation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Problem Statement](#problem-statement)
3. [What is LCA?](#what-is-lca)
4. [Architecture](#architecture)
5. [How It Works](#how-it-works)
6. [Visual Examples](#visual-examples)
7. [Algorithm Explanation](#algorithm-explanation)
8. [Code Walkthrough](#code-walkthrough)
9. [Usage Examples](#usage-examples)
10. [Time & Space Complexity](#time--space-complexity)
11. [Real-World Applications](#real-world-applications)

---

## Overview

The **LCA Organization Hierarchy** system finds the **Lowest Common Ancestor (LCA)** of employees in an organizational tree structure. This is useful for determining the smallest common group that contains multiple employees.

### Key Features
- ✅ Build hierarchical organization structure (tree)
- ✅ Register employees to specific groups
- ✅ Find the lowest common group for any set of employees
- ✅ Efficient LCA algorithm using ancestor tracking

---

## Problem Statement

### Scenario
You have an organization with a hierarchical structure:
- **Groups** can contain **sub-groups** (forming a tree)
- **Employees** belong to specific groups
- You need to find the **smallest common group** that contains multiple employees

### Example Question
*"What is the smallest team/group that contains both Alice and Bob?"*

This is the **Lowest Common Ancestor (LCA)** problem applied to organizational hierarchies.

---

## What is LCA?

### Definition
The **Lowest Common Ancestor (LCA)** of two nodes in a tree is:
> The deepest (lowest) node that has both nodes as descendants (including the nodes themselves).

### Simple Analogy
Think of a family tree:
- You and your cousin both descend from your grandparents
- Your **LCA** is the closest common ancestor (could be grandparents, great-grandparents, etc.)
- The "lowest" means the closest/deepest common ancestor

### In Organization Context
```
         Engineering (CEO)
            /        \
       Backend    Frontend
         /  \
       API  Infra
```

- **Alice** is in API
- **Bob** is in Infra
- Their **LCA** is **Backend** (the smallest group containing both)

---

## Architecture

### Class Structure

```
┌─────────────────────────────────────────────────────────┐
│                  OrganizationHierarchy                  │
├─────────────────────────────────────────────────────────┤
│ - employeeToGroup: Map<String, Group>                   │
│ - root: Group                                           │
├─────────────────────────────────────────────────────────┤
│ + registerEmployee(group, employee)                     │
│ + getCommonGroupForEmployees(employeeNames): Group      │
│ - findLCA(groupA, groupB): Group                        │
└─────────────────────────────────────────────────────────┘
                        │
                        │ uses
                        ▼
┌─────────────────────────────────────────────────────────┐
│                       Group                             │
├─────────────────────────────────────────────────────────┤
│ - name: String                                          │
│ - parent: Group                                         │
│ - subGroups: List<Group>                                │
│ - employees: List<String>                               │
├─────────────────────────────────────────────────────────┤
│ + addSubGroup(child)                                    │
│ + addEmployee(employee)                                 │
└─────────────────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility |
|-----------|---------------|
| **Group** | Represents a team/department in the organization tree |
| **OrganizationHierarchy** | Manages the tree and finds LCA for employees |
| **employeeToGroup Map** | Quick lookup: employee → their group |

---

## How It Works

### Step-by-Step Process

#### 1. **Build the Organization Tree**
```java
Group engineering = new Group("Engineering");
Group backend = new Group("Backend");
Group api = new Group("API");

engineering.addSubGroup(backend);  // Backend's parent = Engineering
backend.addSubGroup(api);          // API's parent = Backend
```

**Result:**
```
Engineering
    └── Backend
            └── API
```

#### 2. **Register Employees**
```java
org.registerEmployee(api, "Alice");
```

**What happens:**
- Alice is added to the API group
- Map stores: `"Alice" → API group`

#### 3. **Find Common Group**
```java
Group common = org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
```

**What happens:**
- Look up Alice's group (API)
- Look up Bob's group (Infra)
- Find LCA of API and Infra → **Backend**

---

## Visual Examples

### Example 1: Complete Organization Tree

```
                   ┌────────────────────────────┐
                   │        Engineering         │   ← Root
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

### Example 2: Finding LCA (Alice, Bob)

**Question:** What is the common group for Alice and Bob?

**Step 1:** Find Alice's group
```
Alice → API
```

**Step 2:** Find Bob's group
```
Bob → Infra
```

**Step 3:** Find LCA of API and Infra
```
API's ancestors:     API → Backend → Engineering
Infra's ancestors:   Infra → Backend → Engineering

First common ancestor: Backend ✓
```

**Answer:** **Backend**

### Example 3: Finding LCA (Alice, Carol)

**Question:** What is the common group for Alice and Carol?

**Step 1:** Find groups
```
Alice → API
Carol → Frontend
```

**Step 2:** Find LCA of API and Frontend
```
API's ancestors:      API → Backend → Engineering
Frontend's ancestors: Frontend → Engineering

First common ancestor: Engineering ✓
```

**Answer:** **Engineering**

### Example 4: Finding LCA (Alice, Bob, Carol)

**Question:** What is the common group for all three?

**Step 1:** Find LCA(Alice, Bob) = Backend

**Step 2:** Find LCA(Backend, Carol's group)
```
Backend's ancestors:  Backend → Engineering
Frontend's ancestors: Frontend → Engineering

First common ancestor: Engineering ✓
```

**Answer:** **Engineering**

---

## Algorithm Explanation

### LCA Algorithm (Two Groups)

The algorithm uses **ancestor tracking**:

```
Algorithm: findLCA(groupA, groupB)
1. Create a set to store ancestors
2. Traverse from groupA to root, adding each node to the set
3. Traverse from groupB to root
4. Return the first node found in the ancestor set
```

### Visual Walkthrough

**Finding LCA(API, Infra):**

```
Step 1: Collect API's ancestors
┌─────────────────────────────────────┐
│ Ancestors Set: {API}                │
└─────────────────────────────────────┘
        API → parent

┌─────────────────────────────────────┐
│ Ancestors Set: {API, Backend}       │
└─────────────────────────────────────┘
        Backend → parent

┌─────────────────────────────────────┐
│ Ancestors Set: {API, Backend, Eng}  │
└─────────────────────────────────────┘
        Engineering → null (root)

Step 2: Traverse Infra's ancestors
        Infra → Is it in set? NO
        Backend → Is it in set? YES! ✓

Result: Backend
```

### Pseudocode

```
function findLCA(groupA, groupB):
    ancestors = empty set
    
    // Phase 1: Collect all ancestors of groupA
    current = groupA
    while current is not null:
        ancestors.add(current)
        current = current.parent
    
    // Phase 2: Find first common ancestor in groupB's chain
    current = groupB
    while current is not null:
        if current in ancestors:
            return current  // Found LCA!
        current = current.parent
    
    return null  // No common ancestor (different trees)
```

---

## Code Walkthrough

### 1. Group Class

<augment_code_snippet path="lca-orghierarchy-lld/src/main/java/Group.java" mode="EXCERPT">
````java
class Group {
    String name;
    Group parent;                    // Parent group (null for root)
    List<Group> subGroups;           // Child groups
    List<String> employees;          // Employees in this group
    
    void addSubGroup(Group child) {
        child.parent = this;         // Set parent pointer
        this.subGroups.add(child);
    }
}
````
</augment_code_snippet>

**Key Points:**
- Each group has a **parent pointer** (enables upward traversal)
- Root group has `parent = null`
- Forms a **tree structure**

### 2. OrganizationHierarchy Class

<augment_code_snippet path="lca-orghierarchy-lld/src/main/java/OrganizationHierarchy.java" mode="EXCERPT">
````java
public class OrganizationHierarchy {
    private Map<String, Group> employeeToGroup;  // Fast employee lookup
    
    public void registerEmployee(Group group, String employee) {
        group.addEmployee(employee);
        employeeToGroup.put(employee, group);    // O(1) lookup
    }
}
````
</augment_code_snippet>

**Key Points:**
- **HashMap** for O(1) employee → group lookup
- Avoids searching the entire tree

### 3. Finding Common Group (Multiple Employees)

<augment_code_snippet path="lca-orghierarchy-lld/src/main/java/OrganizationHierarchy.java" mode="EXCERPT">
````java
public Group getCommonGroupForEmployees(List<String> employeeNames) {
    Group common = employeeToGroup.get(employeeNames.get(0));
    
    for (int i = 1; i < employeeNames.size(); i++) {
        Group other = employeeToGroup.get(employeeNames.get(i));
        common = findLCA(common, other);  // Iteratively find LCA
    }
    
    return common;
}
````
</augment_code_snippet>

**Strategy:**
- Start with first employee's group
- Iteratively find LCA with each subsequent employee
- **LCA(LCA(A, B), C) = LCA(A, B, C)**

### 4. Core LCA Algorithm

<augment_code_snippet path="lca-orghierarchy-lld/src/main/java/OrganizationHierarchy.java" mode="EXCERPT">
````java
private Group findLCA(Group a, Group b) {
    Set<Group> ancestors = new HashSet<>();
    
    // Collect all ancestors of 'a'
    while (a != null) {
        ancestors.add(a);
        a = a.parent;
    }
    
    // Find first common ancestor in 'b's chain
    while (b != null) {
        if (ancestors.contains(b)) return b;
        b = b.parent;
    }
    
    return null;
}
````
</augment_code_snippet>

---

## Usage Examples

### Example 1: Basic Usage

```java
OrganizationHierarchy org = new OrganizationHierarchy();

// Build tree
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

### Example 2: Complex Hierarchy

```java
// Build organization
Group company = new Group("Company");
Group engineering = new Group("Engineering");
Group sales = new Group("Sales");
Group backend = new Group("Backend");
Group frontend = new Group("Frontend");

company.addSubGroup(engineering);
company.addSubGroup(sales);
engineering.addSubGroup(backend);
engineering.addSubGroup(frontend);

// Register employees
org.registerEmployee(backend, "Alice");
org.registerEmployee(frontend, "Bob");
org.registerEmployee(sales, "Carol");

// Queries
org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
// → Engineering

org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol"));
// → Company

org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob", "Carol"));
// → Company
```

---

## Time & Space Complexity

### Time Complexity

| Operation | Complexity | Explanation |
|-----------|-----------|-------------|
| `registerEmployee` | O(1) | HashMap insertion |
| `findLCA(a, b)` | O(h) | h = height of tree |
| `getCommonGroup(n employees)` | O(n × h) | n LCA operations |

Where:
- **h** = height of the organization tree
- **n** = number of employees in query

### Space Complexity

| Component | Complexity | Explanation |
|-----------|-----------|-------------|
| `employeeToGroup` map | O(e) | e = total employees |
| `ancestors` set in LCA | O(h) | h = tree height |
| Overall | O(e + h) | Dominated by employee map |

### Performance Characteristics

**Best Case:** Balanced tree with height O(log n)
- LCA: O(log n)

**Worst Case:** Skewed tree (linear chain)
- LCA: O(n)

**Typical Case:** Corporate hierarchy (3-5 levels)
- LCA: O(1) to O(5) ≈ constant time

---

## Real-World Applications

### 1. **Corporate Organization**
Find the smallest team containing multiple employees for:
- Meeting scheduling
- Resource allocation
- Reporting structure

### 2. **Access Control**
Determine the lowest group with permissions for multiple resources

### 3. **Cost Center Allocation**
Find the department responsible for multiple employees' costs

### 4. **Project Management**
Identify the smallest team that can handle a multi-person task

### 5. **Communication Routing**
Find the common manager for escalation paths

---

## 🎓 Interview Tips

### Common Questions

**Q: What is the time complexity of finding LCA?**
A: O(h) where h is the height of the tree. For balanced trees, this is O(log n).

**Q: Can you optimize this further?**
A: Yes! Using techniques like:
- Binary lifting (O(log n) preprocessing, O(log n) query)
- Tarjan's offline LCA (O(n + q) for q queries)
- Euler tour + RMQ (O(n log n) preprocessing, O(1) query)

**Q: What if the tree is very deep?**
A: Consider path compression or caching common LCA results.

**Q: How do you handle multiple employees?**
A: Iteratively find LCA: `LCA(LCA(a, b), c)` for three employees.

**Q: What are the edge cases?**
A:
- Single employee (returns their own group)
- Employee not found (returns null)
- Employees in same group (returns that group)
- Employees in different trees (returns null)

### Design Considerations

1. **Why use HashMap for employee lookup?**
   - O(1) lookup vs O(n) tree search
   - Critical for performance with many employees

2. **Why store parent pointers?**
   - Enables upward traversal
   - Simpler than storing full paths

3. **Why use HashSet for ancestors?**
   - O(1) membership check
   - Better than List with O(n) search

4. **Alternative approaches?**
   - Store depth for each node (optimize level comparison)
   - Cache LCA results (if queries repeat)
   - Use path compression (for very deep trees)

---

**Next:** See [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md) for practical code examples and [VISUAL_EXAMPLES.md](VISUAL_EXAMPLES.md) for step-by-step visualizations.

