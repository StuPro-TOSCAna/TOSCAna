package org.opentosca.toscana.core.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void delete(File f) {
        delete(f, log);
    }

    public static void delete(File f, Logger log) {
        log.debug("Deleting {}", f.getAbsolutePath());
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                delete(file);
            }
            f.delete();
        }
    }
}
