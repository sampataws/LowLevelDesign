package com.loggingmc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for LogLevel enum
 */
public class LogLevelTest {

    @Test
    public void testLogLevelPriority() {
        assertTrue(LogLevel.DEBUG.getPriority() < LogLevel.INFO.getPriority());
        assertTrue(LogLevel.INFO.getPriority() < LogLevel.WARN.getPriority());
        assertTrue(LogLevel.WARN.getPriority() < LogLevel.ERROR.getPriority());
        assertTrue(LogLevel.ERROR.getPriority() < LogLevel.FATAL.getPriority());
    }

    @Test
    public void testIsAtLeast() {
        assertTrue(LogLevel.ERROR.isAtLeast(LogLevel.INFO));
        assertTrue(LogLevel.ERROR.isAtLeast(LogLevel.ERROR));
        assertFalse(LogLevel.INFO.isAtLeast(LogLevel.ERROR));
        assertTrue(LogLevel.FATAL.isAtLeast(LogLevel.DEBUG));
        assertFalse(LogLevel.DEBUG.isAtLeast(LogLevel.FATAL));
    }
}

