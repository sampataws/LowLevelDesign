package com.loggingmc;

import com.loggingmc.sink.Sink;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for Logger initialization
 */
public class LoggerConfiguration {
    private String loggerName;
    private String timestampFormat;
    private LogLevel logLevel;
    private LoggerType loggerType;
    private int bufferSize;
    private List<Sink> sinks;

    public LoggerConfiguration() {
        this.sinks = new ArrayList<>();
        // Default values
        this.timestampFormat = "dd-MM-yyyy-HH-mm-ss";
        this.logLevel = LogLevel.INFO;
        this.loggerType = LoggerType.SYNC;
        this.bufferSize = 10;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LoggerType getLoggerType() {
        return loggerType;
    }

    public void setLoggerType(LoggerType loggerType) {
        this.loggerType = loggerType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive");
        }
        this.bufferSize = bufferSize;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public void addSink(Sink sink) {
        this.sinks.add(sink);
    }

    public void setSinks(List<Sink> sinks) {
        this.sinks = sinks;
    }

    public enum LoggerType {
        SYNC,
        ASYNC
    }
}

