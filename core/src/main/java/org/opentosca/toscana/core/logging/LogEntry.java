package org.opentosca.toscana.core.logging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogEntry {

    public final long timestamp;
    public final String message;
    @JsonProperty("level") //Used for serialisation by the REST Api!
    public final LogLevel logLevel;

	public LogEntry(String message, LogLevel logLevel) {
		this(System.currentTimeMillis(), message, logLevel);
	}

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
