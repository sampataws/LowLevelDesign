# Interview Strategy: Building a Rate Limiter Quickly

## 🎯 **The 30-60-90 Minute Strategy**

This guide shows you how to build a working rate limiter in an interview setting, focusing on getting a demo FAST, then iterating.

---

## **Phase 1: First 15-20 Minutes - Token Bucket MVP**

### **Step 1: Clarify Requirements (2-3 minutes)**

**What to say:**
```
"Let me confirm the key requirements:
1. Rate limiter controls request rate (e.g., 10 requests per second)
2. Should handle concurrent requests (thread-safe)
3. Return true/false for allow/deny
4. Support per-user rate limiting
5. Which algorithm? Token Bucket is most common - should I start with that?

Any specific requirements on distributed systems or single-node?"
```

### **Step 2: Quick Design (2-3 minutes)**

Draw this on whiteboard:
```
RateLimiter (interface)
    ↓
TokenBucketRateLimiter
    - capacity (max tokens)
    - tokens (current tokens)
    - refillRate (tokens per second)
    - lastRefillTime
    
allowRequest() → boolean
```

**Say:** *"I'll start with Token Bucket - it's simple, allows bursts, and works well for most cases."*

### **Step 3: Code in This Order (10-15 minutes)**

#### **1. RateLimiter interface (1 minute)**
```java
public interface RateLimiter {
    boolean allowRequest();
}
```

**Say:** *"Starting with interface for extensibility - can add other algorithms later..."*

#### **2. Token Bucket Implementation (10 minutes)**
```java
public class TokenBucketRateLimiter implements RateLimiter {
    private final long capacity;        // Max tokens
    private final double refillRate;    // Tokens per second
    private double tokens;              // Current tokens
    private long lastRefillTime;        // Last refill timestamp
    private final Object lock = new Object();
    
    public TokenBucketRateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;  // Start full
        this.lastRefillTime = System.nanoTime();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            refillTokens();
            
            if (tokens >= 1) {
                tokens -= 1;
                return true;  // Allow request
            }
            return false;  // Deny request
        }
    }
    
    private void refillTokens() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
        
        // Add tokens based on elapsed time
        double tokensToAdd = elapsedSeconds * refillRate;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        
        lastRefillTime = now;
    }
}
```

**Say:** *"Using synchronized for thread safety. Refilling tokens based on elapsed time..."*

#### **3. DEMO IT! (2 minutes)**
```java
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        // 5 requests per second, capacity 5
        RateLimiter limiter = new TokenBucketRateLimiter(5, 5.0);
        
        System.out.println("Testing rate limiter (5 req/sec):");
        
        // Try 10 requests immediately
        for (int i = 1; i <= 10; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.println("Request " + i + ": " + 
                (allowed ? "✅ ALLOWED" : "❌ DENIED"));
        }
        
        // Wait 1 second and try again
        System.out.println("\nWaiting 1 second...");
        Thread.sleep(1000);
        
        for (int i = 11; i <= 15; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.println("Request " + i + ": " + 
                (allowed ? "✅ ALLOWED" : "❌ DENIED"));
        }
        
        System.out.println("\n✅ Basic rate limiter working!");
    }
}
```

**RUN IT NOW!** Show it works before adding complexity.

**Expected Output:**
```
Request 1-5: ✅ ALLOWED
Request 6-10: ❌ DENIED
[wait 1 second]
Request 11-15: ✅ ALLOWED (5 tokens refilled)
```

---

## **Phase 2: Next 15-20 Minutes - Add Configuration & Per-User Limiting**

### **Feature 1: Configuration Object (5 minutes)**

