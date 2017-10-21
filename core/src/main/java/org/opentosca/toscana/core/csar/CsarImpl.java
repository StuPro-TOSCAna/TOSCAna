package org.opentosca.toscana.core.csar;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.Property;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    /**
     * The root directory of the unzipped CSAR
     */
    private final File root;

    public CsarImpl(String identifier, File root) {
        this.identifier = identifier;
        this.root = root;
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
    public File getRoot() {
        return root;
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
}

