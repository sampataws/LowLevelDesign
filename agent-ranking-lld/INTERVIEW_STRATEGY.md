# Interview Strategy: Building an Agent Ranking System

## 🎯 **The 45-60 Minute Strategy**

This guide shows you how to build a customer support agent ranking system in an interview setting.

---

## **Phase 1: First 15-20 Minutes - Core Data Models**

### **Step 1: Clarify Requirements (3-5 minutes)**

**What to say:**
```
"Let me confirm the requirements:
1. Track customer support agents and their ratings
2. Ratings are 1-5 stars from customers
3. Rank agents by average rating
4. Support real-time ranking updates
5. Thread-safe for concurrent access
6. Display leaderboards

Questions:
- Should we handle ties in rankings?
- Do we need historical data or just current stats?
- Any specific performance requirements?
- Department-wise rankings needed?"
```

### **Step 2: Quick Design (2-3 minutes)**

Draw this on whiteboard:
```
Agent (id, name, email, dept)
    ↓
Rating (id, agentId, score, timestamp)
    ↓
AgentStats (agentId, totalRatings, avgRating, rank)
    ↓
RankingSystem (manages all)
```

**Say:** *"I'll start with the data models, then build the ranking logic."*

### **Step 3: Code in This Order (10-12 minutes)**

#### **1. Agent class (2 minutes)**
```java
public class Agent {
    private final String agentId;
    private final String name;
    private final String email;
    private final String department;
    
    public Agent(String agentId, String name, String email, String department) {
        this.agentId = agentId;
        this.name = name;
        this.email = email;
        this.department = department;
    }
    
    // Getters
    public String getAgentId() { return agentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
}
```

**Say:** *"Starting with Agent - simple immutable data class..."*

#### **2. Rating class (2 minutes)**
```java
import java.time.LocalDateTime;

public class Rating {
    private final String ratingId;
    private final String agentId;
    private final int score;  // 1-5
    private final LocalDateTime timestamp;
    
    public Rating(String ratingId, String agentId, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be 1-5");
        }
        this.ratingId = ratingId;
        this.agentId = agentId;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getAgentId() { return agentId; }
    public int getScore() { return score; }
}
```

**Say:** *"Rating captures customer feedback with validation..."*

#### **3. AgentStats class (6 minutes)**
```java
public class AgentStats implements Comparable<AgentStats> {
    private final String agentId;
    private int totalRatings;
    private int totalScore;
    private double averageRating;
    private int rank;
    
    public AgentStats(String agentId) {
        this.agentId = agentId;
        this.totalRatings = 0;
        this.totalScore = 0;
        this.averageRating = 0.0;
    }
    
    public synchronized void addRating(int score) {
        totalRatings++;
        totalScore += score;
        averageRating = (double) totalScore / totalRatings;
    }
    
    public double getAverageRating() { return averageRating; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    
    @Override
    public int compareTo(AgentStats other) {
        // Higher average is better
        int avgCompare = Double.compare(other.averageRating, this.averageRating);
        if (avgCompare != 0) return avgCompare;
        
        // More ratings is better for ties
        return Integer.compare(other.totalRatings, this.totalRatings);
    }
}
```

**Say:** *"AgentStats tracks statistics and implements Comparable for ranking..."*

---

## **Phase 2: Next 15-20 Minutes - Ranking System**

