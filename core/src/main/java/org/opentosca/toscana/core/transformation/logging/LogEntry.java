package org.opentosca.toscana.core.transformation.logging;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEntry {

    private final static Logger logger = LoggerFactory.getLogger(LogEntry.class);

    private long timestamp;
    private String message;
    private Level level;
    private long index;

    public LogEntry(long index, String message, Level level) {
        this(System.currentTimeMillis(), index, message, level);
    }

    LogEntry(long timestamp, long index, String message, Level level) {
        this.timestamp = timestamp;
        this.message = message;
        this.level = level;
        this.index = index;
        if (message == null || level == null) {
            throw new IllegalArgumentException(String.format("LogEntry '%s' is invalid", this));
        }
    }

    /**
     * Constructs a LogEntry instance from a log line
     * @param line the logline which gets parsed
     * @precessor the precessor of the current line
     */
    LogEntry(String line, Optional<LogEntry> precessor) throws LogParserException {
        if (isRegularLine(line)){
            parseRegularLine(line, precessor);
        } else {
            parseStackTraceLine(line, precessor);
                
        }
        String[] tokens = line.split(" ", 3);
    }

    private void parseStackTraceLine(String line, Optional<LogEntry> precessor) {
        level = Level.ERROR;
        message = line;
        if (precessor.isPresent()){
            timestamp = precessor.get().timestamp;
            index = precessor.get().index + 1;
        } else {
            timestamp = -1;
            index = 0;
            logger.error("Failed to populate '{}' with timestamp", this);
        }
    }

    private void parseRegularLine(String line, Optional<LogEntry> precessor) throws LogParserException {
        String[] tokens = line.split(" ", 3);
        if (tokens.length == 3) {
            String dateString = tokens[0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PersistentAppender.DATE_FORMAT);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
            timestamp = zonedDateTime.toInstant().toEpochMilli();
            String levelString = tokens[1];
            level = Level.valueOf(levelString);
            message = tokens[2];
            if (precessor.isPresent()){
                index = precessor.get().index + 1;
            } else {
                index = 0;
            }
        } else {
            throw new LogParserException(line);
        }
    }

    // a regular logline is a line with beginning with date and time, else it's considered as part of a stacktrace
    private boolean isRegularLine(String line){
        Matcher m = PersistentAppender.DATE_FORMAT_REGEX.matcher(line);
        return m.find();
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
        return String.format("LogEntry [timestamp='%d', message='%s', level='%s']", timestamp, message, level);
    }
}
