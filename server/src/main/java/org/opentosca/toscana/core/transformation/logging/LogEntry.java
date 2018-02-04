package org.opentosca.toscana.core.transformation.logging;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiModel
public class LogEntry {

    private final static Logger logger = LoggerFactory.getLogger(LogEntry.class);

    private long timestamp;
    private String context;
    private String message;
    private Level level;
    private long index;

    public LogEntry(long index, String context, String message, Level level) {
        this(System.currentTimeMillis(), index, context, message, level);
    }

    private LogEntry(long timestamp, long index, String context, String message, Level level) {
        this.timestamp = timestamp;
        this.context = context;
        this.message = message;
        this.level = level;
        this.index = index;
        if (message == null || level == null) {
            throw new IllegalArgumentException(String.format("LogEntry '%s' is invalid", this));
        }
    }

    /**
     Constructs a LogEntry instance from a log line

     @param line        the log line which gets parsed
     @param predecessor the LogEntry preceding this newly created LogEntry
     */
    LogEntry(String line, LogEntry predecessor) throws LogParserException {
        if (isRegularLine(line)) {
            parseRegularLine(line, predecessor);
        } else {
            parseStackTraceLine(line, predecessor);
        }
    }

    private void parseRegularLine(String line, LogEntry predecessor) throws LogParserException {
        String[] tokens = line.split(" ", 4);
        if (tokens.length == 4) {
            String dateString = tokens[0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PersistentAppender.DATE_FORMAT);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
            timestamp = zonedDateTime.toInstant().toEpochMilli();
            String levelString = tokens[1];
            level = Level.valueOf(levelString);
            context = tokens[2];
            message = tokens[3];
            if (predecessor != null) {
                index = predecessor.index + 1;
            } else {
                index = 0;
            }
        } else {
            throw new LogParserException(line);
        }
    }

    private void parseStackTraceLine(String line, LogEntry precessor) {
        level = Level.ERROR;
        message = line;
        if (precessor != null) {
            timestamp = precessor.timestamp;
            index = precessor.index + 1;
        } else {
            timestamp = -1;
            index = 0;
            logger.error("Failed to populate '{}' with timestamp", this);
        }
    }

    // a regular logline is a line with beginning with date and time, else it's considered as part of a stacktrace

    private boolean isRegularLine(String line) {
        Matcher m = PersistentAppender.DATE_FORMAT_REGEX.matcher(line);
        return m.find();
    }

    public void setIndex(long index) {
        this.index = index;
    }

    @ApiModelProperty(
        required = true,
        notes = "the index of the logline in the list of all loglines for this list of log entries",
        example = "0",
        dataType = "integer"
    )
    @JsonProperty("index")
    public long getIndex() {
        return index;
    }

    @ApiModelProperty(
        required = true,
        notes = "The unix timestamp (in milliseconds) when this log was created",
        example = "1509907624000",
        dataType = "integer"
    )
    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @ApiModelProperty(
        required = true,
        notes = "The log level for this log entry. The value has to be one of the following: " +
            "\"DEBUG\",\"INFO\",\"WARN\",\"TRACE\", \"ERROR\" or \"ALL\"",
        example = "DEBUG"
    )
    @JsonProperty("level")
    public String getLevel() {
        return level.levelStr;
    }

    @ApiModelProperty(
        required = true,
        notes = "The log context",
        example = "org.foo.Bar"
    )
    public String getContext() {
        return context;
    }

    @ApiModelProperty(
        required = true,
        notes = "The log message",
        example = "Some log message"
    )
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("LogEntry [timestamp='%d', message='%s', level='%s']", timestamp, message, level);
    }
}
