# Customer Satisfaction System - Interview Strategy Guide

## 📝 Overview

This guide helps you solve the "Customer Satisfaction" interview question in a 45-60 minute coding interview. The problem is typically asked in multiple parts (a-e), with each part building on the previous one.

## ⏱️ Time Management (60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1. Clarification | 5 min | Understand requirements, ask questions |
| 2. Design | 10 min | Design data structures and API |
| 3. Part (a) | 10 min | Implement basic rating system |
| 4. Part (b) | 8 min | Add tie-breaking strategies |
| 5. Part (c) | 10 min | Add monthly tracking |
| 6. Part (d) | 10 min | Implement export functionality |
| 7. Part (e) | 5 min | Add unsorted/totals features |
| 8. Testing | 2 min | Quick validation |

## 📋 Phase 1: Clarification (5 minutes)

### Questions to Ask

1. **Rating Scale**:
   - "What's the rating scale?" → 1-5
   - "Can ratings be decimal?" → No, integers only
   - "What if invalid rating?" → Throw exception

2. **Agent Identification**:
   - "How are agents identified?" → String ID
   - "Can agent IDs be null/empty?" → No, validate

3. **Tie Breaking**:
   - "How to handle ties?" → Multiple strategies
   - "Default strategy?" → Agent ID alphabetical

4. **Monthly Tracking**:
   - "What defines a month?" → Calendar month (YearMonth)
   - "Timezone considerations?" → Use LocalDateTime

5. **Export**:
   - "Which formats?" → CSV, JSON, XML
   - "Export all data or filtered?" → All historical data

6. **Performance**:
   - "Expected scale?" → Thousands of agents, millions of ratings
   - "Thread safety needed?" → Yes

### What to Say

> "Let me clarify the requirements:
> - Ratings are 1-5 integers for support agents
> - We need to track ratings over time with monthly granularity
> - Multiple tie-breaking strategies for same average ratings
> - Export in CSV, JSON, and XML formats
> - System should be thread-safe for concurrent access
> 
> I'll design a system with three main data structures:
> 1. All ratings list for complete history
> 2. Agent-indexed map for fast agent queries
> 3. Month-indexed map for monthly performance
> 
> Does this align with your expectations?"

## 🏗️ Phase 2: Design (10 minutes)

### Data Structures

```java
// Core data model
class Rating {
    String agentId;
    int score;           // 1-5
    LocalDateTime timestamp;
}

class AgentStats {
    String agentId;
    int totalRatings;
    int totalScore;
    double averageRating;
}

// Main system
class CustomerSatisfactionSystem {
    List<Rating> allRatings;
    Map<String, List<Rating>> agentRatings;
    Map<YearMonth, Map<String, List<Rating>>> monthlyRatings;
    ReadWriteLock lock;
}
```

### API Design

```java
// Part (a)
void addRating(String agentId, int score)
List<AgentStats> getAllAgentsSorted()

// Part (b)
void setTieBreakStrategy(TieBreakStrategy strategy)

// Part (c)
List<AgentStats> getBestAgentsForMonth(YearMonth month)
Set<YearMonth> getAvailableMonths()

// Part (d)
String exportMonthlyAverages(ExportFormat format)

// Part (e)
List<AgentStats> getAllAgentsUnsorted()
String exportMonthlyTotals(ExportFormat format)
```

### What to Say

> "I'll use three data structures:
> 
> 1. **List<Rating>**: Complete history, chronological order
> 2. **Map<String, List<Rating>>**: Agent ID → ratings for fast lookup
> 3. **Map<YearMonth, Map<String, List<Rating>>>**: Monthly indexing
> 
> This gives us:
> - O(1) rating insertion
> - O(N log N) sorted retrieval (N = agents)
> - O(M log M) monthly queries (M = agents in month)
> 
> For thread safety, I'll use ReadWriteLock to allow concurrent reads."

## 💻 Phase 3: Part (a) - Basic Rating System (10 minutes)

### Implementation Order

1. **Rating class** (2 min):
```java
public class Rating {
    private final String agentId;
    private final int score;
    private final LocalDateTime timestamp;
    
    public Rating(String agentId, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be 1-5");
        }
        this.agentId = agentId;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }
}
```

