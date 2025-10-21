# Customer Satisfaction System

A comprehensive customer support agent rating system that tracks agent performance, handles tie-breaking strategies, provides monthly analytics, and exports data in multiple formats.

## 📋 Problem Statement

Design a customer support ticketing system that:

### Part (a): Basic Rating System
- Accept customer ratings (1-5) for support agents
- Show all agents with their average ratings
- Sort agents from highest to lowest rating

### Part (b): Tie Breaking
- Handle cases where agents have the same average rating
- Implement multiple tie-breaking strategies:
  - By agent ID (alphabetical)
  - By total number of ratings
  - By most recent rating
  - No specific ordering

### Part (c): Monthly Performance
- Track agent performance by month
- Get best agents for any specific month
- Support historical analysis

### Part (d): Export Functionality
- Export agent ratings per month
- Support multiple formats: CSV, JSON, XML
- Include all historical data

### Part (e): Additional Features
- Return unsorted agent statistics
- Export total ratings (not averages)
- Flexible data retrieval

## 🏗️ Architecture

### Core Components

```
CustomerSatisfactionSystem
├── Rating                    # Immutable rating record
├── AgentStats               # Agent statistics (avg, total, count)
├── TieBreakStrategy         # Enum for tie-breaking options
├── ExportFormat             # Enum for export formats
└── CustomerSatisfactionSystem # Main tracking system
```

### Data Structures

```java
// All ratings chronologically
List<Rating> allRatings

// Agent ID → List of ratings
Map<String, List<Rating>> agentRatings

// YearMonth → Agent ID → List of ratings
Map<YearMonth, Map<String, List<Rating>>> monthlyRatings

// Thread safety
ReadWriteLock lock
```

## 🚀 Usage

### Basic Operations

```java
CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();

// Add ratings
system.addRating("alice", 5);
system.addRating("alice", 4);
system.addRating("bob", 3);

// Get all agents sorted by average rating
List<AgentStats> stats = system.getAllAgentsSorted();
for (AgentStats stat : stats) {
    System.out.printf("%s: %.2f (from %d ratings)%n",
        stat.getAgentId(), 
        stat.getAverageRating(), 
        stat.getTotalRatings());
}
```

### Tie Breaking

```java
// Set tie-break strategy
system.setTieBreakStrategy(TieBreakStrategy.AGENT_ID);
// or
system.setTieBreakStrategy(TieBreakStrategy.TOTAL_RATINGS);

// Get sorted results with tie-breaking applied
List<AgentStats> stats = system.getAllAgentsSorted();
```

### Monthly Performance

```java
// Add ratings with timestamps
LocalDateTime jan15 = LocalDateTime.of(2024, 1, 15, 10, 0);
system.addRating("alice", 5, jan15);

// Get best agents for January 2024
List<AgentStats> janStats = system.getBestAgentsForMonth(
    YearMonth.of(2024, 1)
);

// Get all available months
Set<YearMonth> months = system.getAvailableMonths();
```

### Export Data

```java
// Export monthly averages as CSV
String csv = system.exportMonthlyAverages(ExportFormat.CSV);

// Export as JSON
String json = system.exportMonthlyAverages(ExportFormat.JSON);

// Export as XML
String xml = system.exportMonthlyAverages(ExportFormat.XML);

// Export total ratings (not averages)
String totalsCsv = system.exportMonthlyTotals(ExportFormat.CSV);
```

### Unsorted Results

```java
// Get unsorted agent statistics
List<AgentStats> unsorted = system.getAllAgentsUnsorted();
```

## 📊 Example Output

### CSV Export
```csv
AgentID,2024-01,2024-02,2024-03
alice,4.50,3.50,5.00
bob,4.00,5.00,3.00
```

### JSON Export
```json
{
  "alice": {
    "2024-01": 4.5,
    "2024-02": 3.5,
    "2024-03": 5.0
  },
  "bob": {
    "2024-01": 4.0,
    "2024-02": 5.0,
    "2024-03": 3.0
  }
}
```

### XML Export
```xml
<?xml version="1.0" encoding="UTF-8"?>
<agents>
  <agent id="alice">
    <month name="2024-01">4.50</month>
    <month name="2024-02">3.50</month>
    <month name="2024-03">5.00</month>
  </agent>
  <agent id="bob">
    <month name="2024-01">4.00</month>
    <month name="2024-02">5.00</month>
    <month name="2024-03">3.00</month>
  </agent>
</agents>
```

## 🔧 Algorithm Details

### Adding Ratings
- **Time Complexity**: O(1)
- **Space Complexity**: O(1) per rating
- Stores in three data structures for different query patterns

### Getting Sorted Agents
- **Time Complexity**: O(N log N) where N = number of agents
- **Space Complexity**: O(N)
- Uses Java streams and custom comparator

