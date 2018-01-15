package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.ServiceGraph;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.capability.OsCapability;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.model.capability.OsCapability.Type;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperUtils.anythingSet;

public class MapperUtilsTest extends BaseUnitTest {

    private static EntityId entityId = new EntityId(Lists.newArrayList("my", "id"));
    private static MappingEntity entity;

    @Before
    public void setUp() {
        ServiceGraph graph = new ServiceGraph();
        entity = new MappingEntity(entityId, graph);
        graph.addEntity(entity);
    }

    @Test
    public void testNoneSet() throws Exception {
        assertFalse(anythingSet(new OsCapability(entity)));
    }

    @Test
    public void testOneSet() throws Exception {
        assertTrue(anythingSet(new OsCapability(entity).setType(Type.LINUX)));
    }
}
