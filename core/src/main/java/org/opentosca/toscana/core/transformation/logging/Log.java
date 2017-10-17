package org.opentosca.toscana.core.transformation.logging;

import java.util.List;

/**
 * The logs of a specific transformation.
 */
public class Log {

    private List<LogEntry> logEntries;

    /**
     * Returns the log entries of this transaction in specified range.
     * They are ordered from old to new.
     *
     * Must hold true: 0 <= <code>firstIndex</code> <= <code>lastIndex</code>
     * @param firstIndex the first entry to be received.
     * @param lastIndex the last entry to be received.
     * @return returns all log entries of this transformation beginning with the given first index
     * up to the given last index of all entries. If no entries are available in given range, returns an empty set.
     * @throws IllegalArgumentException if not (0 <= <code>firstIndex</code> <= <code>lastIndex</code>)
     */
    public List<LogEntry> getLogEntries(int firstIndex, int lastIndex){
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Like <code>getLogs(int firstIndex,int lastIndex)</code>, but omits the lastIndex.
     * Therefore, beginning with start index, all successing log entries are returned.
     * @see #getLogEntries(int firstIndex, int lastIndex)
     * @param firstIndex
     * @return list of log entries sucessing and including the {firstIndex}nth entry
     */
    public List<LogEntry> getLogEntries(int firstIndex){
        // TODO
        throw new UnsupportedOperationException();
    }
}
