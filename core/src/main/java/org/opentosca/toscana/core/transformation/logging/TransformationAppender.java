package org.opentosca.toscana.core.transformation.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * This class implements the object (instance) specific logging functionality for transformations and deployments
 */
public class TransformationAppender extends AppenderBase<ILoggingEvent> {

    private Log log;

    public TransformationAppender(Log log) {
        this.log = log;
    }

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        appendToLog(loggingEvent, loggingEvent.getFormattedMessage());
        if (loggingEvent.getThrowableProxy() != null) {
            appendThrowable(loggingEvent.getThrowableProxy(), loggingEvent);
        }
    }

    private void appendThrowable(IThrowableProxy proxy, ILoggingEvent loggingEvent) {
        //Append Exception Message
        appendToLog(loggingEvent, String.format("%s: %s", proxy.getClassName(), proxy.getMessage()));
        //Append Exception Stack Trace
        for (StackTraceElementProxy element : loggingEvent.getThrowableProxy().getStackTraceElementProxyArray()) {
            appendToLog(loggingEvent, element.getSTEAsString());
        }
        if (proxy.getSuppressed().length > 0) {
            appendToLog(loggingEvent, "Suppressed Exceptions:");
            for (IThrowableProxy p : proxy.getSuppressed()) {
                appendThrowable(p, loggingEvent);
            }
        }
        if (proxy.getCause() != null) {
            appendToLog(loggingEvent, "Cause:");
            appendThrowable(proxy.getCause(), loggingEvent);
        }
    }

    private void appendToLog(ILoggingEvent loggingEvent, String message) {
        log.addLogEntry(
            new LogEntry(
                loggingEvent.getTimeStamp(),
                message,
                loggingEvent.getLevel()
            )
        );
    }
}
