package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationLogs extends HALResource {
    private Long start;
    private Long end;
    private List<LogEntry> logEntries;

    public TransformationLogs(
        @JsonProperty("start") Long start,
        @JsonProperty("end") Long end,
        @JsonProperty("logs") List<LogEntry> logEntries
    ) {
        this.start = start;
        this.end = end;
        this.logEntries = logEntries;
    }

    @JsonProperty("start")
    public Long getStart() {
        return start;
    }

    @JsonProperty("end")
    public Long getEnd() {
        return end;
    }

    @JsonProperty("logs")
    public List<LogEntry> getLogEntries() {
        return logEntries;
    }
}
