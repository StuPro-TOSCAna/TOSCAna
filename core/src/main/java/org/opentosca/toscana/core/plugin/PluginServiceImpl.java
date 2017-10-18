package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginServiceImpl implements PluginService {
	@Override
	public List<Platform> getSupportedPlatforms() {
		return null;
	}

	@Override
	public Platform findById(String id) {
		return null;
	}
}
