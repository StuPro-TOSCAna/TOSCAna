package org.opentosca.toscana.core.util;

import org.opentosca.toscana.core.transformation.Platform;

import java.util.List;

public interface PlatformProvider {
	List<Platform> getSupportedPlatforms();
	Platform findById(String id);
}
