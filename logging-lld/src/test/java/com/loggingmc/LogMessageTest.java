package com.loggingmc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for LogMessage class
 */
public class LogMessageTest {

    @Test
    public void testLogMessageCreation() {
        String content = "Test message";
        LogLevel level = LogLevel.INFO;
        
        LogMessage message = new LogMessage(content, level);
        
        assertEquals(content, message.getContent());
        assertEquals(level, message.getLevel());
        assertNotNull(message.getTimestamp());
    }

    @Test
    public void testTimestampIsSet() throws InterruptedException {
        LogMessage message1 = new LogMessage("First", LogLevel.INFO);
        Thread.sleep(10);
        LogMessage message2 = new LogMessage("Second", LogLevel.INFO);
        
        assertTrue(message2.getTimestamp().isAfter(message1.getTimestamp()) ||
                   message2.getTimestamp().isEqual(message1.getTimestamp()));
    }
}

