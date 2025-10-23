# AsyncLogger Thread Usage - Detailed Examples

## 🧵 Understanding Threads in AsyncLogger

The `AsyncLogger` demonstrates the **Producer-Consumer Pattern** using Java threads and blocking queues.

### Quick Summary

**What is AsyncLogger?**
- A logging system that uses a **background thread** to write log messages
- Main thread queues messages and continues immediately (non-blocking)
- Worker thread processes messages in the background

**Why use threads?**
- **Performance**: Main thread doesn't wait for slow I/O operations
- **Responsiveness**: Application continues running smoothly
- **Ordering**: Messages are written in the order they were logged

**How does it work?**
1. **Main Thread** (Producer): Creates log messages and puts them in a queue
2. **BlockingQueue**: Thread-safe queue that holds pending messages
3. **Worker Thread** (Consumer): Takes messages from queue and writes to sinks
4. **Graceful Shutdown**: POISON_PILL pattern ensures all messages are written

**Key Components:**
```java
BlockingQueue<LogMessage> messageQueue;  // Thread-safe queue
Thread workerThread;                      // Background thread
AtomicBoolean running;                    // Shutdown flag
LogMessage POISON_PILL;                   // Shutdown signal
```

## 🔄 How It Works

### High-Level Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         AsyncLogger System                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Main Thread (Producer)              Worker Thread (Consumer)      │
│  ═══════════════════                 ════════════════════          │
│                                                                     │
│  1. logger.info("Hello")             5. messageQueue.take()        │
│       │                                    │                       │
│       ▼                                    ▼                       │
│  2. Create LogMessage                6. Get message from queue     │
│       │                                    │                       │
│       ▼                                    ▼                       │
│  3. messageQueue.put()  ────────────► 7. Write to sinks           │
│       │                   [Queue]          │                       │
│       ▼                                    ▼                       │
│  4. Return immediately               8. Loop back to step 5        │
│       │                                                            │
│       ▼                                                            │
│  Continue execution                                                │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Detailed Flow

**Phase 1: Initialization**
```
1. Constructor called
   └─> Create BlockingQueue (capacity: 1000)
   └─> Create AtomicBoolean (running = true)
   └─> Create Thread with LogWorker
   └─> Start thread (worker begins running)
```

**Phase 2: Logging (Producer)**
```
Main Thread                          BlockingQueue                Worker Thread
    │                                     │                            │
    │ logger.info("Hello")                │                            │
    ├──> Create LogMessage                │                            │
    │                                     │                            │
    ├──> messageQueue.put(msg) ──────────>│                            │
    │                                     │ [msg1]                     │
    │ <── Returns immediately             │                            │
    │                                     │                            │
    │ logger.info("World")                │                            │
    ├──> messageQueue.put(msg) ──────────>│                            │
    │                                     │ [msg1, msg2]               │
    │ <── Returns immediately             │                            │
    │                                     │                            │
    │ Continue other work...              │                            │
    │                                     │                            │
    │                                     │ <──────── take() ──────────┤
    │                                     │ [msg2]                     │
    │                                     │                            ├─> Write msg1
    │                                     │                            │
    │                                     │ <──────── take() ──────────┤
    │                                     │ []                         │
    │                                     │                            ├─> Write msg2
    │                                     │                            │
    │                                     │ <──────── take() ──────────┤
    │                                     │                            │ (blocks, waiting)
```

**Phase 3: Shutdown (Graceful Termination)**
```
Main Thread                          BlockingQueue                Worker Thread
    │                                     │                            │
    │ logger.shutdown()                   │                            │ (waiting on take())
    ├──> running.set(false)               │                            │
    │                                     │                            │
    ├──> put(POISON_PILL) ───────────────>│                            │
    │                                     │ [POISON_PILL]              │
    │                                     │                            │
    │                                     │ <──────── take() ──────────┤
    │                                     │ []                         │
    │                                     │                            ├─> Check: POISON_PILL?
    │                                     │                            ├─> Yes! Break loop
    │                                     │                            ├─> Thread exits
    │                                     │                            │
    ├──> workerThread.join() ─────────────────────────────────────────>│
    │    (wait for thread to finish)                                  │
    │ <──────────────────────────────────────────────────────────────┘
    │
    ├──> Close all sinks
    │
    └──> Shutdown complete
```

