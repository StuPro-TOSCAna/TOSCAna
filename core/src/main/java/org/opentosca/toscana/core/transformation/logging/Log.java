package org.opentosca.toscana.core.transformation.logging;

import java.util.List;

import ch.qos.logback.classic.Logger;

public interface Log {
    /**
     * Adds a new Log entry to the end of the log. This method is protected, because it should only be accessed by in
     * classes within the same package/subclasses
     *
     * @param e the logenty to add
     */
    void addLogEntry(LogEntry e);

    /**
     * Returns the log entries of this transaction in specified range. They are ordered from old to new. <p> Must hold
     * true: 0 <= <code>first</code> <= <code>last</code>
     *
     * @param first the first entry to be received.
     * @param last  the last entry to be received.
     * @return returns all log entries of this transformation beginning with the given first index up to the given last
     * index of all entries. If no entries are available in given range, returns an empty List. The Produced list is not
     * mutable!
     * @throws IllegalArgumentException if not (0 <= <code>first</code> <= <code>last</code>)
     */
    List<LogEntry> getLogEntries(int first, int last);

    /**
     * Like <code>getLogs(int firstIndex,int lastIndex)</code>, but omits the lastIndex. Therefore, beginning with start
     * index, all successing log entries are returned.
     *
     * @return list of log entries sucessing and including the {firstIndex}nth entry
     * @see #getLogEntries(int firstIndex, int lastIndex)
     */
    List<LogEntry> getLogEntries(int firstIndex);

    /**
     * Creates a logger which appends to this log
     *
     * @param context the context of the logger
     */
    Logger getLogger(String context);

    /**
     * @see Log#getLogger(String)
     */
    Logger getLogger(Class context);
}
