package org.opentosca.toscana.plugins.kubernetes.util;

public class KubernetesScript {
    private String name;
    private String resourcePath;
    private boolean onPush;

    public KubernetesScript(String name, String resourcePath, boolean onPush) {
        this.name = name;
        this.resourcePath = resourcePath;
        this.onPush = onPush;
    }

    public String getName() {
        return name;
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
