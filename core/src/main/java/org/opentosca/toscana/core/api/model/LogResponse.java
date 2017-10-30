package org.opentosca.toscana.core.api.model;

import java.util.List;

import org.opentosca.toscana.core.api.TransformationController;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "log")
public class LogResponse extends ResourceSupport {
    private final long start;
    private final long end;
    private final List<LogEntry> logEntries;

    public LogResponse(
        @JsonProperty("start") long start,
        @JsonProperty("end") long end,
        @JsonProperty("logs") List<LogEntry> logEntries,
        String platform,
        String csarName
    ) {
        this.start = start;
        this.end = end;
        this.logEntries = logEntries;
        //Add Self Link
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, start))
            .withSelfRel().expand(csarName));
        //Add link to retrieve only new log messages (next link)
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, end))
            .withRel("next").expand(csarName));
    }

    @JsonProperty("start")
    public long getStart() {
        return start;
    }

    @JsonProperty("end")
    public long getEnd() {
        return end;
    }

    @JsonProperty("logs")
    public List<LogEntry> getLogEntries() {
        return logEntries;
    }
}
