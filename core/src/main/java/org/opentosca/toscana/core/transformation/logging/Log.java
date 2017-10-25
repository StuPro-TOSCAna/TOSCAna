package org.opentosca.toscana.core.transformation.logging;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The logs of a specific transformation.
 */
public class Log {

    private List<LogEntry> logEntries;
    private AtomicLong index;

    public Log() {
        //Create Synchronized linked list. to prevent any issues regarding concurrency
        this.logEntries = Collections.synchronizedList(new LinkedList<>());
        index = new AtomicLong(0);
    }

    /**
     * Adds a new Log entry to the end of the log. This method is protected,
     * because it should only be accessed by in classes within the same package/subclasses
     *
     * @param e the logenty to add
     */
    void addLogEntry(LogEntry e) {
        e.setIndex(index.getAndIncrement());
        logEntries.add(e);
    }

    /**
     * Returns the log entries of this transaction in specified range.
     * They are ordered from old to new.
     * <p>
     * Must hold true: 0 <= <code>first</code> <= <code>last</code>
     *
     * @param first the first entry to be received.
     * @param last  the last entry to be received.
     * @return returns all log entries of this transformation beginning with the given first index
     * up to the given last index of all entries. If no entries are available in given range, returns an empty List.
     * The Produced list is not mutable!
     * @throws IllegalArgumentException if not (0 <= <code>first</code> <= <code>last</code>)
     */
    public List<LogEntry> getLogEntries(int first, int last) {
        return getLogEntries(first, last, true);
    }

    private List<LogEntry> getLogEntries(int first, int last, boolean checkUpperBound) {
        if (0 > first || (last < first && checkUpperBound)) {
            throw new IllegalArgumentException("Given indicies are not within the bound 0 <= first <= last");
        } else if (first >= logEntries.size()) {
            return Collections.unmodifiableList(new ArrayList<>());
        }
        return Collections.unmodifiableList(logEntries.subList(first, last + 1));
    }

    /**
     * Like <code>getLogs(int firstIndex,int lastIndex)</code>, but omits the lastIndex.
     * Therefore, beginning with start index, all successing log entries are returned.
     *
     * @param firstIndex
     * @return list of log entries sucessing and including the {firstIndex}nth entry
     * @see #getLogEntries(int firstIndex, int lastIndex)
     */
    public List<LogEntry> getLogEntries(int firstIndex) {
        return getLogEntries(firstIndex, logEntries.size() - 1, false);
    }

    /**
     * Creates a logger which appends to this log
     *
     * @param context the context of the logger
     * @return
     */
    public Logger getLogger(String context) {
        Logger tLog = (Logger) LoggerFactory.getLogger(context);

        TransformationAppender appender = new TransformationAppender(this);
        appender.setContext(tLog.getLoggerContext());
        appender.start();
        tLog.addAppender(appender);

        //TODO Add File Appender once mechanism for getting the filepath has been implemented.

        return tLog;
    }


    /**
     * @see Log#getLogger(String)
     */
    public Logger getLogger(Class context) {
        return getLogger(context.getName());
    }
}
