package org.opentosca.toscana.core.data;

public class LogEntry {

    public final long TIMESTAMP;
    public final String MESSAGE;
    public final LogLevel LOG_LEVEL;

    LogEntry(long timestamp, String message, LogLevel level){
        assert message != null;
        assert level != null;
        this.TIMESTAMP = timestamp;
        this.MESSAGE = message;
        this.LOG_LEVEL = level;
    }
}
