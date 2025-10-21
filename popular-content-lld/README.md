# Popular Content Tracker - Low Level Design

A high-performance, thread-safe content popularity tracking system optimized for real-time streaming with instant most popular queries.

## 📋 Problem Statement

Design a system that:
- Receives a stream of content IDs with associated actions
- Actions: `increasePopularity` (likes/comments) or `decreasePopularity` (spam removal)
- Returns the most popular content ID at any time
- Content IDs are positive integers
- Returns -1 if no content has popularity > 0
- Handles real-time streaming efficiently

## 🎯 Requirements

### Functional Requirements
1. **Process Actions**: Handle increase/decrease popularity actions
2. **Get Most Popular**: Return content ID with highest popularity
3. **Automatic Cleanup**: Remove content when popularity ≤ 0
4. **Real-time Updates**: Reflect changes immediately
5. **Handle Ties**: Return any one content if multiple have same max popularity

### Non-Functional Requirements
1. **Performance**: Fast action processing and queries
2. **Thread Safety**: Support concurrent operations
3. **Memory Efficient**: O(N) space where N = active content IDs
4. **Scalability**: Handle millions of content IDs

## 🏗️ Architecture

### Design Decisions

**Dual Data Structure Approach:**
1. **HashMap** (`Map<Integer, Integer>`)
   - Maps content ID → popularity score
   - O(1) popularity lookups
   - O(1) updates

2. **TreeMap** (`TreeMap<Integer, Set<Integer>>`)
   - Maps popularity → set of content IDs
   - Sorted in descending order (highest first)
   - O(log N) insertions/deletions
   - O(1) max popularity queries

**Why This Design?**
- HashMap alone: O(N) to find max popularity ❌
- TreeSet of contents: O(N) to update popularity ❌
- **Both together**: O(log N) updates, O(1) max queries ✅

### Core Classes

#### 1. **ContentAction**
Enum representing actions on content.

```java
enum ContentAction {
    INCREASE_POPULARITY,  // +1 (likes, comments)
    DECREASE_POPULARITY   // -1 (spam removal)
}
```

#### 2. **PopularContentTracker**
Main tracker with dual data structure design.

```java
class PopularContentTracker {
    Map<Integer, Integer> contentPopularity;  // contentId → popularity
    TreeMap<Integer, Set<Integer>> popularityToContents;  // popularity → contentIds
    ReadWriteLock lock;  // Thread safety
    
    void processAction(int contentId, ContentAction action);  // O(log N)
    int getMostPopular();  // O(1)
    int getPopularity(int contentId);  // O(1)
}
```

## 🚀 Usage Examples

### Basic Operations

```java
PopularContentTracker tracker = new PopularContentTracker();

// Increase popularity
tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
tracker.processAction(101, ContentAction.INCREASE_POPULARITY);
tracker.processAction(102, ContentAction.INCREASE_POPULARITY);

// Get most popular - O(1)
int mostPopular = tracker.getMostPopular();  // 101 (popularity: 2)

// Get specific popularity
int popularity = tracker.getPopularity(101);  // 2
```

### Popularity Fluctuations

```java
// Build up popularity
for (int i = 0; i < 10; i++) {
    tracker.processAction(201, ContentAction.INCREASE_POPULARITY);
}
System.out.println(tracker.getPopularity(201));  // 10

// Spam removal
for (int i = 0; i < 5; i++) {
    tracker.processAction(201, ContentAction.DECREASE_POPULARITY);
}
System.out.println(tracker.getPopularity(201));  // 5
```

### Automatic Cleanup

```java
tracker.processAction(301, ContentAction.INCREASE_POPULARITY);
System.out.println(tracker.getPopularity(301));  // 1

// Decrease to zero - content automatically removed
tracker.processAction(301, ContentAction.DECREASE_POPULARITY);
System.out.println(tracker.getPopularity(301));  // 0
System.out.println(tracker.size());  // 0 (content removed)
```

### Real-time Streaming

```java
// Simulate real-time stream
int[][] stream = {
    {101, 1}, {102, 1}, {101, 1},  // +1 actions
    {103, 1}, {101, -1}, {102, 1}  // mixed actions
};

for (int[] action : stream) {
    int contentId = action[0];
    ContentAction act = action[1] == 1 
        ? ContentAction.INCREASE_POPULARITY 
        : ContentAction.DECREASE_POPULARITY;
    
    tracker.processAction(contentId, act);
    
    // Query most popular anytime
    int mostPopular = tracker.getMostPopular();
    System.out.println("Most popular: " + mostPopular);
}
```

### Multiple Contents with Same Popularity

```java
tracker.processAction(1, ContentAction.INCREASE_POPULARITY);
tracker.processAction(2, ContentAction.INCREASE_POPULARITY);
tracker.processAction(3, ContentAction.INCREASE_POPULARITY);

// All have popularity 1
List<Integer> allMostPopular = tracker.getAllMostPopular();
System.out.println(allMostPopular);  // [1, 2, 3]

// getMostPopular() returns any one
int mostPopular = tracker.getMostPopular();  // Could be 1, 2, or 3
```

## 🧮 Algorithm Details

### Process Action

**Algorithm:**
```
1. Acquire write lock
2. Get current popularity of content
3. Calculate new popularity:
   - INCREASE: current + 1
   - DECREASE: current - 1
4. Remove content from old popularity bucket
5. If new popularity > 0:
   - Update contentPopularity map
   - Add to new popularity bucket
6. Else:
   - Remove from contentPopularity map
7. Release write lock
```

