package org.opentosca.toscana.plugins.scripts;

/**
 checks if a given environment exists
 wraps resources/plugins.scripts.util/environment-check.sh
 */
public class EnvironmentCheck {

    public static String checkEnvironment(String command) {
        return "check \"" + command + "\"";
    }
}