2. **AgentStats class** (2 min):
```java
public class AgentStats {
    private final String agentId;
    private final int totalRatings;
    private final int totalScore;
    private final double averageRating;
    
    public AgentStats(String agentId, int totalRatings, int totalScore) {
        this.agentId = agentId;
        this.totalRatings = totalRatings;
        this.totalScore = totalScore;
        this.averageRating = totalRatings > 0 
            ? (double) totalScore / totalRatings 
            : 0.0;
    }
}
```

3. **addRating method** (3 min):
```java
public void addRating(String agentId, int score) {
    Rating rating = new Rating(agentId, score);
    lock.writeLock().lock();
    try {
        allRatings.add(rating);
        agentRatings.computeIfAbsent(agentId, k -> new ArrayList<>())
            .add(rating);
    } finally {
        lock.writeLock().unlock();
    }
}
```

4. **getAllAgentsSorted method** (3 min):
```java
public List<AgentStats> getAllAgentsSorted() {
    lock.readLock().lock();
    try {
        List<AgentStats> agentStatsList = new ArrayList<>();

        for (Map.Entry<String, List<Rating>> entry : agentRatings.entrySet()) {
            String agentId = entry.getKey();
            List<Rating> ratings = entry.getValue();
            int totalScore = 0;

            for (Rating rating : ratings) {
                totalScore += rating.getScore();
            }

            agentStatsList.add(new AgentStats(agentId, ratings.size(), totalScore));
        }

        // Sort the list in descending order of average rating
        Collections.sort(agentStatsList, new Comparator<AgentStats>() {
            @Override
            public int compare(AgentStats a, AgentStats b) {
                return Double.compare(b.getAverageRating(), a.getAverageRating());
            }
        });

        return agentStatsList;
    } finally {
        lock.readLock().unlock();
    }
}

```

### What to Say

> "I'm implementing the basic rating system:
> 
> 1. **Rating class**: Immutable, validates score 1-5
> 2. **AgentStats**: Calculates average from total/count
> 3. **addRating**: Stores in both lists, O(1) operation
> 4. **getAllAgentsSorted**: Streams, maps to stats, sorts by average
> 
> The sorting is descending by average rating."

## 🔗 Phase 4: Part (b) - Tie Breaking (8 minutes)

### Implementation

1. **TieBreakStrategy enum** (1 min):
```java
public enum TieBreakStrategy {
    AGENT_ID,        // Alphabetical
    TOTAL_RATINGS,   // More ratings = higher
    MOST_RECENT,     // Latest rating wins
    NONE             // Arbitrary order
}
```

2. **Update sorting logic** (5 min):
```java
private void sortByStrategy(List<AgentStats> stats) {
    stats.sort((a, b) -> {
        // First by average (descending)
        int avgCompare = Double.compare(
            b.getAverageRating(), 
            a.getAverageRating()
        );
        if (avgCompare != 0) return avgCompare;
        
        // Tie-breaking
        switch (tieBreakStrategy) {
            case AGENT_ID:
                return a.getAgentId().compareTo(b.getAgentId());
            case TOTAL_RATINGS:
                return Integer.compare(
                    b.getTotalRatings(), 
                    a.getTotalRatings()
                );
            case NONE:
                return 0;
            default:
                return a.getAgentId().compareTo(b.getAgentId());
        }
    });
}
```

3. **Test with example** (2 min):
```java
// Example: All have avg 4.0
system.addRating("charlie", 4);
system.addRating("alice", 4);
system.addRating("bob", 4);

system.setTieBreakStrategy(TieBreakStrategy.AGENT_ID);
// Result: alice, bob, charlie
```

### What to Say

> "For tie-breaking, I'm using the Strategy pattern:
> 
> 1. **Enum for strategies**: Type-safe, easy to extend
> 2. **Custom comparator**: First by average, then by strategy
> 3. **Multiple options**:
>    - AGENT_ID: Deterministic, alphabetical
>    - TOTAL_RATINGS: Rewards more feedback
>    - NONE: Arbitrary (faster)
> 
> This makes the system flexible for different business needs."

## 📅 Phase 5: Part (c) - Monthly Performance (10 minutes)

### Implementation

