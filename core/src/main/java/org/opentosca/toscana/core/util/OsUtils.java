package org.opentosca.toscana.core.util;

public class OsUtils {
    private static String OS = null;

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().contains("Windows");
    }

    public static boolean isUnix() {
        return getOsName().contains("Linux");
    }

    public static boolean isMac() {
        return getOsName().contains("Mac");
    }
}
