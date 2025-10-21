# Atlassian Low Level Design

A collection of low-level design implementations for interview preparation and learning.

## 📚 Modules

### 1. [logging-lld](./logging-lld) - Logging Library
A comprehensive, production-ready logging library with support for:
- Synchronous and Asynchronous logging
- Multiple log levels (DEBUG, INFO, WARN, ERROR, FATAL)
- Thread-safe multi-threaded support
- Configurable sinks (STDOUT, File, Database, etc.)
- Message ordering guarantees
- No data loss

**Status:** ✅ Complete (43 tests passing)

[View Documentation](./logging-lld/README.md)

### 2. [ratelimiter-lld](./ratelimiter-lld) - Rate Limiter
A production-ready rate limiter with multiple algorithms:
- Token Bucket (allows bursts, smooth rate limiting)
- Sliding Window Log (most accurate)
- Fixed Window Counter (simplest)
- Sliding Window Counter (balanced approach)
- Per-user rate limiting support
- Thread-safe concurrent request handling

**Status:** ✅ Complete (20 tests passing)

[View Documentation](./ratelimiter-lld/README.md)

### 3. [agent-ranking-lld](./agent-ranking-lld) - Agent Ranking System
A comprehensive customer support agent ranking system:
- Real-time agent ranking based on customer ratings
- Average rating calculation with tiebreaker logic
- Thread-safe concurrent rating submissions
- Department-wise leaderboards
- Rating distribution tracking (5-star, 4-star, etc.)
- Beautiful formatted leaderboard views

**Status:** ✅ Complete (31 tests passing)

[View Documentation](./agent-ranking-lld/README.md)

### 4. [org-hierarchy-lld](./org-hierarchy-lld) - Organization Hierarchy
Find closest common parent group for employees (Classic Atlassian question):
- Hierarchical group structure with subgroups
- Lowest Common Ancestor (LCA) algorithm in DAG
- Shared groups (employees in multiple groups, groups with multiple parents)
- Thread-safe concurrent reads/writes with ReadWriteLock
- Dynamic hierarchy updates
- Flat hierarchy support
- Cycle detection

**Status:** ✅ Complete (27 tests passing)

[View Documentation](./org-hierarchy-lld/README.md)

### 5. [tennis-court-lld](./tennis-court-lld) - Tennis Court Booking System
Expanding Tennis Club - Minimum court allocation (Classic interview question):
- Greedy algorithm with min-heap for optimal court assignment
- Fixed maintenance time after each booking
- Periodic maintenance after X bookings (durability)
- Efficient minimum courts calculation (event sweep algorithm)
- Booking conflict detection
- O(N log N) time complexity

**Status:** ✅ Complete (60 tests passing)

[View Documentation](./tennis-court-lld/README.md)

### 6. [commodity-prices-lld](./commodity-prices-lld) - Commodity Price Tracker
Real-time commodity price tracking with max price queries:
- Dual data structure design (HashMap + TreeSet)
- O(1) max price queries, O(log N) updates
- Out-of-order timestamp handling
- Duplicate timestamp updates
- Thread-safe with ReadWriteLock
- Optimized for frequent reads and writes

**Status:** ✅ Complete (42 tests passing)

[View Documentation](./commodity-prices-lld/README.md)

### 7. [popular-content-lld](./popular-content-lld) - Popular Content Tracker
Real-time content popularity tracking with instant most popular queries:
- Dual data structure design (HashMap + TreeMap)
- O(1) most popular queries, O(log N) updates
- Automatic cleanup when popularity ≤ 0
- Handles increase/decrease popularity actions
- Thread-safe with ReadWriteLock
- Optimized for streaming data

**Status:** ✅ Complete (24 tests passing)

[View Documentation](./popular-content-lld/README.md)

### 8. [customer-satisfaction-lld](./customer-satisfaction-lld) - Customer Satisfaction System
Customer support agent rating system with comprehensive analytics:
- Accept ratings (1-5) for support agents
- Multiple tie-breaking strategies (agent ID, total ratings, none)
- Monthly performance tracking and analytics
- Export in CSV, JSON, XML formats
- Thread-safe with ReadWriteLock
- Unsorted results and total ratings support

**Status:** ✅ Complete (40 tests passing)

