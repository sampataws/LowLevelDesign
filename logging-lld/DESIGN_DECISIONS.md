# LoggingMC - Design Decisions and Trade-offs

## Overview
This document explains the key design decisions made during the development of the LoggingMC library, along with the rationale and trade-offs considered.

## 1. Synchronous vs Asynchronous Logger

### Decision: Provide Both Implementations

**Rationale:**
- Different use cases have different requirements
- Some applications prioritize reliability over performance
- Others need high throughput and can tolerate slight delays

**Comparison:**

| Aspect | Synchronous Logger | Asynchronous Logger |
|--------|-------------------|---------------------|
| **Latency** | Higher (blocking) | Lower (non-blocking) |
| **Throughput** | Limited by sink speed | High (batched processing) |
| **Memory Usage** | Minimal | Moderate (buffer) |
| **Complexity** | Simple | More complex |
| **Reliability** | Immediate write | Delayed write |
| **Thread Usage** | Uses caller thread | Uses background thread |
| **Best For** | Critical logs, low volume | High volume, performance-critical |

**Trade-offs:**
- ✅ Flexibility: Users can choose based on needs
- ❌ Complexity: More code to maintain
- ✅ Performance: Async provides better throughput
- ❌ Resources: Async uses more memory and threads

## 2. Blocking Queue for Async Logger

### Decision: Use `ArrayBlockingQueue` with Blocking Put

**Alternatives Considered:**

| Approach | Pros | Cons | Decision |
|----------|------|------|----------|
| **Blocking Queue (Chosen)** | No data loss, backpressure | Can block caller | ✅ Selected |
| **Non-blocking Queue** | Never blocks caller | Can lose messages | ❌ Rejected |
| **Unbounded Queue** | Never blocks | Can cause OOM | ❌ Rejected |
| **Drop on Full** | Never blocks | Data loss | ❌ Rejected |

**Rationale:**
- **No Data Loss**: Requirement explicitly states "shouldn't be any data loss"
- **Backpressure**: Blocking provides natural backpressure mechanism
- **Bounded Memory**: Prevents out-of-memory errors
- **Predictable Behavior**: Users know max memory usage upfront

**Implementation:**
```java
private final BlockingQueue<LogMessage> messageQueue;
messageQueue = new ArrayBlockingQueue<>(config.getBufferSize());
messageQueue.put(logMessage);  // Blocks if full
```

## 3. Two-Level Filtering (Logger + Sink)

### Decision: Filter at Both Logger and Sink Levels

**Rationale:**
- **Flexibility**: Different sinks can have different levels
- **Performance**: Logger-level filtering avoids creating unnecessary LogMessage objects
- **Use Case**: Console shows INFO+, file shows ERROR+

**Example:**
```java
Logger (WARN) → Filters DEBUG, INFO
  ├─ Sink1 (INFO) → Would show INFO+ (but logger already filtered)
  └─ Sink2 (ERROR) → Shows only ERROR, FATAL
```

**Benefits:**
- ✅ Maximum flexibility for different sink configurations
- ✅ Performance optimization (early filtering)
- ✅ Supports complex logging scenarios

**Trade-offs:**
- ❌ Slightly more complex logic
- ✅ Better performance and flexibility outweigh complexity

## 4. Timestamp at Message Creation

### Decision: Capture Timestamp When LogMessage is Created

**Alternatives:**

| Approach | Timestamp Represents | Accuracy | Decision |
|----------|---------------------|----------|----------|
| **At Creation (Chosen)** | When event occurred | Accurate | ✅ Selected |
| **At Write** | When written to sink | Inaccurate for async | ❌ Rejected |

**Rationale:**
- **Accuracy**: Timestamp should represent when the event occurred, not when it was written
- **Async Logging**: In async mode, write time can be significantly later
- **Debugging**: More useful for debugging to know actual event time

**Implementation:**
```java
public class LogMessage {
    private final LocalDateTime timestamp;
    
    public LogMessage(String content, LogLevel level) {
        this.timestamp = LocalDateTime.now();  // Captured immediately
    }
}
```

## 5. Immutable LogMessage

### Decision: Make LogMessage Fields Final

**Rationale:**
- **Thread Safety**: Immutable objects are inherently thread-safe
- **Correctness**: Prevents accidental modification
- **Async Safety**: Can be safely shared between threads

**Implementation:**
```java
public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final LocalDateTime timestamp;
    // No setters
}
```

**Benefits:**
- ✅ Thread-safe without synchronization
- ✅ Prevents bugs from accidental modification
- ✅ Clear intent: messages don't change after creation

## 6. Poison Pill Pattern for Shutdown

### Decision: Use Poison Pill to Signal Worker Thread Shutdown

**Alternatives:**

| Approach | Pros | Cons | Decision |
|----------|------|------|----------|
| **Poison Pill (Chosen)** | Clean, ensures queue drains | Requires special message | ✅ Selected |
| **Interrupt Thread** | Simple | May lose messages | ❌ Rejected |
| **Volatile Flag** | Simple | May lose messages | ❌ Rejected |

**Rationale:**
- **No Data Loss**: Ensures all queued messages are processed before shutdown
- **Clean Shutdown**: Worker thread exits naturally after processing all messages
- **Reliability**: Guaranteed message delivery

**Implementation:**
```java
private static final LogMessage POISON_PILL = new LogMessage("", LogLevel.DEBUG);

public void shutdown() {
    messageQueue.put(POISON_PILL);  // Signal shutdown
    workerThread.join(5000);        // Wait for completion
}
```