1. **Update Rating class** (2 min):
```java
public YearMonth getYearMonth() {
    return YearMonth.from(timestamp);
}
```

2. **Update addRating** (3 min):
```java
public void addRating(String agentId, int score, LocalDateTime timestamp) {
    Rating rating = new Rating(agentId, score, timestamp);
    lock.writeLock().lock();
    try {
        allRatings.add(rating);
        agentRatings.computeIfAbsent(agentId, k -> new ArrayList<>())
            .add(rating);
        
        // Add to monthly index
        YearMonth yearMonth = rating.getYearMonth();
        monthlyRatings.computeIfAbsent(yearMonth, k -> new HashMap<>())
            .computeIfAbsent(agentId, k -> new ArrayList<>())
            .add(rating);
    } finally {
        lock.writeLock().unlock();
    }
}
```

3. **getBestAgentsForMonth** (5 min):
```java
public List<AgentStats> getBestAgentsForMonth(YearMonth yearMonth) {
    lock.readLock().lock();
    try {
        Map<String, List<Rating>> monthData = monthlyRatings.get(yearMonth);
        if (monthData == null) return Collections.emptyList();
        
        List<AgentStats> stats = monthData.entrySet().stream()
            .map(entry -> {
                String agentId = entry.getKey();
                List<Rating> ratings = entry.getValue();
                int totalScore = ratings.stream()
                    .mapToInt(Rating::getScore)
                    .sum();
                return new AgentStats(agentId, ratings.size(), totalScore);
            })
            .collect(Collectors.toList());
        
        sortByStrategy(stats);
        return stats;
    } finally {
        lock.readLock().unlock();
    }
}
```

### What to Say

> "For monthly tracking:
> 
> 1. **YearMonth as key**: Natural grouping, built-in Java class
> 2. **Nested map structure**: Month → Agent → Ratings
> 3. **Same sorting logic**: Reuse tie-breaking strategies
> 
> This allows:
> - Fast monthly queries: O(M log M) where M = agents in month
> - Historical analysis: All months preserved
> - Consistent behavior: Same sorting as overall stats"

## 📤 Phase 6: Part (d) - Export Functionality (10 minutes)

### Implementation

1. **ExportFormat enum** (1 min):
```java
public enum ExportFormat {
    CSV, JSON, XML
}
```

2. **Build data structure** (3 min):
```java
public String exportMonthlyAverages(ExportFormat format) {
    lock.readLock().lock();
    try {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();
        
        for (var monthEntry : monthlyRatings.entrySet()) {
            String monthStr = monthEntry.getKey().toString();
            for (var agentEntry : monthEntry.getValue().entrySet()) {
                String agentId = agentEntry.getKey();
                double avg = agentEntry.getValue().stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);
                
                data.computeIfAbsent(agentId, k -> new LinkedHashMap<>())
                    .put(monthStr, avg);
            }
        }
        
        return formatData(data, format);
    } finally {
        lock.readLock().unlock();
    }
}
```

3. **CSV export** (2 min):
```java
private String exportAsCSV(Map<String, Map<String, Double>> data) {
    StringBuilder sb = new StringBuilder();
    Set<String> months = new TreeSet<>();
    data.values().forEach(m -> months.addAll(m.keySet()));
    
    sb.append("AgentID");
    months.forEach(m -> sb.append(",").append(m));
    sb.append("\n");
    
    data.forEach((agent, monthData) -> {
        sb.append(agent);
        months.forEach(m -> {
            sb.append(",");
            Double avg = monthData.get(m);
            if (avg != null) sb.append(String.format("%.2f", avg));
        });
        sb.append("\n");
    });
    
    return sb.toString();
}
```

4. **JSON export** (2 min):
```java
private String exportAsJSON(Map<String, Map<String, Double>> data) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(data);
}
```

5. **XML export** (2 min):
```java
private String exportAsXML(Map<String, Map<String, Double>> data) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<agents>\n");
    
    data.forEach((agent, monthData) -> {
        sb.append("  <agent id=\"").append(agent).append("\">\n");
        monthData.forEach((month, avg) -> {
            sb.append("    <month name=\"").append(month).append("\">");
            sb.append(String.format("%.2f", avg));
            sb.append("</month>\n");
        });
        sb.append("  </agent>\n");
    });
    
    sb.append("</agents>");
    return sb.toString();
}
```

