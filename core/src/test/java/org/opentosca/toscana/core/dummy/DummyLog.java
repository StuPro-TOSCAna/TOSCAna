package org.opentosca.toscana.core.dummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class DummyLog implements Log {

    private final List<LogEntry> entries = new ArrayList<>();

    @Override
    public List<LogEntry> getLogEntries(int first, int last) {
        return null;
    }

    @Override
    public List<LogEntry> getLogEntries(int firstIndex) {
        if (firstIndex >= entries.size() && !entries.isEmpty()) {
            return Collections.emptyList();
        }
        for (int i = 0; i < 10; i++) {
            entries.add(new LogEntry(i, "Hallo Welt-" + i + "-" + System.currentTimeMillis(), Level.DEBUG));
        }
        return entries.subList(firstIndex, entries.size());
    }

    @Override
    public Logger getLogger(String context) {
        return null;
    }

    @Override
    public Logger getLogger(Class context) {
        return null;
    }

    @Override
    public void addLogEntry(LogEntry e) {
        entries.add(e);
    }
} 
