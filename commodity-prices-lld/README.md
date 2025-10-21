# Commodity Prices Tracker - Low Level Design

A high-performance, thread-safe commodity price tracking system optimized for frequent reads and writes. Handles out-of-order timestamps and real-time price updates.

## 📋 Problem Statement

Design an in-memory system that:
- Receives a stream of `<timestamp, commodityPrice>` data points
- Returns the maximum commodity price at any point in time
- Handles out-of-order timestamps
- Handles duplicate timestamps (updates existing prices)
- Optimized for frequent reads and writes
- Thread-safe for concurrent operations

## 🎯 Requirements

### Functional Requirements
1. **Update Price**: Add or update price at a given timestamp
2. **Get Max Price**: Return maximum price across all timestamps (O(1))
3. **Get Price At Timestamp**: Return price at specific timestamp (O(1))
4. **Handle Out-of-Order**: Process timestamps in any order
5. **Handle Duplicates**: Update price if timestamp already exists

### Non-Functional Requirements
1. **Performance**: Optimized for frequent reads and writes
2. **Thread Safety**: Support concurrent operations
3. **Memory Efficient**: O(N) space where N = unique timestamps
4. **Real-time**: Low latency for all operations

## 🏗️ Architecture

### Design Decisions

**Dual Data Structure Approach:**
1. **HashMap** (`ConcurrentHashMap<Long, Double>`)
   - O(1) timestamp-to-price lookups
   - O(1) updates
   - Handles duplicate timestamps

2. **TreeSet** (`TreeSet<PriceDataPoint>`)
   - O(log N) insertions/deletions
   - O(1) max price queries (first element)
   - Automatically sorted by price (descending)

**Why This Design?**
- HashMap alone: O(N) for max price queries ❌
- TreeSet alone: O(log N) for timestamp lookups ❌
- **Both together**: O(1) reads, O(log N) writes ✅

### Core Classes

#### 1. **PriceDataPoint**
Immutable data point representing a price at a timestamp.

```java
class PriceDataPoint {
    long timestamp;
    double price;
    
    // Comparable: sorted by price (desc), then timestamp (asc)
    int compareTo(PriceDataPoint other);
}
```

#### 2. **CommodityPriceTracker**
Main tracker with dual data structure design.

```java
class CommodityPriceTracker {
    Map<Long, Double> timestampToPriceMap;  // O(1) lookups
    TreeSet<PriceDataPoint> pricesSortedByValue;  // O(1) max queries
    ReadWriteLock lock;  // Thread safety
    
    void update(long timestamp, double price);  // O(log N)
    Double getMaxPrice();  // O(1)
    Double getPriceAt(long timestamp);  // O(1)
}
```

## 🚀 Usage Examples

### Basic Operations

```java
CommodityPriceTracker tracker = new CommodityPriceTracker();

// Add prices
tracker.update(1000, 100.50);
tracker.update(2000, 105.75);
tracker.update(3000, 98.25);

// Get max price - O(1)
Double maxPrice = tracker.getMaxPrice();  // 105.75

// Get price at timestamp - O(1)
Double price = tracker.getPriceAt(2000);  // 105.75
```

### Out-of-Order Timestamps

```java
// Timestamps can arrive in any order
tracker.update(5000, 120.00);
tracker.update(2000, 115.00);  // Earlier timestamp
tracker.update(8000, 125.00);  // Later timestamp
tracker.update(1000, 110.00);  // Even earlier

// Max price is always correct
Double max = tracker.getMaxPrice();  // 125.00
```

### Duplicate Timestamps (Updates)

```java
tracker.update(1000, 100.00);
tracker.update(2000, 110.00);

System.out.println(tracker.getMaxPrice());  // 110.00

// Update existing timestamp
tracker.update(2000, 95.00);

System.out.println(tracker.getMaxPrice());  // 100.00 (recalculated)
System.out.println(tracker.getPriceAt(2000));  // 95.00 (updated)
```

### Real-time Streaming

```java
// Simulate real-time price stream
for (PriceUpdate update : priceStream) {
    tracker.update(update.timestamp, update.price);
    
    // Query max price anytime - O(1)
    Double currentMax = tracker.getMaxPrice();
    System.out.println("Current max: " + currentMax);
}
```

### Statistics

```java
CommodityPriceTracker.PriceStatistics stats = tracker.getStatistics();

System.out.println("Count: " + stats.getCount());
System.out.println("Min: " + stats.getMinPrice());
System.out.println("Max: " + stats.getMaxPrice());
System.out.println("Avg: " + stats.getAvgPrice());
```

## 🧮 Algorithm Details

### Update Operation

**Algorithm:**
```
1. Acquire write lock
2. Check if timestamp exists in HashMap
3. If exists:
   a. Remove old PriceDataPoint from TreeSet
4. Add new price to HashMap
5. Add new PriceDataPoint to TreeSet
6. Release write lock
```

**Time Complexity:** O(log N)
- HashMap put: O(1)
- TreeSet remove: O(log N)
- TreeSet add: O(log N)

**Space Complexity:** O(1) per operation

### Get Max Price

**Algorithm:**
```
1. Acquire read lock
2. Get first element from TreeSet
3. Return price
4. Release read lock
```

