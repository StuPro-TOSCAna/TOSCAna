package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;

import java.util.Map;
import java.util.Set;

class TransformationImpl implements Transformation {

    private Csar app;
    private TransformationState state;
    private Platform targetPlatform;
    private Set<Property> properties;
    private Log log;
    private Set<TransformationListener> listeners;
    private TargetArtifact targetArtifact;

    /**
     * Creates a new transformation for given app to given targetPlatform.
     * @param csar the subject of transformation
     * @param targetPlatform the target platform
     * @param properties the user-supplied properties for this transformation
     */
    public TransformationImpl(Csar csar, Platform targetPlatform, Set<Property> properties) {
        this.app = app;
        this.targetPlatform = targetPlatform;
        this.properties = properties;
        this.log = new Log();
        // TODO
    }

    public void setProperties(Set<Property> properties){
        // TODO maybe this needs to become setProperty(Property property)
        this.properties = properties;
    }

    @Override
    public TransformationState getState(){
        return state;
    }


    @Override
    public void setOnStateChange(TransformationListener listener){
        // TODO
        // hint: use IdentityHashMap for Listeners
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeOnStateChange(TransformationListener listener){
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Log getLog(){
        // TODO
        throw new UnsupportedOperationException();
    }


    /**
     * @return if this transformation object's state is <code>DONE</code>, returns the target artifact of the transformation.
     * Else returns null.
     */
    public TargetArtifact getTargetArtifact(){
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Platform getPlatform(){
        // TODO
        throw new UnsupportedOperationException();
    }

	@Override
	public void setProperty(String key, String value) {
		
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}
}
