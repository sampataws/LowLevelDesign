# Logging Library - Quick Start Guide

**Part of:** [Atlassian Low Level Design](../README.md) collection

## Running the Demo

### Option 1: Using Maven (Recommended)

From the logging-lld directory:
```bash
# Run the driver application
mvn clean compile exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"

# Run all tests
mvn test
```

Or from the parent directory:
```bash
# Build and test all modules
mvn clean install

# Run the logging demo
cd logging-lld
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

### Option 2: Using Java directly

```bash
# Compile
javac -d target/classes src/main/java/com/loggingmc/**/*.java

# Run
java -cp target/classes com.loggingmc.DriverApplication
```

## Basic Usage Examples

### 1. Simple Synchronous Logger

```java
import com.loggingmc.*;
import com.loggingmc.logger.Logger;
import com.loggingmc.sink.StdoutSink;

// Configure
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("MyApp");
config.setLogLevel(LogLevel.INFO);
config.addSink(new StdoutSink(LogLevel.INFO));

// Create
Logger logger = LoggerFactory.createLogger(config);

// Use
logger.info("Application started");
logger.error("Something went wrong");

// Cleanup
LoggerFactory.shutdownLogger("MyApp");
```

### 2. Asynchronous Logger for High Performance

```java
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("HighPerf");
config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
config.setBufferSize(100);  // Max 100 inflight messages
config.setLogLevel(LogLevel.INFO);
config.addSink(new StdoutSink(LogLevel.INFO));

Logger logger = LoggerFactory.createLogger(config);

// Non-blocking logging
for (int i = 0; i < 1000; i++) {
    logger.info("Processing item " + i);
}

// Waits for all messages to be written
LoggerFactory.shutdownLogger("HighPerf");
```

### 3. Multiple Sinks with Different Levels

```java
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("MultiSink");
config.setLogLevel(LogLevel.DEBUG);

// Console shows INFO and above
config.addSink(new StdoutSink(LogLevel.INFO));

// Another sink for errors only
config.addSink(new StdoutSink(LogLevel.ERROR));

Logger logger = LoggerFactory.createLogger(config);

logger.debug("Debug info");    // Not shown
logger.info("Info message");   // Shown once
logger.error("Error!");        // Shown twice
```

### 4. Custom Timestamp Format

```java
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("CustomTime");
config.setTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
config.addSink(new StdoutSink());

Logger logger = LoggerFactory.createLogger(config);
logger.info("Custom timestamp format");
// Output: 2025-10-11 20:45:31.123 [INFO] Custom timestamp format
```

## Key Features Demonstrated

### ✅ Thread Safety
The library is fully thread-safe. Multiple threads can log concurrently:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    final int threadId = i;
    executor.submit(() -> {
        logger.info("Thread " + threadId + " logging");
    });
}
```

### ✅ Message Ordering
Messages are guaranteed to reach sinks in the order they were sent, even in async mode.

### ✅ No Data Loss
Async logger uses a blocking queue - if the buffer is full, the calling thread waits rather than dropping messages.

### ✅ Log Level Filtering
Two levels of filtering:
1. **Logger level**: Filters at the logger level
2. **Sink level**: Each sink can have its own minimum level

### ✅ Graceful Shutdown
Async logger ensures all queued messages are written before shutdown completes.

## Project Structure

```
logging-lld/
├── pom.xml
├── README.md
├── QUICK_START.md
├── PROJECT_SUMMARY.md
├── DESIGN_DECISIONS.md
└── src/main/java/com/loggingmc/
│   ├── LogLevel.java                    # Log level enum
│   ├── LogMessage.java                  # Message class
│   ├── LoggerConfiguration.java         # Configuration
│   ├── LoggerFactory.java               # Factory for loggers
│   ├── DriverApplication.java           # Demo application
│   ├── logger/
│   │   ├── Logger.java                  # Logger interface
│   │   ├── SyncLogger.java              # Sync implementation
│   │   └── AsyncLogger.java             # Async implementation
│   └── sink/
│       ├── Sink.java                    # Sink interface
│       └── StdoutSink.java              # Console sink
└── src/test/java/com/loggingmc/         # Comprehensive tests
```

## Test Results

All 43 tests pass successfully:
- ✅ LogLevel tests (2)
- ✅ LogMessage tests (2)
- ✅ LoggerConfiguration tests (4)
- ✅ StdoutSink tests (7)
- ✅ SyncLogger tests (9)
- ✅ AsyncLogger tests (9)
- ✅ LoggerFactory tests (10)

## Extending the Library

### Adding a New Sink Type

```java
public class FileSink implements Sink {
    private LogLevel logLevel;
    private String filePath;
    private BufferedWriter writer;
    
    public FileSink(String filePath, LogLevel logLevel) {
        this.filePath = filePath;
        this.logLevel = logLevel;
        // Initialize writer
    }
    
    @Override
    public void write(LogMessage message, String timestampFormat) {
        if (!shouldWrite(message.getLevel())) {
            return;
        }
        // Write to file
    }
    
    @Override
    public void close() {
        // Close writer
    }
    
    // Implement other methods...
}
```

Then use it:
```java
config.addSink(new FileSink("/var/log/app.log", LogLevel.INFO));
```

## Performance Characteristics

### Synchronous Logger
- **Pros**: Immediate writes, simple, predictable
- **Cons**: Slower for high-volume logging
- **Use when**: Reliability > Performance, low volume

### Asynchronous Logger
- **Pros**: High throughput, non-blocking
- **Cons**: Slight delay, uses background thread
- **Use when**: Performance > Immediate writes, high volume

## Common Patterns

### Application Logging
```java
Logger logger = LoggerFactory.createLogger(config);
logger.info("User logged in: " + username);
logger.warn("Retry attempt " + retryCount);
logger.error("Failed to connect to database");
```

### Error Handling
```java
try {
    // risky operation
} catch (Exception e) {
    logger.error("Operation failed: " + e.getMessage());
}
```

### Debug Logging
```java
logger.debug("Request payload: " + payload);
logger.debug("Processing step 1 complete");
```

## Best Practices

1. **Always shutdown loggers** when done to ensure all messages are written
2. **Use appropriate log levels** - don't log everything as ERROR
3. **Choose sync vs async** based on your needs
4. **Set buffer size appropriately** for async loggers
5. **Use multiple sinks** for different purposes (console + file)
6. **Filter at sink level** for flexibility

## Troubleshooting

### Messages not appearing?
- Check logger log level vs message level
- Check sink log level
- Ensure logger hasn't been shut down

### Async logger blocking?
- Buffer might be full - increase buffer size
- Worker thread might be slow - check sink performance

### Thread safety issues?
- The library is thread-safe by design
- If you see issues, check your custom sink implementations

