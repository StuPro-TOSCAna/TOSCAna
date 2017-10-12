package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.opentosca.toscana.core.util.PlatformProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Mock Platform provider to be used in order to test Csar Controller and Transformation Controller
 * Once integration with the rest of the core is done this will be moved in the test package
 */
public class DummyPlatformProvider implements PlatformProvider {

	private final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private List<Platform> platforms = new ArrayList<>();

	public DummyPlatformProvider() {
		ArrayList<Platform> platforms = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			HashSet<Property> properties = new HashSet<>();
			if(i == 0) {
				for (PropertyType type : PropertyType.values()) {
					properties.add(new Property(type.getTypeName()+"_property",type, RequirementType.NEVER));
				}
			}
			platforms.add(new Platform("p-" + chars[i], "platform-" + (i + 1), new HashSet<>()));
		}
		this.platforms = platforms;
	}

	public DummyPlatformProvider(List<Platform> platforms) {
		this.platforms = platforms;
	}

	@Override
	public List<Platform> getSupportedPlatforms() {
		return platforms;
	}

	@Override
	public Platform findById(String id) {
		for (Platform platform : getSupportedPlatforms()) {
			if(platform.id.equals(id)) {
				return platform;
			}
		}
		return null;
	}
}
