package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.CsarParseServiceImpl;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationFilesystemDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationServiceImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.plugins.awscf.CloudFormationPlugin;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin;
import org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import java.util.Arrays;

@Configuration
@PropertySource("classpath:application.yml")
@Profile("!controller_test")
public class TestCoreConfiguration extends CoreConfiguration {
    @Bean
    public CsarDao csarDao(Preferences preferences, @Lazy TransformationDao transformationDao) {
        return new CsarFilesystemDao(preferences, transformationDao);
    }
    
    @Bean
    public PluginService pluginService() {
        return new PluginServiceImpl(Arrays.asList(
           new KubernetesPlugin(),
           new CloudFoundryPlugin(),
           new CloudFormationPlugin() 
        ));
    }
    
    @Bean
    public TestCsars testCsars(CsarDao dao) {
        TestCsars bean = new TestCsars(dao);
        return bean;
    }


    //TODO Replace with filesystem implementation
    @Bean
    public FileSystem fileSystem(Preferences preferences) {
        return new FileSystem(preferences);
    }

    @Bean
    public Preferences preferences() {
        Preferences bean = new Preferences();
        return bean;
    }

    @Bean
    public CsarParseService csarParser() {
        CsarParseServiceImpl bean = new CsarParseServiceImpl();
        return bean;
    }

    @Bean
    public CsarService csarService(CsarDao repo, CsarParseService parser) {
        return new CsarServiceImpl(repo,parser);
    }    

    @Bean
    @Primary
    @Profile("dummy_plugins")
    public PluginService dummyPluginService() {
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }

    @Bean
    @Primary
    public TransformationDao transformationDao(PlatformService platforms,@Lazy CsarDao repo) {
        TransformationFilesystemDao bean = new TransformationFilesystemDao(repo, platforms);
        return bean;
    }

    @Bean
    public TransformationService transformationService(TransformationDao repo, PluginService service) {
        TransformationServiceImpl bean = new TransformationServiceImpl(repo, service);
        return bean;
    }

}
