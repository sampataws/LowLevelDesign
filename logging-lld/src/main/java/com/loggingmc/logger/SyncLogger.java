package com.loggingmc.logger;

import com.loggingmc.LogLevel;
import com.loggingmc.LogMessage;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.sink.Sink;

import java.util.List;

/**
 * Synchronous logger implementation that writes messages immediately
 * Thread-safe implementation supporting multiple concurrent writers
 */
public class SyncLogger implements Logger {
    private final String name;
    private final List<Sink> sinks;
    private final String timestampFormat;
    private final LogLevel logLevel;
    private final Object lock = new Object();

    public SyncLogger(LoggerConfiguration config) {
        if (config.getLoggerName() == null || config.getLoggerName().isEmpty()) {
            throw new IllegalArgumentException("Logger name cannot be null or empty");
        }
        if (config.getSinks() == null || config.getSinks().isEmpty()) {
            throw new IllegalArgumentException("At least one sink must be configured");
        }

        this.name = config.getLoggerName();
        this.sinks = config.getSinks();
        this.timestampFormat = config.getTimestampFormat();
        this.logLevel = config.getLogLevel();
    }

    @Override
    public void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    @Override
    public void info(String message) {
        log(message, LogLevel.INFO);
    }

    @Override
    public void warn(String message) {
        log(message, LogLevel.WARN);
    }

    @Override
    public void error(String message) {
        log(message, LogLevel.ERROR);
    }

    @Override
    public void fatal(String message) {
        log(message, LogLevel.FATAL);
    }

    @Override
    public void log(String message, LogLevel level) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }

        // Filter messages below the logger's configured level
        if (!level.isAtLeast(logLevel)) {
            return;
        }

        LogMessage logMessage = new LogMessage(message, level);

        // Synchronize to ensure message ordering across threads
        synchronized (lock) {
            for (Sink sink : sinks) {
                try {
                    sink.write(logMessage, timestampFormat);
                } catch (Exception e) {
                    // Fail gracefully - print error but continue with other sinks
                    System.err.println("Error writing to sink: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shutdown() {
        synchronized (lock) {
            for (Sink sink : sinks) {
                try {
                    sink.close();
                } catch (Exception e) {
                    System.err.println("Error closing sink: " + e.getMessage());
                }
            }
        }
    }
}

