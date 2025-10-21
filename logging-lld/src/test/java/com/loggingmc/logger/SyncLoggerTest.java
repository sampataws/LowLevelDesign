package com.loggingmc.logger;

import com.loggingmc.LogLevel;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.sink.StdoutSink;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for SyncLogger
 */
public class SyncLoggerTest {

    @After
    public void cleanup() {
        // Clean up any resources
    }

    @Test
    public void testLoggerCreation() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        assertEquals("TestLogger", logger.getName());
        logger.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLoggerName() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName(null);
        config.addSink(new StdoutSink());
        
        new SyncLogger(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyLoggerName() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("");
        config.addSink(new StdoutSink());
        
        new SyncLogger(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSinks() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        
        new SyncLogger(config);
    }

    @Test
    public void testLogMethods() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.setLogLevel(LogLevel.DEBUG);
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        // Should not throw exceptions
        logger.debug("Debug message");
        logger.info("Info message");
        logger.warn("Warn message");
        logger.error("Error message");
        logger.fatal("Fatal message");
        
        logger.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMessage() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        try {
            logger.info(null);
        } finally {
            logger.shutdown();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLogLevel() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        try {
            logger.log("Test", null);
        } finally {
            logger.shutdown();
        }
    }

    @Test
    public void testLogLevelFiltering() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.setLogLevel(LogLevel.WARN);
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        // These should be filtered (no exception should be thrown)
        logger.debug("Should be filtered");
        logger.info("Should be filtered");
        
        // These should pass through
        logger.warn("Should appear");
        logger.error("Should appear");
        logger.fatal("Should appear");
        
        logger.shutdown();
    }

    @Test
    public void testMultiThreadedLogging() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("TestLogger");
        config.setLogLevel(LogLevel.INFO);
        config.addSink(new StdoutSink());
        
        Logger logger = new SyncLogger(config);
        
        // Create multiple threads
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
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
        
        logger.shutdown();
    }
}

