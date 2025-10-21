package com.loggingmc;

import com.loggingmc.logger.Logger;
import com.loggingmc.sink.StdoutSink;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Driver application demonstrating the logging library features
 */
public class DriverApplication {

    public static void main(String[] args) {
        System.out.println("=== Logging Library Demo ===\n");

        // Demo 1: Synchronous Logger
        demonstrateSyncLogger();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Demo 2: Asynchronous Logger
        demonstrateAsyncLogger();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Demo 3: Multi-threaded Logging
        demonstrateMultiThreadedLogging();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Demo 4: Log Level Filtering
        demonstrateLogLevelFiltering();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Demo 5: Multiple Sinks with Different Levels
        demonstrateMultipleSinks();

        System.out.println("\n=== Demo Complete ===");
    }

    /**
     * Demonstrate synchronous logger
     */
    private static void demonstrateSyncLogger() {
        System.out.println("Demo 1: Synchronous Logger");
        System.out.println("-".repeat(50));

        // Create configuration
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("SyncLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.DEBUG);
        config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);
        config.addSink(new StdoutSink(LogLevel.DEBUG));

        // Create logger
        Logger logger = LoggerFactory.createLogger(config);

        // Log messages at different levels
        logger.debug("This is a DEBUG message");
        logger.info("This is an INFO message");
        logger.warn("This is a WARN message");
        logger.error("This is an ERROR message");
        logger.fatal("This is a FATAL message");

        // Shutdown
        LoggerFactory.shutdownLogger("SyncLogger");
    }

    /**
     * Demonstrate asynchronous logger
     */
    private static void demonstrateAsyncLogger() {
        System.out.println("Demo 2: Asynchronous Logger");
        System.out.println("-".repeat(50));

        // Create configuration
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.INFO);
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(25);
        config.addSink(new StdoutSink(LogLevel.INFO));

        // Create logger
        Logger logger = LoggerFactory.createLogger(config);

        // Log messages
        logger.info("Async logger initialized");
        logger.warn("This is a warning from async logger");
        logger.error("This is an error from async logger");

        // Give async logger time to process
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown
        LoggerFactory.shutdownLogger("AsyncLogger");
    }

    /**
     * Demonstrate multi-threaded logging
     */
    private static void demonstrateMultiThreadedLogging() {
        System.out.println("Demo 3: Multi-threaded Logging");
        System.out.println("-".repeat(50));

        // Create configuration
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("MultiThreadLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.INFO);
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(50);
        config.addSink(new StdoutSink(LogLevel.INFO));

        // Create logger
        Logger logger = LoggerFactory.createLogger(config);

        // Create multiple threads
        int threadCount = 5;
        int messagesPerThread = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < messagesPerThread; j++) {
                        logger.info("Message " + j + " from Thread-" + threadId);
                        Thread.sleep(10); // Small delay
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        try {
            latch.await();
            Thread.sleep(200); // Give async logger time to process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        LoggerFactory.shutdownLogger("MultiThreadLogger");
    }

    /**
     * Demonstrate log level filtering
     */
    private static void demonstrateLogLevelFiltering() {
        System.out.println("Demo 4: Log Level Filtering (Logger level: WARN)");
        System.out.println("-".repeat(50));

        // Create configuration with WARN level
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("FilteredLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.WARN); // Only WARN and above
        config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);
        config.addSink(new StdoutSink(LogLevel.DEBUG));

        // Create logger
        Logger logger = LoggerFactory.createLogger(config);

        // These should be filtered out
        logger.debug("This DEBUG message should NOT appear");
        logger.info("This INFO message should NOT appear");

        // These should appear
        logger.warn("This WARN message SHOULD appear");
        logger.error("This ERROR message SHOULD appear");
        logger.fatal("This FATAL message SHOULD appear");

        LoggerFactory.shutdownLogger("FilteredLogger");
    }

    /**
     * Demonstrate multiple sinks with different log levels
     */
    private static void demonstrateMultipleSinks() {
        System.out.println("Demo 5: Multiple Sinks with Different Levels");
        System.out.println("-".repeat(50));
        System.out.println("Sink 1: INFO and above");
        System.out.println("Sink 2: ERROR and above");
        System.out.println("-".repeat(50));

        // Create configuration with multiple sinks
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("MultiSinkLogger");
        config.setTimestampFormat("dd-MM-yyyy-HH-mm-ss");
        config.setLogLevel(LogLevel.DEBUG);
        config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);

        // Add sinks with different levels
        StdoutSink infoSink = new StdoutSink(LogLevel.INFO);
        StdoutSink errorSink = new StdoutSink(LogLevel.ERROR);

        config.addSink(infoSink);
        config.addSink(errorSink);

        // Create logger
        Logger logger = LoggerFactory.createLogger(config);

        logger.debug("DEBUG: Should not appear in any sink");
        logger.info("INFO: Should appear once (Sink 1 only)");
        logger.warn("WARN: Should appear once (Sink 1 only)");
        logger.error("ERROR: Should appear twice (both sinks)");
        logger.fatal("FATAL: Should appear twice (both sinks)");

        LoggerFactory.shutdownLogger("MultiSinkLogger");
    }
}

