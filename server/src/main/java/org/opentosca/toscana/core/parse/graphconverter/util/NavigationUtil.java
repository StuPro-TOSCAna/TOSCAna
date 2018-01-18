package org.opentosca.toscana.core.parse.graphconverter.util;

import org.opentosca.toscana.core.parse.graphconverter.BaseEntity;
import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;

public class NavigationUtil {

    /**
     @return the MappingEntity representing the parent tosca node of given entity
     */
    public static MappingEntity getEnclosingNode(BaseEntity current) {
        BaseEntity parent = current;
        do {
            current = parent;
            parent = current.getParent();
        } while (!ToscaStructure.NODE_TEMPLATES.equals(parent.getId()));

        return (MappingEntity) current;
    }
}