[View Documentation](./customer-satisfaction-lld/README.md)

### 9. [middleware-router-lld](./middleware-router-lld) - Middleware Router
A middleware router for web services with pattern matching:
- Exact path matching (`/users`)
- Wildcard patterns (`/api/*`)
- Path parameter extraction (`/users/:id`)
- Priority-based routing (exact > params > wildcards)
- Thread-safe concurrent routing
- First-match strategy with ordered checking

**Status:** ✅ Complete (40 tests passing)

[View Documentation](./middleware-router-lld/README.md)

### 10. [snake-game-lld](./snake-game-lld) - Snake Game
Classic Snake game implementation with configurable mechanics:
- 4-directional movement (UP, DOWN, LEFT, RIGHT)
- Initial size of 3, grows by 1 every 5 moves (configurable)
- Boundary and self-collision detection
- Strategy pattern for growth mechanics
- Optional food-based growth system
- Random food placement with collision avoidance

**Status:** ✅ Complete (56 tests passing)

[View Documentation](./snake-game-lld/README.md)

### 11. [cost-explorer-lld](./cost-explorer-lld) - Cost Explorer
Subscription billing and cost estimation system for SaaS products:
- Monthly cost calculation with proration for partial months
- Yearly cost estimation for the unit year
- Multiple plans per product support
- Plan upgrades/downgrades with date ranges
- Detailed monthly and yearly cost reports
- Cost breakdown by product

**Status:** ✅ Complete (48 tests passing)

[View Documentation](./cost-explorer-lld/README.md)

---

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build All Modules
```bash
mvn clean install
```

### Build Specific Module
```bash
cd logging-lld
mvn clean install
```

---

## 📖 Module Details

### Logging Library (logging-lld)

**Key Features:**
- ✅ Sync and Async loggers
- ✅ Thread-safe implementation
- ✅ Configurable log levels
- ✅ Multiple sinks support
- ✅ Message ordering
- ✅ No data loss guarantee

