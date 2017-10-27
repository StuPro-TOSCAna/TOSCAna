package org.opentosca.toscana.core.transformation;

import ch.qos.logback.classic.Logger;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

class TransformationImpl implements Transformation {

    private Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    private final Csar csar;
    private final Platform targetPlatform;
    private final Log log;
    private final PropertyInstance properties;
    private TransformationState state = TransformationState.CREATED;
    private TargetArtifact targetArtifact;

    /**
     * Creates a new transformation for given csar to given targetPlatform.
     *
     * @param csar           the subject of transformation
     * @param targetPlatform the target platform
     */
    TransformationImpl(Csar csar, Platform targetPlatform) {
        this.csar = csar;
        this.targetPlatform = targetPlatform;

        //Collect Possible Properties From the Platform and the Model
        Set<Property> properties = new HashSet<>();
        properties.addAll(csar.getModelSpecificProperties());
        properties.addAll(targetPlatform.getProperties());

        //Create property instance
        this.properties = new PropertyInstance(properties);

        //Check if the property list is empty
        if (!properties.isEmpty()) {
            state = TransformationState.INPUT_REQUIRED;
        }

        //intialize internal log object
        this.log = new Log();
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
     * @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the
     * transformation. Else returns null.
     */
    @Override
    public TargetArtifact getTargetArtifact() {
        return targetArtifact;
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
        return "Transformation [csarId='{}', plattformId='{}']".format(csar.getIdentifier(), targetPlatform.id);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Transformation && ((Transformation) o).getPlatform().equals(this.getPlatform())
            && ((Transformation) o).getCsar().equals(this.getCsar()));
    }
}