### Thread States During Execution

```
Worker Thread Lifecycle:
═══════════════════════

NEW ──> RUNNABLE ──> WAITING ──> RUNNABLE ──> WAITING ──> ... ──> TERMINATED
        (started)    (take())    (got msg)    (take())           (POISON_PILL)
                         │           │            │
                         │           │            │
                         └───────────┴────────────┘
                         (repeats until shutdown)
```

### Memory View

```
Heap Memory:
┌─────────────────────────────────────────────────────────────┐
│ AsyncLogger Object                                          │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ messageQueue: ArrayBlockingQueue                        │ │
│ │ ┌─────────────────────────────────────────────────────┐ │ │
│ │ │ Capacity: 1000                                      │ │ │
│ │ │ Current: [LogMessage1, LogMessage2, LogMessage3]    │ │ │
│ │ │          ▲                                          │ │ │
│ │ │          │                                          │ │ │
│ │ │     Both threads access this                       │ │ │
│ │ │     (thread-safe via locks)                        │ │ │
│ │ └─────────────────────────────────────────────────────┘ │ │
│ │                                                         │ │
│ │ running: AtomicBoolean(true)                           │ │
│ │          ▲                                             │ │
│ │          │                                             │ │
│ │     Shared flag (atomic operations)                   │ │
│ │                                                         │ │
│ │ workerThread: Thread                                   │ │
│ │               ▲                                        │ │
│ │               │                                        │ │
│ │          Reference to worker                          │ │
│ └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘

Thread Stacks:
┌──────────────────────┐    ┌──────────────────────┐
│ Main Thread Stack    │    │ Worker Thread Stack  │
├──────────────────────┤    ├──────────────────────┤
│ main()               │    │ run()                │
│ logger.info()        │    │ messageQueue.take()  │
│ messageQueue.put()   │    │ sink.write()         │
└──────────────────────┘    └──────────────────────┘
```

### Synchronization Mechanisms

```
BlockingQueue Internal Synchronization:
═══════════════════════════════════════

put() operation:
1. Acquire lock
2. While queue is full:
   └─> Wait (releases lock, blocks thread)
3. Add element to queue
4. Signal waiting consumers
5. Release lock

take() operation:
1. Acquire lock
2. While queue is empty:
   └─> Wait (releases lock, blocks thread)
3. Remove element from queue
4. Signal waiting producers
5. Release lock
```

### Why This Design Works

**1. Thread Safety**
- BlockingQueue handles all synchronization internally
- No manual locks needed in AsyncLogger
- AtomicBoolean for thread-safe flag operations

**2. Non-Blocking Producer**
- Main thread puts message and returns immediately
- No waiting for I/O operations
- Application continues running smoothly

**3. Ordered Processing**
- Queue preserves FIFO order
- Messages written in the order they were logged
- Single consumer thread ensures sequential writes

**4. Graceful Shutdown**
- POISON_PILL ensures all messages are processed
- join() waits for worker thread to finish
- No messages lost during shutdown

**5. Backpressure Handling**
- Bounded queue prevents memory overflow
- put() blocks if queue is full (backpressure)
- Prevents producer from overwhelming consumer

### Timeline Example

