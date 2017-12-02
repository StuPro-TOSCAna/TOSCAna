package org.opentosca.toscana.plugins.scripts;

/**
 checks if a given environment exists 
 */
public class EnvirnonmentCheck {
    
    protected EnvirnonmentCheck() {
    }
    
    public String checkEnvironment(String command){
        String check = " echo \"Check if \"" + command + "\" is available.\"\n" +
            "        if ! [ -x \"$(command -v \"" + command + "\")\" ]; then\n" +
            "        echo \"Error: \"" + command + "\" is not installed.\" >&2\n" +
            "        return 1\n" +
            "        fi";
        return check;
    }
    
}
