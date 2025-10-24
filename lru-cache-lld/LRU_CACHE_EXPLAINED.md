# LRU Cache - Complete Explanation with Diagrams

## 📚 What is LRU Cache?

**LRU (Least Recently Used) Cache** is a data structure that stores a limited number of items and automatically removes the least recently used item when the cache is full.

### Real-World Analogy

Think of your **desk workspace**:
- You have limited space (cache capacity)
- You keep frequently used items on your desk (cache)
- When you need a new item and desk is full, you remove the item you haven't used in the longest time (LRU eviction)
- Recently used items stay on top (most accessible)

### Key Properties

1. **Fixed Capacity**: Maximum number of items it can hold
2. **Fast Access**: O(1) time for get and put operations
3. **Automatic Eviction**: Removes least recently used item when full
4. **Recency Tracking**: Tracks which items were used recently

## 🎯 Why Use LRU Cache?

### Use Cases

1. **Web Browser Cache**: Store recently visited web pages
2. **Database Query Cache**: Cache frequently accessed query results
3. **CDN (Content Delivery Network)**: Cache popular content
4. **Operating System**: Page replacement in virtual memory
5. **API Rate Limiting**: Track recent API calls
6. **Session Management**: Store active user sessions

### Benefits

- **Improved Performance**: Avoid expensive recomputation or I/O
- **Reduced Latency**: Fast access to frequently used data
- **Memory Efficiency**: Automatically manages memory usage
- **Predictable Behavior**: Clear eviction policy

## 🔄 How LRU Cache Works

### Core Operations

1. **get(key)**: Retrieve value and mark as recently used
2. **put(key, value)**: Insert/update value and mark as recently used
3. **Eviction**: Remove least recently used item when capacity is reached

### Data Structures Used

**Combination of Two Data Structures:**

1. **HashMap**: For O(1) key lookup
2. **Doubly Linked List**: For O(1) insertion/deletion and maintaining order

```
┌─────────────────────────────────────────────────────────────────┐
│                         LRU Cache                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  HashMap (for fast lookup)                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Key  →  Node Reference                                   │  │
│  │ "A"  →  Node(A, 1)  ────────────┐                        │  │
│  │ "B"  →  Node(B, 2)  ──────┐     │                        │  │
│  │ "C"  →  Node(C, 3)  ──┐   │     │                        │  │
│  └──────────────────────┼───┼─────┼────────────────────────┘  │
│                         │   │     │                            │
│  Doubly Linked List (for order)                                │
│  ┌──────────────────────▼───▼─────▼────────────────────────┐  │
│  │                                                          │  │
│  │  HEAD ←→ [C:3] ←→ [B:2] ←→ [A:1] ←→ TAIL               │  │
│  │  (dummy)  ▲                    ▲      (dummy)           │  │
│  │           │                    │                        │  │
│  │      Most Recently        Least Recently                │  │
│  │         Used                  Used                      │  │
│  │                                                          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 Visual Step-by-Step Example

Let's trace through operations on an LRU Cache with **capacity = 3**:

### Initial State (Empty)

```
Capacity: 3
Size: 0

HashMap: {}

Doubly Linked List:
HEAD ←→ TAIL
```

### Step 1: put("A", 1)

```
Operation: put("A", 1)
Action: Insert new node

HashMap: {"A" → Node(A,1)}

Doubly Linked List:
HEAD ←→ [A:1] ←→ TAIL
        ▲
    Most Recent
```

### Step 2: put("B", 2)

```
Operation: put("B", 2)
Action: Insert new node at head

HashMap: {"A" → Node(A,1), "B" → Node(B,2)}

Doubly Linked List:
HEAD ←→ [B:2] ←→ [A:1] ←→ TAIL
        ▲           ▲
    Most Recent  Least Recent
```

### Step 3: put("C", 3)

```
Operation: put("C", 3)
Action: Insert new node at head (capacity reached)

HashMap: {"A" → Node(A,1), "B" → Node(B,2), "C" → Node(C,3)}

Doubly Linked List:
HEAD ←→ [C:3] ←→ [B:2] ←→ [A:1] ←→ TAIL
        ▲                   ▲
    Most Recent        Least Recent
```

### Step 4: get("A")

```
Operation: get("A")
Action: Move A to head (mark as recently used)
Return: 1

HashMap: {"A" → Node(A,1), "B" → Node(B,2), "C" → Node(C,3)}

Doubly Linked List:
HEAD ←→ [A:1] ←→ [C:3] ←→ [B:2] ←→ TAIL
        ▲                   ▲
    Most Recent        Least Recent
