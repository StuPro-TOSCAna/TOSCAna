package org.opentosca.toscana.core.transformation.logging;

/**
 * Occurs when parsing a log line from file failed
 */
class LogParserException extends Exception {
    
    final String logLine;
    
    LogParserException(String logLine){
        super("Failed to parse log line '" + logLine + "'");
        this.logLine = logLine;
    }
}