### **Build RankingSystem class (15 minutes)**

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RankingSystem {
    private final ConcurrentHashMap<String, Agent> agents;
    private final ConcurrentHashMap<String, AgentStats> agentStats;
    private final ConcurrentHashMap<String, Rating> ratings;
    private final Object rankingLock = new Object();
    
    public RankingSystem() {
        this.agents = new ConcurrentHashMap<>();
        this.agentStats = new ConcurrentHashMap<>();
        this.ratings = new ConcurrentHashMap<>();
    }
    
    public void registerAgent(Agent agent) {
        agents.put(agent.getAgentId(), agent);
        agentStats.putIfAbsent(agent.getAgentId(), new AgentStats(agent.getAgentId()));
    }
    
    public void submitRating(Rating rating) {
        String agentId = rating.getAgentId();
        
        if (!agents.containsKey(agentId)) {
            throw new IllegalArgumentException("Agent not found");
        }
        
        // Store rating
        ratings.put(rating.getRatingId(), rating);
        
        // Update stats
        AgentStats stats = agentStats.get(agentId);
        stats.addRating(rating.getScore());
        
        // Recalculate rankings
        updateRankings();
    }
    
    private void updateRankings() {
        synchronized (rankingLock) {
            List<AgentStats> sorted = new ArrayList<>(agentStats.values());
            Collections.sort(sorted);
            
            int rank = 1;
            for (AgentStats stats : sorted) {
                stats.setRank(rank++);
            }
        }
    }
    
    public int getAgentRank(String agentId) {
        AgentStats stats = agentStats.get(agentId);
        return stats != null ? stats.getRank() : -1;
    }
    
    public List<AgentStats> getTopAgents(int n) {
        synchronized (rankingLock) {
            return agentStats.values().stream()
                .sorted()
                .limit(n)
                .collect(Collectors.toList());
        }
    }
}
```

**Say:** *"Using ConcurrentHashMap for thread safety. Rankings update after each rating..."*

---

## **Phase 3: Next 10-15 Minutes - Demo & Polish**

### **Create Demo (10 minutes)**

```java
public class Demo {
    public static void main(String[] args) {
        RankingSystem system = new RankingSystem();
        
        // Register agents
        system.registerAgent(new Agent("A001", "Alice", "alice@co.com", "Support"));
        system.registerAgent(new Agent("A002", "Bob", "bob@co.com", "Support"));
        system.registerAgent(new Agent("A003", "Carol", "carol@co.com", "Support"));
        
        // Submit ratings
        system.submitRating(new Rating("R1", "A001", 5));
        system.submitRating(new Rating("R2", "A001", 5));
        system.submitRating(new Rating("R3", "A001", 4));
        
        system.submitRating(new Rating("R4", "A002", 4));
        system.submitRating(new Rating("R5", "A002", 4));
        
        system.submitRating(new Rating("R6", "A003", 3));
        
        // Display rankings
        System.out.println("Top Agents:");
        List<AgentStats> top = system.getTopAgents(3);
        for (AgentStats stats : top) {
            System.out.printf("Rank %d: Agent %s - Avg: %.2f%n",
                stats.getRank(), stats.getAgentId(), stats.getAverageRating());
        }
    }
}
```

**RUN IT!** Show it works.

---

## **Phase 4: Final 10 Minutes - Enhancements**

### **Add Leaderboard View (Optional)**

```java
public class LeaderboardView {
    private final RankingSystem system;
    
    public LeaderboardView(RankingSystem system) {
        this.system = system;
    }
    
    public void displayTopAgents(int n) {
        List<AgentStats> top = system.getTopAgents(n);
        
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║  Agent Leaderboard                 ║");
        System.out.println("╠════════════════════════════════════╣");
        
        for (AgentStats stats : top) {
            Agent agent = system.getAgent(stats.getAgentId());
            System.out.printf("║ %d. %-20s %.2f ║%n",
                stats.getRank(), agent.getName(), stats.getAverageRating());
        }
        
        System.out.println("╚════════════════════════════════════╝");
    }
}
```

---

## 🎤 **What to SAY During the Interview**

### **Opening**
```
"I'll build this incrementally:
1. Data models (Agent, Rating, AgentStats)
2. Ranking system with thread safety
3. Demo showing it works
4. Add enhancements if time permits"
```

### **While Coding**
```
"Starting with Agent - simple data class..."

"Rating validates score is 1-5..."

"AgentStats implements Comparable for automatic sorting..."

"Using ConcurrentHashMap for thread-safe concurrent access..."

"updateRankings() recalculates after each rating - could optimize with lazy update..."

"Let me run this to verify it works..."
```

### **When Discussing Trade-offs**
```
"Ranking Update Strategy:
- Eager (current): Update after each rating - O(N log N)
- Lazy: Update only when queried - better for write-heavy
- Periodic: Batch updates every X seconds

