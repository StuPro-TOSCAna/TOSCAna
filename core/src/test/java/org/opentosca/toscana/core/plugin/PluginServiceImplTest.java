package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PluginServiceImplTest extends BaseSpringTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PluginService service;

    @Test
    public void listPlugins() throws Exception {
        assertEquals(TestPlugins.PLATFORMS.size(), service.getSupportedPlatforms().size());
    }

    @Test
    public void listPlatforms() throws Exception {
        assertEquals(TestPlugins.PLUGINS.size(), service.getPlugins().size());
    }

    @Test
    public void searchExistingPlatform() throws Exception {
        assertNotNull(service.findPlatformById(TestPlugins.PLATFORM1.id));
    }

    @Test
    public void searchNonExistingPlatform() throws Exception {
        assertNull(service.findPlatformById(TestPlugins.PLATFORM_NOT_SUPPORTED.id));
    }

    @Test
    public void findPluginByPlatform() throws Exception {
        assertNotNull(service.findPluginByPlatform(service.findPlatformById(TestPlugins.PLATFORM2.id)));
    }

    @Test
    public void findByPluginNull() throws Exception {
        assertNull(service.findPluginByPlatform(null));
    }

    @Test
    public void findPluginByInvalidPlatform() throws Exception {
        assertNull(service.findPluginByPlatform(service.findPlatformById(TestPlugins.PLATFORM_NOT_SUPPORTED.id)));
    }

    @Test
    public void isSupportedPlattform() {
        for (Platform platform : TestPlugins.PLATFORMS) {
            assertTrue(service.isSupported(platform));
        }
    }
}
