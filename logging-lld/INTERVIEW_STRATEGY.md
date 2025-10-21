# Interview Strategy: Building a Demoable Logging Library Quickly

## 🎯 **The 30-60-90 Minute Strategy**

This guide shows you how to build a working logging library in an interview setting, focusing on getting a demo FAST, then iterating.

---

## **Phase 1: First 15-20 Minutes - Minimum Viable Product**

### **Step 1: Clarify Requirements (2-3 minutes)**

**What to say:**
```
"Let me confirm the key requirements:
1. Logger accepts messages with levels (DEBUG, INFO, WARN, ERROR, FATAL)
2. Messages go to sinks (starting with STDOUT)
3. Support sync and async modes
4. Thread-safe, ordered messages, no data loss
5. Configurable via a config object

Is that correct? Any priorities?"
```

### **Step 2: Quick Design (2-3 minutes)**

Draw this on whiteboard/paper:
```
LogLevel (enum) → LogMessage → Logger → Sink
                                  ↓
                          SyncLogger / AsyncLogger
```

**Say:** *"I'll start with the simplest working version, then add features."*

### **Step 3: Code in This Order (10-15 minutes)**

**Order matters! Build from bottom up:**

#### **1. LogLevel enum (1 minute)**
```java
public enum LogLevel {
    DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
    
    private final int priority;
    
    LogLevel(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
}
```

**Say:** *"Starting with LogLevel because it has no dependencies..."*

#### **2. LogMessage class (2 minutes)**
```java
import java.time.LocalDateTime;

public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final LocalDateTime timestamp;
    
    public LogMessage(String content, LogLevel level) {
        this.content = content;
        this.level = level;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getContent() { return content; }
    public LogLevel getLevel() { return level; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

**Say:** *"Keeping it immutable for thread safety..."*

#### **3. Sink interface (1 minute)**
```java
public interface Sink {
    void write(LogMessage message);
}
```

**Say:** *"Interface for extensibility - can add FileSink, DatabaseSink later..."*

#### **4. StdoutSink implementation (2 minutes)**
```java
public class StdoutSink implements Sink {
    @Override
    public void write(LogMessage message) {
        System.out.printf("%s [%s] %s%n",
            message.getTimestamp(),
            message.getLevel(),
            message.getContent());
    }
}
```

**Say:** *"StdoutSink is the simplest sink to get something working..."*

#### **5. Simple SyncLogger (5 minutes)**
```java
import java.util.List;

public class SyncLogger {
    private final List<Sink> sinks;
    
    public SyncLogger(List<Sink> sinks) {
        this.sinks = sinks;
    }
    
    public void log(String message, LogLevel level) {
        LogMessage logMsg = new LogMessage(message, level);
        for (Sink sink : sinks) {
            sink.write(logMsg);
        }
    }
    
    // Convenience methods
    public void debug(String msg) { log(msg, LogLevel.DEBUG); }
    public void info(String msg) { log(msg, LogLevel.INFO); }
    public void warn(String msg) { log(msg, LogLevel.WARN); }
    public void error(String msg) { log(msg, LogLevel.ERROR); }
    public void fatal(String msg) { log(msg, LogLevel.FATAL); }
}
```

#### **6. DEMO IT! (2 minutes)**
```java
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        // Create logger with stdout sink
        SyncLogger logger = new SyncLogger(
            Arrays.asList(new StdoutSink())
        );
        
        // Log some messages
        logger.info("Application started");
        logger.warn("This is a warning");
        logger.error("Something went wrong");
        
        System.out.println("\n✅ Basic logger working!");
    }
}
```

**RUN IT NOW!** Show it works before adding complexity.

**Say:** *"Let me run this to verify the basic functionality works..."*

---

## **Phase 2: Next 15-20 Minutes - Add Critical Features**

Now that you have a working demo, add features **one at a time**, testing after each:

### **Feature 1: Thread Safety (5 minutes)**

```java
public class SyncLogger {
    private final List<Sink> sinks;
    private final Object lock = new Object();  // ADD THIS
    
    public SyncLogger(List<Sink> sinks) {
        this.sinks = sinks;
    }
    
    public void log(String message, LogLevel level) {
        LogMessage logMsg = new LogMessage(message, level);
        
        // ADD SYNCHRONIZATION
        synchronized (lock) {
            for (Sink sink : sinks) {
                sink.write(logMsg);
            }
        }
    }
    