```java
public enum RateLimiterAlgorithm {
    TOKEN_BUCKET,
    SLIDING_WINDOW,
    FIXED_WINDOW
}

public class RateLimiterConfig {
    private long maxRequests = 10;
    private long timeWindowSeconds = 1;
    private RateLimiterAlgorithm algorithm = RateLimiterAlgorithm.TOKEN_BUCKET;
    private long bucketCapacity;  // Optional, defaults to maxRequests
    
    public RateLimiterConfig setMaxRequests(long maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }
    
    public RateLimiterConfig setTimeWindowSeconds(long seconds) {
        this.timeWindowSeconds = seconds;
        return this;
    }
    
    public RateLimiterConfig setAlgorithm(RateLimiterAlgorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }
    
    public RateLimiterConfig setBucketCapacity(long capacity) {
        this.bucketCapacity = capacity;
        return this;
    }
    
    // Getters
    public long getMaxRequests() { return maxRequests; }
    public long getTimeWindowSeconds() { return timeWindowSeconds; }
    public RateLimiterAlgorithm getAlgorithm() { return algorithm; }
    public long getBucketCapacity() { 
        return bucketCapacity > 0 ? bucketCapacity : maxRequests; 
    }
}
```

**Say:** *"Adding configuration for cleaner API and future extensibility..."*

### **Feature 2: Factory Pattern (3 minutes)**

```java
public class RateLimiterFactory {
    public static RateLimiter create(RateLimiterConfig config) {
        switch (config.getAlgorithm()) {
            case TOKEN_BUCKET:
                double refillRate = (double) config.getMaxRequests() / 
                                   config.getTimeWindowSeconds();
                return new TokenBucketRateLimiter(
                    config.getBucketCapacity(), 
                    refillRate
                );
            // Add other algorithms later
            default:
                throw new IllegalArgumentException(
                    "Unsupported algorithm: " + config.getAlgorithm()
                );
        }
    }
}
```

### **Feature 3: Per-User Rate Limiting (7 minutes)**

```java
import java.util.concurrent.ConcurrentHashMap;

public class UserRateLimiterManager {
    private final ConcurrentHashMap<String, RateLimiter> userLimiters;
    private final RateLimiterConfig config;
    
    public UserRateLimiterManager(RateLimiterConfig config) {
        this.userLimiters = new ConcurrentHashMap<>();
        this.config = config;
    }
    
    public boolean allowRequest(String userId) {
        RateLimiter limiter = userLimiters.computeIfAbsent(
            userId, 
            id -> RateLimiterFactory.create(config)
        );
        return limiter.allowRequest();
    }
    
    public void removeUser(String userId) {
        userLimiters.remove(userId);
    }
    
    public int getUserCount() {
        return userLimiters.size();
    }
}
```

**Say:** *"Using ConcurrentHashMap for thread-safe per-user limiters. computeIfAbsent creates limiter on first request..."*

#### **Test Per-User Limiting:**
```java
public static void testPerUser() throws InterruptedException {
    RateLimiterConfig config = new RateLimiterConfig()
        .setMaxRequests(3)
        .setTimeWindowSeconds(1);
    
    UserRateLimiterManager manager = new UserRateLimiterManager(config);
    
    // User1: 5 requests
    System.out.println("User1 requests:");
    for (int i = 1; i <= 5; i++) {
        boolean allowed = manager.allowRequest("user1");
        System.out.println("  Request " + i + ": " + 
            (allowed ? "✅" : "❌"));
    }
    
    // User2: 5 requests (should have own limit)
    System.out.println("\nUser2 requests:");
    for (int i = 1; i <= 5; i++) {
        boolean allowed = manager.allowRequest("user2");
        System.out.println("  Request " + i + ": " + 
            (allowed ? "✅" : "❌"));
    }
    
    System.out.println("\n✅ Per-user rate limiting works!");
}
```

---

## **Phase 3: Next 15-20 Minutes - Add Another Algorithm**

### **Sliding Window Log (More Accurate)**