## 7. Synchronized Console Output

### Decision: Synchronize on System.out in StdoutSink

**Rationale:**
- **Thread Safety**: Multiple threads may write to console simultaneously
- **Output Integrity**: Prevents interleaved output
- **Readability**: Ensures each log line is complete

**Implementation:**
```java
synchronized (System.out) {
    System.out.println(logEntry);
}
```

**Benefits:**
- ✅ Clean, readable output
- ✅ No interleaved log lines
- ✅ Thread-safe console writes

**Trade-offs:**
- ❌ Slight performance impact (minimal for console I/O)
- ✅ Correctness more important than performance for console output

## 8. Interface-Based Design

### Decision: Use Interfaces for Logger and Sink

**Rationale:**
- **Extensibility**: Easy to add new implementations
- **Testability**: Can mock interfaces for testing
- **Flexibility**: Users can provide custom implementations
- **SOLID Principles**: Dependency Inversion Principle

**Structure:**
```
Logger (interface)
  ├─ SyncLogger
  └─ AsyncLogger

Sink (interface)
  ├─ StdoutSink
  ├─ FileSink (future)
  └─ DatabaseSink (future)
```

**Benefits:**
- ✅ Easy to extend with new sink types
- ✅ Testable with mocks
- ✅ Follows OO best practices
- ✅ Users can provide custom implementations

## 9. Configuration Object Pattern

### Decision: Use LoggerConfiguration Object Instead of Builder

**Alternatives:**

| Approach | Pros | Cons | Decision |
|----------|------|------|----------|
| **Config Object (Chosen)** | Simple, flexible | Mutable | ✅ Selected |
| **Builder Pattern** | Immutable, fluent API | More complex | ❌ Not needed |
| **Constructor Params** | Simple | Too many params | ❌ Rejected |

**Rationale:**
- **Simplicity**: Configuration object is straightforward
- **Flexibility**: Easy to add new configuration options
- **Readability**: Clear what each setting does

**Implementation:**
```java
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("MyLogger");
config.setLogLevel(LogLevel.INFO);
config.addSink(new StdoutSink());
```

## 10. Factory Pattern for Logger Creation

### Decision: Use LoggerFactory Instead of Direct Instantiation

**Rationale:**
- **Centralized Management**: Single place to manage all loggers
- **Lifecycle Management**: Factory handles creation and cleanup
- **Singleton Pattern**: Ensures one logger per name
- **Convenience**: Easy retrieval of existing loggers

**Benefits:**
- ✅ Prevents duplicate loggers with same name
- ✅ Centralized shutdown (shutdownAll)
- ✅ Easy logger retrieval by name
- ✅ Follows Factory pattern best practices

**Implementation:**
```java
Logger logger = LoggerFactory.createLogger(config);
Logger same = LoggerFactory.getLogger("MyLogger");
LoggerFactory.shutdownAll();
```

## 11. Error Handling Strategy

### Decision: Fail Gracefully, Continue with Other Sinks

**Rationale:**
- **Resilience**: One failing sink shouldn't stop all logging
- **Availability**: Logging should be highly available
- **Debugging**: Error messages help identify issues

**Implementation:**
```java
for (Sink sink : sinks) {
    try {
        sink.write(logMessage, timestampFormat);
    } catch (Exception e) {
        System.err.println("Error writing to sink: " + e.getMessage());
        // Continue with other sinks
    }
}
```

**Benefits:**
- ✅ High availability
- ✅ Partial success better than total failure
- ✅ Errors are reported but don't crash application

## 12. Log Level Priority

### Decision: Use Integer Priority with Comparison Method

**Rationale:**
- **Clarity**: Clear ordering of levels
- **Performance**: Fast integer comparison
- **Extensibility**: Easy to add new levels if needed

**Implementation:**
```java
public enum LogLevel {
    DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
    
    public boolean isAtLeast(LogLevel level) {
        return this.priority >= level.priority;
    }
}
```

**Benefits:**
- ✅ Fast comparison
- ✅ Clear semantics
- ✅ Easy to understand and use

## Summary of Key Trade-offs

| Decision | Benefit | Cost | Verdict |
|----------|---------|------|---------|
| Both Sync/Async | Flexibility | Complexity | ✅ Worth it |
| Blocking Queue | No data loss | Can block | ✅ Worth it |
| Two-level Filtering | Flexibility | Complexity | ✅ Worth it |
| Timestamp at Creation | Accuracy | None | ✅ Clear win |
| Immutable Message | Thread safety | None | ✅ Clear win |
| Poison Pill | Clean shutdown | Special message | ✅ Worth it |
| Synchronized Output | Clean output | Slight perf cost | ✅ Worth it |
| Interface Design | Extensibility | More files | ✅ Worth it |
| Config Object | Simplicity | Mutable | ✅ Good enough |
| Factory Pattern | Management | Indirection | ✅ Worth it |
| Graceful Errors | Resilience | Partial failure | ✅ Worth it |

## Conclusion

All design decisions were made with the following priorities:
1. **Correctness**: No data loss, thread safety, message ordering
2. **Reliability**: Graceful error handling, high availability
3. **Performance**: Async option for high throughput
4. **Extensibility**: Easy to add new features
5. **Simplicity**: Clear, readable code

The trade-offs were carefully considered, and in all cases, the benefits outweigh the costs for a production-ready logging library.

