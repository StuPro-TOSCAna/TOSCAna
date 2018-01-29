package org.opentosca.toscana.core.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginServiceImpl implements PluginService {

    private final static Logger logger = LoggerFactory.getLogger(PluginServiceImpl.class);
    private final List<TOSCAnaPlugin> plugins;
    private final Set<Platform> platforms = new HashSet<>();

    @Autowired
    public PluginServiceImpl(List<TOSCAnaPlugin> plugins) {
        this.plugins = plugins;
        Map<String, TOSCAnaPlugin> pluginMap = new HashMap<>();
        for (TOSCAnaPlugin plugin : plugins) {
            if (pluginMap.get(plugin.getPlatform().id) != null) {
                logger.error("Found duplicate plugin identifier '{}'", plugin.getPlatform().id);
                throw new IllegalArgumentException("The platform id '"
                    + plugin.getPlatform().id + "' is not unique.");
            }
            pluginMap.put(plugin.getPlatform().id, plugin);
        }
        for (TOSCAnaPlugin plugin : plugins) {
            platforms.add(plugin.getPlatform());
        }
        logger.info("Loaded {} Plugins", plugins.size());
    }

    @Override
    public Set<Platform> getSupportedPlatforms() {
        return platforms;
    }

    @Override
    public Optional<Platform> findPlatformById(String id) {
        return plugins.stream()
            .filter(plugin -> plugin.getPlatform().id.equals(id))
            .map(transformationPlugin -> transformationPlugin.getPlatform())
            .findFirst();
    }

    @Override
    public List<TOSCAnaPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public boolean isSupported(Platform platform) {
        return platforms.contains(platform);
    }
}