```java
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlidingWindowLogRateLimiter implements RateLimiter {
    private final long maxRequests;
    private final long windowSizeNanos;
    private final ConcurrentLinkedQueue<Long> requestTimestamps;
    private final Object lock = new Object();
    
    public SlidingWindowLogRateLimiter(long maxRequests, long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSeconds * 1_000_000_000L;
        this.requestTimestamps = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            long now = System.nanoTime();
            long windowStart = now - windowSizeNanos;
            
            // Remove old timestamps outside window
            while (!requestTimestamps.isEmpty() && 
                   requestTimestamps.peek() < windowStart) {
                requestTimestamps.poll();
            }
            
            // Check if under limit
            if (requestTimestamps.size() < maxRequests) {
                requestTimestamps.offer(now);
                return true;
            }
            
            return false;
        }
    }
}
```

**Say:** *"Sliding Window Log is more accurate - tracks exact timestamps. Trade-off is O(N) memory vs O(1) for Token Bucket..."*

#### **Update Factory:**
```java
public class RateLimiterFactory {
    public static RateLimiter create(RateLimiterConfig config) {
        switch (config.getAlgorithm()) {
            case TOKEN_BUCKET:
                double refillRate = (double) config.getMaxRequests() / 
                                   config.getTimeWindowSeconds();
                return new TokenBucketRateLimiter(
                    config.getBucketCapacity(), refillRate
                );
            
            case SLIDING_WINDOW:
                return new SlidingWindowLogRateLimiter(
                    config.getMaxRequests(),
                    config.getTimeWindowSeconds()
                );
            
            default:
                throw new IllegalArgumentException(
                    "Unsupported algorithm: " + config.getAlgorithm()
                );
        }
    }
}
```

---

## **Phase 4: Final 10-15 Minutes - Polish & Demo**

### **Add Fixed Window (Simplest Algorithm)**

```java
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowRateLimiter implements RateLimiter {
    private final long maxRequests;
    private final long windowSizeNanos;
    private final AtomicLong counter;
    private volatile long windowStart;
    private final Object lock = new Object();
    
    public FixedWindowRateLimiter(long maxRequests, long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSeconds * 1_000_000_000L;
        this.counter = new AtomicLong(0);
        this.windowStart = System.nanoTime();
    }
    
    @Override
    public boolean allowRequest() {
        synchronized (lock) {
            long now = System.nanoTime();
            
            // Check if window expired
            if (now - windowStart >= windowSizeNanos) {
                counter.set(0);
                windowStart = now;
            }
            
            // Check if under limit
            if (counter.get() < maxRequests) {
                counter.incrementAndGet();
                return true;
            }
            
            return false;
        }
    }
}
```

**Say:** *"Fixed Window is simplest but has boundary issue - can get 2x requests at window edges. Good for simple use cases..."*

### **Final Comprehensive Demo**

```java
public class FinalDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Rate Limiter Demo ===\n");
        
        // Demo 1: Token Bucket
        System.out.println("1. Token Bucket (allows bursts):");
        testAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET);
        
        // Demo 2: Sliding Window
        System.out.println("\n2. Sliding Window (precise):");
        testAlgorithm(RateLimiterAlgorithm.SLIDING_WINDOW);
        
        // Demo 3: Per-User
        System.out.println("\n3. Per-User Rate Limiting:");
        testPerUser();
        
        System.out.println("\n✅ Complete rate limiter working!");
    }
    
    private static void testAlgorithm(RateLimiterAlgorithm algo) 
            throws InterruptedException {
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(5)
            .setTimeWindowSeconds(1)
            .setAlgorithm(algo);
        
        RateLimiter limiter = RateLimiterFactory.create(config);
        
        // 10 rapid requests
        int allowed = 0, denied = 0;
        for (int i = 0; i < 10; i++) {
            if (limiter.allowRequest()) allowed++;
            else denied++;
        }
        System.out.println("  Rapid: " + allowed + " allowed, " + denied + " denied");
        
        // Wait and retry
        Thread.sleep(1000);
        allowed = denied = 0;
        for (int i = 0; i < 10; i++) {
            if (limiter.allowRequest()) allowed++;
            else denied++;
        }
        System.out.println("  After 1s: " + allowed + " allowed, " + denied + " denied");
    }
    
    private static void testPerUser() {
        RateLimiterConfig config = new RateLimiterConfig()
            .setMaxRequests(3)
            .setTimeWindowSeconds(1);
        
        UserRateLimiterManager manager = new UserRateLimiterManager(config);
        
        // Each user gets own limit
        for (String user : new String[]{"alice", "bob"}) {
            int allowed = 0;
            for (int i = 0; i < 5; i++) {
                if (manager.allowRequest(user)) allowed++;
            }
            System.out.println("  " + user + ": " + allowed + "/5 allowed");
        }
    }
}
```

