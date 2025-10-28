# LCA Organization Hierarchy - Visual Examples

This document provides detailed visual examples showing how the LCA algorithm works step-by-step.

---

## Example 1: Simple Two-Employee LCA

### Organization Structure

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \
            API     Infra
             |        |
          Alice     Bob
```

### Query: Find LCA(Alice, Bob)

#### Step 1: Lookup Employee Groups

```
Employee Map:
┌─────────┬─────────┐
│ Alice   │ API     │
│ Bob     │ Infra   │
└─────────┴─────────┘

Alice's Group: API
Bob's Group:   Infra
```

#### Step 2: Collect API's Ancestors

```
Iteration 1: current = API
┌──────────────────────┐
│ Ancestors: {API}     │
└──────────────────────┘
Move to parent: Backend

Iteration 2: current = Backend
┌──────────────────────────────────┐
│ Ancestors: {API, Backend}        │
└──────────────────────────────────┘
Move to parent: Engineering

Iteration 3: current = Engineering
┌────────────────────────────────────────────────┐
│ Ancestors: {API, Backend, Engineering}        │
└────────────────────────────────────────────────┘
Move to parent: null (stop)
```

#### Step 3: Find First Common Ancestor in Infra's Chain

```
Iteration 1: current = Infra
Is Infra in ancestors? NO
┌────────────────────────────────────────────────┐
│ Ancestors: {API, Backend, Engineering}        │
│ Checking: Infra ✗                             │
└────────────────────────────────────────────────┘
Move to parent: Backend

Iteration 2: current = Backend
Is Backend in ancestors? YES! ✓
┌────────────────────────────────────────────────┐
│ Ancestors: {API, Backend, Engineering}        │
│ Checking: Backend ✓ FOUND!                    │
└────────────────────────────────────────────────┘

RESULT: Backend
```

#### Visual Summary

```
        Engineering
        /          \
   Backend ←─────── Frontend
   /     \          (LCA = Backend)
API     Infra
 |        |
Alice   Bob
 └────┬───┘
      │
   LCA Query
```

---

## Example 2: Cross-Branch LCA

### Organization Structure

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \           |
            API     Infra     Carol
             |        |
          Alice     Bob
```

### Query: Find LCA(Alice, Carol)

#### Step 1: Lookup Groups

```
Alice → API
Carol → Frontend
```

#### Step 2: Collect API's Ancestors

```
API → Backend → Engineering → null

Ancestors Set: {API, Backend, Engineering}
```

#### Step 3: Traverse Frontend's Chain

```
Check Frontend:
  Is Frontend in {API, Backend, Engineering}? NO

Check Engineering (Frontend's parent):
  Is Engineering in {API, Backend, Engineering}? YES! ✓

RESULT: Engineering
```

#### Visual Summary

```
        Engineering ←─────────────┐
        /          \               │
   Backend      Frontend           │
   /     \           |             │
API     Infra     Carol            │
 |                                 │
Alice ─────────────────────────────┘
                LCA Query
```

---

## Example 3: Three-Employee LCA

### Organization Structure

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \           |
            API     Infra     Carol
             |        |
          Alice     Bob
```

### Query: Find LCA(Alice, Bob, Carol)

#### Step 1: Find LCA(Alice, Bob)

```
Alice → API
Bob → Infra

LCA(API, Infra) = Backend
```

#### Step 2: Find LCA(Backend, Carol)

```
Backend (from step 1)
Carol → Frontend

LCA(Backend, Frontend) = Engineering
```

#### Visual Summary

```
Step 1: LCA(Alice, Bob)
        Engineering
        /          \
   Backend ←─────── Frontend
   /     \           |
API     Infra     Carol
 |        |
Alice   Bob
 └───┬───┘
     │
  Backend

Step 2: LCA(Backend, Carol)
        Engineering ←─────┐
        /          \      │
   Backend      Frontend  │
   (result1)        |     │
                 Carol ───┘
                 
Final Result: Engineering
```

---

## Example 4: Same Group

### Organization Structure

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \
            API     Infra
             |        |
          Alice    Bob, Charlie
```

### Query: Find LCA(Bob, Charlie)

#### Step 1: Lookup Groups

```
Bob → Infra
Charlie → Infra
```

#### Step 2: Find LCA(Infra, Infra)

```
Collect Infra's ancestors:
Ancestors: {Infra, Backend, Engineering}

Check Infra's chain:
Is Infra in ancestors? YES! ✓

RESULT: Infra (themselves!)
```

#### Visual Summary

```
        Engineering
        /          \
   Backend      Frontend
   /     \
API     Infra ←─────┐
         |          │
      Bob, Charlie  │
         └─────┬────┘
               │
          LCA Query
          
Result: Infra (same group)
```

---

## Example 5: Parent-Child Relationship

### Organization Structure

```
                    Engineering
                    /          \
               Backend      Frontend
               /     \
            API     Infra
             |        |
          Alice     Bob
```

### Query: Find LCA(Alice, Backend Manager)

Assume Backend has a manager registered directly to it.

```
Backend
   |
Manager
```

#### Step 1: Lookup Groups

```
Alice → API
Manager → Backend
```

#### Step 2: Find LCA(API, Backend)

```
Collect API's ancestors:
Ancestors: {API, Backend, Engineering}

Check Backend's chain:
Is Backend in ancestors? YES! ✓

RESULT: Backend
```

#### Visual Summary

```
        Engineering
        /          \
   Backend ←─────────┐
   /  |   \          │
API Manager Infra    │
 |                   │
Alice ───────────────┘

LCA(Alice, Manager) = Backend
```

