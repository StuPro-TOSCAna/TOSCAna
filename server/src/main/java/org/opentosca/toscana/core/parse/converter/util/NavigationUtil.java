package org.opentosca.toscana.core.parse.converter.util;

import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;

public class NavigationUtil {

    /**
     @return the MappingEntity representing the parent tosca node of given entity
     */
    public static MappingEntity getEnclosingNode(Entity current) {
        Entity parent = current;
        do {
            current = parent;
            parent = current.getParent();
        } while (!ToscaStructure.NODE_TEMPLATES.equals(parent.getId()));

        return (MappingEntity) current;
    }
}
