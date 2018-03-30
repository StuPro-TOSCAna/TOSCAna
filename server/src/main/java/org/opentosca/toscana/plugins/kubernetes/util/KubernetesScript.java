package org.opentosca.toscana.plugins.kubernetes.util;

/**
 Represents a helper shell script for the Kubernetes Plugin
 */
public class KubernetesScript {
    /**
     The output file name of the Script
     */
    private String fileName;
    /**
     The path to the resource (within the classpath to use <code>getResourceAsStream()</code>
     */
    private String resourcePath;
    /**
     This flag is set to false if the script should only be copied when the image have not been pushed to a registry
     */
    private boolean onPush;

    public KubernetesScript(String fileName, String resourcePath, boolean onPush) {
        this.fileName = fileName;
        this.resourcePath = resourcePath;
        this.onPush = onPush;
    }

    public String getFileName() {
        return fileName;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    /**
     @return true if the script should be copied even if the images have already been pushed
     */
    public boolean copyIfPushed() {
        return onPush;
    }
}
