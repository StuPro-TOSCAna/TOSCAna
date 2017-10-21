package org.opentosca.toscana.core.plugin;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.toscana.core.dummy.DummyPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

public class PluginServiceTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    private PluginService service;

    @Before
    public void setUp() throws Exception {
        log.info("Creating dummy plugins");
        ArrayList<TransformationPlugin> plugins = new ArrayList<>();
        addDummiesToList(plugins);
        log.info("Creating service");
        service = new PluginServiceImpl(plugins);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExistingPluginIdentifier() throws Exception {
        log.info("Discarding old data");
        log.info("Creating dummy plugins");
        ArrayList<TransformationPlugin> plugins = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            addDummiesToList(plugins);
        }
        log.info("Creating service");
        service = new PluginServiceImpl(plugins);
    }

    private void addDummiesToList(ArrayList<TransformationPlugin> plugins) {
        for (int i = 0; i < 10; i++) {
            StringBuilder name = new StringBuilder("platform-a");
            for (int j = 0; j < i; j++) {
                name.append("a");
            }
            plugins.add(new DummyPlugin(new Platform(name.toString(), name.toString(), new HashSet<>())));
        }
    }

    @Test
    public void listPlugins() throws Exception {
        assertTrue(service.getSupportedPlatforms().size() == 10);
    }

    @Test
    public void listPlatforms() throws Exception {
        assertTrue(service.getPlugins().size() == 10);
    }

    @Test
    public void searchExistingPlatform() throws Exception {
        assertTrue(service.findById("platform-a") != null);
    }

    @Test
    public void searchNonExistingPlatform() throws Exception {
        assertTrue(service.findById("platform-z") == null);
    }

    @Test
    public void findPluginByPlatform() throws Exception {
        assertTrue(service.findPluginByPlatform(service.findById("platform-a")) != null);
    }

    @Test
    public void findByPluginNull() throws Exception {
        assertTrue(service.findPluginByPlatform(null) == null);
    }

    @Test
    public void findPluginByInvalidPlatform() throws Exception {
        assertTrue(service.findPluginByPlatform(service.findById("platform-s")) == null);
    }
}
