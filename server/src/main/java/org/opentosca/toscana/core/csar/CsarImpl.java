package org.opentosca.toscana.core.csar;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.slf4j.Logger;

public class CsarImpl implements Csar {

    /**
     Stores all scheduled, ongoing or finished transformations of this CSAR. Key is the platform identifier.
     */
    private final Map<String, Transformation> transformations = new HashMap<>();
    private final String identifier;
    /**
     null if not yet parsed
     */
    private final Log log;
    private final Logger logger;
    private final File rootDir;
    private final File contentDir;
    private File template;
    private final List<LifecyclePhase> lifecyclePhases;

    public CsarImpl(File rootDir, String identifier, Log log) {
        this.rootDir = rootDir;
        this.contentDir = new File(rootDir, CONTENT_DIR);
        this.identifier = identifier;
        this.log = log;
        this.logger = log.getLogger(getClass());
        lifecyclePhases = initLifecyclePhases();
    }

    private List<LifecyclePhase> initLifecyclePhases() {
        List<LifecyclePhase> phases = new ArrayList<>();
        for (Phase phaseName : Phase.values()) {
            LifecyclePhase phase = new LifecyclePhase(phaseName.getName(), this, log.getLogger(LifecyclePhase.class));
            phases.add(phase);
        }
        return phases;
    }

    // this call is expensive, use with care
    @Override
    public boolean validate() {
        return wineryValidate() && parseValidate();
    }

    private boolean wineryValidate() {
        LifecyclePhase phase = getLifecyclePhase(Csar.Phase.VALIDATE);
        phase.setState(LifecyclePhase.State.EXECUTING);
        logger.info("Validating csar '{}'", identifier);
        logger.debug("  > Validating TOSCA template", identifier);
        try {
            Reader.getReader().parse(Paths.get(this.contentDir.toString()), Paths.get(getTemplate().toString()));
            logger.info("Template validation successful");
            phase.setState(LifecyclePhase.State.DONE);
            return true;
        } catch (InvalidCsarException e) {
            logger.error("Template validation failed");
            phase.setState(LifecyclePhase.State.FAILED);
            return false;
        } catch (MultiException e) {
            logger.error("Template validation failed", e);
            phase.setState(LifecyclePhase.State.FAILED);
            return false;
        }
    }

    private boolean parseValidate() {
        LifecyclePhase phase = getLifecyclePhase(Csar.Phase.PARSE);
        phase.setState(LifecyclePhase.State.EXECUTING);
        logger.info("  > Constructing model from TOSCA template");
        try {
            new EffectiveModelFactory().create(this);
            logger.info("Model construction successful");
            phase.setState(LifecyclePhase.State.DONE);
            return true;
        } catch (Exception e) {
            logger.error("Model construction failed", this.identifier, e);
            phase.setState(LifecyclePhase.State.FAILED);
            return false;
        }
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
    public List<LifecyclePhase> getLifecyclePhases() {
        return lifecyclePhases;
    }

    @Override
    public LifecyclePhase getLifecyclePhase(Phase phase) {
        String phaseName = phase.getName();
        return getLifecyclePhases().stream().filter(p -> p.getName().equals(phaseName)).findFirst().orElseThrow(()
            -> new IllegalArgumentException(String.format("Csar does not have a lifecycle phase called %s", phaseName)));
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
    public File getTemplate() throws InvalidCsarException {
        if (template == null) {
            template = new EntrypointDetector(log).findEntryPoint(contentDir);
        }
        return template;
    }

    @Override
    public String toString() {
        return String.format("Csar [id: %s]", getIdentifier());
    }
}

