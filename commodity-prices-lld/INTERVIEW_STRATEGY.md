# Commodity Prices Tracker - Interview Strategy

Complete guide to solving the commodity price tracking interview question in 45-60 minutes.

## 📋 Problem Overview

**Question:** Design a system that tracks commodity prices from a stream and returns the maximum price at any time.

**Requirements:**
- Stream of `<timestamp, price>` data points
- Get max price at any time
- Handle out-of-order timestamps
- Handle duplicate timestamps (updates)
- Optimize for frequent reads and writes

## ⏱️ Time Management (45-60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1 | 5 min | Clarify requirements & constraints |
| 2 | 10 min | Design data structures |
| 3 | 20 min | Implement core functionality |
| 4 | 5 min | Test with examples |
| 5 | 10 min | Add thread safety & optimizations |
| 6 | 5 min | Discuss trade-offs & alternatives |

## 🎯 Phase 1: Clarify Requirements (5 min)

### Questions to Ask

**Q1:** "What operations need to be supported?"  
**A:** Update price, get max price, get price at timestamp

**Q2:** "How frequent are reads vs writes?"  
**A:** Reads are very frequent, writes are less frequent

**Q3:** "Can timestamps be out of order?"  
**A:** Yes, timestamps can arrive in any order

**Q4:** "What happens with duplicate timestamps?"  
**A:** Update the price at that timestamp

**Q5:** "Do we need thread safety?"  
**A:** Yes, multiple threads will access the system

**Q6:** "What's the expected data volume?"  
**A:** Millions of data points, need efficient memory usage

### Example to Discuss

```
Stream:
[1000, 100.00]
[3000, 105.00]  ← Out of order
[2000, 110.00]  ← Out of order
[2000, 95.00]   ← Duplicate (update)

Queries:
getMaxPrice() → 105.00
getPriceAt(2000) → 95.00
```

**Say:** "Let me verify my understanding with an example..."

## 🏗️ Phase 2: Design Data Structures (10 min)

### Step 1: Analyze Requirements

**Say:** "Let me analyze the complexity requirements:"

| Operation | Frequency | Target Complexity |
|-----------|-----------|-------------------|
| getMaxPrice() | Very High | O(1) |
| getPriceAt() | High | O(1) |
| update() | Medium | O(log N) acceptable |

### Step 2: Consider Approaches

**Approach 1: HashMap Only**
```java
Map<Long, Double> prices;

// getMaxPrice() - O(N) ❌
double max = Collections.max(prices.values());
```
**Problem:** O(N) for max queries - too slow!

**Approach 2: TreeSet Only**
```java
TreeSet<PriceDataPoint> prices;  // sorted by price

// getMaxPrice() - O(1) ✓
double max = prices.first().getPrice();

// getPriceAt(timestamp) - O(N) ❌
// Need to scan to find timestamp
```
**Problem:** O(N) for timestamp lookups!

**Approach 3: HashMap + TreeSet (Optimal)**
```java
Map<Long, Double> timestampToPrice;  // O(1) lookups
TreeSet<PriceDataPoint> pricesSorted;  // O(1) max

// getMaxPrice() - O(1) ✓
// getPriceAt() - O(1) ✓
// update() - O(log N) ✓
```
**Solution:** Use both! Trade write complexity for read performance.

**Say:** "I'll use a dual data structure approach to optimize for frequent reads."

### Step 3: Design Classes

**PriceDataPoint:**
```java
class PriceDataPoint implements Comparable<PriceDataPoint> {
    long timestamp;
    double price;
    
    // Sort by price (desc), then timestamp (asc)
    public int compareTo(PriceDataPoint other) {
        int priceCompare = Double.compare(other.price, this.price);
        if (priceCompare != 0) return priceCompare;
        return Long.compare(this.timestamp, other.timestamp);
    }
}
```

**CommodityPriceTracker:**
```java
class CommodityPriceTracker {
    Map<Long, Double> timestampToPriceMap;
    TreeSet<PriceDataPoint> pricesSortedByValue;
    
    void update(long timestamp, double price);
    Double getMaxPrice();
    Double getPriceAt(long timestamp);
}
```

## 💻 Phase 3: Implement Core Functionality (20 min)

### Implementation

