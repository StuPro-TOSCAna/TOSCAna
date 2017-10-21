package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Mock Platform provider to be used in order to test Csar Controller and Transformation Controller
 * Once integration with the rest of the core is done this will be moved in the test package
 */
public class DummyPlatformService implements PluginService {

	private final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private List<Platform> platforms = new ArrayList<>();
	private List<TransformationPlugin> plugins = new ArrayList<>();

	public DummyPlatformService() {
		ArrayList<Platform> platforms = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			HashSet<Property> properties = new HashSet<>();
			for (PropertyType type : PropertyType.values()) {
				properties.add(new Property(type.getTypeName() + "_property", type, RequirementType.NEVER));
			}
			platforms.add(new Platform("p-" + chars[i], "platform-" + (i + 1), properties));
		}
		this.platforms = platforms;
        for (Platform platform : this.platforms) {
            plugins.add(new DummyPlugin(platform));
        }

    }

	public DummyPlatformService(List<Platform> platforms) {
		this.platforms = platforms;
	}

	@Override
	public List<Platform> getSupportedPlatforms() {
		return platforms;
	}

	@Override
	public Platform findById(String id) {
		for (Platform platform : getSupportedPlatforms()) {
			if (platform.id.equals(id)) {
				return platform;
			}
		}
		return null;
	}

    @Override
    public List<TransformationPlugin> getPlugins() {
        return plugins;
    }
    
}
