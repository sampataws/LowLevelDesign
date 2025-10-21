# Quick Start Guide - Rate Limiter

Get up and running with the Rate Limiter in 5 minutes!

## 🚀 Build and Run

### 1. Build the Project
```bash
cd ratelimiter-lld
mvn clean compile
```

### 2. Run the Demo
```bash
mvn exec:java -Dexec.mainClass="com.ratelimiter.DriverApplication"
```

### 3. Run Examples
```bash
mvn exec:java -Dexec.mainClass="com.ratelimiter.examples.UsageExamples"
```

### 4. Run Tests
```bash
mvn test
```

## 📝 Basic Usage

### Simple Rate Limiter (5 requests/second)
```java
RateLimiter limiter = RateLimiterFactory.createSimple(5);

if (limiter.allowRequest()) {
    // Process request
    System.out.println("Request allowed");
} else {
    // Reject request
    System.out.println("Rate limit exceeded");
}
```

### With Configuration
```java
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(10)
    .setTimeWindowSeconds(1)
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);

RateLimiter limiter = RateLimiterFactory.create(config);
```

### Per-User Rate Limiting
```java
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(100)
    .setTimeWindowSeconds(60);

UserRateLimiterManager manager = new UserRateLimiterManager(config);

if (manager.allowRequest("user123")) {
    // Process request for user123
}
```

## 🎯 Common Scenarios

### API Rate Limiting
```java
// 1000 requests per hour per user
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(1000)
    .setTimeWindowSeconds(3600)
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
```

### Burst Handling
```java
// 10 req/sec average, allow bursts up to 50
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(10)
    .setTimeWindowSeconds(1)
    .setBucketCapacity(50)
    .setAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
```

### Strict Rate Limiting
```java
// Precise limiting with no boundary issues
RateLimiterConfig config = new RateLimiterConfig()
    .setMaxRequests(100)
    .setTimeWindowSeconds(60)
    .setAlgorithm(RateLimiterAlgorithm.SLIDING_WINDOW_LOG);
```

## 🔍 Choosing an Algorithm

| Use Case | Algorithm | Why |
|----------|-----------|-----|
| Most APIs | TOKEN_BUCKET | Allows bursts, smooth limiting |
| Strict limits | SLIDING_WINDOW_LOG | Most accurate |
| Simple cases | FIXED_WINDOW | Simplest, lowest memory |
| High traffic | SLIDING_WINDOW_COUNTER | Good balance |

## 📊 Monitoring

```java
// Check available capacity
long available = limiter.getAvailableCapacity();
System.out.println("Available: " + available);

// For Token Bucket
TokenBucketRateLimiter bucket = (TokenBucketRateLimiter) limiter;
System.out.println("Current tokens: " + bucket.getCurrentTokens());
```

## 🧪 Testing Your Integration

```java
@Test
public void testRateLimiting() {
    RateLimiter limiter = RateLimiterFactory.createSimple(5);
    
    // Should allow 5 requests
    for (int i = 0; i < 5; i++) {
        assertTrue(limiter.allowRequest());
    }
    
    // Should deny 6th request
    assertFalse(limiter.allowRequest());
}
```

## 🎓 Next Steps

1. Read [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) for interview tips
2. Check [README.md](README.md) for detailed documentation
3. Explore [UsageExamples.java](src/main/java/com/ratelimiter/examples/UsageExamples.java)
4. Run tests: `mvn test`

## 💡 Tips

- Start with Token Bucket for most use cases
- Use per-user limiting for multi-tenant systems
- Monitor available capacity for metrics
- Test with concurrent requests
- Consider distributed scenarios (Redis) for production

## 🐛 Troubleshooting

**Issue**: Requests denied immediately
- Check if rate limit is too low
- Verify time window configuration
- Ensure tokens have time to refill

**Issue**: Too many requests allowed
- Check algorithm choice (Fixed Window has boundary issues)
- Verify configuration values
- Test thread safety

**Issue**: Memory usage high
- Consider using Fixed Window or Token Bucket (O(1) memory)
- Avoid Sliding Window Log for high-traffic scenarios
- Implement user cleanup for inactive users

## 📞 Need Help?

- Check the examples in `src/main/java/com/ratelimiter/examples/`
- Read the interview strategy guide
- Review test cases for usage patterns