```

### Step 5: put("D", 4)

```
Operation: put("D", 4)
Action: Cache is full! Evict LRU (B), then insert D

1. Remove B (least recently used)
2. Insert D at head

HashMap: {"A" → Node(A,1), "C" → Node(C,3), "D" → Node(D,4)}

Doubly Linked List:
HEAD ←→ [D:4] ←→ [A:1] ←→ [C:3] ←→ TAIL
        ▲                   ▲
    Most Recent        Least Recent

Evicted: B
```

### Step 6: get("C")

```
Operation: get("C")
Action: Move C to head
Return: 3

HashMap: {"A" → Node(A,1), "C" → Node(C,3), "D" → Node(D,4)}

Doubly Linked List:
HEAD ←→ [C:3] ←→ [D:4] ←→ [A:1] ←→ TAIL
        ▲                   ▲
    Most Recent        Least Recent
```

### Step 7: put("E", 5)

```
Operation: put("E", 5)
Action: Cache is full! Evict LRU (A), then insert E

HashMap: {"C" → Node(C,3), "D" → Node(D,4), "E" → Node(E,5)}

Doubly Linked List:
HEAD ←→ [E:5] ←→ [C:3] ←→ [D:4] ←→ TAIL
        ▲                   ▲
    Most Recent        Least Recent

Evicted: A
```

## 🔍 Detailed Operation Flow

### get(key) Operation

```
┌─────────────────────────────────────────────────────────────┐
│ get(key)                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Check if key exists in HashMap                         │
│     │                                                       │
│     ├─ NO  → Return -1 (or null)                           │
│     │                                                       │
│     └─ YES → Continue                                       │
│                                                             │
│  2. Get node from HashMap                                  │
│     node = map.get(key)                                    │
│                                                             │
│  3. Move node to head (mark as recently used)              │
│     ┌─────────────────────────────────────────┐            │
│     │ a. Remove node from current position    │            │
│     │    node.prev.next = node.next           │            │
│     │    node.next.prev = node.prev           │            │
│     │                                         │            │
│     │ b. Insert node at head                  │            │
│     │    node.next = head.next                │            │
│     │    node.prev = head                     │            │
│     │    head.next.prev = node                │            │
│     │    head.next = node                     │            │
│     └─────────────────────────────────────────┘            │
│                                                             │
│  4. Return node.value                                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘

Time Complexity: O(1)
```

### put(key, value) Operation

```
┌─────────────────────────────────────────────────────────────┐
│ put(key, value)                                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Check if key exists in HashMap                         │
│     │                                                       │
│     ├─ YES → Update existing node                          │
│     │         node.value = value                           │
│     │         Move node to head                            │
│     │         Return                                        │
│     │                                                       │
│     └─ NO  → Continue                                       │
│                                                             │
│  2. Check if cache is full                                 │
│     │                                                       │
│     └─ YES → Evict LRU (node before tail)                  │
│                ┌──────────────────────────────┐            │
│                │ a. Get LRU node              │            │
│                │    lru = tail.prev           │            │
│                │                              │            │
│                │ b. Remove from list          │            │
│                │    lru.prev.next = tail      │            │
│                │    tail.prev = lru.prev      │            │
│                │                              │            │
│                │ c. Remove from HashMap       │            │
│                │    map.remove(lru.key)       │            │
│                └──────────────────────────────┘            │
│                                                             │
│  3. Create new node                                        │
│     node = new Node(key, value)                            │
│                                                             │
│  4. Insert at head                                         │
│     node.next = head.next                                  │
│     node.prev = head                                       │
│     head.next.prev = node                                  │
│     head.next = node                                       │
│                                                             │
│  5. Add to HashMap                                         │
│     map.put(key, node)                                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘

