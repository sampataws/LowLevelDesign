package com.loggingmc.logger;

import com.loggingmc.LogLevel;

/**
 * Interface for all logger implementations
 */
public interface Logger {
    /**
     * Log a message at DEBUG level
     * @param message The message to log
     */
    void debug(String message);

    /**
     * Log a message at INFO level
     * @param message The message to log
     */
    void info(String message);

    /**
     * Log a message at WARN level
     * @param message The message to log
     */
    void warn(String message);

    /**
     * Log a message at ERROR level
     * @param message The message to log
     */
    void error(String message);

    /**
     * Log a message at FATAL level
     * @param message The message to log
     */
    void fatal(String message);

    /**
     * Log a message at the specified level
     * @param message The message to log
     * @param level The log level
     */
    void log(String message, LogLevel level);

    /**
     * Get the logger name
     * @return The logger name
     */
    String getName();

    /**
     * Shutdown the logger and release resources
     */
    void shutdown();
}

