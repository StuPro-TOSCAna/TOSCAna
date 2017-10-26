package org.opentosca.toscana.core.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import static org.junit.Assert.fail;

public class FileHelper {
    public static String readFileToString(File file) {
        String result = "";
        try {
            result = FileUtils.readFileToString(file, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to open " + file.getName() + "test resource file.");
        }
        return result;
    }
}