For this use case, eager is fine since rankings are frequently viewed."
```

---

## ⏱️ **Time Management**

| Time | What to Have | Status |
|------|--------------|--------|
| **15 min** | Data models complete | ✅ |
| **30 min** | RankingSystem working | ✅ DEMO |
| **40 min** | Demo running | ✅ DEMO |
| **50 min** | Leaderboard view | ✅ DEMO |
| **60 min** | Polish & tests | ✅ |

---

## 🎯 **Priority Order**

### 1. ✅ **Must Have** (First 30 min)
- Agent class
- Rating class
- AgentStats class
- RankingSystem with basic operations
- Demo working

### 2. ✅ **Should Have** (Next 15 min)
- Thread safety (ConcurrentHashMap)
- Proper ranking updates
- Top N agents query
- Error handling

### 3. ✅ **Nice to Have** (Next 15 min)
- LeaderboardView
- Department-wise rankings
- Rating distribution
- System statistics

### 4. ⭐ **If Time Permits**
- Remove rating functionality
- Historical data tracking
- Performance metrics
- Caching for rankings

---

## 💬 **Common Interview Questions & Answers**

### **Q: "How do you handle ties in rankings?"**
```
A: "In AgentStats.compareTo(), I first compare by average rating. 
If tied, I compare by total ratings - more ratings indicates more 
experience and reliability. Could also add timestamp as third tiebreaker."
```

### **Q: "What if we have millions of agents?"**
```
A: "Current approach sorts all agents on each update - O(N log N).
Optimizations:
1. Lazy ranking - only sort when leaderboard is requested
2. Use TreeSet to maintain sorted order - O(log N) per insert
3. Cache top K agents, only recalculate if affected
4. Distributed system with sharding by department
5. Approximate rankings for non-top agents"
```

### **Q: "How do you ensure thread safety?"**
```
A: "Multiple layers:
1. ConcurrentHashMap for agent/stats storage
2. synchronized in AgentStats.addRating() for atomic updates
3. rankingLock for consistent ranking calculations
4. Immutable Agent and Rating classes

For higher concurrency, could use:
- ReadWriteLock for rankings
- Lock-free data structures
- Optimistic locking with versioning"
```

### **Q: "What about malicious ratings?"**
```
A: "Several approaches:
1. Rate limiting per customer
2. Verify customer actually interacted with agent
3. Flag suspicious patterns (all 1s or 5s)
4. Weighted ratings (verified customers count more)
5. Time decay (recent ratings matter more)
6. Outlier detection and removal"
```

### **Q: "How would you scale this?"**
```
A: "For distributed system:
1. Database: Store agents/ratings in DB (PostgreSQL)
2. Cache: Redis for rankings and stats
3. Message Queue: Kafka for rating events
4. Async Processing: Update rankings asynchronously
5. Sharding: By department or region
6. Read Replicas: For leaderboard queries
7. CDN: Cache leaderboard pages"
```

### **Q: "What about historical rankings?"**
```
A: "Add RankingSnapshot:
- Store daily/weekly snapshots
- Track rank changes over time
- Show trending (↑↓ indicators)
- Historical charts

Implementation:
- Scheduled job to snapshot rankings
- Time-series database (InfluxDB)
- Separate table for historical data"
```

---

## 🚀 **Quick Start Checklist**

```
☐ 1. Agent class (2 min)
☐ 2. Rating class (2 min)
☐ 3. AgentStats class (6 min)
☐ 4. RankingSystem class (15 min)
☐ 5. Demo main() (5 min)
☐ 6. RUN IT! ✅
☐ 7. Add LeaderboardView (10 min)
☐ 8. Add department rankings (5 min)
☐ 9. Polish & error handling (5 min)
```

**Total: ~50 minutes for full working solution**

---

## 🎓 **Final Tips**

### **DO:**
✅ Start with simple data models
✅ Explain your ranking algorithm
✅ Demo frequently
✅ Discuss thread safety
✅ Consider scalability
✅ Handle edge cases (no ratings, ties)

### **DON'T:**
❌ Over-engineer initially
❌ Forget to validate input (score 1-5)
❌ Ignore thread safety
❌ Skip the demo
❌ Forget about ties in ranking

### **Remember:**
> **A working basic system beats a half-finished complex one!** 🎯

---

## 📋 **Algorithm Complexity**

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Register Agent | O(1) | O(1) |
| Submit Rating | O(N log N) | O(1) |
| Get Agent Rank | O(1) | O(1) |
| Get Top N | O(N log N) | O(N) |
| Update Rankings | O(N log N) | O(N) |

**Optimization**: Use TreeSet for O(log N) ranking updates

---

## 🏆 **Success Criteria**

By the end, you should have:

✅ Working agent registration
✅ Rating submission with validation
✅ Automatic ranking calculation
✅ Thread-safe implementation
✅ Leaderboard display
✅ Demo running
✅ Explained design decisions
✅ Discussed scalability

**Good luck with your interview!** 🚀

Remember: **Start simple, iterate, and always demo!**