### Monthly Performance
- **Time Complexity**: O(M log M) where M = agents in that month
- **Space Complexity**: O(M)
- Pre-indexed by YearMonth for fast retrieval

### Export Operations
- **Time Complexity**: O(A × M) where A = agents, M = months
- **Space Complexity**: O(A × M)
- Builds complete data structure then formats

## 🔒 Thread Safety

The system uses `ReadWriteLock` for thread safety:
- **Multiple concurrent readers**: Can read statistics simultaneously
- **Exclusive writers**: Only one thread can add ratings at a time
- **No data races**: All shared data structures are properly synchronized

## 🧪 Testing

### Test Coverage

**RatingTest** (14 tests):
- Valid ratings (1-5)
- Invalid scores (0, 6, negative)
- Null/empty agent IDs
- Timestamp handling
- YearMonth extraction

**AgentStatsTest** (8 tests):
- Average calculation
- Zero ratings handling
- Comparable implementation
- Tie-breaking logic

**CustomerSatisfactionSystemTest** (30+ tests):
- Basic rating operations
- Sorting by average
- All tie-breaking strategies
- Monthly performance tracking
- All export formats (CSV, JSON, XML)
- Unsorted results
- Total ratings export
- Concurrent operations

### Run Tests

```bash
# Run all tests
mvn test -pl customer-satisfaction-lld

# Run with coverage
mvn clean test jacoco:report -pl customer-satisfaction-lld
```

## 🎯 Design Decisions

### 1. Immutable Rating Class
- Thread-safe by design
- Prevents accidental modifications
- Clear data model

### 2. Three-Level Indexing
- `allRatings`: Complete history
- `agentRatings`: Fast agent lookup
- `monthlyRatings`: Fast monthly queries
- Trade-off: More memory for faster queries

### 3. Enum-Based Strategies
- Type-safe configuration
- Easy to extend
- Clear intent

### 4. Flexible Export
- Multiple formats supported
- Both averages and totals
- Easy to add new formats

### 5. ReadWriteLock
- Optimized for read-heavy workloads
- Better than synchronized for concurrent reads
- Prevents data corruption

## 🎓 Interview Tips

### Key Points to Mention

1. **Data Structure Choice**:
   - HashMap for O(1) agent lookup
   - Nested maps for monthly indexing
   - List for maintaining order

2. **Tie-Breaking Strategies**:
   - Multiple options show flexibility
   - Comparator pattern for custom sorting
   - Strategy pattern for behavior selection

3. **Monthly Tracking**:
   - YearMonth as key for natural grouping
   - Pre-indexing for performance
   - TreeSet for sorted month retrieval

4. **Export Formats**:
   - Builder pattern for CSV/XML
   - Gson for JSON
   - Easy to extend with new formats

5. **Thread Safety**:
   - ReadWriteLock for concurrent reads
   - Proper lock management
   - No data races

### Follow-up Questions

**Q: How would you handle millions of ratings?**
- Database storage with in-memory caching
- Pagination for large result sets
- Aggregation tables for monthly stats

**Q: How to handle deleted agents?**
- Soft delete flag
- Archive old ratings
- Separate active/inactive views

**Q: Real-time leaderboard updates?**
- Maintain sorted data structure (TreeMap)
- Incremental updates
- WebSocket for push notifications

**Q: How to detect rating manipulation?**
- Rate limiting per customer
- Anomaly detection algorithms
- Audit trail for all ratings

## 🚀 Running the Demo

```bash
# Compile
mvn clean compile -pl customer-satisfaction-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.customersatisfaction.DriverApplication" \
  -pl customer-satisfaction-lld

# Run tests
mvn test -pl customer-satisfaction-lld
```

## 📈 Complexity Summary

| Operation | Time | Space |
|-----------|------|-------|
| addRating() | O(1) | O(1) |
| getAllAgentsSorted() | O(N log N) | O(N) |
| getAllAgentsUnsorted() | O(N) | O(N) |
| getBestAgentsForMonth() | O(M log M) | O(M) |
| exportMonthlyAverages() | O(A × M) | O(A × M) |
| exportMonthlyTotals() | O(A × M) | O(A × M) |

Where:
- N = total number of agents
- M = number of agents in a specific month
- A = total agents, M = total months

## 🎯 Real-World Applications

- **Customer Support**: Track agent performance
- **E-commerce**: Seller ratings and reviews
- **Ride Sharing**: Driver ratings
- **Food Delivery**: Restaurant and delivery ratings
- **Freelance Platforms**: Contractor performance
- **Education**: Teacher evaluations

---

**Status**: ✅ Production-ready implementation with comprehensive testing and documentation

