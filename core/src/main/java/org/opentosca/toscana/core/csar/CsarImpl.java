package org.opentosca.toscana.core.csar;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;

import java.util.*;

class CsarImpl implements Csar {

    /**
     * Stores all scheduled, ongoing or finished transformations of this CSAR.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations = new HashMap<>();
    private String identifier;
    /**
     * null if not yet parsed
     */
    private TServiceTemplate template;
    private final Log log;

    public CsarImpl(String identifier, Log log) {
        this.identifier = identifier;
        this.log = log;
    }

    @Override
    public Map<String, Transformation> getTransformations() {
        return transformations;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public TServiceTemplate getTemplate() {
        return template;
    }

    @Override
    public Set<Property> getModelSpecificProperties() {
        return new HashSet<>();
    }

    @Override
    public void setTemplate(TServiceTemplate template) {
        this.template = template;

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

    public void setTransformations(List<Transformation> transformations) {
        for (Transformation transformation : transformations) {
            this.transformations.put(transformation.getPlatform().id, transformation);
        }
    }
}

