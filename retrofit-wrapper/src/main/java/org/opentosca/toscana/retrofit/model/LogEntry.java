package org.opentosca.toscana.retrofit.model;

import java.time.Instant;

import org.opentosca.toscana.retrofit.model.hal.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogEntry extends Resource {
    
    private Long timestamp;
    private String level;
    private String context;
    private String message;
    private long index;

    public LogEntry(
        @JsonProperty("timestamp") Long timestamp,
        @JsonProperty("level") String level,
        @JsonProperty("context") String context,
        @JsonProperty("message") String message,
        @JsonProperty("index") long index
    ) {
        this.timestamp = timestamp;
        this.level = level;
        this.context = context;
        this.message = message;
        this.index = index;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("level")
    public String getLevel() {
        return level;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("index")
    public long getIndex() {
        return index;
    }

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @Override
    public String toString() {
        return String.format("  > %-26s %-7s %s", Instant.ofEpochMilli(timestamp), level, message);
    }
}