**Quick Start:**
```bash
cd logging-lld
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

**Run Tests:**
```bash
cd logging-lld
mvn test
```

### Rate Limiter (ratelimiter-lld)

**Key Features:**
- ✅ 4 rate limiting algorithms
- ✅ Token Bucket (recommended)
- ✅ Sliding Window Log (most accurate)
- ✅ Fixed Window (simplest)
- ✅ Per-user rate limiting
- ✅ Thread-safe concurrent handling

**Quick Start:**
```bash
cd ratelimiter-lld
mvn exec:java -Dexec.mainClass="com.ratelimiter.DriverApplication"
```

**Run Tests:**
```bash
cd ratelimiter-lld
mvn test
```

### Agent Ranking System (agent-ranking-lld)

**Key Features:**
- ✅ Real-time agent rankings
- ✅ Average rating calculation
- ✅ Tiebreaker logic (total ratings)
- ✅ Department-wise leaderboards
- ✅ Rating distribution tracking
- ✅ Thread-safe concurrent operations

**Quick Start:**
```bash
cd agent-ranking-lld
mvn exec:java -Dexec.mainClass="com.agentranking.DriverApplication"
```

**Run Tests:**
```bash
cd agent-ranking-lld
mvn test
```

### Commodity Prices Tracker (commodity-prices-lld)

**Key Features:**
- ✅ Dual data structure design (HashMap + TreeSet)
- ✅ O(1) max price queries
- ✅ O(log N) updates
- ✅ Out-of-order timestamp handling
- ✅ Duplicate timestamp updates
- ✅ Thread-safe with ReadWriteLock
- ✅ Optimized for frequent reads and writes
- ✅ Real-time streaming support

**Quick Start:**
```bash
cd commodity-prices-lld
mvn exec:java -Dexec.mainClass="com.commodityprices.DriverApplication"
```

**Run Tests:**
```bash
cd commodity-prices-lld
mvn test
```

### Popular Content Tracker (popular-content-lld)

**Key Features:**
- ✅ Dual data structure design (HashMap + TreeMap)
- ✅ O(1) most popular queries
- ✅ O(log N) action processing
- ✅ Automatic cleanup when popularity ≤ 0
- ✅ Increase/decrease popularity actions
- ✅ Thread-safe with ReadWriteLock
- ✅ Real-time streaming support
- ✅ Handles ties (multiple contents with same popularity)

**Quick Start:**
```bash
cd popular-content-lld
mvn exec:java -Dexec.mainClass="com.popularcontent.DriverApplication"
```

**Run Tests:**
```bash
cd popular-content-lld
mvn test
```

### Customer Satisfaction System (customer-satisfaction-lld)

**Key Features:**
- ✅ Accept ratings (1-5) for support agents
- ✅ Sort agents by average rating (highest to lowest)
- ✅ Multiple tie-breaking strategies (agent ID, total ratings, none)
- ✅ Monthly performance tracking
- ✅ Export in CSV, JSON, XML formats
- ✅ Unsorted results option
- ✅ Total ratings export (not averages)
- ✅ Thread-safe with ReadWriteLock

**Quick Start:**
```bash
cd customer-satisfaction-lld
mvn exec:java -Dexec.mainClass="com.customersatisfaction.DriverApplication"
```

**Run Tests:**
```bash
cd customer-satisfaction-lld
mvn test
```

---

## 🎯 Interview Preparation

Each module includes:
- ✅ Complete implementation
- ✅ Comprehensive test suite
- ✅ Design documentation
- ✅ Interview strategy guide
- ✅ Quick reference cheat sheet

### Interview Resources
- [Logging Interview Strategy](logging-lld/INTERVIEW_STRATEGY.md) - How to build logging library in interviews
- [Rate Limiter Interview Strategy](ratelimiter-lld/INTERVIEW_STRATEGY.md) - How to build rate limiter in interviews
- [Agent Ranking Interview Strategy](agent-ranking-lld/INTERVIEW_STRATEGY.md) - How to build agent ranking system in interviews
- [Org Hierarchy Interview Strategy](org-hierarchy-lld/INTERVIEW_STRATEGY.md) - How to build org hierarchy system in interviews
- [Tennis Court Interview Strategy](tennis-court-lld/INTERVIEW_STRATEGY.md) - How to solve tennis court booking in interviews
- [Commodity Prices Interview Strategy](commodity-prices-lld/INTERVIEW_STRATEGY.md) - How to build commodity price tracker in interviews
- [Popular Content Interview Strategy](popular-content-lld/INTERVIEW_STRATEGY.md) - How to build popular content tracker in interviews
- [Customer Satisfaction Interview Strategy](customer-satisfaction-lld/INTERVIEW_STRATEGY.md) - How to build customer satisfaction system in interviews
- [Interview Cheat Sheet](./INTERVIEW_CHEAT_SHEET.md) - Quick reference for coding

---

## 📁 Project Structure

```
AtlassianLowLevelDesign/
├── pom.xml                          # Parent POM
├── README.md                        # This file
├── INTERVIEW_CHEAT_SHEET.md         # Quick reference
│
├── logging-lld/                     # Logging Library Module
│   ├── pom.xml
│   ├── README.md
│   ├── QUICK_START.md
│   ├── PROJECT_SUMMARY.md
│   ├── DESIGN_DECISIONS.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/loggingmc/
│       │   ├── LogLevel.java
│       │   ├── LogMessage.java
│       │   ├── LoggerConfiguration.java
│       │   ├── LoggerFactory.java
│       │   ├── DriverApplication.java
│       │   ├── logger/
│       │   ├── sink/
│       │   └── examples/
│       └── test/java/com/loggingmc/
│           └── ... (43 tests)
│
├── ratelimiter-lld/                 # Rate Limiter Module
│   ├── pom.xml
│   ├── README.md
│   ├── QUICK_START.md
│   ├── PROJECT_SUMMARY.md
│   ├── DESIGN_DECISIONS.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/ratelimiter/
│       │   ├── RateLimiter.java
│       │   ├── RateLimiterAlgorithm.java
│       │   ├── RateLimiterConfig.java
│       │   ├── RateLimiterFactory.java
│       │   ├── TokenBucketRateLimiter.java
│       │   ├── SlidingWindowLogRateLimiter.java
│       │   ├── FixedWindowRateLimiter.java
│       │   ├── SlidingWindowCounterRateLimiter.java
│       │   ├── UserRateLimiterManager.java
│       │   ├── DriverApplication.java
│       │   └── examples/
│       │       └── UsageExamples.java
│       └── test/java/com/ratelimiter/
│           └── ... (20 tests)
│
├── agent-ranking-lld/               # Agent Ranking System Module
│   ├── pom.xml
│   ├── README.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/agentranking/
│       │   ├── Agent.java
│       │   ├── Rating.java
│       │   ├── AgentStats.java
│       │   ├── RankingSystem.java
│       │   ├── LeaderboardView.java
│       │   └── DriverApplication.java
│       └── test/java/com/agentranking/
│           └── ... (31 tests)
│
├── org-hierarchy-lld/               # Organization Hierarchy Module
│   ├── pom.xml
│   ├── README.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/orghierarchy/
│       │   ├── Employee.java
│       │   ├── Group.java
│       │   ├── OrgHierarchy.java
│       │   └── DriverApplication.java
│       └── test/java/com/orghierarchy/
│           └── ... (27 tests)
│
├── tennis-court-lld/                # Tennis Court Booking Module
│   ├── pom.xml
│   ├── README.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/tenniscourt/
│       │   ├── BookingRecord.java
│       │   ├── Court.java
│       │   ├── CourtAssignmentSystem.java
│       │   └── DriverApplication.java
│       └── test/java/com/tenniscourt/
│           └── ... (60 tests)
│
├── commodity-prices-lld/            # Commodity Prices Tracker Module
│   ├── pom.xml
│   ├── README.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/commodityprices/
│       │   ├── PriceDataPoint.java
│       │   ├── CommodityPriceTracker.java
│       │   └── DriverApplication.java
│       └── test/java/com/commodityprices/
│           └── ... (42 tests)
│
├── popular-content-lld/             # Popular Content Tracker Module
│   ├── pom.xml
│   ├── README.md
│   ├── INTERVIEW_STRATEGY.md
│   └── src/
│       ├── main/java/com/popularcontent/
│       │   ├── ContentAction.java
│       │   ├── PopularContentTracker.java
│       │   └── DriverApplication.java
│       └── test/java/com/popularcontent/
│           └── ... (24 tests)
│
└── customer-satisfaction-lld/       # Customer Satisfaction System Module
    ├── pom.xml
    ├── README.md
    ├── INTERVIEW_STRATEGY.md
    └── src/
        ├── main/java/com/customersatisfaction/
        │   ├── Rating.java
        │   ├── AgentStats.java
        │   ├── TieBreakStrategy.java
        │   ├── ExportFormat.java
        │   ├── CustomerSatisfactionSystem.java
        │   └── DriverApplication.java
        └── test/java/com/customersatisfaction/
            └── ... (40 tests)
