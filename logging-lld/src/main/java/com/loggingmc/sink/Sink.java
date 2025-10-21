package com.loggingmc.sink;

import com.loggingmc.LogLevel;
import com.loggingmc.LogMessage;

/**
 * Interface for all sink implementations
 */
public interface Sink {
    /**
     * Write a log message to the sink
     * @param message The log message to write
     * @param timestampFormat The format to use for timestamp
     */
    void write(LogMessage message, String timestampFormat);

    /**
     * Get the minimum log level for this sink
     * @return The minimum log level
     */
    LogLevel getLogLevel();

    /**
     * Set the minimum log level for this sink
     * @param level The minimum log level
     */
    void setLogLevel(LogLevel level);

    /**
     * Check if a message should be written to this sink based on its level
     * @param messageLevel The level of the message
     * @return true if the message should be written, false otherwise
     */
    default boolean shouldWrite(LogLevel messageLevel) {
        return messageLevel.isAtLeast(getLogLevel());
    }

    /**
     * Close the sink and release any resources
     */
    void close();
}

