package org.opentosca.toscana.core;

import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationFilesystemDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationServiceImpl;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:application.yml")
public class TestCoreConfiguration extends CoreConfiguration {

    @Bean
    public TestCsars testCsars() {
        TestCsars bean = new TestCsars(csarDao());
        return bean;
    }

    @Bean
    @Profile(value = TestProfiles.DUMMY_PLUGIN_SERVICE_TEST)
    @Primary
    public PlatformService platformService() {
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }
    
    @Bean
    @Profile(value = TestProfiles.DUMMY_PLUGIN_SERVICE_TEST)
    @Primary
    public PluginService pluginService() {
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }

    @Profile(value = TestProfiles.DUMMY_PLUGIN_SERVICE_TEST)
    @Bean
    public TransformationDao transformationDao() {
        TransformationFilesystemDao bean = new TransformationFilesystemDao(csarDao(), platformService());
        return bean;
    }

    @Profile(value = TestProfiles.DUMMY_PLUGIN_SERVICE_TEST)
    @Bean
    public TransformationService transformationService() {
        TransformationServiceImpl bean = new TransformationServiceImpl(transformationDao(), pluginService());
        return bean;
    }
}