    public void info(String msg) { log(msg, LogLevel.INFO); }
    public void error(String msg) { log(msg, LogLevel.ERROR); }
    // ... other methods
}
```

**Test it:**
```java
public static void testMultiThreaded() {
    SyncLogger logger = new SyncLogger(Arrays.asList(new StdoutSink()));
    
    // Create 3 threads logging concurrently
    for (int i = 0; i < 3; i++) {
        final int id = i;
        new Thread(() -> {
            for (int j = 0; j < 5; j++) {
                logger.info("Thread " + id + " - Message " + j);
            }
        }).start();
    }
    
    System.out.println("✅ Multi-threaded logging works!");
}
```

**Say:** *"Adding thread safety with synchronized block to ensure messages don't get interleaved..."*

### **Feature 2: Log Level Filtering (5 minutes)**

```java
public class SyncLogger {
    private final List<Sink> sinks;
    private final Object lock = new Object();
    private final LogLevel minLevel;  // ADD THIS
    
    public SyncLogger(List<Sink> sinks, LogLevel minLevel) {
        this.sinks = sinks;
        this.minLevel = minLevel;
    }
    
    public void log(String message, LogLevel level) {
        // ADD FILTERING
        if (level.getPriority() < minLevel.getPriority()) {
            return;  // Filter out messages below minimum level
        }
        
        LogMessage logMsg = new LogMessage(message, level);
        synchronized (lock) {
            for (Sink sink : sinks) {
                sink.write(logMsg);
            }
        }
    }
    
    // ... rest of methods
}
```

**Test it:**
```java
public static void testFiltering() {
    // Only WARN and above
    SyncLogger logger = new SyncLogger(
        Arrays.asList(new StdoutSink()),
        LogLevel.WARN
    );
    
    logger.debug("Won't appear");
    logger.info("Won't appear");
    logger.warn("Will appear");
    logger.error("Will appear");
    
    System.out.println("✅ Log level filtering works!");
}
```

**Say:** *"Adding log level filtering to avoid processing unnecessary messages..."*

### **Feature 3: Async Logger (10 minutes)**

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AsyncLogger {
    private final List<Sink> sinks;
    private final BlockingQueue<LogMessage> queue;
    private final Thread workerThread;
    private volatile boolean running = true;
    
    public AsyncLogger(List<Sink> sinks, int bufferSize) {
        this.sinks = sinks;
        this.queue = new ArrayBlockingQueue<>(bufferSize);
        
        // Start background worker thread
        this.workerThread = new Thread(() -> {
            while (running) {
                try {
                    LogMessage msg = queue.take();
                    for (Sink sink : sinks) {
                        sink.write(msg);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        workerThread.start();
    }
    
    public void log(String message, LogLevel level) {
        try {
            queue.put(new LogMessage(message, level));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void info(String msg) { log(msg, LogLevel.INFO); }
    public void error(String msg) { log(msg, LogLevel.ERROR); }
    
    public void shutdown() {
        running = false;
        workerThread.interrupt();
    }
}
```

**Test it:**
```java
public static void testAsync() throws InterruptedException {
    AsyncLogger logger = new AsyncLogger(
        Arrays.asList(new StdoutSink()),
        10  // buffer size
    );
    
    for (int i = 0; i < 20; i++) {
        logger.info("Async message " + i);
    }
    
    Thread.sleep(200);  // Let it process
    logger.shutdown();
    
    System.out.println("✅ Async logger works!");
}
```

**Say:** *"Using BlockingQueue for thread-safe async processing. It blocks when full to prevent data loss..."*

---

## **Phase 3: Final 15-20 Minutes - Polish & Configuration**

### **Add Configuration Object**

```java
import java.util.ArrayList;
import java.util.List;

public class LoggerConfig {
    private String name;
    private LogLevel level = LogLevel.INFO;
    private boolean async = false;
    private int bufferSize = 10;
    private List<Sink> sinks = new ArrayList<>();
    
    public LoggerConfig setName(String name) {
        this.name = name;
        return this;
    }
    
    public LoggerConfig setLevel(LogLevel level) {
        this.level = level;
        return this;
    }
    
    public LoggerConfig setAsync(boolean async) {
        this.async = async;
        return this;
    }
    
    public LoggerConfig setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
    
    public LoggerConfig addSink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }
    
    // Getters
    public String getName() { return name; }
    public LogLevel getLevel() { return level; }
    public boolean isAsync() { return async; }
    public int getBufferSize() { return bufferSize; }
    public List<Sink> getSinks() { return sinks; }
}
```

### **Add Factory Pattern**

```java
public class LoggerFactory {
    public static Object createLogger(LoggerConfig config) {
        if (config.isAsync()) {
            return new AsyncLogger(config.getSinks(), config.getBufferSize());
        } else {
            return new SyncLogger(config.getSinks(), config.getLevel());
        }
    }
}
```

### **Final Demo**