**Time Complexity:** O(1)
- TreeSet first(): O(1)

**Space Complexity:** O(1)

### Get Price At Timestamp

**Algorithm:**
```
1. Acquire read lock
2. Get price from HashMap
3. Release read lock
```

**Time Complexity:** O(1)
- HashMap get: O(1)

**Space Complexity:** O(1)

## 📊 Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| update() | O(log N) | O(1) |
| getMaxPrice() | O(1) | O(1) |
| getPriceAt() | O(1) | O(1) |
| remove() | O(log N) | O(1) |
| getStatistics() | O(N) | O(1) |
| Overall Space | - | O(N) |

Where N = number of unique timestamps

## 🔒 Thread Safety

### ReadWriteLock Strategy

**Why ReadWriteLock?**
- Multiple concurrent reads (common case)
- Exclusive writes (less common)
- Better performance than synchronized

**Read Operations** (shared lock):
- `getMaxPrice()`
- `getPriceAt()`
- `getStatistics()`
- `size()`, `isEmpty()`

**Write Operations** (exclusive lock):
- `update()`
- `remove()`
- `clear()`

### Concurrent Performance

```java
// Multiple threads can read simultaneously
Thread 1: tracker.getMaxPrice()  ✓ Concurrent
Thread 2: tracker.getMaxPrice()  ✓ Concurrent
Thread 3: tracker.getPriceAt(1000)  ✓ Concurrent

// Writes are exclusive
Thread 4: tracker.update(2000, 100.00)  ⏳ Waits for reads
```

## 🧪 Testing

### Test Coverage
- **CommodityPriceTrackerTest**: 38 tests
  - Basic operations
  - Out-of-order timestamps
  - Duplicate timestamp updates
  - Max price queries
  - Remove operations
  - Statistics
  - Concurrent operations
  - Validation

- **PriceDataPointTest**: 14 tests
  - Creation and validation
  - Comparison and sorting
  - Equality and hashing

**Total: 52 comprehensive tests**

### Run Tests
```bash
mvn test -pl commodity-prices-lld
```

## 🎮 Demo Application

Run the comprehensive demo:
```bash
mvn exec:java -Dexec.mainClass="com.commodityprices.DriverApplication" -pl commodity-prices-lld
```

**Demo Scenarios:**
1. Basic operations
2. Out-of-order timestamps
3. Duplicate timestamps (updates)
4. Concurrent reads and writes
5. Real-time streaming simulation

## 🎓 Interview Tips

### Key Points to Mention

1. **Design Choice:**
   - "I'll use a dual data structure approach: HashMap for O(1) lookups and TreeSet for O(1) max queries"
   - "This trades O(log N) writes for O(1) reads, which is optimal for frequent reads"

2. **Time Complexity:**
   - "Updates are O(log N) due to TreeSet operations"
   - "Max price queries are O(1) - just get first element from TreeSet"
   - "Timestamp lookups are O(1) using HashMap"

3. **Thread Safety:**
   - "I'll use ReadWriteLock for better concurrent read performance"
   - "Multiple readers can access simultaneously"
   - "Writers have exclusive access"

4. **Edge Cases:**
   - Empty tracker (return null)
   - Duplicate timestamps (update existing)
   - Out-of-order timestamps (handled naturally)
   - Concurrent updates to same timestamp

### Alternative Approaches

**Approach 1: HashMap Only**
- ❌ O(N) for max price queries
- ✅ O(1) for updates
- **Not optimal for frequent reads**

**Approach 2: TreeSet Only**
- ❌ O(log N) for timestamp lookups
- ✅ O(1) for max price
- **Not optimal for timestamp queries**

**Approach 3: HashMap + Max Heap**
- ❌ Can't efficiently remove from heap on updates
- ❌ Complex to maintain heap invariant
- **Not suitable for updates**

**Approach 4: HashMap + TreeSet (Our Choice)**
- ✅ O(1) for max price
- ✅ O(1) for timestamp lookups
- ✅ O(log N) for updates
- ✅ Handles duplicates efficiently
- **Optimal for this use case**

## 🔧 Build and Run

```bash
# Build the module
mvn clean compile -pl commodity-prices-lld

# Run tests
mvn test -pl commodity-prices-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.commodityprices.DriverApplication" -pl commodity-prices-lld
```

## 📚 Related Concepts

- **Dual Data Structure Pattern**: Using multiple data structures for different operations
- **ReadWriteLock**: Concurrent read optimization
- **TreeSet**: Self-balancing BST for sorted data
- **ConcurrentHashMap**: Thread-safe hash table
- **Streaming Data**: Real-time data processing
- **Time Series Data**: Timestamp-based data management

## 🎯 Real-World Applications

- **Stock Price Tracking**: Real-time stock price monitoring
- **Cryptocurrency Exchanges**: Price tracking and analysis
- **Commodity Trading**: Gold, oil, wheat price tracking
- **Sensor Data**: IoT sensor readings over time
- **Metrics Monitoring**: System metrics tracking
- **Financial Analytics**: Price trend analysis

---

**Status:** ✅ Complete - Optimized for frequent reads and writes

[View Interview Strategy](./INTERVIEW_STRATEGY.md)

