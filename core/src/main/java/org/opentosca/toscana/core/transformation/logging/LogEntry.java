package org.opentosca.toscana.core.transformation.logging;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogEntry {

	private final long timestamp;
	private final String message;
	private final Level level;
	private long index = -1;	
	
	public LogEntry(String message, Level level) {
		this(System.currentTimeMillis(), message, level);
	}

	LogEntry(long timestamp, String message, Level level) {
		this.timestamp = timestamp;
		this.message = message;
		this.level = level;

		if (message == null || level == null) {
			throw new IllegalArgumentException(String.format("LogEntry '%s' is invalid", this));
		}
	}

	void setIndex(long index) {
		this.index = index;
	}

	@JsonProperty("index")
	public long getIndex() {
		return index;
	}

	@JsonProperty("timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("level")
	public String getLevel() {
		return level.levelStr;
	}

	public String toString() {
		return String.format("LogEntry [timestamp='%d', message='%s', level='%s']");
	}
}
