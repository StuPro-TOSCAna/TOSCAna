package org.opentosca.toscana.core.dummy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

public class DummyCsar implements Csar {

    public Set<Property> modelSpecificProperties = new HashSet<>();
    
    private final String name;
    private byte[] data;

    private final Map<String, Transformation> transformations = new HashMap<>();

    public DummyCsar(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Transformation> getTransformations() {
        return transformations;
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public TServiceTemplate getTemplate() {
        return null;
    }

    @Override
    public Set<Property> getModelSpecificProperties() {
        return modelSpecificProperties;
    }

    @Override
    public void setTemplate(TServiceTemplate template) {
        //noop
    }

    @Override
    public Log getLog() {
        return null;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Csar && getIdentifier().equals(((Csar) o).getIdentifier());
    }
}