```java
public class FinalDemo {
    public static void main(String[] args) throws InterruptedException {
        // Sync logger
        LoggerConfig syncConfig = new LoggerConfig()
            .setName("SyncLogger")
            .setLevel(LogLevel.INFO)
            .setAsync(false)
            .addSink(new StdoutSink());
        
        SyncLogger syncLogger = (SyncLogger) LoggerFactory.createLogger(syncConfig);
        syncLogger.info("Sync message");
        
        // Async logger
        LoggerConfig asyncConfig = new LoggerConfig()
            .setName("AsyncLogger")
            .setLevel(LogLevel.INFO)
            .setAsync(true)
            .setBufferSize(25)
            .addSink(new StdoutSink());
        
        AsyncLogger asyncLogger = (AsyncLogger) LoggerFactory.createLogger(asyncConfig);
        asyncLogger.info("Async message");
        
        Thread.sleep(100);
        asyncLogger.shutdown();
        
        System.out.println("✅ Complete logging library working!");
    }
}
```

---

## 🎤 **What to SAY During the Interview**

### **Opening (First 2 minutes)**
```
"I'll start with a minimal working version that demonstrates the core 
functionality, then iteratively add features. This ensures we have 
something demoable at every stage.

My approach:
1. Build basic sync logger first (15 min)
2. Add thread safety and filtering (15 min)
3. Add async logger (15 min)
4. Add configuration and polish (15 min)

Let me start with the foundational classes..."
```

### **While Coding**
```
"I'm starting with LogLevel enum because it has no dependencies..."

"Now LogMessage - keeping it immutable for thread safety..."

"StdoutSink is the simplest sink to get something working..."

"Let me run this now to verify it works before adding complexity..."

"✅ Great! Basic logging works. Now let me add thread safety..."
```

### **When Stuck**
```
"I'm thinking about two approaches here:
1. Blocking queue - no data loss but can block
2. Non-blocking - faster but might lose messages

Given the requirement of 'no data loss', I'll go with blocking queue."
```

---

## ⏱️ **Time Management**

| Time | What to Have | Status |
|------|--------------|--------|
| **15 min** | Basic sync logger working | ✅ DEMO |
| **30 min** | Thread-safe + filtering | ✅ DEMO |
| **45 min** | Async logger working | ✅ DEMO |
| **60 min** | Configuration + polish | ✅ DEMO |

**Key Rule**: Always have something demoable. Don't spend 45 minutes designing without running code!

---

## 🎯 **Priority Order**

If running out of time, implement in this order:

### 1. ✅ **Must Have** (First 20 min)
- LogLevel enum
- LogMessage class
- Sink interface
- StdoutSink implementation
- Basic SyncLogger
- Demo working

### 2. ✅ **Should Have** (Next 20 min)
- Thread safety (synchronized)
- Log level filtering
- Multiple sinks support

### 3. ✅ **Nice to Have** (Next 20 min)
- AsyncLogger with BlockingQueue
- Configuration object
- Factory pattern

### 4. ⭐ **If Time Permits**
- Unit tests
- Error handling (try-catch in sinks)
- Graceful shutdown
- Sink-level filtering

---

## 🚀 **Quick Start Checklist**

Before the interview, memorize this order:

```
☐ 1. LogLevel enum (1 min)
☐ 2. LogMessage class (2 min)
☐ 3. Sink interface (1 min)
☐ 4. StdoutSink impl (2 min)
☐ 5. SyncLogger (5 min)
☐ 6. Demo main() (2 min)
☐ 7. RUN IT! ✅
☐ 8. Add thread safety (5 min)
☐ 9. Add filtering (5 min)
☐ 10. Add AsyncLogger (10 min)
☐ 11. Add config (10 min)
```

**Total: ~45 minutes for full working solution**

---

## 💬 **Common Interview Questions & Answers**

### **Q: "Why did you start with SyncLogger instead of AsyncLogger?"**
```
A: "I wanted to get a working demo quickly. SyncLogger is simpler and 
demonstrates the core functionality. Once that works, AsyncLogger is 
just adding a queue and worker thread. This incremental approach 
reduces risk."
```

### **Q: "How would you handle errors in sinks?"**
```
A: "I'd wrap each sink.write() in try-catch and continue with other 
sinks. One failing sink shouldn't stop all logging. I'd also log the 
error to System.err."

Code:
for (Sink sink : sinks) {
    try {
        sink.write(logMsg);
    } catch (Exception e) {
        System.err.println("Error in sink: " + e.getMessage());
    }
}
```

### **Q: "What if the async queue fills up?"**
```
A: "I used BlockingQueue.put() which blocks the caller. This prevents 
data loss per requirements. Alternative would be to drop messages, 
but that violates 'no data loss' requirement.

If blocking is unacceptable, I could:
1. Use offer() with timeout
2. Add metrics to monitor queue depth
3. Implement backpressure strategy"
```

