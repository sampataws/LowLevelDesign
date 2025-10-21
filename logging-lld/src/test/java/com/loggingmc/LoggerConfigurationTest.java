package com.loggingmc;

import com.loggingmc.sink.StdoutSink;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for LoggerConfiguration class
 */
public class LoggerConfigurationTest {

    @Test
    public void testDefaultConfiguration() {
        LoggerConfiguration config = new LoggerConfiguration();
        
        assertEquals("dd-MM-yyyy-HH-mm-ss", config.getTimestampFormat());
        assertEquals(LogLevel.INFO, config.getLogLevel());
        assertEquals(LoggerConfiguration.LoggerType.SYNC, config.getLoggerType());
        assertEquals(10, config.getBufferSize());
        assertNotNull(config.getSinks());
        assertTrue(config.getSinks().isEmpty());
    }

    @Test
    public void testSetConfiguration() {
        LoggerConfiguration config = new LoggerConfiguration();
        
        config.setLoggerName("TestLogger");
        config.setTimestampFormat("yyyy-MM-dd");
        config.setLogLevel(LogLevel.ERROR);
        config.setLoggerType(LoggerConfiguration.LoggerType.ASYNC);
        config.setBufferSize(50);
        config.addSink(new StdoutSink());
        
        assertEquals("TestLogger", config.getLoggerName());
        assertEquals("yyyy-MM-dd", config.getTimestampFormat());
        assertEquals(LogLevel.ERROR, config.getLogLevel());
        assertEquals(LoggerConfiguration.LoggerType.ASYNC, config.getLoggerType());
        assertEquals(50, config.getBufferSize());
        assertEquals(1, config.getSinks().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBufferSize() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setBufferSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeBufferSize() {
        LoggerConfiguration config = new LoggerConfiguration();
        config.setBufferSize(-5);
    }
}

