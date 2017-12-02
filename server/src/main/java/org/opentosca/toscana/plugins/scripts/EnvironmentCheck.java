package org.opentosca.toscana.plugins.scripts;

/**
 checks if a given environment exists 
 */
public class EnvironmentCheck {
        
    public static String checkEnvironment(String command){
        return "check \"" + command + "\"";
    }
    
}
