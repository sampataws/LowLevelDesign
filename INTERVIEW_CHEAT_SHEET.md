# Logging Library - Interview Cheat Sheet

## ⚡ Quick Reference for 60-Minute Interview

---

## 📝 **Opening Statement (30 seconds)**
```
"I'll build incrementally: basic sync logger (15 min), add thread safety (15 min), 
async logger (15 min), then polish (15 min). This ensures something demoable at each stage."
```

---

## 🎯 **Implementation Order (Memorize This!)**

```
1. LogLevel enum          (1 min)   ← No dependencies
2. LogMessage class       (2 min)   ← Uses LogLevel
3. Sink interface         (1 min)   ← Contract
4. StdoutSink impl        (2 min)   ← Implements Sink
5. SyncLogger             (5 min)   ← Uses Sink
6. Demo main()            (2 min)   ← RUN IT! ✅
7. Add thread safety      (5 min)   ← synchronized
8. Add filtering          (5 min)   ← level check
9. AsyncLogger            (10 min)  ← BlockingQueue
10. Config + Factory      (10 min)  ← Polish
```

**Total: ~45 minutes**

---

## 💻 **Copy-Paste Template**

### **1. LogLevel.java**
```java
public enum LogLevel {
    DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);
    private final int priority;
    LogLevel(int p) { this.priority = p; }
    public int getPriority() { return priority; }
}
```

### **2. LogMessage.java**
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

### **3. Sink.java**
```java
public interface Sink {
    void write(LogMessage message);
}
```

### **4. StdoutSink.java**
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

### **5. SyncLogger.java (Basic)**
```java
import java.util.List;

public class SyncLogger {
    private final List<Sink> sinks;
    
    public SyncLogger(List<Sink> sinks) {
        this.sinks = sinks;
    }
    
    public void log(String message, LogLevel level) {
        LogMessage msg = new LogMessage(message, level);
        for (Sink sink : sinks) {
            sink.write(msg);
        }
    }
    
    public void info(String m) { log(m, LogLevel.INFO); }
    public void error(String m) { log(m, LogLevel.ERROR); }
}
```

### **6. Demo.java**
```java
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        SyncLogger logger = new SyncLogger(
            Arrays.asList(new StdoutSink())
        );
        
        logger.info("Started");
        logger.error("Error occurred");
        
        System.out.println("\n✅ Basic logger works!");
    }
}
```

**🚨 RUN THIS NOW! Don't proceed until it works!**

---

## 🔒 **7. Add Thread Safety**

```java
public class SyncLogger {
    private final List<Sink> sinks;
    private final Object lock = new Object();  // ADD
    
    public void log(String message, LogLevel level) {
        LogMessage msg = new LogMessage(message, level);
        synchronized (lock) {  // ADD
            for (Sink sink : sinks) {
                sink.write(msg);
            }
        }
    }
}
```

**Test:**
```java
for (int i = 0; i < 3; i++) {
    final int id = i;
    new Thread(() -> logger.info("Thread " + id)).start();
}
```

---

## 🎚️ **8. Add Filtering**

```java
public class SyncLogger {
    private final LogLevel minLevel;  // ADD
    
    public SyncLogger(List<Sink> sinks, LogLevel minLevel) {
        this.sinks = sinks;
        this.minLevel = minLevel;
    }
    
    public void log(String message, LogLevel level) {
        if (level.getPriority() < minLevel.getPriority()) {
            return;  // Filter
        }
        // ... rest
    }
}
```

---

## ⚡ **9. AsyncLogger**

```java
import java.util.concurrent.*;

public class AsyncLogger {
    private final List<Sink> sinks;
    private final BlockingQueue<LogMessage> queue;
    private final Thread worker;
    private volatile boolean running = true;
    
    public AsyncLogger(List<Sink> sinks, int bufferSize) {
        this.sinks = sinks;
        this.queue = new ArrayBlockingQueue<>(bufferSize);
        this.worker = new Thread(() -> {
            while (running) {
                try {
                    LogMessage msg = queue.take();
                    for (Sink sink : sinks) {
                        sink.write(msg);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        worker.start();
    }
    
    public void log(String message, LogLevel level) {
        try {
            queue.put(new LogMessage(message, level));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void info(String m) { log(m, LogLevel.INFO); }
    
    public void shutdown() {
        running = false;
        worker.interrupt();
    }
}
```

---

## ⚙️ **10. Config + Factory**

