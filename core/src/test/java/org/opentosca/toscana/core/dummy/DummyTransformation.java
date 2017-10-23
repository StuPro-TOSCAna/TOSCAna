package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import java.io.File;

public class DummyTransformation implements Transformation {

    private TransformationState state = TransformationState.INPUT_REQUIRED;
    private Platform platform;
    private Log log = new DummyLog();
    private boolean returnTargetArtifact = true;

    private PropertyInstance properties;

    public DummyTransformation(Platform platform) {
        this.platform = platform;
        this.properties = new PropertyInstance(platform.properties);
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
    public TargetArtifact getTargetArtifact() {
        return returnTargetArtifact ? new TargetArtifact() : null;
    }


    @Override
    public Csar getCsar() {
        return null;
    }

    public void setReturnTargetArtifact(boolean returnTargetArtifact) {
        this.returnTargetArtifact = returnTargetArtifact;
    }
}