```java
public class CommodityPriceTracker {
    private final Map<Long, Double> timestampToPriceMap;
    private final TreeSet<PriceDataPoint> pricesSortedByValue;
    
    public CommodityPriceTracker() {
        this.timestampToPriceMap = new HashMap<>();
        this.pricesSortedByValue = new TreeSet<>();
    }
    
    /**
     * Updates price at timestamp.
     * Handles duplicates by updating existing entry.
     * 
     * Time: O(log N)
     */
    public void update(long timestamp, double price) {
        // Check if timestamp exists
        Double oldPrice = timestampToPriceMap.get(timestamp);
        
        if (oldPrice != null) {
            // Remove old data point from TreeSet
            pricesSortedByValue.remove(
                new PriceDataPoint(timestamp, oldPrice)
            );
        }
        
        // Add new data point
        timestampToPriceMap.put(timestamp, price);
        pricesSortedByValue.add(
            new PriceDataPoint(timestamp, price)
        );
    }
    
    /**
     * Gets maximum price across all timestamps.
     * 
     * Time: O(1)
     */
    public Double getMaxPrice() {
        if (pricesSortedByValue.isEmpty()) {
            return null;
        }
        return pricesSortedByValue.first().getPrice();
    }
    
    /**
     * Gets price at specific timestamp.
     * 
     * Time: O(1)
     */
    public Double getPriceAt(long timestamp) {
        return timestampToPriceMap.get(timestamp);
    }
}
```

### Complexity Analysis

**Say:** "Let me analyze the complexity:"

- **update()**: O(log N)
  - HashMap put: O(1)
  - TreeSet remove: O(log N)
  - TreeSet add: O(log N)
  - Total: O(log N)

- **getMaxPrice()**: O(1)
  - TreeSet first(): O(1)

- **getPriceAt()**: O(1)
  - HashMap get: O(1)

- **Space**: O(N) where N = unique timestamps

## 🧪 Phase 4: Test with Examples (5 min)

### Test Case 1: Basic Operations

```java
tracker.update(1000, 100.00);
tracker.update(2000, 110.00);
tracker.update(3000, 105.00);

assert tracker.getMaxPrice() == 110.00;
assert tracker.getPriceAt(2000) == 110.00;
```

### Test Case 2: Out-of-Order

```java
tracker.update(5000, 120.00);
tracker.update(2000, 115.00);  // Earlier
tracker.update(8000, 125.00);  // Later

assert tracker.getMaxPrice() == 125.00;
```

### Test Case 3: Duplicate Timestamps

```java
tracker.update(1000, 100.00);
tracker.update(2000, 110.00);

assert tracker.getMaxPrice() == 110.00;

// Update existing
tracker.update(2000, 95.00);

assert tracker.getMaxPrice() == 100.00;  // Recalculated
assert tracker.getPriceAt(2000) == 95.00;  // Updated
```

**Say:** "The algorithm correctly handles all edge cases."

## 🔒 Phase 5: Thread Safety & Optimizations (10 min)

### Add Thread Safety

**Interviewer:** "How would you make this thread-safe?"

**Say:** "I'll use ReadWriteLock for better concurrent read performance."

```java
public class CommodityPriceTracker {
    private final Map<Long, Double> timestampToPriceMap;
    private final TreeSet<PriceDataPoint> pricesSortedByValue;
    private final ReadWriteLock lock;
    
    public CommodityPriceTracker() {
        this.timestampToPriceMap = new ConcurrentHashMap<>();
        this.pricesSortedByValue = new TreeSet<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    public void update(long timestamp, double price) {
        lock.writeLock().lock();
        try {
            // ... update logic ...
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public Double getMaxPrice() {
        lock.readLock().lock();
        try {
            if (pricesSortedByValue.isEmpty()) {
                return null;
            }
            return pricesSortedByValue.first().getPrice();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public Double getPriceAt(long timestamp) {
        lock.readLock().lock();
        try {
            return timestampToPriceMap.get(timestamp);
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

**Why ReadWriteLock?**
- Multiple concurrent reads (common case)
- Exclusive writes (less common)
- Better than synchronized for read-heavy workloads

### Optimizations

**Say:** "Here are some optimizations:"

1. **ConcurrentHashMap**: Already thread-safe for basic ops
2. **ReadWriteLock**: Allows concurrent reads
3. **TreeSet**: Red-black tree for O(log N) operations
4. **Lazy Deletion**: Could batch TreeSet updates

## 🎯 Phase 6: Trade-offs & Alternatives (5 min)

### Trade-offs

**Interviewer:** "What are the trade-offs of your design?"

**Say:** "Here are the key trade-offs:"

**Pros:**
- ✅ O(1) max price queries (optimized for frequent reads)
- ✅ O(1) timestamp lookups
- ✅ Handles out-of-order timestamps naturally
- ✅ Handles duplicates efficiently
- ✅ Thread-safe with good concurrent read performance

**Cons:**
- ❌ O(log N) updates (TreeSet operations)
- ❌ 2x memory overhead (two data structures)
- ❌ More complex than single data structure

**Justification:**
"Since reads are much more frequent than writes, trading O(log N) writes for O(1) reads is the right choice."

### Alternative Approaches

**Interviewer:** "What other approaches did you consider?"

**1. HashMap + Cached Max**
```java
Map<Long, Double> prices;
Double cachedMax;