---

## Example 6: Complex Multi-Level Hierarchy

### Organization Structure

```
                        Company
                    /              \
            Engineering          Sales
            /          \            |
       Backend      Frontend    Regional
       /     \          |           |
    API     Infra    Mobile      West
     |        |        |           |
  Alice     Bob     Carol       David
```

### Query 1: Find LCA(Alice, Bob)

```
Alice → API
Bob → Infra

API ancestors: {API, Backend, Engineering, Company}
Infra chain: Infra → Backend ✓

Result: Backend
```

### Query 2: Find LCA(Alice, Carol)

```
Alice → API
Carol → Mobile

API ancestors: {API, Backend, Engineering, Company}
Mobile chain: Mobile → Frontend → Engineering ✓

Result: Engineering
```

### Query 3: Find LCA(Alice, David)

```
Alice → API
David → West

API ancestors: {API, Backend, Engineering, Company}
West chain: West → Regional → Sales → Company ✓

Result: Company
```

### Query 4: Find LCA(Alice, Bob, Carol, David)

```
Step 1: LCA(Alice, Bob) = Backend
Step 2: LCA(Backend, Carol) = Engineering
Step 3: LCA(Engineering, David) = Company

Result: Company
```

#### Visual Summary

```
                        Company ←─────────────────────┐
                    /              \                  │
            Engineering          Sales                │
            /          \            |                 │
       Backend      Frontend    Regional             │
       /     \          |           |                 │
    API     Infra    Mobile      West                │
     |        |        |           |                  │
  Alice     Bob     Carol       David ────────────────┘
     └───┬────┘        └─────┬─────┘
         │                   │
      Backend           Engineering
         └────────┬──────────┘
                  │
              Engineering
                  └──────────┬──────────┐
                             │          │
                          Company ←─────┘
```

---

## Example 7: Edge Cases

### Case 1: Single Employee

```
Query: Find LCA(Alice)

Result: API (Alice's own group)
```

### Case 2: Employee Not Found

```
Query: Find LCA(Alice, Unknown)

Alice → API
Unknown → null

Result: null (employee not found)
```

### Case 3: Root Level Employees

```
                    Engineering
                    /    |     \
               Backend  CEO  Frontend
```

```
Query: Find LCA(CEO, Alice)

CEO → Engineering
Alice → API

API ancestors: {API, Backend, Engineering}
Engineering chain: Engineering ✓

Result: Engineering
```

---

## Example 8: Algorithm Trace (Detailed)

### Setup

```
        A
       / \
      B   C
     /
    D
   /
  E
```

### Query: Find LCA(E, C)

#### Detailed Trace

```
┌─────────────────────────────────────────────────────────┐
│ Phase 1: Collect E's Ancestors                          │
└─────────────────────────────────────────────────────────┘

Step 1: current = E
  ancestors = {}
  ancestors.add(E)
  ancestors = {E}
  current = E.parent = D

Step 2: current = D
  ancestors = {E}
  ancestors.add(D)
  ancestors = {E, D}
  current = D.parent = B

Step 3: current = B
  ancestors = {E, D}
  ancestors.add(B)
  ancestors = {E, D, B}
  current = B.parent = A

Step 4: current = A
  ancestors = {E, D, B}
  ancestors.add(A)
  ancestors = {E, D, B, A}
  current = A.parent = null

Phase 1 Complete: ancestors = {E, D, B, A}

┌─────────────────────────────────────────────────────────┐
│ Phase 2: Find First Common in C's Chain                 │
└─────────────────────────────────────────────────────────┘

Step 1: current = C
  Is C in {E, D, B, A}? NO
  current = C.parent = A

Step 2: current = A
  Is A in {E, D, B, A}? YES! ✓
  
RETURN: A
```

#### Visual Trace

```
Phase 1: Climbing from E
E → D → B → A → null
│   │   │   │
✓   ✓   ✓   ✓
Ancestors: {E, D, B, A}

Phase 2: Climbing from C
C → A
│   │
✗   ✓ FOUND!

Result: A
```

---

## Example 9: Performance Visualization

### Balanced Tree (Best Case)

```
Height = 3, Nodes = 7

            1
          /   \
         2     3
        / \   / \
       4   5 6   7

LCA(4, 5) requires:
- Collect 4's ancestors: 4 → 2 → 1 (3 steps)
- Check 5's chain: 5 → 2 ✓ (2 steps)
Total: O(log n) = O(3)
```

### Skewed Tree (Worst Case)

```
Height = 7, Nodes = 7

1
 \
  2
   \
    3
     \
      4
       \
        5
         \
          6
           \
            7

LCA(7, 6) requires:
- Collect 7's ancestors: 7 → 6 → 5 → 4 → 3 → 2 → 1 (7 steps)
- Check 6's chain: 6 ✓ (1 step)
Total: O(n) = O(7)
```

---

## Summary of Visual Patterns

### Pattern 1: Same Branch
```
    A
   /
  B
 /
C

LCA(C, B) = B (ancestor is one of the nodes)
```

### Pattern 2: Different Branches
```
    A
   / \
  B   C

LCA(B, C) = A (parent of both)
```

### Pattern 3: Deep vs Shallow
```
      A
     / \
    B   C
   /
  D

LCA(D, C) = A (climb to common ancestor)
```

### Pattern 4: Multiple Nodes
```
      A
     /|\
    B C D

LCA(B, C, D) = A (iterative LCA)
```

---

**These visual examples demonstrate all common scenarios you'll encounter when finding the Lowest Common Ancestor in an organization hierarchy!**

