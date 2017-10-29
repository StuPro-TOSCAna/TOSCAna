package org.opentosca.toscana.core.transformation.logging;

/**
 Occurs when parsing a log line from file failed
 */
class LogParserException extends Exception {

    LogParserException(String logLine) {
        super("Failed to parse log line '" + logLine + "'");
    }
}
