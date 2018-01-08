package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CsarUploadErrorResponse {

    @JsonIgnore
    private final InvalidCsarException exception;
    @JsonIgnore
    private final String path;
    @JsonIgnore
    private final long timestamp = System.currentTimeMillis();
    @JsonIgnore
    private final int status;

    public CsarUploadErrorResponse(
        InvalidCsarException exception,
        String path,
        int status
    ) {
        this.exception = exception;
        this.path = path;
        this.status = status;
    }

    @ApiModelProperty(
        required = true,
        allowEmptyValue = true,
        notes = "The list of log entries that accumulated while performing the parsing of the CSAR"
    )
    @JsonProperty("logs")
    public List<LogEntry> getErrorLogs() {
        return exception.getLog().getLogEntries(0);
    }

    @ApiModelProperty(
        required = true,
        dataType = "integer",
        example = "1509907624000",
        notes = "The Unix timestamp (in milliseconds) when this exception has occrued (Was constructed internally)"
    )
    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @ApiModelProperty(
        required = true,
        example = "/api/csars/test",
        notes = "The HTTP Path that was called and caused the exception."
    )
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @ApiModelProperty(
        required = true,
        example = "400",
        notes = "The HTTP Status code of the exception"
    )
    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @ApiModelProperty(
        required = true,
        example = "Could not parse CSAR, No Entrypoint found!",
        notes = "The message of the exception"
    )
    @JsonProperty("message")
    public String getMessage() {
        return exception.getMessage();
    }

    @ApiModelProperty(
        required = true,
        example = "org.opentosca.toscana.core.parse.InvalidCsarException",
        notes = "The java class name of the exception that occurred on the server while performing the parsing operation."
    )
    @JsonProperty("exception")
    public String getExceptionClass() {
        return exception.getClass().getName();
    }
}