```
Time  Main Thread                    Queue State           Worker Thread
════  ═══════════                    ═══════════           ═════════════
0ms   new AsyncLogger()              []                    [CREATED]
      └─> Start worker thread                              └─> run() starts
                                                            └─> take() [WAITING]

10ms  logger.info("A")               []                    [WAITING on take()]
      └─> put("A") ──────────────>   ["A"]
      └─> returns                                          └─> take() returns "A"
                                                            └─> write("A") [10ms]

15ms  logger.info("B")               []                    [WRITING "A"]
      └─> put("B") ──────────────>   ["B"]
      └─> returns

20ms  logger.info("C")               ["B"]                 [WRITING "A"]
      └─> put("C") ──────────────>   ["B","C"]
      └─> returns

25ms  doOtherWork()                  ["B","C"]             [WRITING "A"]
      └─> processing...

30ms  [still working]                ["B","C"]             └─> write("A") done
                                                            └─> take() returns "B"
                                                            └─> write("B") [10ms]

40ms  [still working]                ["C"]                 └─> write("B") done
                                                            └─> take() returns "C"
                                                            └─> write("C") [10ms]

50ms  logger.shutdown()              ["C"]                 [WRITING "C"]
      └─> running = false
      └─> put(POISON_PILL) ──────>   ["C", POISON_PILL]

55ms  └─> join() [WAITING]           ["C", POISON_PILL]    └─> write("C") done
                                                            └─> take() returns POISON
                                                            └─> break loop
                                                            └─> thread exits

60ms  └─> join() returns             []                    [TERMINATED]
      └─> close sinks
      └─> shutdown complete
```

### Real-World Analogy

Think of AsyncLogger like a **restaurant kitchen**:

```
┌─────────────────────────────────────────────────────────────┐
│                      Restaurant Kitchen                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Waiter (Main Thread)          Chef (Worker Thread)        │
│  ═════════════════             ═══════════════             │
│                                                             │
│  1. Take customer order        4. Check order queue        │
│     │                              │                       │
│     ▼                              ▼                       │
│  2. Write on ticket            5. Take next ticket         │
│     │                              │                       │
│     ▼                              ▼                       │
│  3. Clip to order wheel ──────> 6. Cook the meal          │
│     │         [Queue]              │                       │
│     ▼                              ▼                       │
│  Return to customers           7. Serve when ready         │
│     │                              │                       │
│     ▼                              ▼                       │
│  Take next order               8. Check for next order     │
│                                                             │
│  Benefits:                                                  │
│  • Waiter doesn't wait for cooking                         │
│  • Can serve multiple customers                            │
│  • Orders processed in sequence                            │
│  • Kitchen handles backlog                                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Mapping:**
- **Waiter** = Main Thread (takes orders quickly)
- **Order Wheel** = BlockingQueue (holds pending orders)
- **Chef** = Worker Thread (cooks in background)
- **Ticket** = LogMessage (the work item)
- **Closing Time** = shutdown() (finish all orders before leaving)

## 📚 Core Concepts

### 1. Producer-Consumer Pattern

**Producer** (Main Thread):
- Produces log messages
- Puts them in a queue
- Continues execution immediately (non-blocking)

**Consumer** (Worker Thread):
- Consumes log messages from queue
- Processes them in background
- Writes to sinks (file, console, etc.)

### 2. Key Thread Components

```java
// Thread-safe queue (bounded buffer)
private final BlockingQueue<LogMessage> messageQueue;

// Background worker thread
private final Thread workerThread;

// Thread-safe shutdown flag
private final AtomicBoolean running;

// Signal to stop worker thread
private static final LogMessage POISON_PILL;
```

## 🎬 Complete Lifecycle Example

Let's trace a complete example from start to finish:

```java
// Step 1: Create AsyncLogger
AsyncLogger logger = new AsyncLogger(config);

// Step 2: Log some messages
logger.info("Message 1");
logger.info("Message 2");
logger.info("Message 3");

