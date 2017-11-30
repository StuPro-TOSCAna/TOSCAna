package org.opentosca.toscana.retrofit.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerError {
    @JsonProperty("path")
    private String path;
    @JsonProperty("error")
    private String errorName;
    @JsonProperty("exception")
    private String exception;
    @JsonProperty("message")
    private String message;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("status")
    private Long status;
    @JsonProperty("logs")
    private List<LogEntry> logs;

    public String getPath() {
        return path;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getStatus() {
        return status;
    }

    public List<LogEntry> getLogs() {
        return logs == null ? new ArrayList<>() : logs;
    }
}
