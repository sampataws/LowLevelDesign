package com.loggingmc;

import java.time.LocalDateTime;

/**
 * Represents a log message with content, level, and timestamp
 */
public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final LocalDateTime timestamp;

    public LogMessage(String content, LogLevel level) {
        this.content = content;
        this.level = level;
        this.timestamp = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