Time Complexity: O(1)
```

## 🎨 Memory Layout

```
┌─────────────────────────────────────────────────────────────────┐
│                    Memory Representation                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  LRUCache Object                                                │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │ capacity: 3                                               │ │
│  │ map: HashMap<String, Node>                                │ │
│  │ head: Node (dummy)                                        │ │
│  │ tail: Node (dummy)                                        │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
│  HashMap                                                        │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │ Entry: "A" → 0x1001  ──────────────┐                      │ │
│  │ Entry: "B" → 0x1002  ────────┐     │                      │ │
│  │ Entry: "C" → 0x1003  ──┐     │     │                      │ │
│  └────────────────────────┼─────┼─────┼───────────────────────┘ │
│                           │     │     │                         │
│  Nodes in Memory          │     │     │                         │
│  ┌────────────────────────▼─────▼─────▼───────────────────────┐ │
│  │                                                             │ │
│  │  0x1000: HEAD (dummy)                                      │ │
│  │  ┌──────────────────────────────────────┐                 │ │
│  │  │ key: null                            │                 │ │
│  │  │ value: null                          │                 │ │
│  │  │ prev: null                           │                 │ │
│  │  │ next: 0x1003 ────────────────┐       │                 │ │
│  │  └──────────────────────────────┼───────┘                 │ │
│  │                                 │                         │ │
│  │  0x1003: Node C                 │                         │ │
│  │  ┌──────────────────────────────▼───────┐                 │ │
│  │  │ key: "C"                             │                 │ │
│  │  │ value: 3                             │                 │ │
│  │  │ prev: 0x1000 ◄───────────────────────┼─┐               │ │
│  │  │ next: 0x1002 ────────────────┐       │ │               │ │
│  │  └──────────────────────────────┼───────┘ │               │ │
│  │                                 │         │               │ │
│  │  0x1002: Node B                 │         │               │ │
│  │  ┌──────────────────────────────▼───────┐ │               │ │
│  │  │ key: "B"                             │ │               │ │
│  │  │ value: 2                             │ │               │ │
│  │  │ prev: 0x1003 ◄───────────────────────┼─┘               │ │
│  │  │ next: 0x1001 ────────────────┐       │                 │ │
│  │  └──────────────────────────────┼───────┘                 │ │
│  │                                 │                         │ │
│  │  0x1001: Node A                 │                         │ │
│  │  ┌──────────────────────────────▼───────┐                 │ │
│  │  │ key: "A"                             │                 │ │
│  │  │ value: 1                             │                 │ │
│  │  │ prev: 0x1002 ◄───────────────────────┼─┐               │ │
│  │  │ next: 0x1004 ────────────────┐       │ │               │ │
│  │  └──────────────────────────────┼───────┘ │               │ │
│  │                                 │         │               │ │
│  │  0x1004: TAIL (dummy)           │         │               │ │
│  │  ┌──────────────────────────────▼───────┐ │               │ │
│  │  │ key: null                            │ │               │ │
│  │  │ value: null                          │ │               │ │
│  │  │ prev: 0x1001 ◄───────────────────────┼─┘               │ │
│  │  │ next: null                           │                 │ │
│  │  └──────────────────────────────────────┘                 │ │
│  │                                                             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## ⚡ Performance Analysis

### Time Complexity

| Operation | Time Complexity | Explanation |
|-----------|----------------|-------------|
| `get(key)` | **O(1)** | HashMap lookup + Doubly linked list node removal/insertion |
| `put(key, value)` | **O(1)** | HashMap lookup/insert + Doubly linked list operations |
| `evict()` | **O(1)** | Remove tail node + HashMap removal |

### Space Complexity

| Aspect | Space Complexity | Explanation |
|--------|-----------------|-------------|
| Overall | **O(capacity)** | Stores at most `capacity` nodes |
| HashMap | **O(capacity)** | One entry per cached item |
| Linked List | **O(capacity)** | One node per cached item |

### Why O(1)?

**HashMap provides O(1) lookup:**
- Direct access to node by key

**Doubly Linked List provides O(1) insertion/deletion:**
- No need to traverse the list
- Direct pointer manipulation
- Dummy head and tail simplify edge cases

## 🏗️ Implementation Details

### Node Structure

```java
class Node {
    String key;      // Key for HashMap removal during eviction
    int value;       // Cached value
    Node prev;       // Previous node (towards tail)
    Node next;       // Next node (towards head)
}
```

**Why store key in Node?**
- During eviction, we need to remove the node from HashMap
- We have the node reference but need the key to remove from HashMap

### Doubly Linked List vs Singly Linked List

**Why Doubly Linked List?**

```
Singly Linked List:
  A → B → C → D

  To remove C:
  - Need to find B (previous node)
  - Requires O(n) traversal ❌

Doubly Linked List:
  A ←→ B ←→ C ←→ D

  To remove C:
  - Have direct reference to B (C.prev)
  - O(1) removal ✅
```

### Dummy Head and Tail

**Why use dummy nodes?**

```
Without Dummy Nodes:
  - Need to handle null checks
  - Special cases for empty list
  - Special cases for single element

With Dummy Nodes:
  HEAD ←→ [actual nodes] ←→ TAIL

  - No null checks needed
  - Consistent insertion/deletion logic
  - Simplified code
```

## 🎯 Common Interview Questions

### Q1: Why not use LinkedHashMap?

**Answer:**
Java's `LinkedHashMap` with `accessOrder=true` provides LRU behavior, but:
- Less educational (hides implementation details)
- In interviews, you're expected to implement from scratch
- Custom implementation allows modifications (e.g., TTL, weighted LRU)