// Step 3: Shutdown
logger.shutdown();
```

**What happens internally:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│ STEP 1: new AsyncLogger(config)                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ Main Thread:                                                            │
│   1. Create messageQueue = new ArrayBlockingQueue<>(1000)              │
│   2. Create running = new AtomicBoolean(true)                          │
│   3. Create workerThread = new Thread(new LogWorker())                 │
│   4. workerThread.start()  ──────────────────────┐                     │
│                                                   │                     │
│                                                   ▼                     │
│                                          Worker Thread:                 │
│                                            1. run() method starts       │
│                                            2. Enter while(true) loop    │
│                                            3. Call messageQueue.take()  │
│                                            4. BLOCKS (queue is empty)   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ STEP 2a: logger.info("Message 1")                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ Main Thread:                                Worker Thread:              │
│   1. Create LogMessage("Message 1")           [BLOCKED on take()]      │
│   2. messageQueue.put(msg1)  ────────────┐                             │
│      └─> Queue: [msg1]                   │                             │
│      └─> Signal worker thread            │                             │
│   3. Return immediately                  │                             │
│                                           │                             │
│                                           └──> take() returns msg1      │
│                                                Queue: []                │
│                                                Write msg1 to sinks      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ STEP 2b: logger.info("Message 2")                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ Main Thread:                                Worker Thread:              │
│   1. Create LogMessage("Message 2")           [WRITING msg1]           │
│   2. messageQueue.put(msg2)  ────────────┐                             │
│      └─> Queue: [msg2]                   │                             │
│   3. Return immediately                  │                             │
│                                           │                             │
│                                           └──> Finish writing msg1      │
│                                                take() returns msg2      │
│                                                Queue: []                │
│                                                Write msg2 to sinks      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ STEP 2c: logger.info("Message 3")                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ Main Thread:                                Worker Thread:              │
│   1. Create LogMessage("Message 3")           [WRITING msg2]           │
│   2. messageQueue.put(msg3)  ────────────┐                             │
│      └─> Queue: [msg3]                   │                             │
│   3. Return immediately                  │                             │
│                                           │                             │
│                                           └──> Finish writing msg2      │
│                                                take() returns msg3      │
│                                                Queue: []                │
│                                                Write msg3 to sinks      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ STEP 3: logger.shutdown()                                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│ Main Thread:                                Worker Thread:              │
│   1. running.compareAndSet(true, false)       [WRITING msg3]           │
│      └─> running = false                                               │
│                                                                         │
│   2. messageQueue.put(POISON_PILL)  ─────┐                             │
│      └─> Queue: [POISON_PILL]            │                             │
│                                           │                             │
│   3. workerThread.join()                 │                             │
│      └─> WAIT for worker to finish       │                             │
│                                           │                             │
│                                           └──> Finish writing msg3      │
│                                                take() returns POISON    │
│                                                Check: msg == POISON?    │
│                                                └─> YES! break loop      │
│                                                run() method exits       │
│                                                Thread TERMINATES        │
│                                                                         │
│   4. join() returns (worker finished)    ◄────┘                        │
│   5. Close all sinks                                                    │
│   6. Shutdown complete                                                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

**Key Observations:**

1. **Non-blocking**: Main thread returns immediately after queuing
2. **Ordered**: Messages written in FIFO order (1, 2, 3)
3. **Asynchronous**: Writing happens in parallel with main thread
4. **Graceful**: shutdown() waits for all messages to be written
5. **Safe**: No messages lost, even during shutdown

## 🔍 Step-by-Step Breakdown

### Step 1: Thread Creation (Constructor)

```java
public AsyncLogger(LoggerConfiguration config) {
    // Create bounded queue (e.g., capacity 1000)
    this.messageQueue = new ArrayBlockingQueue<>(config.getBufferSize());
    
    // Create flag for thread coordination
    this.running = new AtomicBoolean(true);
    
    // Create and start background thread
    this.workerThread = new Thread(new LogWorker(), "AsyncLogger-" + name);
    this.workerThread.setDaemon(false); // Non-daemon: completes before JVM exits
    this.workerThread.start(); // Start immediately
}
```

**What happens:**
1. Creates a queue with fixed capacity (e.g., 1000 messages)
2. Creates a new thread with a `Runnable` (LogWorker)
3. Names the thread for debugging
4. Starts the thread (calls `run()` method in background)

### Step 2: Logging (Producer)

```java
@Override
public void log(String message, LogLevel level) {
    // Create log message
    LogMessage logMessage = new LogMessage(message, level);
    
    try {
        // Put message in queue (BLOCKS if queue is full)
        messageQueue.put(logMessage);
        // Returns immediately after queuing
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Interrupted while queuing message");
    }
}
```

**What happens:**
1. Main thread creates a log message
2. Puts it in the queue (blocks if queue is full)
3. Returns immediately - doesn't wait for message to be written
4. Worker thread will process it asynchronously

### Step 3: Background Processing (Consumer)

```java
private class LogWorker implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                // Take message from queue (BLOCKS if queue is empty)
                LogMessage message = messageQueue.take();
                
                // Check for shutdown signal
                if (message == POISON_PILL) {
                    break; // Exit loop, thread terminates
                }
                
                // Process message (write to sinks)
                for (Sink sink : sinks) {
                    sink.write(message, timestampFormat);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

**What happens:**
1. Worker thread runs in infinite loop
2. Blocks on `take()` waiting for messages
3. When message arrives, processes it
4. Continues until POISON_PILL received

### Step 4: Graceful Shutdown

```java
@Override
public void shutdown() {
    // Atomically set running flag to false
    if (!running.compareAndSet(true, false)) {
        return; // Already shut down
    }
    
    try {
        // Send poison pill to signal worker to stop
        messageQueue.put(POISON_PILL);
        
        // Wait for worker thread to finish (up to 5 seconds)
        workerThread.join(5000);
        
        if (workerThread.isAlive()) {
            // Force interrupt if still running
            workerThread.interrupt();
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    // Close all sinks
    for (Sink sink : sinks) {
        sink.close();
    }
}
```

**What happens:**
1. Sets shutdown flag (prevents new messages)
2. Sends POISON_PILL to signal worker thread
3. Waits for worker thread to finish processing
4. Closes all resources

## 📝 Complete Example

```java
import com.loggingmc.*;
import com.loggingmc.logger.*;
import com.loggingmc.sink.*;

public class AsyncThreadExample {
    public static void main(String[] args) throws InterruptedException {
        // Create async logger
        LoggerConfiguration config = new LoggerConfiguration.Builder()
            .setLoggerName("MyAsyncLogger")
            .setLogLevel(LogLevel.DEBUG)
            .addSink(new StdoutSink())
            .setBufferSize(100) // Queue capacity
            .build();
        
        AsyncLogger logger = new AsyncLogger(config);
        
        System.out.println("Main thread: " + Thread.currentThread().getName());
        
        // Log messages from main thread
        for (int i = 0; i < 10; i++) {
            logger.info("Message " + i);
            System.out.println("Main thread: Queued message " + i);
        }
        
        System.out.println("Main thread: All messages queued, continuing...");
        
        // Main thread continues immediately (doesn't wait for logging)
        doOtherWork();
        
        // Graceful shutdown (waits for all messages to be processed)
        logger.shutdown();
        System.out.println("Main thread: Logger shut down");
    }
    
    private static void doOtherWork() {
        System.out.println("Main thread: Doing other work...");
        // Simulate work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**Output:**
```
Main thread: main
Main thread: Queued message 0
Main thread: Queued message 1
Main thread: Queued message 2
...
Main thread: All messages queued, continuing...
Main thread: Doing other work...
[INFO] Message 0
[INFO] Message 1
[INFO] Message 2
...
Main thread: Logger shut down
```

## 🎯 Multi-Threading Example

```java
public class MultiThreadedLoggingExample {
    public static void main(String[] args) throws InterruptedException {
        // Create async logger
        AsyncLogger logger = createAsyncLogger();
        
        // Create multiple threads that log concurrently
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    logger.info("Thread " + threadId + " - Message " + j);
                    
                    // Simulate work
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Worker-" + i);
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Shutdown logger
        logger.shutdown();
    }
    
    private static AsyncLogger createAsyncLogger() {
        LoggerConfiguration config = new LoggerConfiguration.Builder()
            .setLoggerName("MultiThreadLogger")
            .setLogLevel(LogLevel.INFO)
            .addSink(new StdoutSink())
            .setBufferSize(1000)
            .build();
        
        return new AsyncLogger(config);
    }
}
```

**What happens:**
1. 5 threads log concurrently
2. All messages go to the same queue (thread-safe)
3. Single worker thread processes them in order
4. No race conditions or data corruption

## 🔑 Key Thread Concepts Used

### 1. BlockingQueue

```java
BlockingQueue<LogMessage> messageQueue = new ArrayBlockingQueue<>(100);

// Producer (main thread)
messageQueue.put(message); // Blocks if queue is full

// Consumer (worker thread)
LogMessage msg = messageQueue.take(); // Blocks if queue is empty
```

**Benefits:**
- Thread-safe (no manual synchronization needed)
- Automatic blocking (no busy-waiting)
- Bounded buffer (prevents memory overflow)

### 2. Thread.start() vs Thread.run()

```java
// CORRECT: Starts new thread
workerThread.start(); // Calls run() in new thread

// WRONG: Runs in current thread
workerThread.run(); // Runs in current thread (no parallelism)
```

### 3. Thread.join()

```java
// Wait for thread to complete
workerThread.join(); // Blocks until thread terminates

// Wait with timeout
workerThread.join(5000); // Wait up to 5 seconds
```

### 4. AtomicBoolean

```java
AtomicBoolean running = new AtomicBoolean(true);

// Thread-safe check-and-set
if (running.compareAndSet(true, false)) {
    // Only one thread will enter here
}

// Thread-safe read
if (running.get()) {
    // Safe to read from multiple threads
}
```

### 5. Poison Pill Pattern

```java
// Special message to signal shutdown
private static final LogMessage POISON_PILL = new LogMessage("", LogLevel.DEBUG);

// Producer sends poison pill
messageQueue.put(POISON_PILL);

// Consumer checks for poison pill
if (message == POISON_PILL) {
    break; // Exit loop
}
```

## 🎓 Why Use Async Logging?

### Without Async (Synchronous):
```
Main Thread Timeline:
|--log()--|--write to file--|--log()--|--write to file--|
         ↑ SLOW I/O blocks main thread
```

### With Async:
```
Main Thread:
|--log()--|--log()--|--log()--|--continue work--|

Worker Thread:
         |--write to file--|--write to file--|--write to file--|
```

**Benefits:**
1. **Non-blocking**: Main thread doesn't wait for I/O
2. **Better performance**: I/O happens in background
3. **Ordered messages**: Queue preserves order
4. **No data loss**: Bounded queue blocks if full

## 🚨 Common Pitfalls

### 1. Forgetting to shutdown()
```java
// BAD: Worker thread keeps running
AsyncLogger logger = new AsyncLogger(config);
logger.info("Message");
// Program exits, messages may be lost

// GOOD: Graceful shutdown
AsyncLogger logger = new AsyncLogger(config);
logger.info("Message");
logger.shutdown(); // Wait for messages to be written
```

### 2. Daemon threads
```java
// BAD: JVM exits before messages are written
workerThread.setDaemon(true);

// GOOD: Thread completes before JVM exits
workerThread.setDaemon(false);
```

### 3. Ignoring InterruptedException
```java
// BAD: Swallows interruption
catch (InterruptedException e) {
    // Do nothing
}

// GOOD: Restore interrupt status
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

## 📊 Performance Comparison

```java
public class PerformanceComparison {
    public static void main(String[] args) {
        // Synchronous logging
        long syncStart = System.currentTimeMillis();
        SyncLogger syncLogger = new SyncLogger(config);
        for (int i = 0; i < 10000; i++) {
            syncLogger.info("Message " + i);
        }
        long syncTime = System.currentTimeMillis() - syncStart;
        
        // Asynchronous logging
        long asyncStart = System.currentTimeMillis();
        AsyncLogger asyncLogger = new AsyncLogger(config);
        for (int i = 0; i < 10000; i++) {
            asyncLogger.info("Message " + i);
        }
        asyncLogger.shutdown();
        long asyncTime = System.currentTimeMillis() - asyncStart;
        
        System.out.println("Sync time: " + syncTime + "ms");
        System.out.println("Async time: " + asyncTime + "ms");
        System.out.println("Speedup: " + (syncTime / (double) asyncTime) + "x");
    }
}
```

**Typical Results:**
- Sync: 500ms (blocks on each write)
- Async: 50ms (queues and returns immediately)
- Speedup: 10x faster

---

**Key Takeaway**: AsyncLogger uses a background thread with a blocking queue to process log messages asynchronously, allowing the main application to continue without waiting for I/O operations.

