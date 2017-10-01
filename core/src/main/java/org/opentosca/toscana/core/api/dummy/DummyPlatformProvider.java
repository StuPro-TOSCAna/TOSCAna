package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.util.PlatformProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class DummyPlatformProvider implements PlatformProvider {

	private final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	@Override
	public List<Platform> getSupportedPlatforms() {
		ArrayList<Platform> platforms = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			platforms.add(new Platform("p-" + chars[i], "platform-" + (i + 1), new HashSet<>()));
		}

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
