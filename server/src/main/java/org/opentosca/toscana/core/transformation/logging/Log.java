package org.opentosca.toscana.core.transformation.logging;

import java.util.List;

import org.slf4j.Logger;

public interface Log {
    /**
     Adds a new Log entry to the end of the log.

     @param e the LogEntry to add
     */
    void addLogEntry(LogEntry e);

    /**
     Returns the log entries of this transaction in specified range. They are ordered from old to new.
     <p>
     Must hold true: 0 <= <code>first</code> <= <code>last</code>

     @param first the first entry to be received.
     @param last  the last entry to be received.
     @return returns all log entries of this transformation beginning with the given first index up to the given last
     index of all entries. If no entries are available in given range, returns an empty List. The Produced list is not
     mutable!
     @throws IllegalArgumentException if not (0 <= <code>first</code> <= <code>last</code>)
     */
    List<LogEntry> getLogEntries(int first, int last);

    /**
     Like {@link #getLogEntries(int first, int last)}, but omits the lastIndex. Therefore, beginning with start
     index, all following log entries are returned.

     @return list of log entries following and including the {firstIndex}nth entry
     @see #getLogEntries(int firstIndex, int lastIndex)
     */
    List<LogEntry> getLogEntries(int firstIndex);

    /**
     Creates a logger which appends to this log

     @param context the context of the logger
     */
    Logger getLogger(String context);

    /**
     @see Log#getLogger(String)
     */
    Logger getLogger(Class context);

    void close();
}
