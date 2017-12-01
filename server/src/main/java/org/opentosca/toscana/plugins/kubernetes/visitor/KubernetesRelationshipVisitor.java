package org.opentosca.toscana.plugins.kubernetes.visitor;

import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesRelationshipVisitor implements RelationshipVisitor {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesRelationshipVisitor.class.getName());

    @Override
    public void visit(HostedOn relation) {
        logger.info("Visiting the HostedOn relationship.");
    }

    @Override
    public void visit(AttachesTo relation) {
        logger.info("Visiting the AttachesTo relationship.");
    }
}
