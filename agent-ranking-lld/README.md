# Customer Support Agent Ranking System - Low Level Design

A production-ready system for ranking customer support agents based on customer ratings and feedback.

## 📋 Overview

This project implements a comprehensive agent ranking system that tracks customer support agents, collects customer ratings, calculates average ratings, and maintains real-time rankings. Perfect for interview preparation and understanding ranking system design.

## 🎯 Features

- **Agent Management**: Register and track customer support agents
- **Rating System**: Collect 1-5 star ratings from customers
- **Real-time Rankings**: Automatic ranking updates based on average ratings
- **Thread-Safe**: Concurrent rating submissions and ranking calculations
- **Leaderboard Views**: Beautiful formatted leaderboards
- **Department Rankings**: Rank agents within departments
- **Statistics**: Comprehensive agent and system statistics
- **Rating Distribution**: Track 5-star, 4-star, etc. counts

## 🏗️ Architecture

```
Agent (Data Model)
    ↓
Rating (Customer Feedback)
    ↓
AgentStats (Statistics & Ranking)
    ↓
RankingSystem (Core Logic)
    ↓
LeaderboardView (Display)
```

### Core Components

1. **Agent**: Represents a customer support agent
   - Agent ID, name, email, department
   - Immutable data class

2. **Rating**: Customer feedback for an agent
   - 1-5 star rating
   - Timestamp, customer ID, interaction ID
   - Validation for score range

3. **AgentStats**: Statistics for each agent
   - Total ratings, average rating, rank
   - Rating distribution (5-star, 4-star, etc.)
   - Implements Comparable for sorting

4. **RankingSystem**: Main system managing everything
   - Agent registration
   - Rating submission
   - Ranking calculation
   - Thread-safe operations

5. **LeaderboardView**: Formatted display
   - Top N agents
   - Agent details
   - Department leaderboards
   - System statistics

## 🚀 Quick Start

### Build and Run

```bash
cd agent-ranking-lld
mvn clean compile
```

### Run Demo

```bash
mvn exec:java -Dexec.mainClass="com.agentranking.DriverApplication"
```

### Run Tests

```bash
mvn test
```

## 📝 Usage Examples

### Basic Usage

```java
// Create ranking system
RankingSystem system = new RankingSystem();

// Register agents
Agent alice = new Agent("A001", "Alice Johnson", "alice@co.com", "Support");
system.registerAgent(alice);

// Submit ratings
Rating rating = new Rating("R001", "A001", "CUST123", 5, "Excellent!", "INT001");
system.submitRating(rating);

// Get agent rank
int rank = system.getAgentRank("A001");
System.out.println("Alice's rank: " + rank);

// Get top agents
List<AgentStats> top5 = system.getTopAgents(5);
```

### Display Leaderboard

```java
LeaderboardView view = new LeaderboardView(system);

// Display top 10 agents
System.out.println(view.displayTopAgents(10));

// Display agent details
System.out.println(view.displayAgentDetails("A001"));

// Display department leaderboard
System.out.println(view.displayDepartmentLeaderboard("Technical Support"));
```

### Real-time Updates

```java
// Initial ranking
System.out.println("Bob's rank: " + system.getAgentRank("A002"));

// Submit new ratings
submitRatings(system, "A002", new int[]{5, 5, 5, 5, 5});

// Ranking automatically updated
System.out.println("Bob's new rank: " + system.getAgentRank("A002"));
```

## 🎯 Ranking Algorithm

### Primary Sort: Average Rating (Descending)
- Higher average rating = better rank
- Calculated as: `totalScore / totalRatings`

### Tiebreaker: Total Ratings (Descending)
- If average ratings are equal, more ratings = better rank
- Indicates more experience and consistency

### Example:
```
Agent A: 4.8 avg (100 ratings) → Rank 1
Agent B: 4.8 avg (50 ratings)  → Rank 2
Agent C: 4.5 avg (200 ratings) → Rank 3
```

## 📊 Key Features Explained

### 1. Thread Safety

- **ConcurrentHashMap**: For agent and stats storage
- **Synchronized Methods**: In AgentStats for atomic updates
- **Ranking Lock**: For consistent ranking calculations

```java
private final Object rankingLock = new Object();

private void updateRankings() {
    synchronized (rankingLock) {
        // Atomic ranking update
    }
}
```

### 2. Real-time Ranking Updates

Rankings are recalculated after each rating submission:

