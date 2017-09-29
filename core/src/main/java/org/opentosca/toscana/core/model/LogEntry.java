package org.opentosca.toscana.core.model;

public class LogEntry {

    public final long timestamp;
    public final String message;
    public final LogLevel logLevel;

    LogEntry(long timestamp, String message, LogLevel level) {
        this.timestamp = timestamp;
        this.message = message;
        this.logLevel = level;

        if (message == null || level == null) {
            throw new IllegalArgumentException(String.format("LogEntry '%s' is invalid", this));
        }
    }

    public String toString() {
        return String.format("LogEntry [timestamp='%d', message='%s', logLevel='%s']");
    }
}