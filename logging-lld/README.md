# Logging Library - Low Level Design

A comprehensive, thread-safe logging library for Java applications with support for both synchronous and asynchronous logging.

**Part of:** [Atlassian Low Level Design](../README.md) collection

## Features

- **Multiple Log Levels**: DEBUG, INFO, WARN, ERROR, FATAL (in order of priority)
- **Synchronous and Asynchronous Logging**: Choose based on your performance requirements
- **Thread-Safe**: Supports concurrent logging from multiple threads
- **Message Ordering**: Guarantees messages reach sinks in the order they were sent
- **Configurable Sinks**: Support for multiple sinks with individual log level filtering
- **No Data Loss**: Async logger uses bounded buffer with blocking to prevent message loss
- **Extensible Design**: Easy to add new sink types (File, Database, etc.)
- **Graceful Error Handling**: Fails gracefully without crashing the application

## Architecture

### Core Components

1. **LogLevel**: Enum defining log levels (DEBUG < INFO < WARN < ERROR < FATAL)
2. **LogMessage**: Represents a log message with content, level, and timestamp
3. **LoggerConfiguration**: Configuration object for logger initialization
4. **Sink Interface**: Contract for all sink implementations
5. **Logger Interface**: Contract for all logger implementations
6. **LoggerFactory**: Factory for creating and managing logger instances

### Implementations

- **StdoutSink**: Writes log messages to standard output
- **SyncLogger**: Synchronous logger that writes messages immediately
- **AsyncLogger**: Asynchronous logger with bounded buffer and background processing

## Usage

### Basic Example - Synchronous Logger

```java
import com.loggingmc.*;
import com.loggingmc.logger.Logger;
import com.loggingmc.sink.StdoutSink;

// Create configuration
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("MyLogger");
config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
config.setLogLevel(LogLevel.INFO);
config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);
config.addSink(new StdoutSink(LogLevel.INFO));

// Create logger
Logger logger = LoggerFactory.createLogger(config);

// Log messages
logger.info("Application started");
logger.warn("This is a warning");
logger.error("An error occurred");

// Shutdown
LoggerFactory.shutdownLogger("MyLogger");
```

### Asynchronous Logger

```java
// Create configuration for async logger
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("AsyncLogger");
config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
config.setLogLevel(LogLevel.INFO);
config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
config.setBufferSize(25); // Maximum inflight messages
config.addSink(new StdoutSink(LogLevel.INFO));

// Create logger
Logger logger = LoggerFactory.createLogger(config);

// Log messages (non-blocking)
logger.info("Async message 1");
logger.info("Async message 2");

// Shutdown (waits for all messages to be processed)
LoggerFactory.shutdownLogger("AsyncLogger");
```

### Multiple Sinks with Different Levels

```java
LoggerConfiguration config = new LoggerConfiguration();
config.setLoggerName("MultiSinkLogger");
config.setLogLevel(LogLevel.DEBUG);

// Sink 1: All messages INFO and above
config.addSink(new StdoutSink(LogLevel.INFO));

// Sink 2: Only ERROR and FATAL
config.addSink(new StdoutSink(LogLevel.ERROR));

Logger logger = LoggerFactory.createLogger(config);

logger.debug("Debug message");  // Filtered by both sinks
logger.info("Info message");    // Appears in Sink 1 only
logger.error("Error message");  // Appears in both sinks
```

## Configuration Options

| Option | Description | Default |
|--------|-------------|---------|
| loggerName | Unique name for the logger | Required |
| timestampFormat | Format for timestamps (Java DateTimeFormatter pattern) | "dd-MM-yyyy-HH-mm-ss" |
| logLevel | Minimum log level for the logger | INFO |
| loggerType | SYNC or ASYNC | SYNC |
| bufferSize | Buffer size for async logger (max inflight messages) | 10 |
| sinks | List of sinks to write to | Required (at least one) |

## Sample Output

```
03-01-2024-09-30-00 [INFO] This is a sample log message.
03-01-2024-09-30-01 [WARN] This is a warning message.
03-01-2024-09-30-02 [ERROR] This is an error message.
```

## Running the Demo

### Using Maven

From the parent directory:
```bash
# Build all modules
mvn clean install

# Run tests for logging module
cd logging-lld
mvn test

# Run the driver application
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

Or from the logging-lld directory:
```bash
# Compile the module
mvn clean compile

# Run the driver application
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"

# Run tests
mvn test
```

### Using Java directly

```bash
# Compile
javac -d target/classes src/main/java/com/loggingmc/**/*.java

# Run
java -cp target/classes com.loggingmc.DriverApplication
```

## Testing

The library includes comprehensive unit tests covering:

- Log level priority and filtering
- Message creation and timestamp handling
- Configuration validation
- Sink filtering and writing
- Synchronous logger thread-safety
- Asynchronous logger buffering and ordering
- Multi-threaded logging scenarios
- Edge cases and error handling

Run tests with:
```bash
mvn test
```

## Design Principles

### Modularity
- Clear separation of concerns (Logger, Sink, Configuration)
- Each class has a single, well-defined responsibility

### Extensibility
- Interface-based design allows easy addition of new sink types
- Factory pattern for logger creation
- Configuration object pattern for flexible initialization

### Thread Safety
- Synchronous logger uses locks to ensure thread-safe writes
- Asynchronous logger uses thread-safe BlockingQueue
- Message ordering guaranteed across threads

### Reliability
- No data loss in async logger (blocking queue)
- Graceful error handling (continues with other sinks on failure)
- Proper resource cleanup on shutdown

### Performance
- Async logger for high-throughput scenarios
- Sync logger for immediate writes
- Configurable buffer size for async logger

## Future Enhancements

Potential extensions to the library:

1. **Additional Sinks**:
   - FileSink (write to files with rotation)
   - DatabaseSink (write to database)
   - NetworkSink (send to remote logging server)

2. **Advanced Features**:
   - Log message formatting templates
   - Conditional logging (predicates)
   - Log message batching
   - Metrics and statistics

3. **Configuration**:
   - Configuration file support (JSON, YAML, Properties)
   - Dynamic configuration updates
   - Environment-based configuration

## License

This is a sample project for educational purposes.

