package com.loggingmc.examples;

import com.loggingmc.LogLevel;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.LoggerFactory;
import com.loggingmc.logger.Logger;
import com.loggingmc.sink.StdoutSink;

/**
 * Comprehensive usage examples for the LoggingMC library
 */
public class UsageExamples {

    /**
     * Example 1: Basic synchronous logging
     */
    public static void example1_BasicSyncLogging() {
        System.out.println("\n=== Example 1: Basic Synchronous Logging ===\n");

        // Step 1: Create configuration
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("BasicLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.INFO);
        config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);
        
        // Step 2: Add sink
        config.addSink(new StdoutSink(LogLevel.INFO));
        
        // Step 3: Create logger
        Logger logger = LoggerFactory.createLogger(config);
        
        // Step 4: Log messages
        logger.info("Application started successfully");
        logger.warn("Low memory warning");
        logger.error("Failed to connect to database");
        
        // Step 5: Cleanup
        LoggerFactory.shutdownLogger("BasicLogger");
    }

    /**
     * Example 2: Asynchronous logging for high performance
     */
    public static void example2_AsyncLogging() throws InterruptedException {
        System.out.println("\n=== Example 2: Asynchronous Logging ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(50);  // Can hold up to 50 messages
        config.setLogLevel(LogLevel.DEBUG);
        config.addSink(new StdoutSink(LogLevel.DEBUG));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // Log many messages quickly (non-blocking)
        for (int i = 0; i < 10; i++) {
            logger.info("Processing request #" + i);
        }
        
        // Give async logger time to process
        Thread.sleep(100);
        
        // Shutdown waits for all messages to be written
        LoggerFactory.shutdownLogger("AsyncLogger");
    }

    /**
     * Example 3: Different log levels
     */
    public static void example3_LogLevels() {
        System.out.println("\n=== Example 3: Different Log Levels ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("LevelLogger");
        config.setLogLevel(LogLevel.DEBUG);  // Accept all levels
        config.addSink(new StdoutSink(LogLevel.DEBUG));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // Use different log levels
        logger.debug("Detailed debug information");
        logger.info("General information");
        logger.warn("Warning - something might be wrong");
        logger.error("Error - something went wrong");
        logger.fatal("Fatal - system is unusable");
        
        LoggerFactory.shutdownLogger("LevelLogger");
    }

    /**
     * Example 4: Log level filtering
     */
    public static void example4_LogLevelFiltering() {
        System.out.println("\n=== Example 4: Log Level Filtering ===\n");

        // Logger configured to only accept WARN and above
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("FilteredLogger");
        config.setLogLevel(LogLevel.WARN);  // Filter at logger level
        config.addSink(new StdoutSink(LogLevel.DEBUG));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // These will be filtered out by the logger
        logger.debug("This won't appear");
        logger.info("This won't appear either");
        
        // These will appear
        logger.warn("This will appear");
        logger.error("This will also appear");
        
        LoggerFactory.shutdownLogger("FilteredLogger");
    }

    /**
     * Example 5: Multiple sinks with different levels
     */
    public static void example5_MultipleSinks() {
        System.out.println("\n=== Example 5: Multiple Sinks ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("MultiSinkLogger");
        config.setLogLevel(LogLevel.DEBUG);
        
        // Sink 1: Shows INFO and above
        StdoutSink infoSink = new StdoutSink(LogLevel.INFO);
        
        // Sink 2: Shows only ERROR and FATAL
        StdoutSink errorSink = new StdoutSink(LogLevel.ERROR);
        
        config.addSink(infoSink);
        config.addSink(errorSink);
        
        Logger logger = LoggerFactory.createLogger(config);
        
        System.out.println("DEBUG message (no output expected):");
        logger.debug("Debug info");
        
        System.out.println("\nINFO message (appears once):");
        logger.info("Information message");
        
        System.out.println("\nERROR message (appears twice):");
        logger.error("Error message");
        
        LoggerFactory.shutdownLogger("MultiSinkLogger");
    }

    /**
     * Example 6: Multi-threaded logging
     */
    public static void example6_MultiThreaded() throws InterruptedException {
        System.out.println("\n=== Example 6: Multi-threaded Logging ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("MTLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(100);
        config.addSink(new StdoutSink(LogLevel.INFO));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // Create multiple threads that log concurrently
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    logger.info("Thread-" + threadId + " message " + j);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }
        
        Thread.sleep(200);  // Let async logger process
        LoggerFactory.shutdownLogger("MTLogger");
    }

    /**
     * Example 7: Custom timestamp format
     */
    public static void example7_CustomTimestamp() {
        System.out.println("\n=== Example 7: Custom Timestamp Format ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("CustomTimeLogger");
        
        // Use a different timestamp format
        config.setTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        config.addSink(new StdoutSink(LogLevel.INFO));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        logger.info("Message with custom timestamp format");
        logger.warn("Another message");
        
        LoggerFactory.shutdownLogger("CustomTimeLogger");
    }

    /**
     * Example 8: Error handling
     */
    public static void example8_ErrorHandling() {
        System.out.println("\n=== Example 8: Error Handling ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("ErrorLogger");
        config.addSink(new StdoutSink(LogLevel.INFO));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // Simulate error scenarios
        try {
            // Some risky operation
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("Arithmetic error: " + e.getMessage());
        }
        
        try {
            // Another risky operation
            String str = null;
            str.length();
        } catch (NullPointerException e) {
            logger.error("Null pointer error: " + e.getMessage());
        }
        
        logger.info("Error handling complete");
        
        LoggerFactory.shutdownLogger("ErrorLogger");
    }

    /**
     * Example 9: Application lifecycle logging
     */
    public static void example9_ApplicationLifecycle() throws InterruptedException {
        System.out.println("\n=== Example 9: Application Lifecycle ===\n");

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AppLogger");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new StdoutSink(LogLevel.INFO));
        
        Logger logger = LoggerFactory.createLogger(config);
        
        // Application startup
        logger.info("Application starting...");
        logger.info("Loading configuration");
        logger.info("Connecting to database");
        logger.info("Application ready");
        
        // Simulate some work
        Thread.sleep(100);
        logger.info("Processing user request");
        logger.info("Request completed successfully");
        
        // Application shutdown
        logger.info("Shutting down application");
        logger.info("Closing database connections");
        logger.info("Application stopped");
        
        LoggerFactory.shutdownLogger("AppLogger");
    }

    /**
     * Example 10: Using LoggerFactory to manage multiple loggers
     */
    public static void example10_MultipleLoggers() {
        System.out.println("\n=== Example 10: Multiple Loggers ===\n");

        // Create logger for authentication module
        LoggerConfiguration authConfig = new LoggerConfiguration();
        authConfig.setLoggerName("AuthLogger");
        authConfig.setLogLevel(LogLevel.INFO);
        authConfig.addSink(new StdoutSink(LogLevel.INFO));
        Logger authLogger = LoggerFactory.createLogger(authConfig);
        
        // Create logger for database module
        LoggerConfiguration dbConfig = new LoggerConfiguration();
        dbConfig.setLoggerName("DBLogger");
        dbConfig.setLogLevel(LogLevel.WARN);
        dbConfig.addSink(new StdoutSink(LogLevel.WARN));
        Logger dbLogger = LoggerFactory.createLogger(dbConfig);
        
        // Use different loggers
        authLogger.info("User logged in: john@example.com");
        dbLogger.warn("Database connection pool running low");
        authLogger.info("User logged out: john@example.com");
        dbLogger.error("Database connection failed");
        
        // Retrieve logger by name
        Logger retrievedLogger = LoggerFactory.getLogger("AuthLogger");
        retrievedLogger.info("Retrieved logger works!");
        
        // Shutdown all loggers at once
        LoggerFactory.shutdownAll();
    }

    /**
     * Main method to run all examples
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   LoggingMC - Comprehensive Examples      ║");
        System.out.println("╚════════════════════════════════════════════╝");

        example1_BasicSyncLogging();
       /* example2_AsyncLogging();
        example3_LogLevels();
        example4_LogLevelFiltering();
        example5_MultipleSinks();
        example6_MultiThreaded();
        example7_CustomTimestamp();
        example8_ErrorHandling();
        example9_ApplicationLifecycle();
        example10_MultipleLoggers();*/

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   All Examples Completed Successfully!     ║");
        System.out.println("╚════════════════════════════════════════════╝");
    }
}

