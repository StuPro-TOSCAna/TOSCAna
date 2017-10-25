package org.opentosca.toscana.core.transformation.artifacts;

public class TargetArtifact {

    private String relativeDlUrl = "";

    public TargetArtifact() {
    }

    public TargetArtifact(String relativeDlUrl) {
        this.relativeDlUrl = relativeDlUrl;
    }

    public String getArtifactDownloadURL() {
        return relativeDlUrl;
    }

    public void setArtifactDownloadURL(String url) {
        this.relativeDlUrl = url;
    }
}
