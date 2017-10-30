package org.opentosca.toscana.core.dummy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

/**
 Mock Platform provider to be used in order to test Csar Controller and Transformation Controller Once integration
 with the rest of the core is done this will be moved in the test package
 */
public class DummyPlatformService implements PluginService {

    private Set<Platform> platforms = new HashSet<>();
    private final List<TransformationPlugin> plugins = new ArrayList<>();

    public DummyPlatformService() {
        Set<Platform> platforms = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            HashSet<Property> properties = new HashSet<>();
            for (PropertyType type : PropertyType.values()) {
                properties.add(new Property(type.getTypeName() + "_property", type));
            }
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            platforms.add(new Platform("p-" + chars[i], "platform-" + (i + 1), properties));
        }
        this.platforms = platforms;
        for (Platform platform : this.platforms) {
            plugins.add(new DummyPlugin(platform));
        }
    }

    public DummyPlatformService(Set<Platform> platforms) {
        this.platforms = platforms;
    }

    @Override
    public Set<Platform> getSupportedPlatforms() {
        return platforms;
    }

    @Override
    public Optional<Platform> findPlatformById(String id) {
        return getSupportedPlatforms().stream()
            .filter(platform -> platform.id.equals(id))
            .findFirst();
    }

    @Override
    public boolean isSupported(Platform platform) {
        return platforms.contains(platform);
    }

    @Override
    public List<TransformationPlugin> getPlugins() {
        return plugins;
    }
}
