package org.opentosca.toscana.core;

import java.util.List;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.CsarParseServiceImpl;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.testdata.TestTransformationContext;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationFilesystemDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationServiceImpl;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactServiceImpl;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.Preferences;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@Profile("!controller_test")
public class TestCoreConfiguration extends CoreConfiguration {

    private List<TransformationPlugin> plugins;

    public TestCoreConfiguration() {
        this(TestPlugins.PLUGINS);
    }

    public TestCoreConfiguration(List<TransformationPlugin> plugins) {
        this.plugins = plugins;
    }

    @Bean
    public ArtifactService artifactManagementService(Preferences preferences, TransformationDao transformationDao) {
        return new ArtifactServiceImpl(preferences, transformationDao);
    }

    @Bean
    public CsarDao csarDao(Preferences preferences, @Lazy TransformationDao transformationDao) {
        return new CsarFilesystemDao(preferences, transformationDao);
    }

    @Bean
    public TestCsars testCsars() {
        TestCsars bean = new TestCsars();
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
        return new CsarServiceImpl(repo, parser);
    }

    @Bean
    public PluginService dummyPluginService() {
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }

    @Bean
    @Primary
    public TransformationDao transformationDao(PlatformService platforms) {
        TransformationFilesystemDao bean = new TransformationFilesystemDao(platforms);
        return bean;
    }

    @Bean
    public TransformationService transformationService(
        TransformationDao repo,
        @Lazy CsarDao csarDao,
        PluginService service,
        ArtifactService ams
    ) {
        TransformationServiceImpl bean = new TransformationServiceImpl(repo, service, csarDao, ams);
        return bean;
    }

    @Bean
    public TestTransformationContext testContext() {
        TestTransformationContext bean = new TestTransformationContext();
        return bean;
    }
}