### Q2: How to make it thread-safe?

**Answer:**
```java
// Option 1: Synchronized methods
public synchronized int get(int key) { ... }
public synchronized void put(int key, int value) { ... }

// Option 2: ReentrantReadWriteLock
private final ReadWriteLock lock = new ReentrantReadWriteLock();

public int get(int key) {
    lock.readLock().lock();
    try { ... } finally { lock.readLock().unlock(); }
}

// Option 3: ConcurrentHashMap + synchronized list operations
```

### Q3: What if we need LRU with expiration (TTL)?

**Answer:**
Add timestamp to each node:
```java
class Node {
    String key;
    int value;
    long timestamp;  // When item was added/accessed
    Node prev, next;
}

// Check expiration on get()
if (System.currentTimeMillis() - node.timestamp > TTL) {
    remove(node);
    return -1;
}
```

### Q4: How to implement LFU (Least Frequently Used) instead?

**Answer:**
LFU tracks access frequency instead of recency:
- Each node has a frequency counter
- Maintain frequency buckets (HashMap<Integer, DoublyLinkedList>)
- Evict from lowest frequency bucket

## 🔄 Variations and Extensions

### 1. LRU with Size-based Eviction

Instead of counting items, track total size:
```java
class LRUCache {
    private long currentSize;
    private long maxSize;

    void put(String key, byte[] value) {
        while (currentSize + value.length > maxSize) {
            evictLRU();
        }
        // ... insert
        currentSize += value.length;
    }
}
```

### 2. LRU with Priority

Some items are more important:
```java
class Node {
    String key;
    int value;
    int priority;  // Higher priority = harder to evict
}

// Evict lowest priority LRU item
```

### 3. 2Q (Two Queue) LRU

Separate queues for first-time and repeated access:
- Prevents cache pollution from one-time accesses
- Used in database systems

### 4. ARC (Adaptive Replacement Cache)

Balances between recency and frequency:
- Maintains two LRU lists
- Adapts to workload patterns

## 📊 Comparison with Other Cache Policies

| Policy | Eviction Strategy | Use Case | Complexity |
|--------|------------------|----------|------------|
| **LRU** | Least Recently Used | General purpose, temporal locality | O(1) |
| **LFU** | Least Frequently Used | Frequency matters more than recency | O(log n) |
| **FIFO** | First In First Out | Simple, no access pattern tracking | O(1) |
| **Random** | Random eviction | Simple, no overhead | O(1) |
| **MRU** | Most Recently Used | Specific patterns (e.g., sequential scan) | O(1) |

## 🎓 Real-World Examples

### 1. Redis LRU

Redis uses **approximate LRU**:
- Samples random keys instead of tracking all
- Trade-off: Less accurate but more memory efficient
- Good enough for most use cases

### 2. Linux Page Cache

Linux kernel uses LRU for page replacement:
- Active and inactive lists
- Second-chance algorithm
- Handles millions of pages efficiently

### 3. Browser Cache

Browsers use LRU-like policies:
- Cache recently visited pages
- Evict old pages when cache is full
- Combined with other factors (size, type)

### 4. CDN Edge Caching

CDNs cache popular content:
- LRU for automatic cache management
- Combined with popularity metrics
- Geographic considerations

## 💡 Key Takeaways

1. **LRU Cache = HashMap + Doubly Linked List**
   - HashMap for O(1) lookup
   - Doubly Linked List for O(1) insertion/deletion

2. **Both get() and put() are O(1)**
   - No iteration needed
   - Direct pointer manipulation

3. **Dummy nodes simplify implementation**
   - No null checks
   - Consistent logic

4. **Store key in node for eviction**
   - Need key to remove from HashMap
   - Node only has value reference

5. **Move to head = mark as recently used**
   - Head = most recent
   - Tail = least recent

6. **Evict from tail**
   - Tail.prev is the LRU item
   - Remove from both list and HashMap

## 🚀 Next Steps

1. **Implement the basic LRU Cache** (see `LRUCache.java`)
2. **Write comprehensive tests** (see `LRUCacheTest.java`)
3. **Try variations**:
   - Thread-safe version
   - TTL-based eviction
   - Size-based eviction
4. **Optimize**:
   - Generic types (LRUCache<K, V>)
   - Custom eviction callbacks
   - Statistics tracking (hit rate, miss rate)

## 📚 Further Reading

- **LeetCode Problem**: [146. LRU Cache](https://leetcode.com/problems/lru-cache/)
- **System Design**: Cache strategies in distributed systems
- **Operating Systems**: Page replacement algorithms
- **Database Systems**: Buffer pool management


