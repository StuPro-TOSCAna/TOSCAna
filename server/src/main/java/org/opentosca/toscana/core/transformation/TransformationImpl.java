package org.opentosca.toscana.core.transformation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import static java.lang.String.format;

public class TransformationImpl implements Transformation {

    private final Csar csar;
    private final Platform targetPlatform;
    private final Log log;
    private final PropertyInstance properties;
    private TransformationState state = TransformationState.READY;
    private TargetArtifact targetArtifact;

    /**
     Creates a new transformation for given csar to given targetPlatform.

     @param csar           the subject of transformation
     @param targetPlatform the target platform
     */
    public TransformationImpl(Csar csar, Platform targetPlatform, Log log) {
        this.csar = csar;
        this.targetPlatform = targetPlatform;
        this.log = log;

        //Collect Possible Properties From the Platform and the Model
        Set<Property> properties = new HashSet<>();
        properties.addAll(csar.getModelSpecificProperties().values());
        properties.addAll(targetPlatform.getProperties());

        // transformationState can get set to INPUT_REQUIRED by this call
        this.properties = new PropertyInstance(properties, this);
    }

    @Override
    public TransformationState getState() {
        return state;
    }

    @Override
    public void setState(TransformationState state) {
        this.state = state;
    }

    @Override
    public Csar getCsar() {
        return csar;
    }

    @Override
    public Log getLog() {
        return log;
    }

    /**
     @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the
     transformation wrapped in an Optional. Else returns empty Optional.
     */
    @Override
    public Optional<TargetArtifact> getTargetArtifact() {
        return Optional.ofNullable(targetArtifact);
    }

    @Override
    public void setTargetArtifact(TargetArtifact artifact) {
        this.targetArtifact = artifact;
    }

    @Override
    public Platform getPlatform() {
        return targetPlatform;
    }

    @Override
    public PropertyInstance getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return format("Transformation [csarId='%s', platformId='%s']", csar.getIdentifier(), targetPlatform.id);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Transformation && ((Transformation) o).getPlatform().equals(this.getPlatform())
            && ((Transformation) o).getCsar().equals(this.getCsar()));
    }
}
