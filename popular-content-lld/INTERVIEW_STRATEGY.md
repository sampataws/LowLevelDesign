# Popular Content Tracker - Interview Strategy

Complete guide to solving the popular content tracking interview question in 45-60 minutes.

## 📋 Problem Overview

**Question:** Design a system that tracks content popularity from a stream and returns the most popular content at any time.

**Requirements:**
- Stream of content IDs with actions (increase/decrease popularity)
- Get most popular content ID at any time
- Return -1 if no content has popularity > 0
- Content IDs are positive integers
- Handle real-time streaming efficiently

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
**A:** Process actions (increase/decrease), get most popular

**Q2:** "How frequent are queries vs updates?"  
**A:** Both are frequent - need to optimize both

**Q3:** "What happens when popularity reaches 0?"  
**A:** Content should be removed from tracking

**Q4:** "What if multiple contents have same max popularity?"  
**A:** Return any one of them

**Q5:** "Do we need thread safety?"  
**A:** Yes, multiple threads will access the system

**Q6:** "What's the expected data volume?"  
**A:** Millions of content IDs, need efficient memory

### Example to Discuss

```
Stream:
[101, INCREASE]  → Most popular: 101
[102, INCREASE]  → Most popular: 101 or 102
[101, INCREASE]  → Most popular: 101
[101, DECREASE]  → Most popular: 101 or 102
[101, DECREASE]  → Most popular: 102
[102, DECREASE]  → Most popular: -1 (none)
```

**Say:** "Let me verify my understanding with an example..."

## 🏗️ Phase 2: Design Data Structures (10 min)

### Step 1: Analyze Requirements

**Say:** "Let me analyze the complexity requirements:"

| Operation | Frequency | Target Complexity |
|-----------|-----------|-------------------|
| getMostPopular() | Very High | O(1) or O(log N) |
| processAction() | High | O(log N) acceptable |

### Step 2: Consider Approaches

**Approach 1: HashMap Only**
```java
Map<Integer, Integer> contentPopularity;

// getMostPopular() - O(N) ❌
int max = Collections.max(contentPopularity.values());
```
**Problem:** O(N) for most popular queries - too slow!

**Approach 2: Max Heap**
```java
PriorityQueue<Content> maxHeap;

// getMostPopular() - O(1) ✓
// processAction() - Can't update efficiently ❌
```
**Problem:** Can't update popularity in heap efficiently!

**Approach 3: HashMap + TreeMap (Optimal)**
```java
Map<Integer, Integer> contentPopularity;  // contentId → popularity
TreeMap<Integer, Set<Integer>> popularityToContents;  // popularity → contentIds

// getMostPopular() - O(1) ✓
// processAction() - O(log N) ✓
```
**Solution:** Use both! HashMap for lookups, TreeMap for max queries.

**Say:** "I'll use HashMap + TreeMap to optimize both operations."

### Step 3: Design Classes

**ContentAction:**
```java
enum ContentAction {
    INCREASE_POPULARITY,
    DECREASE_POPULARITY
}
```

**PopularContentTracker:**
```java
class PopularContentTracker {
    Map<Integer, Integer> contentPopularity;
    TreeMap<Integer, Set<Integer>> popularityToContents;
    
    void processAction(int contentId, ContentAction action);
    int getMostPopular();
    int getPopularity(int contentId);
}
```

## 💻 Phase 3: Implement Core Functionality (20 min)

### Implementation

