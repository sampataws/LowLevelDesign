# Design Decisions - Rate Limiter

This document explains the key design decisions made in implementing the rate limiter.

## 🏗️ Architecture Decisions

### 1. Interface-Based Design

**Decision**: Use `RateLimiter` interface with multiple implementations

**Rationale**:
- Allows easy addition of new algorithms
- Enables algorithm swapping without code changes
- Follows Open/Closed Principle (open for extension, closed for modification)
- Makes testing easier with mock implementations

**Trade-offs**:
- Slightly more complex than single implementation
- Need factory pattern for creation

### 2. Factory Pattern

**Decision**: Use `RateLimiterFactory` for creating instances

**Rationale**:
- Centralizes creation logic
- Hides implementation details from clients
- Makes it easy to add new algorithms
- Provides convenience methods for common cases

**Alternative Considered**: Builder pattern
- Rejected: Factory is simpler for this use case

### 3. Configuration Object

**Decision**: Use `RateLimiterConfig` with builder pattern

**Rationale**:
- Clean, readable API
- Easy to add new configuration options
- Validation in one place
- Immutable after creation (thread-safe)

**Example**:
```java
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(10)
    .setTimeWindowSeconds(1)
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
```

## 🔐 Thread Safety

### Decision: Synchronized Blocks

**Rationale**:
- Simple and correct
- Low contention in typical use cases
- Easy to understand and maintain

**Alternatives Considered**:
1. **ReentrantLock**: More features but overkill for this use case
2. **Lock-free (CAS)**: Complex, harder to maintain
3. **No synchronization**: Not thread-safe

**Implementation**:
```java
private final Object lock = new Object();

public boolean allowRequest() {
    synchronized (lock) {
        // Thread-safe operations
    }
}
```

### Per-User Concurrency

**Decision**: Use `ConcurrentHashMap` for user limiters

**Rationale**:
- Thread-safe without external synchronization
- Good performance for concurrent access
- Built-in atomic operations (computeIfAbsent)

## ⏰ Time Handling

### Decision: Use `System.nanoTime()`

