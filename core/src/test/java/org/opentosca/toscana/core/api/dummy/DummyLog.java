package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;
import org.opentosca.toscana.core.transformation.logging.LogLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DummyLog extends Log {
	
	private List<LogEntry> entries = new ArrayList<>();
		
	@Override
	public List<LogEntry> getLogEntries(int firstIndex, int lastIndex) {
		return null;
	}

	@Override
	public List<LogEntry> getLogEntries(int firstIndex) {
		if(firstIndex >= entries.size() && !entries.isEmpty()) {
			return Collections.emptyList();
		}
		for (int i = 0; i < 10; i++) {
			entries.add(new LogEntry("Hallo Welt-"+i+"-"+System.currentTimeMillis(), LogLevel.DEBUG));
		}
		return entries.subList(firstIndex, entries.size());
	}
}