---

## 🎤 **What to SAY During the Interview**

### **Opening (First 2 minutes)**
```
"I'll implement a rate limiter starting with Token Bucket algorithm - it's 
the most commonly used and balances simplicity with real-world needs.

My approach:
1. Basic Token Bucket (15 min) - get it working
2. Add configuration and per-user limiting (15 min)
3. Add another algorithm for comparison (15 min)
4. Polish and comprehensive demo (15 min)

Let me start with the interface..."
```

### **While Coding**
```
"Starting with interface for extensibility..."

"Token Bucket refills tokens over time - allows bursts up to capacity..."

"Using synchronized for thread safety - could use ReentrantLock for fairness..."

"Let me run this to verify basic functionality..."

"✅ Great! Now adding per-user limiting with ConcurrentHashMap..."
```

### **When Discussing Trade-offs**
```
"Token Bucket vs Sliding Window:
- Token Bucket: O(1) memory, allows bursts, simpler
- Sliding Window: O(N) memory, more precise, no boundary issues

For most APIs, Token Bucket is preferred. For strict limits, Sliding Window."
```

---

## ⏱️ **Time Management**

| Time | What to Have | Status |
|------|--------------|--------|
| **15 min** | Token Bucket working | ✅ DEMO |
| **30 min** | Config + per-user limiting | ✅ DEMO |
| **45 min** | Second algorithm added | ✅ DEMO |
| **60 min** | Complete with demos | ✅ DEMO |

---

## 🎯 **Priority Order**

### 1. ✅ **Must Have** (First 20 min)
- RateLimiter interface
- TokenBucketRateLimiter implementation
- Basic demo working
- Thread safety

### 2. ✅ **Should Have** (Next 20 min)
- Configuration object
- Factory pattern
- Per-user rate limiting
- UserRateLimiterManager

### 3. ✅ **Nice to Have** (Next 20 min)
- Second algorithm (Sliding Window or Fixed Window)
- Comprehensive demo
- Algorithm comparison

### 4. ⭐ **If Time Permits**
- Unit tests
- Distributed rate limiter discussion
- Redis-based implementation
- Metrics/monitoring hooks

---

## 💬 **Common Interview Questions & Answers**

### **Q: "Why Token Bucket over Leaky Bucket?"**
```
A: "Token Bucket allows bursts up to capacity, which is more practical for 
real-world APIs. Leaky Bucket enforces strict constant rate. For example, 
if a user hasn't made requests for a while, Token Bucket lets them make 
several requests immediately (up to capacity), while Leaky Bucket would 
still enforce the rate."
```

### **Q: "How would you implement this in a distributed system?"**
```
A: "I'd use Redis with atomic operations:

1. Token Bucket with Redis:
   - Store: tokens, lastRefillTime
   - Use Lua script for atomic refill + decrement
   - EXPIRE for automatic cleanup

2. Sliding Window with Redis:
   - Use ZSET with timestamps as scores
   - ZREMRANGEBYSCORE to remove old entries
   - ZCARD to count requests in window

3. Fixed Window with Redis:
   - Simple INCR with EXPIRE
   - Key: user:timestamp_window

Trade-off: Network latency vs accuracy. Consider local cache with 
eventual consistency for high-traffic scenarios."
```

