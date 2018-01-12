package org.opentosca.toscana.api.docs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 Modelling class, used to model the error message from the spring /error API (for documentation purposes)
 */
@ApiModel
public class RestErrorResponse {

    @ApiModelProperty(
        required = true,
        dataType = "integer",
        example = "1509907624000",
        notes = "The Unix timestamp (in milliseconds) when this exception has occrued (Was constructed internally)"
    )
    @JsonProperty("timestamp")
    public long timestamp;

    @ApiModelProperty(
        required = true,
        dataType = "integer",
        example = "404",
        notes = "The HTTP Status code of the exception"
    )
    @JsonProperty("status")
    public int status;

    @ApiModelProperty(
        required = true,
        notes = "Text description of the HTTP Error",
        example = "Not Found"
    )
    @JsonProperty("error")
    public String error;

    @ApiModelProperty(
        required = true,
        example = "java.io.FileNotFoundException",
        notes = "The java class name of the exception that occurred on the server while performing the parsing operation."
    )
    @JsonProperty("exception")
    public String exception;

    @ApiModelProperty(
        required = true,
        example = "File was not found",
        notes = "The message of the exception"
    )
    @JsonProperty("message")
    public String message;

    @ApiModelProperty(
        required = true,
        example = "/files/test.txt",
        notes = "The HTTP Path that was called and caused the exception."
    )
    @JsonProperty("path")
    public String path;
}