### **Q: "How do you ensure message ordering?"**
```
A: "For SyncLogger, the synchronized block ensures sequential processing.
For AsyncLogger, the single worker thread processes the BlockingQueue 
in FIFO order, maintaining message order."
```

### **Q: "How would you add a FileSink?"**
```
A: "I'd implement the Sink interface:

public class FileSink implements Sink {
    private final BufferedWriter writer;
    
    public FileSink(String filePath) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(filePath, true));
    }
    
    @Override
    public void write(LogMessage message) {
        try {
            writer.write(format(message));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e);
        }
    }
}

The interface makes it easy to add new sink types."
```

### **Q: "How would you test this?"**
```
A: "I'd write unit tests for:
1. LogLevel priority ordering
2. LogMessage immutability
3. SyncLogger thread safety (multiple threads)
4. AsyncLogger message ordering
5. Log level filtering
6. Sink error handling

For integration tests:
1. Multi-threaded stress test
2. Async queue full scenario
3. Multiple sinks with different levels"
```

---

## 🎓 **Final Tips**

### **DO:**
✅ Talk while coding - explain your thought process
✅ Run code frequently - demo after each feature
✅ Start simple - don't over-engineer initially
✅ Ask questions - clarify requirements early
✅ Handle errors - show you think about edge cases
✅ Test as you go - quick manual tests in main()

### **DON'T:**
❌ Design for 30 minutes without coding
❌ Try to make it perfect the first time
❌ Add features without testing previous ones
❌ Ignore edge cases (null checks, empty lists)
❌ Forget to demo your working code

### **Remember:**
> **A simple working solution beats a complex broken one!** 🎯

---

## 📋 **Complete Code Template (Copy-Paste Ready)**

Save this as a starting template:

```java
// LogLevel.java
public enum LogLevel {
    DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
    private final int priority;
    LogLevel(int priority) { this.priority = priority; }
    public int getPriority() { return priority; }
}

// LogMessage.java
import java.time.LocalDateTime;
public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final LocalDateTime timestamp;
    public LogMessage(String content, LogLevel level) {
        this.content = content;
        this.level = level;
        this.timestamp = LocalDateTime.now();
    }
    public String getContent() { return content; }
    public LogLevel getLevel() { return level; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

// Sink.java
public interface Sink {
    void write(LogMessage message);
}

// StdoutSink.java
public class StdoutSink implements Sink {
    @Override
    public void write(LogMessage message) {
        System.out.printf("%s [%s] %s%n",
            message.getTimestamp(), message.getLevel(), message.getContent());
    }
}

// SyncLogger.java
import java.util.List;
public class SyncLogger {
    private final List<Sink> sinks;
    private final Object lock = new Object();
    public SyncLogger(List<Sink> sinks) { this.sinks = sinks; }
    public void log(String message, LogLevel level) {
        LogMessage msg = new LogMessage(message, level);
        synchronized (lock) {
            for (Sink sink : sinks) { sink.write(msg); }
        }
    }
    public void info(String msg) { log(msg, LogLevel.INFO); }
    public void error(String msg) { log(msg, LogLevel.ERROR); }
}

// Demo.java
import java.util.Arrays;
public class Demo {
    public static void main(String[] args) {
        SyncLogger logger = new SyncLogger(Arrays.asList(new StdoutSink()));
        logger.info("Started");
        logger.error("Error");
        System.out.println("✅ Works!");
    }
}
```

---

## 🎬 **Interview Simulation**

### **Minute 0-2: Introduction**
**Interviewer:** "Build a logging library with sync/async support, multiple log levels, and sinks."

**You:** "Let me clarify the requirements... [ask questions]. I'll build incrementally - basic sync logger first, then add features."

### **Minute 2-15: Basic Implementation**
**You:** "Starting with LogLevel enum... [code]... Now LogMessage... [code]... Let me run this..."
**Output:** ✅ Basic logger works!

### **Minute 15-30: Add Features**
**You:** "Adding thread safety... [code]... Testing with multiple threads... [run]"
**Output:** ✅ Thread-safe!

### **Minute 30-45: Async Logger**
**You:** "Now async logger with BlockingQueue... [code]... This prevents data loss by blocking when full..."
**Output:** ✅ Async works!

### **Minute 45-60: Polish**
**You:** "Adding configuration object for cleaner API... [code]"
**Output:** ✅ Complete solution!

---

## 🏆 **Success Criteria**

By the end of the interview, you should have:

✅ Working sync logger
✅ Working async logger  
✅ Thread-safe implementation
✅ Log level filtering
✅ Multiple sinks support
✅ Configuration object
✅ Demonstrated code running
✅ Explained design decisions
✅ Handled edge cases

**Good luck with your interview!** 🚀

Remember: **Code first, perfect later!**

