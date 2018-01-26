package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.MysqlDbmsRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MysqlDatabase extends Database {

    public static ToscaKey<MysqlDbmsRequirement> HOST = new RequirementKey<>("host")
        .subTypes(ContainerCapability.class, MysqlDbms.class, HostedOn.class)
        .type(MysqlDbmsRequirement.class);
    

    public MysqlDatabase(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(HOST, new MysqlDbmsRequirement(getChildEntity(HOST)));
    }

    /**
     Sets {@link #HOST}
     */
    public MysqlDatabase setHost(MysqlDbmsRequirement host) {
        set(HOST, host);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
