package org.opentosca.toscana.core.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginServiceImpl implements PluginService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final List<TransformationPlugin> plugins;
    private final Set<Platform> platforms = new HashSet<>();

    @Autowired
    public PluginServiceImpl(List<TransformationPlugin> plugins) {
        this.plugins = plugins;
        Map<String, TransformationPlugin> pluginMap = new HashMap<>();
        for (TransformationPlugin plugin : plugins) {
            if (pluginMap.get(plugin.getPlatform().id) != null) {
                log.error("Found duplicate plugin identifier '{}'", plugin.getPlatform().id);
                throw new IllegalArgumentException("The platform id '"
                    + plugin.getPlatform().id + "' is not unique.");
            }
            pluginMap.put(plugin.getPlatform().id, plugin);
        }
        for (TransformationPlugin plugin : plugins) {
            platforms.add(plugin.getPlatform());
        }
        log.info("Loaded {} Plugins", plugins.size());
    }

    @Override
    public Set<Platform> getSupportedPlatforms() {
        return platforms;
    }

    @Override
    public Platform findPlatformById(String id) {
        Platform p = null;
        for (TransformationPlugin plugin : plugins) {
            if (id.equals(plugin.getPlatform().id)) {
                p = plugin.getPlatform();
            }
        }
        return p;
    }

    @Override
    public List<TransformationPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public boolean isSupported(Platform platform) {
        return platforms.contains(platform);
    }
}