**Time Complexity:** O(log N)
- HashMap get/put: O(1)
- TreeMap remove: O(log N)
- TreeMap put: O(log N)

**Space Complexity:** O(1) per operation

### Get Most Popular

**Algorithm:**
```
1. Acquire read lock
2. Get first entry from TreeMap (highest popularity)
3. Return any content ID from that bucket
4. Release read lock
```

**Time Complexity:** O(1)
- TreeMap firstEntry(): O(1)
- Set iterator.next(): O(1)

**Space Complexity:** O(1)

## 📊 Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| processAction() | O(log N) | O(1) |
| getMostPopular() | O(1) | O(1) |
| getAllMostPopular() | O(K) | O(K) |
| getPopularity() | O(1) | O(1) |
| getMaxPopularity() | O(1) | O(1) |
| getStatistics() | O(N) | O(1) |
| Overall Space | - | O(N) |

Where:
- N = number of unique content IDs with popularity > 0
- K = number of contents with max popularity

## 🔒 Thread Safety

### ReadWriteLock Strategy

**Why ReadWriteLock?**
- Multiple concurrent reads (common case)
- Exclusive writes (less common)
- Better performance than synchronized

**Read Operations** (shared lock):
- `getMostPopular()`
- `getAllMostPopular()`
- `getPopularity()`
- `getMaxPopularity()`
- `size()`, `isEmpty()`
- `getStatistics()`

**Write Operations** (exclusive lock):
- `processAction()`
- `clear()`

### Concurrent Performance

```java
// Multiple threads can read simultaneously
Thread 1: tracker.getMostPopular()  ✓ Concurrent
Thread 2: tracker.getPopularity(101)  ✓ Concurrent
Thread 3: tracker.getMaxPopularity()  ✓ Concurrent

// Writes are exclusive
Thread 4: tracker.processAction(...)  ⏳ Waits for reads
```

## 🧪 Testing

### Test Coverage
- **PopularContentTrackerTest**: 35 tests
  - Basic operations (increase, decrease, get most popular)
  - Multiple contents with different/same popularities
  - Edge cases (empty tracker, zero/negative popularity)
  - Automatic cleanup when popularity ≤ 0
  - Validation (negative IDs, null actions)
  - Statistics calculation
  - Complex scenarios (fluctuations, removals)
  - Concurrent operations (reads, writes, mixed)

**Total: 35 comprehensive tests**

### Run Tests
```bash
mvn test -pl popular-content-lld
```

## 🎮 Demo Application

Run the comprehensive demo:
```bash
mvn exec:java -Dexec.mainClass="com.popularcontent.DriverApplication" -pl popular-content-lld
```

**Demo Scenarios:**
1. Basic operations
2. Multiple contents with same popularity
3. Popularity fluctuations and automatic cleanup
4. Concurrent operations (10 threads, 1000 operations)
5. Real-time streaming simulation

## 🎓 Interview Tips

### Key Points to Mention

1. **Design Choice:**
   - "I'll use HashMap for O(1) lookups and TreeMap for O(1) max queries"
   - "TreeMap sorted in descending order gives us instant access to highest popularity"

2. **Time Complexity:**
   - "Updates are O(log N) due to TreeMap operations"
   - "Most popular queries are O(1) - just get first entry from TreeMap"
   - "Popularity lookups are O(1) using HashMap"

3. **Automatic Cleanup:**
   - "Content is automatically removed when popularity drops to 0 or below"
   - "This keeps memory usage optimal - only tracking active content"

4. **Thread Safety:**
   - "ReadWriteLock allows multiple concurrent reads"
   - "Writers have exclusive access to maintain consistency"

5. **Edge Cases:**
   - Empty tracker (return -1)
   - Multiple contents with same max popularity (return any)
   - Decreasing from zero (stays at zero, not tracked)
   - Concurrent updates to same content

### Alternative Approaches

**Approach 1: HashMap Only**
- ❌ O(N) for most popular queries
- ✅ O(1) for updates
- **Not optimal for frequent queries**

**Approach 2: Max Heap**
- ❌ Can't efficiently update popularity
- ❌ Can't remove arbitrary elements
- **Not suitable for dynamic updates**

**Approach 3: Sorted List**
- ❌ O(N) for insertions
- ✅ O(1) for max queries
- **Not suitable for frequent updates**

**Approach 4: HashMap + TreeMap (Our Choice)**
- ✅ O(1) for most popular queries
- ✅ O(log N) for updates
- ✅ Handles all operations efficiently
- **Optimal for this use case**

## 🔧 Build and Run

```bash
# Build the module
mvn clean compile -pl popular-content-lld

# Run tests
mvn test -pl popular-content-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.popularcontent.DriverApplication" -pl popular-content-lld
```

## 📚 Related Concepts

- **Dual Data Structure Pattern**: Using multiple data structures for different operations
- **TreeMap**: Sorted map for ordered data
- **ReadWriteLock**: Concurrent read optimization
- **Streaming Data**: Real-time data processing
- **Popularity Tracking**: Social media metrics

## 🎯 Real-World Applications

- **Social Media**: Track trending posts, videos, stories
- **E-commerce**: Track popular products
- **News Platforms**: Track trending articles
- **Video Platforms**: Track viral videos
- **Gaming**: Track popular games/players
- **Content Moderation**: Track spam content for removal

---

**Status:** ✅ Complete - Optimized for real-time streaming with instant queries

[View Interview Strategy](./INTERVIEW_STRATEGY.md)

