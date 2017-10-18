package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PluginServiceImpl implements PluginService {
	
	@Autowired
	public List<TransformationPlugin> plugins; 
	
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
}
