package org.opentosca.toscana.core.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.OutputProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import static java.lang.String.format;

public class TransformationImpl implements Transformation {

    private final Csar csar;
    private final Platform targetPlatform;
    private final Log log;
    private final PropertyInstance inputs;
    private final List<OutputProperty> outputs;
    private final EffectiveModel model;
    private TransformationState state = TransformationState.READY;
    private TargetArtifact targetArtifact;
    private List<? extends LifecyclePhase> lifecyclePhases = new ArrayList<>();

    /**
     Creates a new transformation for given csar to given targetPlatform.

     @param csar           the subject of transformation
     @param targetPlatform the target platform
     */
    public TransformationImpl(Csar csar, Platform targetPlatform, Log log, EffectiveModel model) {
        this.csar = csar;
        this.targetPlatform = targetPlatform;
        this.log = log;
        this.model = model;
        Set<InputProperty> properties = new HashSet<>();
        properties.addAll(model.getInputs().values());
        properties.addAll(targetPlatform.getProperties());
        // caution: side effect
        // transformationState can get set to INPUT_REQUIRED by this call
        this.inputs = new PropertyInstance(properties, this);
        this.outputs = Collections.unmodifiableList(new ArrayList<>(model.getOutputs().values()));
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
    public EffectiveModel getModel() {
        return model;
    }

    @Override
    public List<OutputProperty> getOutputs() throws IllegalStateException {
        return outputs;
    }

    @Override
    public void setLifecyclePhases(List<? extends LifecyclePhase> lifecyclePhases) {
        this.lifecyclePhases = lifecyclePhases;
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
    public PropertyInstance getInputs() {
        return inputs;
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

    @Override
    public List<? extends LifecyclePhase> getLifecyclePhases() {
        return lifecyclePhases;
    }
}
