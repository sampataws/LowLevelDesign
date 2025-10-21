package com.loggingmc;

/**
 * Enum representing log levels in order of priority.
 * DEBUG < INFO < WARN < ERROR < FATAL
 */
public enum LogLevel {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Check if this log level is at least as severe as the given level
     */
    public boolean isAtLeast(LogLevel level) {
        return this.priority >= level.priority;
    }
}

