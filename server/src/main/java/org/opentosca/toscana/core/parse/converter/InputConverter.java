package org.opentosca.toscana.core.parse.converter;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.eclipse.winery.model.tosca.yaml.TParameterDefinition;
import org.eclipse.winery.yaml.common.Namespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Converts inputs declared in the TTopologyTemplate to corresponding {@link Property} objects
 */
public class InputConverter {

    private final static Logger logger = LoggerFactory.getLogger(InputConverter.class.getName());

    public Set<Property> convert(Map<String, TParameterDefinition> inputs) {
        return inputs.entrySet()
            .stream()
            .map(this::convert)
            .collect(Collectors.toSet());
    }

    private Property convert(Map.Entry<String, TParameterDefinition> propertyEntry) {
        String name = propertyEntry.getKey();
        TParameterDefinition parameter = propertyEntry.getValue();
        PropertyType type = convertType(parameter.getType());
        System.out.println("PROPERTY TYPE: '" + parameter.getType() + "'");
        Property property = new Property(name, type, parameter.getDescription(), parameter.getRequired());
        return property;
    }

    private PropertyType convertType(QName type) {
        if  (type == null){
            return PropertyType.TEXT;
        }
        switch(type.getNamespaceURI()){
            case Namespaces.YAML_NS:
                return convertYamlType(type.getLocalPart());
            default:
                throw new UnsupportedOperationException(
                    String.format("The type '%s' is not supported as input parameter type", type));
        }
    }

    private PropertyType convertYamlType(String localPart) {
        switch (localPart){
            case "string":
                return PropertyType.TEXT;
            case "integer":
                return PropertyType.INTEGER;
            case "float":
                return PropertyType.FLOAT;
            case "boolean":
                return PropertyType.BOOLEAN;
            default:
                throw new UnsupportedOperationException(String.format("'%s' is not a supported yaml type", localPart));
        }
    }
}
