package org.opentosca.toscana.core.api.dummy;

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
public class DummyPlatformService implements PlatformService {

	private final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private List<Platform> platforms = new ArrayList<>();

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
}
