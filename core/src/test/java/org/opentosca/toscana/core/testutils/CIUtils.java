package org.opentosca.toscana.core.testutils;

public class CIUtils {
    public static boolean isCI() {
        String env = System.getenv("TEST_MODE");
        return env != null && env.equals("ci");
    }
}
