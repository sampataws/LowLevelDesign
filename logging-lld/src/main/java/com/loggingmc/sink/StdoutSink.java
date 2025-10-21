package com.loggingmc.sink;

import com.loggingmc.LogLevel;
import com.loggingmc.LogMessage;

import java.time.format.DateTimeFormatter;

/**
 * Sink implementation that writes log messages to standard output
 */
public class StdoutSink implements Sink {
    private LogLevel logLevel;

    public StdoutSink() {
        this.logLevel = LogLevel.DEBUG; // Accept all levels by default
    }

    public StdoutSink(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void write(LogMessage message, String timestampFormat) {
        if (!shouldWrite(message.getLevel())) {
            return; // Discard messages below the sink's log level
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
        String formattedTimestamp = message.getTimestamp().format(formatter);
        String logEntry = String.format("%s [%s] %s",
                formattedTimestamp,
                message.getLevel(),
                message.getContent());

        // Synchronized to ensure thread-safe console output
        synchronized (System.out) {
            System.out.println(logEntry);
        }
    }

    @Override
    public LogLevel getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(LogLevel level) {
        this.logLevel = level;
    }

    @Override
    public void close() {
        // Nothing to close for stdout
    }
}