```java
public class LoggerConfig {
    private String name;
    private LogLevel level = LogLevel.INFO;
    private boolean async = false;
    private int bufferSize = 10;
    private List<Sink> sinks = new ArrayList<>();
    
    public LoggerConfig setName(String n) { name = n; return this; }
    public LoggerConfig setLevel(LogLevel l) { level = l; return this; }
    public LoggerConfig setAsync(boolean a) { async = a; return this; }
    public LoggerConfig addSink(Sink s) { sinks.add(s); return this; }
    
    // Getters...
}

public class LoggerFactory {
    public static Object createLogger(LoggerConfig config) {
        if (config.isAsync()) {
            return new AsyncLogger(config.getSinks(), config.getBufferSize());
        }
        return new SyncLogger(config.getSinks(), config.getLevel());
    }
}
```

---

## 💬 **Key Phrases to Say**

| When | Say This |
|------|----------|
| **Starting** | "I'll build incrementally to ensure we have working code at each stage" |
| **After each class** | "Let me run this to verify it works..." |
| **Adding thread safety** | "Using synchronized to prevent message interleaving" |
| **Adding async** | "BlockingQueue prevents data loss by blocking when full" |
| **When stuck** | "Let me think about the trade-offs... [explain options]" |

---

## ❓ **Common Questions - Quick Answers**

**Q: Why sync first?**
> "Simpler to demo. Async is just adding a queue."

**Q: What if queue fills up?**
> "BlockingQueue.put() blocks to prevent data loss per requirements."

**Q: How ensure ordering?**
> "Sync: synchronized block. Async: single worker thread + FIFO queue."

**Q: Error handling?**
> "Try-catch around each sink.write(), continue with others."

**Q: How to add FileSink?**
> "Implement Sink interface - that's why we used interfaces."

---

## ⏱️ **Time Checkpoints**

| Time | Must Have |
|------|-----------|
| 15 min | ✅ Basic sync logger running |
| 30 min | ✅ Thread-safe + filtering |
| 45 min | ✅ Async logger working |
| 60 min | ✅ Config + polish |

---

## 🚨 **Common Mistakes to Avoid**

❌ Designing for 30 min without coding
❌ Not running code frequently
❌ Over-engineering initially
❌ Forgetting null checks
❌ Not explaining while coding

---

## ✅ **Pre-Interview Checklist**

```
☐ Memorize implementation order (1-10)
☐ Practice typing LogLevel, LogMessage, Sink
☐ Know BlockingQueue API (put, take)
☐ Prepare opening statement
☐ Practice explaining trade-offs
```

---

## 🎯 **If Running Out of Time**

**Priority 1 (Must Have):**
- LogLevel, LogMessage, Sink, StdoutSink
- Basic SyncLogger
- Working demo

**Priority 2 (Should Have):**
- Thread safety
- Log level filtering

**Priority 3 (Nice to Have):**
- AsyncLogger
- Configuration

**Skip if needed:**
- Tests (mention you would write them)
- Multiple sinks
- Factory pattern

---

## 🏆 **Success = Working Code + Good Explanation**

Remember:
1. **Code first, perfect later**
2. **Demo after each feature**
3. **Explain your thinking**
4. **Handle edge cases**
5. **Stay calm and iterate**

---

## 📱 **Emergency: 30 Minutes Left?**

Skip to minimal async:

```java
// Minimal AsyncLogger
public class AsyncLogger {
    private BlockingQueue<LogMessage> queue = new ArrayBlockingQueue<>(10);
    
    public AsyncLogger(List<Sink> sinks) {
        new Thread(() -> {
            while (true) {
                try {
                    LogMessage msg = queue.take();
                    for (Sink s : sinks) s.write(msg);
                } catch (Exception e) { break; }
            }
        }).start();
    }
    
    public void log(String m, LogLevel l) {
        try { queue.put(new LogMessage(m, l)); }
        catch (InterruptedException e) {}
    }
}
```

---

## 🎬 **Final Demo Script**

```java
public static void main(String[] args) throws Exception {
    System.out.println("=== Sync Logger ===");
    SyncLogger sync = new SyncLogger(
        Arrays.asList(new StdoutSink()),
        LogLevel.INFO
    );
    sync.info("Sync message");
    
    System.out.println("\n=== Async Logger ===");
    AsyncLogger async = new AsyncLogger(
        Arrays.asList(new StdoutSink()),
        10
    );
    async.info("Async message");
    Thread.sleep(100);
    async.shutdown();
    
    System.out.println("\n=== Multi-threaded ===");
    for (int i = 0; i < 3; i++) {
        final int id = i;
        new Thread(() -> sync.info("Thread " + id)).start();
    }
    
    System.out.println("\n✅ All features working!");
}
```

---

## 🚀 **You Got This!**

**Remember:** The interviewer wants to see:
1. ✅ Working code
2. ✅ Clear thinking
3. ✅ Incremental approach
4. ✅ Good communication
5. ✅ Edge case awareness

**Not:** Perfect code on first try!

---

**Good luck! 🍀**

