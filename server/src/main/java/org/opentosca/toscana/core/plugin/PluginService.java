package org.opentosca.toscana.core.plugin;

import java.util.List;

import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

public interface PluginService extends PlatformService {

    List<ToscanaPlugin> getPlugins();

    default ToscanaPlugin findPluginByPlatform(Platform platform) {
        if (platform == null)
            return null;
        List<ToscanaPlugin> p = getPlugins();
        for (ToscanaPlugin e : p) {
            if (e.getPlatform().id.equals(platform.id)) {
                return e;
            }
        }
        return null;
    }
}