### **Q: "What about the boundary problem in Fixed Window?"**
```
A: "Fixed Window can allow 2x requests at window boundaries. Example:
- Window: 0-60 seconds, limit: 100 req/min
- User makes 100 requests at 59s
- Window resets at 60s
- User makes 100 more requests at 61s
- Result: 200 requests in 2 seconds!

Solutions:
1. Use Sliding Window (tracks exact timestamps)
2. Use Sliding Window Counter (approximation, better than fixed)
3. Accept the trade-off for simplicity (often acceptable)"
```

### **Q: "How do you handle memory for per-user limiters?"**
```
A: "Several strategies:

1. LRU Cache: Evict least recently used limiters
2. TTL: Remove inactive users after timeout
3. Lazy cleanup: Remove on next access if expired
4. Separate tier: Different limits for different user tiers

Implementation:
- Use ConcurrentHashMap with periodic cleanup
- Or use Guava Cache with expiration
- Monitor memory usage and adjust capacity

For millions of users, consider:
- Distributed cache (Redis)
- Bloom filters for quick checks
- Rate limit at API gateway level"
```

### **Q: "How would you test this?"**
```
A: "Unit tests:
1. Single request allowed
2. Exceeding limit denies requests
3. Tokens refill over time
4. Thread safety (concurrent requests)
5. Per-user isolation

Integration tests:
1. Burst traffic handling
2. Sustained load
3. Time-based refilling
4. Edge cases (clock skew, negative time)

Load tests:
1. Concurrent users
2. High request rate
3. Memory usage under load"
```

---

## 🚀 **Quick Start Checklist**

```
☐ 1. RateLimiter interface (1 min)
☐ 2. TokenBucketRateLimiter (10 min)
☐ 3. Basic demo (2 min)
☐ 4. RUN IT! ✅
☐ 5. RateLimiterConfig (5 min)
☐ 6. RateLimiterFactory (3 min)
☐ 7. UserRateLimiterManager (7 min)
☐ 8. Test per-user (3 min)
☐ 9. Add second algorithm (15 min)
☐ 10. Final comprehensive demo (5 min)
```

**Total: ~50 minutes for full working solution**

---

## 🎓 **Final Tips**

### **DO:**
✅ Start with Token Bucket (most practical)
✅ Explain trade-offs between algorithms
✅ Demo frequently
✅ Discuss thread safety
✅ Consider distributed scenarios
✅ Talk about monitoring

### **DON'T:**
❌ Try to implement all algorithms at once
❌ Forget thread safety
❌ Ignore time precision (use nanoTime)
❌ Over-engineer initially
❌ Skip the demo

### **Remember:**
> **A working Token Bucket beats a half-finished complex solution!** 🎯

---

## 📋 **Algorithm Comparison Table**

| Algorithm | Memory | Accuracy | Complexity | Bursts | Use Case |
|-----------|--------|----------|------------|--------|----------|
| Token Bucket | O(1) | Good | Medium | Yes | Most APIs |
| Sliding Window Log | O(N) | Excellent | Medium | No | Strict limits |
| Fixed Window | O(1) | Poor | Simple | Boundary | Simple cases |
| Sliding Window Counter | O(1) | Good | Medium | Partial | High traffic |

---

## 🏆 **Success Criteria**

By the end of the interview, you should have:

✅ Working rate limiter (at least Token Bucket)
✅ Thread-safe implementation
✅ Per-user rate limiting
✅ Configuration object
✅ Factory pattern
✅ Demonstrated code running
✅ Explained algorithm trade-offs
✅ Discussed distributed scenarios

**Good luck with your interview!** 🚀

Remember: **Start simple, iterate, and always demo!**

