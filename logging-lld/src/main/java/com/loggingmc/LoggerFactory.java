package com.loggingmc;

import com.loggingmc.logger.AsyncLogger;
import com.loggingmc.logger.Logger;
import com.loggingmc.logger.SyncLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class for creating and managing logger instances
 */
public class LoggerFactory {
    private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    /**
     * Create a logger based on the provided configuration
     * @param config The logger configuration
     * @return A logger instance
     */
    public static Logger createLogger(LoggerConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        String loggerName = config.getLoggerName();
        
        // Check if logger already exists
        if (loggers.containsKey(loggerName)) {
            throw new IllegalStateException("Logger with name '" + loggerName + "' already exists");
        }

        Logger logger;
        if (config.getLoggerType() == LoggerConfiguration.LoggerType.ASYNC) {
            logger = new AsyncLogger(config);
        } else {
            logger = new SyncLogger(config);
        }

        loggers.put(loggerName, logger);
        return logger;
    }

    /**
     * Get an existing logger by name
     * @param name The logger name
     * @return The logger instance, or null if not found
     */
    public static Logger getLogger(String name) {
        return loggers.get(name);
    }

    /**
     * Shutdown a specific logger
     * @param name The logger name
     */
    public static void shutdownLogger(String name) {
        Logger logger = loggers.remove(name);
        if (logger != null) {
            logger.shutdown();
        }
    }

    /**
     * Shutdown all loggers
     */
    public static void shutdownAll() {
        for (Logger logger : loggers.values()) {
            try {
                logger.shutdown();
            } catch (Exception e) {
                System.err.println("Error shutting down logger: " + e.getMessage());
            }
        }
        loggers.clear();
    }
}

