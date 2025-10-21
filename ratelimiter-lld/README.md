# Rate Limiter - Low Level Design

A production-ready rate limiter implementation with multiple algorithms for controlling request rates in distributed systems.

## 📋 Overview

This project implements various rate limiting algorithms commonly used in API gateways, microservices, and distributed systems. It demonstrates clean architecture, thread safety, and extensibility.

## 🎯 Features

- **Multiple Algorithms**:
  - Token Bucket (recommended for most use cases)
  - Sliding Window Log
  - Fixed Window Counter
  - Sliding Window Counter

- **Thread-Safe**: All implementations are thread-safe and can handle concurrent requests
- **Configurable**: Easy configuration for rate limits, time windows, and capacity
- **User-Based Limiting**: Support for per-user rate limiting
- **Extensible**: Easy to add new rate limiting strategies

## 🏗️ Architecture

```
RateLimiter (interface)
    ├── TokenBucketRateLimiter
    ├── SlidingWindowLogRateLimiter
    ├── FixedWindowRateLimiter
    └── SlidingWindowCounterRateLimiter

RateLimiterConfig (configuration)
UserRateLimiterManager (manages per-user limiters)
```

## 🚀 Quick Start

### Basic Usage

```java
// Create a rate limiter: 10 requests per second
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(10)
    .setTimeWindowSeconds(1)
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);

RateLimiter rateLimiter = RateLimiterFactory.create(config);

// Check if request is allowed
if (rateLimiter.allowRequest()) {
    // Process request
    System.out.println("Request allowed");
} else {
    // Reject request
    System.out.println("Rate limit exceeded");
}
```

### Per-User Rate Limiting

```java
// Create user-based rate limiter
UserRateLimiterManager manager = new UserRateLimiterManager(config);

// Check for specific user
String userId = "user123";
if (manager.allowRequest(userId)) {
    // Process request for user
    System.out.println("Request allowed for " + userId);
} else {
    System.out.println("Rate limit exceeded for " + userId);
}
```

## 📊 Rate Limiting Algorithms

### 1. Token Bucket (Recommended)
- **Best for**: Most use cases, smooth traffic flow
- **Pros**: Allows bursts, smooth rate limiting
- **Cons**: Slightly more complex
- **Use case**: API rate limiting, request throttling

### 2. Sliding Window Log
- **Best for**: Precise rate limiting
- **Pros**: Most accurate, no boundary issues
- **Cons**: Higher memory usage
- **Use case**: Critical APIs, strict rate limits

### 3. Fixed Window Counter
- **Best for**: Simple use cases
- **Pros**: Simple, low memory
- **Cons**: Boundary issues (burst at window edges)
- **Use case**: Basic rate limiting, analytics

### 4. Sliding Window Counter
- **Best for**: Balance between accuracy and performance
- **Pros**: Good accuracy, lower memory than log
- **Cons**: Approximate counting
- **Use case**: High-traffic APIs, distributed systems

## 🔧 Configuration

```java
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(100)              // Max requests allowed
    .setTimeWindowSeconds(60)         // Time window in seconds
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET)
    .setBucketCapacity(150);          // For token bucket (optional)
```

## 📝 Examples

See `UsageExamples.java` for comprehensive examples:
- Basic rate limiting
- Per-user rate limiting
- Different algorithms comparison
- Concurrent request handling
- Burst traffic handling

## 🧪 Running Tests

```bash
mvn test
```

## 🏃 Running Examples

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.ratelimiter.examples.UsageExamples"
```

## 📚 Design Decisions

1. **Interface-based design**: Easy to add new algorithms
2. **Thread-safe**: Uses concurrent data structures and synchronization
3. **Time-based**: Uses System.nanoTime() for precise timing
4. **Configurable**: Builder pattern for easy configuration
5. **User isolation**: Separate rate limiters per user

## 🎓 Interview Tips

- Start with Token Bucket (simplest to implement)
- Explain trade-offs between algorithms
- Discuss thread safety considerations
- Consider distributed scenarios (Redis-based)
- Talk about monitoring and metrics

## 🔍 Common Interview Questions

**Q: Why Token Bucket over others?**
A: Token Bucket allows controlled bursts while maintaining average rate, making it ideal for real-world scenarios where occasional bursts are acceptable.

**Q: How to handle distributed rate limiting?**
A: Use Redis with atomic operations (INCR, EXPIRE) or distributed counters. Consider eventual consistency trade-offs.

**Q: What about memory usage?**
A: Token Bucket and Fixed Window use O(1) memory. Sliding Window Log uses O(N) where N is request count in window.

**Q: How to handle clock skew?**
A: Use monotonic time (System.nanoTime()), not wall clock time. In distributed systems, use logical clocks or centralized time service.

## 📖 Further Reading

- [Token Bucket Algorithm](https://en.wikipedia.org/wiki/Token_bucket)
- [Leaky Bucket Algorithm](https://en.wikipedia.org/wiki/Leaky_bucket)
- [Rate Limiting Strategies](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)

## 🤝 Contributing

This is an educational project for interview preparation. Feel free to extend with:
- Leaky Bucket algorithm
- Distributed rate limiter (Redis-based)
- Rate limiter middleware
- Metrics and monitoring
- Adaptive rate limiting

## 📄 License

MIT License - Free to use for learning and interview preparation.

