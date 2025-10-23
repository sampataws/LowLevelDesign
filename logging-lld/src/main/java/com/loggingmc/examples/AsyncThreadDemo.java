package com.loggingmc.examples;

import com.loggingmc.LogLevel;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.logger.AsyncLogger;
import com.loggingmc.logger.SyncLogger;
import com.loggingmc.sink.StdoutSink;

/**
 * Demonstrates how AsyncLogger uses threads for background processing.
 * Shows the difference between synchronous and asynchronous logging.
 */
public class AsyncThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(80));
        System.out.println("ASYNC LOGGER THREAD DEMONSTRATION");
        System.out.println("=".repeat(80));
        System.out.println();

        // Demo 1: Basic async logging
        demo1_BasicAsyncLogging();

        // Demo 2: Multi-threaded logging
        demo2_MultiThreadedLogging();

        // Demo 3: Performance comparison
        demo3_PerformanceComparison();

        // Demo 4: Queue blocking behavior
        demo4_QueueBlocking();

        // Demo 5: Graceful shutdown
        demo5_GracefulShutdown();
    }

    /**
     * Demo 1: Shows how async logging works with background thread
     */
    private static void demo1_BasicAsyncLogging() throws InterruptedException {
        System.out.println("📋 Demo 1: Basic Async Logging");
        System.out.println("-".repeat(80));

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncDemo");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new StdoutSink());
        config.setBufferSize(100);

        AsyncLogger logger = new AsyncLogger(config);

        System.out.println("Main thread: " + Thread.currentThread().getName());
        System.out.println("Main thread: Starting to log messages...");

        // Log messages - these return immediately
        for (int i = 0; i < 5; i++) {
            logger.info("Message " + i);
            System.out.println("Main thread: Queued message " + i + " (returned immediately)");
        }

        System.out.println("Main thread: All messages queued!");
        System.out.println("Main thread: Continuing with other work...");

        // Simulate other work
        Thread.sleep(100);

        System.out.println("Main thread: Shutting down logger...");
        logger.shutdown();
        System.out.println("Main thread: Logger shut down (all messages written)");
        System.out.println();
    }

    /**
     * Demo 2: Multiple threads logging concurrently
     */
    private static void demo2_MultiThreadedLogging() throws InterruptedException {
        System.out.println("📋 Demo 2: Multi-Threaded Logging");
        System.out.println("-".repeat(80));

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("MultiThreadDemo");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new StdoutSink());
        config.setBufferSize(1000);

        AsyncLogger logger = new AsyncLogger(config);

        System.out.println("Creating 5 worker threads...");

        // Create 5 threads that log concurrently
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    logger.info("Thread " + threadId + " - Message " + j);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Worker-" + i);
        }

        // Start all threads
        System.out.println("Starting all worker threads...");
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        System.out.println("Waiting for worker threads to complete...");
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("All worker threads completed!");
        logger.shutdown();
        System.out.println();
    }

    /**
     * Demo 3: Performance comparison between sync and async
     */
    private static void demo3_PerformanceComparison() throws InterruptedException {
        System.out.println("📋 Demo 3: Performance Comparison");
        System.out.println("-".repeat(80));

        int messageCount = 1000;

        // Test synchronous logging
        LoggerConfiguration syncConfig = new LoggerConfiguration();
        syncConfig.setLoggerName("SyncPerf");
        syncConfig.setLogLevel(LogLevel.INFO);
        syncConfig.addSink(new StdoutSink());

        SyncLogger syncLogger = new SyncLogger(syncConfig);

        System.out.println("Testing synchronous logging (" + messageCount + " messages)...");
        long syncStart = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            syncLogger.info("Sync message " + i);
        }

        long syncTime = System.currentTimeMillis() - syncStart;
        System.out.println("Synchronous logging time: " + syncTime + "ms");

        // Test asynchronous logging
        LoggerConfiguration asyncConfig = new LoggerConfiguration();
        asyncConfig.setLoggerName("AsyncPerf");
        asyncConfig.setLogLevel(LogLevel.INFO);
        asyncConfig.addSink(new StdoutSink());
        asyncConfig.setBufferSize(2000);

        AsyncLogger asyncLogger = new AsyncLogger(asyncConfig);

        System.out.println("Testing asynchronous logging (" + messageCount + " messages)...");
        long asyncStart = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            asyncLogger.info("Async message " + i);
        }

        long asyncQueueTime = System.currentTimeMillis() - asyncStart;
        System.out.println("Asynchronous queuing time: " + asyncQueueTime + "ms");

        asyncLogger.shutdown();
        long asyncTotalTime = System.currentTimeMillis() - asyncStart;
        System.out.println("Asynchronous total time (including write): " + asyncTotalTime + "ms");

        System.out.println();
        System.out.println("Results:");
        System.out.println("  Sync time: " + syncTime + "ms");
        System.out.println("  Async queue time: " + asyncQueueTime + "ms");
        System.out.println("  Async total time: " + asyncTotalTime + "ms");
        System.out.println("  Speedup (queuing): " + String.format("%.2f", syncTime / (double) asyncQueueTime) + "x");
        System.out.println();
    }

    /**
     * Demo 4: Shows queue blocking behavior when full
     */
    private static void demo4_QueueBlocking() throws InterruptedException {
        System.out.println("📋 Demo 4: Queue Blocking Behavior");
        System.out.println("-".repeat(80));

        // Create logger with small buffer
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("BlockingDemo");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new SlowSink()); // Slow sink to fill queue
        config.setBufferSize(10); // Small buffer

        AsyncLogger logger = new AsyncLogger(config);

        System.out.println("Buffer size: 10 messages");
        System.out.println("Logging 20 messages with slow sink...");

        for (int i = 0; i < 20; i++) {
            System.out.println("Main thread: Logging message " + i + "...");
            long start = System.currentTimeMillis();
            logger.info("Message " + i);
            long time = System.currentTimeMillis() - start;

            if (time > 10) {
                System.out.println("  -> BLOCKED for " + time + "ms (queue was full)");
            } else {
                System.out.println("  -> Queued immediately");
            }
        }

        logger.shutdown();
        System.out.println();
    }

    /**
     * Demo 5: Graceful shutdown with poison pill
     */
    private static void demo5_GracefulShutdown() throws InterruptedException {
        System.out.println("📋 Demo 5: Graceful Shutdown");
        System.out.println("-".repeat(80));

        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("ShutdownDemo");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new StdoutSink());
        config.setBufferSize(100);

        AsyncLogger logger = new AsyncLogger(config);

        System.out.println("Logging 10 messages...");
        for (int i = 0; i < 10; i++) {
            logger.info("Message " + i);
        }

        System.out.println("Calling shutdown()...");
        System.out.println("  1. Sets running flag to false");
        System.out.println("  2. Sends POISON_PILL to worker thread");
        System.out.println("  3. Waits for worker thread to finish (join)");
        System.out.println("  4. Closes all sinks");

        logger.shutdown();

        System.out.println("Shutdown complete! All messages written.");
        System.out.println();
    }

    /**
     * Slow sink for demonstrating queue blocking
     */
    private static class SlowSink extends StdoutSink {
        @Override
        public void write(com.loggingmc.LogMessage message, String timestampFormat) {
            try {
                Thread.sleep(100); // Simulate slow I/O
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            super.write(message, timestampFormat);
        }
    }
}