```java
public class PopularContentTracker {
    private final Map<Integer, Integer> contentPopularity;
    private final TreeMap<Integer, Set<Integer>> popularityToContents;
    
    public PopularContentTracker() {
        this.contentPopularity = new HashMap<>();
        // TreeMap with reverse order (highest first)
        this.popularityToContents = new TreeMap<>(Collections.reverseOrder());
    }
    
    /**
     * Processes an action on content.
     * Time: O(log N)
     */
    public void processAction(int contentId, ContentAction action) {
        if (contentId <= 0) {
            throw new IllegalArgumentException("Content ID must be positive");
        }
        
        int currentPopularity = contentPopularity.getOrDefault(contentId, 0);
        int newPopularity;
        
        if (action == ContentAction.INCREASE_POPULARITY) {
            newPopularity = currentPopularity + 1;
        } else {
            newPopularity = currentPopularity - 1;
        }
        
        // Remove from old popularity bucket
        if (currentPopularity != 0) {
            Set<Integer> oldBucket = popularityToContents.get(currentPopularity);
            if (oldBucket != null) {
                oldBucket.remove(contentId);
                if (oldBucket.isEmpty()) {
                    popularityToContents.remove(currentPopularity);
                }
            }
        }
        
        // Update popularity
        if (newPopularity <= 0) {
            contentPopularity.remove(contentId);
        } else {
            contentPopularity.put(contentId, newPopularity);
            popularityToContents
                .computeIfAbsent(newPopularity, k -> new HashSet<>())
                .add(contentId);
        }
    }
    
    /**
     * Gets most popular content ID.
     * Time: O(1)
     */
    public int getMostPopular() {
        if (popularityToContents.isEmpty()) {
            return -1;
        }
        
        Map.Entry<Integer, Set<Integer>> highestEntry = 
            popularityToContents.firstEntry();
        
        if (highestEntry == null || highestEntry.getValue().isEmpty()) {
            return -1;
        }
        
        return highestEntry.getValue().iterator().next();
    }
    
    /**
     * Gets popularity of content.
     * Time: O(1)
     */
    public int getPopularity(int contentId) {
        return contentPopularity.getOrDefault(contentId, 0);
    }
}
```

### Complexity Analysis

**Say:** "Let me analyze the complexity:"

- **processAction()**: O(log N)
  - HashMap get/put: O(1)
  - TreeMap remove: O(log N)
  - TreeMap put: O(log N)
  - Total: O(log N)

- **getMostPopular()**: O(1)
  - TreeMap firstEntry(): O(1)
  - Set iterator.next(): O(1)

- **getPopularity()**: O(1)
  - HashMap get: O(1)

- **Space**: O(N) where N = unique content IDs

## 🧪 Phase 4: Test with Examples (5 min)

### Test Case 1: Basic Operations

```java
tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
assert tracker.getMostPopular() == 101;
assert tracker.getPopularity(101) == 1;

tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
assert tracker.getPopularity(101) == 2;
```

### Test Case 2: Multiple Contents

```java
tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
tracker.processAction(2, ContentAction.INCREASE_POPULARITY);

assert tracker.getMostPopular() == 2;
assert tracker.getPopularity(2) == 2;
```

### Test Case 3: Automatic Cleanup

```java
tracker.processAction(301, ContentAction.INCREASE_POPULARITY);
assert tracker.getMostPopular() == 301;

tracker.processAction(301, ContentAction.DECREASE_POPULARITY);
assert tracker.getMostPopular() == -1;  // Removed
assert tracker.getPopularity(301) == 0;
```

**Say:** "The algorithm correctly handles all edge cases."

## 🔒 Phase 5: Thread Safety & Optimizations (10 min)

### Add Thread Safety

**Interviewer:** "How would you make this thread-safe?"

**Say:** "I'll use ReadWriteLock for better concurrent read performance."

```java
public class PopularContentTracker {
    private final Map<Integer, Integer> contentPopularity;
    private final TreeMap<Integer, Set<Integer>> popularityToContents;
    private final ReadWriteLock lock;
    
    public PopularContentTracker() {
        this.contentPopularity = new HashMap<>();
        this.popularityToContents = new TreeMap<>(Collections.reverseOrder());
        this.lock = new ReentrantReadWriteLock();
    }
    
    public void processAction(int contentId, ContentAction action) {
        lock.writeLock().lock();
        try {
            // ... update logic ...
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public int getMostPopular() {
        lock.readLock().lock();
        try {
            if (popularityToContents.isEmpty()) {
                return -1;
            }
            Map.Entry<Integer, Set<Integer>> highestEntry = 
                popularityToContents.firstEntry();
            return highestEntry.getValue().iterator().next();
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

1. **TreeMap with Reverse Order**: Highest popularity first
2. **Automatic Cleanup**: Remove content when popularity ≤ 0
3. **HashSet in Buckets**: Handle multiple contents with same popularity
4. **ReadWriteLock**: Concurrent read optimization

## 🎯 Phase 6: Trade-offs & Alternatives (5 min)

### Trade-offs

**Interviewer:** "What are the trade-offs of your design?"

**Say:** "Here are the key trade-offs:"

**Pros:**
- ✅ O(1) most popular queries
- ✅ O(log N) updates (acceptable)
- ✅ Automatic cleanup saves memory
- ✅ Thread-safe with good concurrent read performance
- ✅ Handles ties naturally

**Cons:**
- ❌ O(log N) updates (not O(1))
- ❌ 2x memory overhead (two data structures)
- ❌ More complex than single data structure

**Justification:**
"Since queries are very frequent, O(1) query time is critical. O(log N) updates are acceptable."

### Alternative Approaches

**Interviewer:** "What other approaches did you consider?"

**1. HashMap + Cached Max**
```java
Map<Integer, Integer> popularity;
int cachedMaxContentId;
int cachedMaxPopularity;

