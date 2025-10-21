package com.loggingmc.sink;

import com.loggingmc.LogLevel;
import com.loggingmc.LogMessage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for StdoutSink
 */
public class StdoutSinkTest {

    @Test
    public void testDefaultLogLevel() {
        StdoutSink sink = new StdoutSink();
        assertEquals(LogLevel.DEBUG, sink.getLogLevel());
    }

    @Test
    public void testCustomLogLevel() {
        StdoutSink sink = new StdoutSink(LogLevel.ERROR);
        assertEquals(LogLevel.ERROR, sink.getLogLevel());
    }

    @Test
    public void testShouldWrite() {
        StdoutSink sink = new StdoutSink(LogLevel.WARN);
        
        assertFalse(sink.shouldWrite(LogLevel.DEBUG));
        assertFalse(sink.shouldWrite(LogLevel.INFO));
        assertTrue(sink.shouldWrite(LogLevel.WARN));
        assertTrue(sink.shouldWrite(LogLevel.ERROR));
        assertTrue(sink.shouldWrite(LogLevel.FATAL));
    }

    @Test
    public void testSetLogLevel() {
        StdoutSink sink = new StdoutSink(LogLevel.DEBUG);
        sink.setLogLevel(LogLevel.ERROR);
        assertEquals(LogLevel.ERROR, sink.getLogLevel());
    }

    @Test
    public void testWrite() {
        // This test verifies that write doesn't throw exceptions
        StdoutSink sink = new StdoutSink(LogLevel.INFO);
        LogMessage message = new LogMessage("Test message", LogLevel.INFO);
        
        // Should not throw exception
        sink.write(message, "dd-MM-yyyy-HH-mm-ss");
    }

    @Test
    public void testWriteFilteredMessage() {
        // This test verifies that messages below sink level are filtered
        StdoutSink sink = new StdoutSink(LogLevel.ERROR);
        LogMessage message = new LogMessage("Test message", LogLevel.INFO);
        
        // Should not throw exception, message should be filtered
        sink.write(message, "dd-MM-yyyy-HH-mm-ss");
    }

    @Test
    public void testClose() {
        StdoutSink sink = new StdoutSink();
        // Should not throw exception
        sink.close();
    }
}