```java
public void submitRating(Rating rating) {
    // Store rating
    ratings.put(rating.getRatingId(), rating);
    
    // Update stats
    agentStats.get(agentId).addRating(rating.getScore());
    
    // Recalculate rankings
    updateRankings();
}
```

### 3. Rating Distribution

Track how many of each star rating:

```java
AgentStats stats = system.getAgentStats("A001");
System.out.println("5 stars: " + stats.getFiveStarCount());
System.out.println("4 stars: " + stats.getFourStarCount());
// ... etc
```

### 4. Department Rankings

Get rankings within a specific department:

```java
List<AgentStats> techSupport = system.getAgentsByDepartment("Technical Support");
```

## 🧪 Testing

### Test Coverage

- **RankingSystemTest**: 18 tests
  - Agent registration
  - Rating submission
  - Ranking calculation
  - Concurrent access
  - Department filtering
  - Edge cases

- **AgentStatsTest**: 16 tests
  - Statistics calculation
  - Rating distribution
  - Comparison logic
  - Thread safety
  - Validation

**Total: 34 tests, all passing**

### Run Tests

```bash
mvn test
```

## 📈 Performance Characteristics

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Register Agent | O(1) | O(1) |
| Submit Rating | O(N log N) | O(1) |
| Get Agent Rank | O(1) | O(1) |
| Get Top N | O(N log N) | O(N) |
| Update Rankings | O(N log N) | O(N) |

**Note**: Ranking update is O(N log N) due to sorting. Can be optimized with TreeSet or lazy evaluation.

## 🎓 Interview Tips

### What to Highlight

1. **Ranking Algorithm**: Explain average rating + tiebreaker
2. **Thread Safety**: ConcurrentHashMap + synchronized
3. **Real-time Updates**: Automatic recalculation
4. **Scalability**: Discuss optimizations for millions of agents
5. **Data Integrity**: Validation and error handling

### Common Questions

**Q: How to handle millions of agents?**
- Lazy ranking (only sort when queried)
- TreeSet for O(log N) updates
- Cache top K agents
- Distributed system with sharding

**Q: How to prevent rating manipulation?**
- Rate limiting per customer
- Verify customer-agent interaction
- Detect suspicious patterns
- Weighted ratings (verified customers)

**Q: How to scale for high traffic?**
- Database for persistence
- Redis for caching rankings
- Message queue for async processing
- Read replicas for queries

## 🔧 Optimizations

### Current Implementation
- Eager ranking update: O(N log N) per rating
- Good for: Small to medium datasets, frequent queries

### Possible Optimizations

1. **Lazy Ranking**: Only sort when leaderboard requested
2. **TreeSet**: Maintain sorted order, O(log N) updates
3. **Top-K Cache**: Cache top 100 agents, update selectively
4. **Batch Updates**: Update rankings periodically, not per rating
5. **Approximate Rankings**: For non-top agents

## 🌟 Real-World Applications

- **Customer Support**: Rank support agents
- **E-commerce**: Seller ratings and rankings
- **Ride Sharing**: Driver ratings (Uber, Lyft)
- **Food Delivery**: Restaurant and delivery ratings
- **Freelance Platforms**: Freelancer rankings (Upwork, Fiverr)
- **Education**: Teacher/course ratings

## 📚 Design Patterns Used

1. **Data Transfer Object (DTO)**: Agent, Rating
2. **Facade**: RankingSystem simplifies complex operations
3. **Observer**: Automatic ranking updates on rating changes
4. **Strategy**: Comparable for flexible sorting
5. **View**: LeaderboardView separates display logic

## 🎯 Learning Outcomes

After studying this project, you'll understand:

- Ranking algorithm design
- Real-time data aggregation
- Thread-safe concurrent systems
- Efficient sorting and comparison
- Statistics calculation
- Leaderboard implementation
- System scalability considerations

## 🚀 Future Enhancements

- [ ] Historical ranking snapshots
- [ ] Trending indicators (↑↓)
- [ ] Time-weighted ratings (recent matters more)
- [ ] Percentile rankings
- [ ] A/B testing for ranking algorithms
- [ ] Machine learning for fraud detection
- [ ] REST API endpoints
- [ ] Web dashboard

## 📖 Further Reading

- [Ranking Algorithms](https://en.wikipedia.org/wiki/Ranking)
- [Leaderboard Design Patterns](https://redis.io/topics/leaderboards)
- [Rating Systems](https://en.wikipedia.org/wiki/Rating_system)

## 🤝 Contributing

This is an educational project for interview preparation. Suggestions welcome!

## 📄 License

MIT License - Free to use for learning and interview preparation.

