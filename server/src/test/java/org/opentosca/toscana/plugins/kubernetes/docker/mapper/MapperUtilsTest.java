package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import org.opentosca.toscana.core.BaseUnitTest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.model.capability.OsCapability.Type;
import static org.opentosca.toscana.model.capability.OsCapability.builder;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperUtils.anythingSet;

public class MapperUtilsTest extends BaseUnitTest {

    @Test
    public void testNoneSet() throws Exception {
        assertFalse(anythingSet(builder().build()));
    }

    @Test
    public void testOneSet() throws Exception {
        assertTrue(anythingSet(builder().type(Type.LINUX).build()));
    }
}
