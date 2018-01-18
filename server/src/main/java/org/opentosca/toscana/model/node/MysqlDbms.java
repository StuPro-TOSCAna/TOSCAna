package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 A MysqlDbms database (server)
 TOSCA Simple Profile in YAML Version 1.1, p. 221)
 */
@EqualsAndHashCode
@ToString
public class MysqlDbms extends Dbms {

    public MysqlDbms(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        ContainerCapability containerHost = getContainerHost();
        Set<Class<? extends RootNode>> validSourceTypes = containerHost.getValidSourceTypes();
        if (validSourceTypes.isEmpty()) {
            containerHost.getValidSourceTypes().add(MysqlDatabase.class);
        }
        setDefault(PORT, getPort().orElse(3306));
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
