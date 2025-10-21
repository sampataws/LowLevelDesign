package com.loggingmc.logger;

import com.loggingmc.LogLevel;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.sink.StdoutSink;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for AsyncLogger
 */
public class AsyncLoggerTest {

    @After
    public void cleanup() {
        // Clean up any resources
    }

    @Test
    public void testLoggerCreation() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(10);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        assertEquals("AsyncTestLogger", logger.getName());
        
        Thread.sleep(100); // Give time for worker thread to start
        logger.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLoggerName() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName(null);
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.addSink(new StdoutSink());
        
        new AsyncLogger(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSinks() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        
        new AsyncLogger(config);
    }

    @Test
    public void testAsyncLogging() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(25);
        config.setLogLevel(LogLevel.DEBUG);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        // Log messages
        logger.debug("Async debug message");
        logger.info("Async info message");
        logger.warn("Async warn message");
        logger.error("Async error message");
        logger.fatal("Async fatal message");
        
        // Give time for async processing
        Thread.sleep(200);
        
        logger.shutdown();
    }

    @Test
    public void testBufferCapacity() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(5);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        // Log more messages than buffer size
        for (int i = 0; i < 10; i++) {
            logger.info("Message " + i);
        }
        
        // Give time for processing
        Thread.sleep(300);
        
        logger.shutdown();
    }

    @Test
    public void testMessageOrdering() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(50);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        // Log messages in order
        for (int i = 0; i < 20; i++) {
            logger.info("Ordered message " + i);
        }
        
        // Give time for processing
        Thread.sleep(300);
        
        logger.shutdown();
    }

    @Test
    public void testMultiThreadedAsyncLogging() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(100);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        // Create multiple threads
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    logger.info("Thread " + threadId + " - Message " + j);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Give time for async processing
        Thread.sleep(500);
        
        logger.shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void testLoggingAfterShutdown() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(10);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        logger.shutdown();
        
        Thread.sleep(100);
        
        // This should throw IllegalStateException
        logger.info("This should fail");
    }

    @Test
    public void testGracefulShutdown() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncTestLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(50);
        config.addSink(new StdoutSink());
        
        Logger logger = new AsyncLogger(config);
        
        // Log many messages
        for (int i = 0; i < 30; i++) {
            logger.info("Message " + i);
        }
        
        // Shutdown should wait for all messages to be processed
        logger.shutdown();
    }
}

