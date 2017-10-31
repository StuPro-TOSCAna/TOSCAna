package org.opentosca.toscana.core.dummy;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import org.apache.commons.io.FileUtils;

public class DummyTransformation implements Transformation {

    private TransformationState state = TransformationState.INPUT_REQUIRED;
    private final Platform platform;
    private final Log log = new DummyLog();
    private boolean returnTargetArtifact = true;
    private Csar csar;

    private final PropertyInstance properties;

    public DummyTransformation(Platform platform) {
        this.platform = platform;
        this.properties = new PropertyInstance(platform.properties, this);
    }

    public DummyTransformation(Platform platform, TransformationState s) {
        this(platform);
        this.state = s;
    }

    @Override
    public TransformationState getState() {
        return state;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void setProperty(String key, String value) {
        properties.setPropertyValue(key, value);
    }

    @Override
    public void setState(TransformationState state) {
        this.state = state;
    }

    @Override
    public PropertyInstance getProperties() {
        return properties;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public Optional<TargetArtifact> getTargetArtifact() {
        File out = new File("test-artifact");
        //TODO Remove this after Removing all dummies
        if (returnTargetArtifact) {
            if (out.exists()) {
                out.delete();
            }
            byte[] data = TestCsars.getFFBytes();
            try {
                FileUtils.writeByteArrayToFile(out, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnTargetArtifact ? Optional.of(new TargetArtifact(new File("test-artifact"))) : Optional.empty();
    }

    @Override
    public void setTargetArtifact(TargetArtifact targetArtifact) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Csar getCsar() {
        return csar;
    }

    public void setCsar(Csar csar) {
        this.csar = csar;
    }

    public void setReturnTargetArtifact(boolean returnTargetArtifact) {
        this.returnTargetArtifact = returnTargetArtifact;
    }
} 
