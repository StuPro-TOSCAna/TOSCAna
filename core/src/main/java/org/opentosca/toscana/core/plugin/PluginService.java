package org.opentosca.toscana.core.plugin;

import java.util.List;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

public interface PluginService extends PlatformService {

    List<TransformationPlugin> getPlugins();

    default TransformationPlugin findPluginByPlatform(Platform platform) {
        if (platform == null)
            return null;
        List<TransformationPlugin> p = getPlugins();
        for (TransformationPlugin e : p) {
            if (e.getPlatform().id.equals(platform.id)) {
                return e;
            }
        }
        return null;
    }
}
