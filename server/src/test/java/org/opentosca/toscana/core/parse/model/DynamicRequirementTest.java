package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.TestTemplates;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 Tests accessing dynamic requirements (requirements, which are not specified in the type
 */
public class DynamicRequirementTest extends BaseUnitTest {

    @Test
    public void dynamicRequirementTest() {
        EffectiveModel model = new EffectiveModelFactory().create(TestTemplates.Requirements.DYNAMIC_REQUIREMENT, logMock());
        WebApplication app = (WebApplication) model.getNodes().iterator().next();
        Requirement<? extends Capability, ? extends RootNode, ? extends RootRelationship> dynamicRequirement = app.getRequirements().stream()
            .filter(r -> "dynamic-requirement".equals(r.getEntityName()))
            .findFirst().orElseThrow(() -> new IllegalStateException("dynamic requirement should exist"));
        RootRelationship relationship = dynamicRequirement.getRelationship().get();
        assertEquals(ConnectsTo.class, relationship.getClass());
        Capability capability = dynamicRequirement.get(dynamicRequirement.CAPABILITY);
        assertEquals(EndpointCapability.class, capability.getClass());
        RootNode fulfiller = dynamicRequirement.getFulfillers().iterator().next();
        assertEquals(app, fulfiller);
    }
}
