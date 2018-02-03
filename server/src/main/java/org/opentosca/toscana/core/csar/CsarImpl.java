package org.opentosca.toscana.core.csar;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.parse.EntrypointDetector;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.util.LifecyclePhase;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.slf4j.Logger;

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
    private final Logger logger;
    private final File rootDir;
    private final File contentDir;
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
        for (Phase phase : Phase.values()) {
            phases.add(new LifecyclePhase(phase.getName()));
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
            File entryPoint = new EntrypointDetector(this.log).findEntryPoint(this.contentDir);
            Reader.getReader().parse(Paths.get(this.contentDir.toString()), Paths.get(entryPoint.toString()));
            phase.setState(LifecyclePhase.State.DONE);
            logger.info("Template validation successful");
            return true;
        } catch (InvalidCsarException e) {
            phase.setState(LifecyclePhase.State.FAILED);
            logger.error("Template validation failed");
            return false;
        } catch (MultiException e) {
            phase.setState(LifecyclePhase.State.FAILED);
            logger.error("Template validation failed", e);
            return false;
        }
    }

    private boolean parseValidate() {
        LifecyclePhase phase = getLifecyclePhase(Csar.Phase.PARSE);
        phase.setState(LifecyclePhase.State.EXECUTING);
        logger.info("  > Constructing model from TOSCA template");
        try {
            new EffectiveModelFactory().create(this);
            phase.setState(LifecyclePhase.State.DONE);
            logger.info("Model construction successful");
            return true;
        } catch (Exception e) {
            phase.setState(LifecyclePhase.State.FAILED);
            logger.error("Model construction failed", this.identifier, e);
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
    public String toString() {
        return String.format("Csar [id: %s]", getIdentifier());
    }
}

