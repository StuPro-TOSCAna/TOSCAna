package org.opentosca.toscana.core.dummy;

import ch.qos.logback.classic.Level;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DummyLog extends Log {

	private List<LogEntry> entries = new ArrayList<>();

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
			entries.add(new LogEntry("Hallo Welt-" + i + "-" + System.currentTimeMillis(), Level.DEBUG));
		}
		return entries.subList(firstIndex, entries.size());
	}
}
