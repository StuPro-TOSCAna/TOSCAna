package org.opentosca.toscana.core.transformation.logging;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

public class PersistentAppender extends FileAppender<ILoggingEvent> {

    // important: if you change the date format, also change the date regex!!
    public final static String DATE_FORMAT = "yyyy-MM-dd/HH:mm:ss.SSS/zzz";
    public final static Pattern DATE_FORMAT_REGEX = Pattern.compile("[0-9]{4}-[01][0-9]-[0123][0-9]/[012][0-9]:[012345][0-9]:[012345][0-9].[0-9]{3}/.{3}");
    public final static String PATTERN_LAYOUT = "%d{" + DATE_FORMAT + "} %-5level %-36logger{36} %msg%n";

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(PersistentAppender.class);

    PersistentAppender(File targetFile) {
        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            logger.error("Failed to create logfile '{}'", targetFile, e);
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        setupLayout(lc);
        setFile(targetFile.getAbsolutePath());
        setContext(lc);
        start();
    }

    private void setupLayout(LoggerContext lc) {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(PATTERN_LAYOUT);
        ple.setContext(lc);
        ple.start();
        setEncoder(ple);
    }
}