```

---

## 🎓 Learning Path

### For Interview Preparation:

1. **Read the Interview Strategy** ([INTERVIEW_STRATEGY.md](logging-lld/INTERVIEW_STRATEGY.md))
   - Understand the incremental approach
   - Learn what to say during interviews
   - Practice the implementation order

2. **Study the Implementation** (logging-lld module)
   - Review the code structure
   - Understand design decisions
   - Run the tests

3. **Practice Coding**
   - Use the cheat sheet ([INTERVIEW_CHEAT_SHEET.md](./INTERVIEW_CHEAT_SHEET.md))
   - Time yourself (aim for 45-60 minutes)
   - Build from scratch without looking

4. **Review Design Decisions** (logging-lld/DESIGN_DECISIONS.md)
   - Understand trade-offs
   - Prepare for follow-up questions
   - Know alternative approaches

---

## 🛠️ Technology Stack

- **Language:** Java 11
- **Build Tool:** Maven
- **Testing:** JUnit 4
- **Design Patterns:** Factory, Strategy, Interface Segregation

---

## 📊 Test Coverage

### Logging Library
- **Total Tests:** 43
- **Status:** ✅ All Passing
- **Coverage:**
  - LogLevel: 2 tests
  - LogMessage: 2 tests
  - LoggerConfiguration: 4 tests
  - StdoutSink: 7 tests
  - SyncLogger: 9 tests
  - AsyncLogger: 9 tests
  - LoggerFactory: 10 tests

### Rate Limiter
- **Total Tests:** 20
- **Status:** ✅ All Passing
- **Coverage:**
  - TokenBucketRateLimiter: 10 tests
  - UserRateLimiterManager: 10 tests
  - Thread safety tests
  - Time-based behavior tests

### Agent Ranking System
- **Total Tests:** 31
- **Status:** ✅ All Passing
- **Coverage:**
  - RankingSystemTest: 16 tests
  - AgentStatsTest: 15 tests
  - Thread safety tests
  - Concurrent rating submission tests
  - Ranking calculation tests

### Organization Hierarchy
- **Total Tests:** 27
- **Status:** ✅ All Passing
- **Coverage:**
  - OrgHierarchyTest: 27 tests
  - LCA algorithm tests
  - Shared groups tests
  - Concurrent reads/writes tests
  - Cycle detection tests
  - Flat hierarchy tests

### Tennis Court Booking System
- **Total Tests:** 60
- **Status:** ✅ All Passing
- **Coverage:**
  - CourtAssignmentSystemTest: 31 tests
  - BookingRecordTest: 16 tests
  - CourtTest: 13 tests
  - All 5 interview parts (a-e) covered
  - Greedy algorithm tests
  - Maintenance handling tests
  - Conflict detection tests

### Commodity Prices Tracker
- **Total Tests:** 42
- **Status:** ✅ All Passing
- **Coverage:**
  - CommodityPriceTrackerTest: 30 tests
  - PriceDataPointTest: 12 tests
  - Out-of-order timestamp tests
  - Duplicate timestamp update tests
  - Concurrent reads/writes tests
  - Max price query tests
  - Statistics calculation tests

### Popular Content Tracker
- **Total Tests:** 24
- **Status:** ✅ All Passing
- **Coverage:**
  - PopularContentTrackerTest: 24 tests
  - Basic operations (increase/decrease)
  - Multiple contents with same/different popularity
  - Automatic cleanup when popularity ≤ 0
  - Edge cases (empty, zero, negative)
  - Concurrent operations tests
  - Complex scenarios (fluctuations, removals)

### Customer Satisfaction System
- **Total Tests:** 40
- **Status:** ✅ All Passing
- **Coverage:**
  - RatingTest: 12 tests
  - AgentStatsTest: 7 tests
  - CustomerSatisfactionSystemTest: 21 tests
  - Basic rating operations (add, sort)
  - All tie-breaking strategies
  - Monthly performance tracking
  - All export formats (CSV, JSON, XML)
  - Unsorted results and total ratings
  - Concurrent operations tests

---

## 🎯 Future Modules

Planned low-level design implementations:

- [x] **Rate Limiter** - Token bucket, sliding window algorithms ✅
- [x] **Agent Ranking System** - Customer support agent ranking ✅
- [x] **Organization Hierarchy** - Closest common group finder (Atlassian) ✅
- [x] **Tennis Court Booking** - Minimum court allocation (Expanding Tennis Club) ✅
- [x] **Commodity Prices Tracker** - Real-time price tracking with max queries ✅
- [x] **Popular Content Tracker** - Real-time popularity tracking with instant queries ✅
- [x] **Customer Satisfaction System** - Agent rating system with monthly analytics ✅
- [ ] **Cache System** - LRU, LFU cache implementations
- [ ] **URL Shortener** - Distributed URL shortening service
- [ ] **Parking Lot** - Multi-level parking system
- [ ] **Task Scheduler** - Cron-like task scheduling system
- [ ] **Notification System** - Multi-channel notification service
- [ ] **File System** - In-memory file system implementation

---

## 💡 Design Principles

All implementations follow:

1. **SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

2. **Clean Code**
   - Readable and maintainable
   - Well-documented
   - DRY (Don't Repeat Yourself)
   - Proper error handling

3. **Production-Ready**
   - Thread-safe
   - Tested thoroughly
   - Handles edge cases
   - Graceful error handling

---

## 🤝 Contributing

This is a personal learning project, but suggestions are welcome!

---

## 📝 License

This project is for educational purposes.

---

## 📧 Contact

For questions or discussions about the implementations, feel free to reach out.

---

## 🌟 Acknowledgments

These implementations are inspired by common interview questions from:
- Atlassian
- Amazon
- Google
- Microsoft
- Other top tech companies

---

**Happy Learning! 🚀**

