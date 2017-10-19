package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PluginServiceImpl implements PluginService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final List<TransformationPlugin> plugins;

	@Autowired
	public PluginServiceImpl(List<TransformationPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	public List<Platform> getSupportedPlatforms() {
		ArrayList<Platform> platforms = new ArrayList<>();
		for (TransformationPlugin plugin : plugins) {
			platforms.add(plugin.getPlatformDetails());
		}
		return platforms;
	}

	@Override
	public Platform findById(String id) {
		Platform p = null;
		for (TransformationPlugin plugin : plugins) {
			if(id.equals(plugin.getPlatformDetails().id)) {
				p = plugin.getPlatformDetails();
			}
		}
		return p;
	}

	@Override
	public List<TransformationPlugin> getPlugins() {
		return plugins;
	}
}
