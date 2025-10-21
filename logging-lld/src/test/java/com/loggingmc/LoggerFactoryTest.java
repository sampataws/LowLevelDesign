package com.loggingmc;

import com.loggingmc.logger.AsyncLogger;
import com.loggingmc.logger.Logger;
import com.loggingmc.logger.SyncLogger;
import com.loggingmc.sink.StdoutSink;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for LoggerFactory
 */
public class LoggerFactoryTest {

    @After
    public void cleanup() {
        LoggerFactory.shutdownAll();
    }

    @Test
    public void testCreateSyncLogger() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("SyncLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.SYNC);
        config.addSink(new StdoutSink());
        
        Logger logger = LoggerFactory.createLogger(config);
        
        assertNotNull(logger);
        assertTrue(logger instanceof SyncLogger);
        assertEquals("SyncLogger", logger.getName());
    }

    @Test
    public void testCreateAsyncLogger() throws InterruptedException {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("AsyncLogger");
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(10);
        config.addSink(new StdoutSink());
        
        Logger logger = LoggerFactory.createLogger(config);
        
        assertNotNull(logger);
        assertTrue(logger instanceof AsyncLogger);
        assertEquals("AsyncLogger", logger.getName());
        
        Thread.sleep(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLoggerWithNullConfig() {
        LoggerFactory.createLogger(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateDuplicateLogger() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("DuplicateLogger");
        config.addSink(new StdoutSink());
        
        LoggerFactory.createLogger(config);
        
        // This should throw IllegalStateException
        LoggerFactory.createLogger(config);
    }

    @Test
    public void testGetLogger() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("GetLogger");
        config.addSink(new StdoutSink());
        
        Logger created = LoggerFactory.createLogger(config);
        Logger retrieved = LoggerFactory.getLogger("GetLogger");
        
        assertNotNull(retrieved);
        assertSame(created, retrieved);
    }

    @Test
    public void testGetNonExistentLogger() {
        Logger logger = LoggerFactory.getLogger("NonExistent");
        assertNull(logger);
    }

    @Test
    public void testShutdownLogger() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setLoggerName("ShutdownLogger");
        config.addSink(new StdoutSink());
        
        LoggerFactory.createLogger(config);
        LoggerFactory.shutdownLogger("ShutdownLogger");
        
        Logger logger = LoggerFactory.getLogger("ShutdownLogger");
        assertNull(logger);
    }

    @Test
    public void testShutdownNonExistentLogger() {
        // Should not throw exception
        LoggerFactory.shutdownLogger("NonExistent");
    }

    @Test
    public void testShutdownAll() throws InterruptedException {
        LoggerConfiguration config1 = new LoggerConfiguration();
        config1.setLoggerName("Logger1");
        config1.addSink(new StdoutSink());
        
        LoggerConfiguration config2 = new LoggerConfiguration();
        config2.setLoggerName("Logger2");
        config2.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config2.setBufferSize(10);
        config2.addSink(new StdoutSink());
        
        LoggerFactory.createLogger(config1);
        LoggerFactory.createLogger(config2);
        
        Thread.sleep(100);
        
        LoggerFactory.shutdownAll();
        
        assertNull(LoggerFactory.getLogger("Logger1"));
        assertNull(LoggerFactory.getLogger("Logger2"));
    }

    @Test
    public void testMultipleLoggers() {
        LoggerConfiguration config1 = new LoggerConfiguration();
        config1.setLoggerName("Logger1");
        config1.addSink(new StdoutSink());
        
        LoggerConfiguration config2 = new LoggerConfiguration();
        config2.setLoggerName("Logger2");
        config2.addSink(new StdoutSink());
        
        Logger logger1 = LoggerFactory.createLogger(config1);
        Logger logger2 = LoggerFactory.createLogger(config2);
        
        assertNotNull(logger1);
        assertNotNull(logger2);
        assertNotSame(logger1, logger2);
        
        logger1.info("From logger 1");
        logger2.info("From logger 2");
    }
}

