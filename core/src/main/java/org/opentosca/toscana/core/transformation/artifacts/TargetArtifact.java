package org.opentosca.toscana.core.transformation.artifacts;

public class TargetArtifact {

    private final String relativePath;

    /**
     * @param relativePath relative path to the target artifact
     */
    public TargetArtifact(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getArtifactDownloadURL() {
        return relativePath;
    }
}
