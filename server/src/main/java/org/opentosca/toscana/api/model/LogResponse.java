package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.CsarController;
import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
@Relation(collectionRelation = "log")
public class LogResponse extends HiddenResourceSupport {
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
        this.add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, start))
            .withSelfRel().expand(csarName));
        //Add link to retrieve only new log messages (next link)
        this.add(linkTo(methodOn(TransformationController.class)
            .getTransformationLogs(csarName, platform, end))
            .withRel("next").expand(csarName));
    }

    public LogResponse(
        @JsonProperty("start") long start,
        @JsonProperty("end") long end,
        @JsonProperty("logs") List<LogEntry> logEntries,
        String csarName
    ) {
        this.start = start;
        this.end = end;
        this.logEntries = logEntries;
        //Add Self Link
        this.add(ControllerLinkBuilder.linkTo(methodOn(CsarController.class)
            .getLogs(csarName, start))
            .withSelfRel().expand(csarName));
        //Add link to retrieve only new log messages (next link)
        this.add(linkTo(methodOn(CsarController.class)
            .getLogs(csarName, end))
            .withRel("next").expand(csarName));
    }

    @ApiModelProperty(
        required = true,
        notes = "The requested start index",
        example = "0",
        dataType = "integer"
    )
    @JsonProperty("start")
    public long getStart() {
        return start;
    }

    @ApiModelProperty(
        required = true,
        notes = "The End index of the logfile at the time of requesting this information.",
        example = "0",
        dataType = "integer"
    )
    @JsonProperty("end")
    public long getEnd() {
        return end;
    }

    @ApiModelProperty(
        required = true,
        notes = "The sorted list (ascending by timestamp) of all the logs between start index and the current end index"
    )
    @JsonProperty("logs")
    public List<LogEntry> getLogEntries() {
        return logEntries;
    }
}