### What to Say

> "For export functionality:
> 
> 1. **Build unified data structure**: Agent → Month → Average
> 2. **Format-specific rendering**: CSV, JSON, XML
> 3. **CSV**: Header row + data rows, handles missing months
> 4. **JSON**: Use Gson library for clean output
> 5. **XML**: Manual building, proper structure
> 
> All formats include complete historical data."

## 📊 Phase 7: Part (e) - Unsorted & Totals (5 minutes)

### Implementation

1. **Unsorted results** (2 min):
```java
public List<AgentStats> getAllAgentsUnsorted() {
    lock.readLock().lock();
    try {
        return agentRatings.entrySet().stream()
            .map(entry -> {
                String agentId = entry.getKey();
                List<Rating> ratings = entry.getValue();
                int totalScore = ratings.stream()
                    .mapToInt(Rating::getScore)
                    .sum();
                return new AgentStats(agentId, ratings.size(), totalScore);
            })
            .collect(Collectors.toList());
    } finally {
        lock.readLock().unlock();
    }
}
```

2. **Export totals** (3 min):
```java
public String exportMonthlyTotals(ExportFormat format) {
    // Similar to exportMonthlyAverages but use sum() instead of average()
    Map<String, Map<String, Integer>> data = new LinkedHashMap<>();
    
    for (var monthEntry : monthlyRatings.entrySet()) {
        String monthStr = monthEntry.getKey().toString();
        for (var agentEntry : monthEntry.getValue().entrySet()) {
            String agentId = agentEntry.getKey();
            int total = agentEntry.getValue().stream()
                .mapToInt(Rating::getScore)
                .sum();
            
            data.computeIfAbsent(agentId, k -> new LinkedHashMap<>())
                .put(monthStr, total);
        }
    }
    
    return formatTotals(data, format);
}
```

## ✅ Phase 8: Testing (2 minutes)

### Quick Validation

```java
// Test basic flow
system.addRating("alice", 5);
system.addRating("alice", 4);
system.addRating("bob", 3);

List<AgentStats> stats = system.getAllAgentsSorted();
// alice: 4.5, bob: 3.0

// Test monthly
LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
system.addRating("alice", 5, jan);

List<AgentStats> janStats = system.getBestAgentsForMonth(
    YearMonth.of(2024, 1)
);

// Test export
String csv = system.exportMonthlyAverages(ExportFormat.CSV);
```

## 🎯 Common Interview Questions

**Q: How to optimize for millions of ratings?**
> "Use database with indexes on (agentId, timestamp). Cache monthly aggregates. Paginate large results."

**Q: How to handle real-time updates?**
> "Maintain TreeMap sorted by average. Update incrementally. Use WebSocket for push notifications."

**Q: What if ratings can be updated/deleted?**
> "Add updateRating() and deleteRating() methods. Recalculate affected aggregates. Consider event sourcing."

**Q: How to prevent rating manipulation?**
> "Rate limiting per customer. Anomaly detection. Require verified purchases. Audit trail."

**Q: How to scale horizontally?**
> "Shard by agent ID. Use distributed cache (Redis). Aggregate in background jobs. CQRS pattern."

## 📝 Interview Checklist

- [ ] Clarified all requirements
- [ ] Designed data structures
- [ ] Implemented basic rating system
- [ ] Added tie-breaking strategies
- [ ] Implemented monthly tracking
- [ ] Added export in all formats
- [ ] Implemented unsorted/totals
- [ ] Added thread safety
- [ ] Tested with examples
- [ ] Discussed trade-offs
- [ ] Mentioned scalability

## 🎓 Success Tips

1. **Start simple**: Get part (a) working first
2. **Incremental**: Build on previous parts
3. **Communicate**: Explain your thinking
4. **Test as you go**: Validate each part
5. **Time management**: Don't over-optimize early
6. **Ask questions**: Clarify ambiguities
7. **Consider edge cases**: Empty data, ties, invalid input
8. **Discuss trade-offs**: Memory vs speed, simplicity vs features

---

**Remember**: The interviewer wants to see your problem-solving process, not just the final code. Communicate clearly, write clean code, and be ready to discuss alternatives!

