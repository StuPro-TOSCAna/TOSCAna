package org.opentosca.toscana.core;

import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class TestCoreConfiguration extends CoreConfiguration {

    @Bean
    public TestCsars testCsars() {
        TestCsars bean = new TestCsars(csarDao());
        return bean;
    }
    
    @Bean
    public PlatformService platformService(){
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }
}
