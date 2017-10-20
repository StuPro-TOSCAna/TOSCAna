package org.opentosca.toscana.core.transformation.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class TransformationAppender extends AppenderBase<ILoggingEvent> {

	private Log log;

	public TransformationAppender(Log log) {
		this.log = log;
	}

	@Override
	protected void append(ILoggingEvent iLoggingEvent) {
		
	}
}