// Update: Need to recalculate if max changes
// Query: O(1) if cached
```
**Problem:** Complex cache invalidation

**2. Sorted Array**
```java
List<Content> sortedContents;

// Binary search for updates: O(N) due to insertion
// getMax: O(1)
```
**Problem:** O(N) updates

**3. Max Heap**
```java
PriorityQueue<Content> maxHeap;

// getMax: O(1)
// Update: Can't efficiently update heap
```
**Problem:** Can't update popularity efficiently

## 💡 Common Interview Questions

### Q1: "How would you get top K popular contents?"

**A:** "I'd iterate through TreeMap entries and collect K contents."

```java
public List<Integer> getTopK(int k) {
    lock.readLock().lock();
    try {
        List<Integer> result = new ArrayList<>();
        for (Set<Integer> contents : popularityToContents.values()) {
            result.addAll(contents);
            if (result.size() >= k) {
                return result.subList(0, k);
            }
        }
        return result;
    } finally {
        lock.readLock().unlock();
    }
}
```

### Q2: "What if we need to track popularity over time?"

**A:** "I'd add a timestamp to each action and maintain a time-series data structure."

```java
class PopularityEvent {
    int contentId;
    int popularity;
    long timestamp;
}

TreeMap<Long, PopularityEvent> timeline;
```

### Q3: "How would you handle popularity decay over time?"

**A:** "I'd use a scheduled task to periodically decrease old content popularity."

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    // Decrease popularity of old content
}, 1, 1, TimeUnit.HOURS);
```

### Q4: "What about memory constraints?"

**A:** "I'd add an LRU eviction policy or limit max tracked contents."

```java
if (contentPopularity.size() > MAX_CONTENTS) {
    evictLeastPopular();
}
```

### Q5: "How would you persist this data?"

**A:** "I'd use write-ahead logging (WAL) for durability and periodic snapshots."

## ✅ Interview Checklist

Before saying "I'm done":

- [ ] Clarified all requirements
- [ ] Explained design choice (HashMap + TreeMap)
- [ ] Implemented core operations
- [ ] Analyzed time/space complexity
- [ ] Tested with examples
- [ ] Added thread safety
- [ ] Discussed automatic cleanup
- [ ] Mentioned alternative approaches
- [ ] Handled edge cases
- [ ] Code is clean and readable

## 🎓 Key Takeaways

### What Interviewers Look For

✅ **Problem Understanding**: Ask about query vs update frequency  
✅ **Design Choice**: Justify dual data structure approach  
✅ **Complexity Analysis**: Accurate O(1) and O(log N) analysis  
✅ **Edge Cases**: Handle empty, zero popularity, ties  
✅ **Thread Safety**: Understand ReadWriteLock benefits  

### Common Mistakes

❌ Using only HashMap (O(N) queries)  
❌ Using only heap (can't update efficiently)  
❌ Not handling popularity ≤ 0  
❌ Forgetting thread safety  
❌ Not optimizing for frequent queries  

### Success Tips

💡 **Optimize for common case**: Frequent queries → O(1) queries  
💡 **Use multiple data structures**: Each optimized for different operations  
💡 **Think about concurrency**: ReadWriteLock for read-heavy workloads  
💡 **Test edge cases**: Empty, zero, negative, ties  
💡 **Explain trade-offs**: Memory vs performance  

## 📚 Related Problems to Practice

1. **LRU Cache** (LeetCode 146)
2. **LFU Cache** (LeetCode 460)
3. **Top K Frequent Elements** (LeetCode 347)
4. **Design Twitter** (LeetCode 355)
5. **Leaderboard** (LeetCode 1244)

---

**Good luck with your interview! 🚀**

Remember: This problem tests your ability to choose the right data structures for specific performance requirements.

