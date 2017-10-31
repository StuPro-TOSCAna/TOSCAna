package org.opentosca.toscana.core.transformation.artifacts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TargetArtifact {

    public final String name;
    private final File artifactFile;

    /**
     @param artifactFile the file related to the artifact
     */
    public TargetArtifact(File artifactFile) {
        this.artifactFile = artifactFile;
        this.name = artifactFile.getName();
    }

    public OutputStream writeAccess() throws FileNotFoundException {
        return new FileOutputStream(artifactFile);
    }

    public InputStream readAccess() throws FileNotFoundException {
        return new FileInputStream(artifactFile);
    }

    /**
     @return the file size of the artifact file in bytes.
     */
    public long getFileSize() {
        return artifactFile.length();
    }
}