// Update: O(1) but need to recalculate max if max changes
// getMaxPrice: O(1) if cached, O(N) if invalidated
```
**Problem:** Cache invalidation is complex

**2. Sorted Array**
```java
List<PriceDataPoint> sortedPrices;

// Binary search for updates: O(N) due to insertion
// getMax: O(1)
```
**Problem:** O(N) updates

**3. Max Heap + HashMap**
```java
PriorityQueue<PriceDataPoint> maxHeap;
Map<Long, Double> prices;

// getMax: O(1)
// Update: Can't efficiently remove from heap
```
**Problem:** Can't update heap efficiently

## 💡 Common Interview Questions

### Q1: "How would you handle price deletions?"

**A:** "I'd add a remove() method that removes from both data structures in O(log N) time."

```java
public boolean remove(long timestamp) {
    lock.writeLock().lock();
    try {
        Double price = timestampToPriceMap.remove(timestamp);
        if (price != null) {
            pricesSortedByValue.remove(new PriceDataPoint(timestamp, price));
            return true;
        }
        return false;
    } finally {
        lock.writeLock().unlock();
    }
}
```

### Q2: "What if we need min price too?"

**A:** "TreeSet already maintains sorted order. I can get min with last() in O(1)."

```java
public Double getMinPrice() {
    lock.readLock().lock();
    try {
        if (pricesSortedByValue.isEmpty()) {
            return null;
        }
        return pricesSortedByValue.last().getPrice();
    } finally {
        lock.readLock().unlock();
    }
}
```

### Q3: "How would you handle time-range queries?"

**A:** "I'd add a TreeMap sorted by timestamp for efficient range queries."

```java
TreeMap<Long, Double> timestampSorted;

public List<PriceDataPoint> getPricesInRange(long start, long end) {
    return timestampSorted.subMap(start, end).entrySet()
        .stream()
        .map(e -> new PriceDataPoint(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
}
```

### Q4: "What about memory constraints?"

**A:** "I could add an LRU eviction policy or time-based expiration."

```java
// Evict old timestamps
if (timestampToPriceMap.size() > MAX_SIZE) {
    evictOldest();
}
```

### Q5: "How would you persist this data?"

**A:** "I'd use write-ahead logging (WAL) for durability and periodic snapshots for recovery."

## ✅ Interview Checklist

Before saying "I'm done":

- [ ] Clarified all requirements
- [ ] Explained design choice (dual data structure)
- [ ] Implemented core operations
- [ ] Analyzed time/space complexity
- [ ] Tested with examples
- [ ] Added thread safety
- [ ] Discussed trade-offs
- [ ] Mentioned alternative approaches
- [ ] Handled edge cases
- [ ] Code is clean and readable

## 🎓 Key Takeaways

### What Interviewers Look For

✅ **Problem Understanding**: Ask about read/write frequency  
✅ **Design Choice**: Justify dual data structure approach  
✅ **Complexity Analysis**: Accurate O(1) and O(log N) analysis  
✅ **Thread Safety**: Understand ReadWriteLock benefits  
✅ **Trade-offs**: Explain memory vs performance trade-off  

### Common Mistakes

❌ Using only HashMap (O(N) max queries)  
❌ Using only TreeSet (O(log N) timestamp lookups)  
❌ Not handling duplicate timestamps  
❌ Forgetting thread safety  
❌ Not optimizing for read-heavy workload  

### Success Tips

💡 **Optimize for the common case**: Frequent reads → O(1) reads  
💡 **Use multiple data structures**: Each optimized for different operations  
💡 **Think about concurrency**: ReadWriteLock for read-heavy workloads  
💡 **Test edge cases**: Empty, duplicates, out-of-order  
💡 **Explain trade-offs**: Memory vs performance  

## 📚 Related Problems to Practice

1. **LRU Cache** (LeetCode 146)
2. **Stock Price Fluctuation** (LeetCode 2034)
3. **Time Based Key-Value Store** (LeetCode 981)
4. **Design In-Memory File System** (LeetCode 588)
5. **Design Hit Counter** (LeetCode 362)

---

**Good luck with your interview! 🚀**

Remember: This problem tests your ability to choose the right data structures for specific performance requirements.

