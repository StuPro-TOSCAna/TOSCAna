package org.opentosca.toscana.core.csar;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CsarImpl implements Csar {

    /**
     the name of the directory which contains the unzipped content of the uploaded CSAR
     */
    public final static String CONTENT_DIR = "content";
    /**
     Stores all scheduled, ongoing or finished transformations of this CSAR. Key is the platform identifier.
     */
    private final Map<String, Transformation> transformations = new HashMap<>();
    private final String identifier;
    /**
     null if not yet parsed
     */
    private final Log log;
    private final File rootDir;
    private final File contentDir;

    public CsarImpl(File rootDir, String identifier, Log log) {
        this.rootDir = rootDir;
        this.contentDir = new File(rootDir, CONTENT_DIR);
        this.identifier = identifier;
        this.log = log;
    }

    @Override
    public Map<String, Transformation> getTransformations() {
        return transformations;
    }

    @Override
    public Optional<Transformation> getTransformation(String platformId) {
        Transformation t = transformations.get(platformId);
        return Optional.ofNullable(t);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Csar) && (((Csar) obj).getIdentifier().equals(identifier));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
            .append(identifier)
            .toHashCode();
    }

    @Override
    public void setTransformations(List<Transformation> transformations) {
        for (Transformation transformation : transformations) {
            this.transformations.put(transformation.getPlatform().id, transformation);
        }
    }

    @Override
    public File getContentDir() {
        return contentDir;
    }

    @Override
    public String toString() {
        return String.format("Csar [id: %s]", getIdentifier());
    }
}

