package com.loggingmc.logger;

import com.loggingmc.LogLevel;
import com.loggingmc.LogMessage;
import com.loggingmc.LoggerConfiguration;
import com.loggingmc.sink.Sink;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Asynchronous logger implementation with bounded buffer
 * Messages are queued and processed by a background thread
 * Ensures message ordering and thread-safety
 */
public class AsyncLogger implements Logger {
    private final String name;
    private final List<Sink> sinks;
    private final String timestampFormat;
    private final LogLevel logLevel;
    private final BlockingQueue<LogMessage> messageQueue;
    private final Thread workerThread;
    private final AtomicBoolean running;
    private static final LogMessage POISON_PILL = new LogMessage("", LogLevel.DEBUG);

    public AsyncLogger(LoggerConfiguration config) {
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
        this.messageQueue = new ArrayBlockingQueue<>(config.getBufferSize());
        this.running = new AtomicBoolean(true);

        // Start background worker thread
        this.workerThread = new Thread(new LogWorker(), "AsyncLogger-" + name);
        this.workerThread.setDaemon(false); // Ensure thread completes before JVM shutdown
        this.workerThread.start();
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

        if (!running.get()) {
            throw new IllegalStateException("Logger has been shut down");
        }

        // Filter messages below the logger's configured level
        if (!level.isAtLeast(logLevel)) {
            return;
        }

        LogMessage logMessage = new LogMessage(message, level);

        try {
            // Block if queue is full to prevent data loss
            messageQueue.put(logMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fail gracefully - log error but don't lose the message
            System.err.println("Interrupted while queuing message: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shutdown() {
        if (!running.compareAndSet(true, false)) {
            return; // Already shut down
        }

        try {
            // Send poison pill to signal worker thread to stop
            messageQueue.put(POISON_PILL);
            // Wait for worker thread to finish processing all messages
            workerThread.join(5000); // Wait up to 5 seconds

            if (workerThread.isAlive()) {
                System.err.println("Worker thread did not terminate gracefully");
                workerThread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during shutdown: " + e.getMessage());
        }

        // Close all sinks
        for (Sink sink : sinks) {
            try {
                sink.close();
            } catch (Exception e) {
                System.err.println("Error closing sink: " + e.getMessage());
            }
        }
    }

    /**
     * Worker thread that processes messages from the queue
     */
    private class LogWorker implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    LogMessage message = messageQueue.take();

                    // Check for poison pill
                    if (message == POISON_PILL) {
                        break;
                    }

                    // Write to all sinks
                    for (Sink sink : sinks) {
                        try {
                            sink.write(message, timestampFormat);
                        } catch (Exception e) {
                            // Fail gracefully - print error but continue with other sinks
                            System.err.println("Error writing to sink: " + e.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}

