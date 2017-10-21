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

    private Csar csar;
    private TransformationState state = TransformationState.CREATED;
    private Platform targetPlatform;
    private PropertyInstance properties;
    private Log log;
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

//    @Override
//    public Logger getTransformationLogger(Class<?> clazz) {
////        Logger tLog = (Logger) LoggerFactory.getLogger(clazz);
////
////        TransformationAppender appender = new TransformationAppender(log);
////        appender.setContext(tLog.getLoggerContext());
////        appender.start();
////        tLog.addAppender(appender);
////
////        //TODO Add File Appender once mechanism for getting the filepath has been implemented.
////
////        return tLog;
//    }

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
     * @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the transformation.
     * Else returns null.
     */
    public TargetArtifact getTargetArtifact() {
        return targetArtifact;
    }

    @Override
    public Platform getPlatform() {
        return targetPlatform;
    }

    @Override
    public PropertyInstance getProperties() {
        return properties;
    }

}
