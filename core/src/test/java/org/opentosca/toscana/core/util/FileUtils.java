package org.opentosca.toscana.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void delete(File f) {
        log.info("Deleting {}", f.getAbsolutePath());
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
