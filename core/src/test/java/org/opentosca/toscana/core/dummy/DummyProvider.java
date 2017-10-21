package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.status.StatusService;
import org.springframework.context.annotation.Bean;

//TODO Replace dummies with true implementations

/**
 * Provider Class to supply springs dependency Injection with dummy instances
 */
//@Configuration
public class DummyProvider {
    @Bean
    public Preferences getPreferences() {
        return null;
    }

    @Bean
    public TransformationService getTransformationService() {
        return new DummyTransformationService();
    }

    @Bean
    public CsarService getCsarService() {
        return new DummyCsarService();
    }

    @Bean
    public StatusService getSystemStatusProvider() {
        return new DummySystemStatusProvicer();
    }

    @Bean
    public PluginService getPlatformProvider() {
        return new DummyPlatformService();
    }

    @Bean
    public FileSystem getFileSystem() {
        return new FileSystemDummy();
    }
}