**Rationale**:
- Monotonic (doesn't go backwards)
- High precision
- Not affected by system clock changes
- Better for measuring elapsed time

**Alternative Rejected**: `System.currentTimeMillis()`
- Can go backwards (NTP adjustments)
- Lower precision
- Affected by clock skew

**Note**: For distributed systems, would need different approach (logical clocks, centralized time service)

## 🎯 Algorithm Implementations

### 1. Token Bucket

**Key Decisions**:
- Start with full bucket (immediate burst allowed)
- Continuous refill (not discrete intervals)
- Capacity limits maximum tokens

**Rationale**:
- Most practical for real-world APIs
- Allows bursts while maintaining average rate
- O(1) memory complexity

**Implementation Detail**:
```java
double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
double tokensToAdd = elapsedSeconds * refillRate;
tokens = Math.min(capacity, tokens + tokensToAdd);
```

### 2. Sliding Window Log

**Key Decisions**:
- Use `ConcurrentLinkedQueue` for timestamps
- Clean up old timestamps on each request
- Store exact timestamps (not rounded)

**Rationale**:
- Most accurate algorithm
- No boundary issues
- Simple to understand

**Trade-off**: O(N) memory where N is requests in window

### 3. Fixed Window

**Key Decisions**:
- Use `AtomicLong` for counter
- Reset on window expiry
- Accept boundary issue as documented trade-off

**Rationale**:
- Simplest implementation
- O(1) memory
- Good enough for many use cases

**Known Issue**: Can allow 2x requests at window boundaries

### 4. Sliding Window Counter

**Key Decisions**:
- Maintain two windows (current and previous)
- Weight previous window by elapsed percentage
- Rotate windows on expiry

**Rationale**:
- Better accuracy than Fixed Window
- O(1) memory like Fixed Window
- Good balance for high-traffic scenarios

**Formula**:
```java
weightedCount = previousCount * (1 - elapsed%) + currentCount
```

## 📦 Memory Management

### Decision: No Automatic Cleanup

**Rationale**:
- Keep implementation simple
- Let clients manage lifecycle
- Avoid background threads

**For Production**: Would add:
- LRU cache for user limiters
- TTL-based cleanup
- Configurable max users

## 🔄 Reset Functionality

**Decision**: Provide `reset()` method

**Rationale**:
- Useful for testing
- Allows manual intervention
- Simple to implement

**Note**: Not typically used in production

## 🎨 API Design

### Method Naming

**Decision**: `allowRequest()` returns boolean

**Rationale**:
- Clear intent
- Easy to use in if statements
- Follows common patterns

**Alternative Considered**: `tryAcquire()` (Guava style)
- Rejected: Less clear for rate limiting context

### Capacity Checking

**Decision**: Optional `getAvailableCapacity()` method

**Rationale**:
- Useful for monitoring
- Not all algorithms support it
- Returns -1 if not supported

## 🧪 Testing Strategy

### Unit Tests

**Decisions**:
- Test each algorithm separately
- Test thread safety explicitly
- Test time-based behavior with sleeps

**Trade-off**: Tests with sleeps are slower but more realistic

### Integration Tests

**Decisions**:
- Test concurrent scenarios
- Test per-user isolation
- Test algorithm comparison

## 🚀 Performance Considerations

### Optimization Decisions

1. **Lazy Cleanup**: Clean up old data only when needed
2. **No Background Threads**: Avoid complexity
3. **Minimal Allocations**: Reuse objects where possible

### Scalability

**Current Design**: Single-node, in-memory

**For Distributed Systems**: Would need:
- Redis-based implementation
- Lua scripts for atomicity
- Eventual consistency handling

## 📊 Monitoring Hooks

**Decision**: Provide inspection methods

**Examples**:
- `getCurrentTokens()` for Token Bucket
- `getCurrentRequestCount()` for Sliding Window Log
- `getTimeUntilReset()` for Fixed Window

**Rationale**:
- Useful for debugging
- Enable metrics collection
- Help with capacity planning

## 🎓 Educational Decisions

### Code Clarity Over Optimization

**Decision**: Prioritize readability

**Rationale**:
- This is for interview preparation
- Clear code is easier to explain
- Premature optimization is evil

**Example**: Could use lock-free algorithms but chose synchronized for clarity

### Comprehensive Examples

**Decision**: Include multiple example scenarios

**Rationale**:
- Help users understand usage patterns
- Demonstrate best practices
- Show algorithm differences

## 🔮 Future Enhancements

### Not Implemented (But Could Be)

1. **Leaky Bucket**: Strict constant rate
2. **Adaptive Rate Limiting**: Adjust based on load
3. **Distributed Rate Limiting**: Redis-based
4. **Rate Limit Headers**: HTTP header support
5. **Metrics Integration**: Prometheus, etc.
6. **Async API**: CompletableFuture support

### Why Not Included

- Keep scope manageable
- Focus on core concepts
- Avoid over-engineering
- Interview time constraints

## 📝 Documentation Strategy

**Decision**: Multiple documentation files

**Files**:
- `README.md`: Overview and features
- `INTERVIEW_STRATEGY.md`: How to build in interview
- `QUICK_START.md`: Get started quickly
- `DESIGN_DECISIONS.md`: This file

**Rationale**:
- Different audiences
- Different purposes
- Easy to navigate

## 🎯 Summary

The design prioritizes:
1. **Correctness**: Thread-safe, accurate
2. **Clarity**: Easy to understand and explain
3. **Extensibility**: Easy to add new algorithms
4. **Practicality**: Solves real-world problems
5. **Educational Value**: Good for learning and interviews

These decisions make the code suitable for:
- Interview demonstrations
- Learning rate limiting concepts
- Understanding algorithm trade-offs
- Building production systems (with modifications)

