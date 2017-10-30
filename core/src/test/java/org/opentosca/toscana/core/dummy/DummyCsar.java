package org.opentosca.toscana.core.dummy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public Optional<Transformation> getTransformation(String platformId) {
        return Optional.ofNullable(transformations.get(platformId));
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public Optional<TServiceTemplate> getTemplate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Property> getModelSpecificProperties() {
        return modelSpecificProperties;
    }

    @Override
    public void setTemplate(TServiceTemplate template) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Log getLog() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTransformations(List<Transformation> transformations) {
        throw new UnsupportedOperationException();
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
