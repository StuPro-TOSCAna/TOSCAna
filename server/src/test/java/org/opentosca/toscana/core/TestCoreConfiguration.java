package org.opentosca.toscana.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationFilesystemDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationServiceImpl;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactServiceImpl;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.TagStorage;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import static org.opentosca.toscana.core.testdata.TestProfiles.INTEGRATION_TEST_PROFILE;

@SuppressWarnings({"UnnecessaryLocalVariable", "Duplicates"})
@Configuration
@PropertySource("classpath:application.yml")
//Exclude Controller test profile and the integration test profile, 
//used for applications that need to launch the application with a normal context
@Profile({"!" + INTEGRATION_TEST_PROFILE})
public class TestCoreConfiguration extends CoreConfiguration {

    @Bean
    public ArtifactService artifactManagementService(TransformationDao transformationDao) {
        return new ArtifactServiceImpl(transformationDao);
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

    @Bean
    public Preferences preferences() {
        Preferences bean = new Preferences();
        return bean;
    }

    @Bean
    public CsarService csarService(CsarDao repo) {
        return new CsarServiceImpl(repo, effectiveModelFactory());
    }

    @Bean
    public PluginService dummyPluginService() {
        PluginServiceImpl bean = new PluginServiceImpl(TestPlugins.PLUGINS);
        return bean;
    }

    @Bean
    @Primary
    public TransformationDao transformationDao(PlatformService platforms) {
        TransformationFilesystemDao bean = new TransformationFilesystemDao(platforms, effectiveModelFactory());
        return bean;
    }

    @Bean
    public EffectiveModelFactory effectiveModelFactory() {
        return new EffectiveModelFactory();
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
    public TagStorage tagStorage(Preferences preferences) throws Exception {
        new File(preferences.getDataDir(), "misc").mkdirs();
        File file = new File(preferences.getDataDir(), TagStorage.DOCKER_IMAGE_TAGS);
        InputStream input = getClass().getResourceAsStream("/kubernetes/base-image-mapper/docker-tagbase.json");
        FileOutputStream out = new FileOutputStream(file);

        IOUtils.copy(input, out);
        input.close();
        out.close();

        return new TagStorage(preferences);
    }

    @Bean
    public BaseImageMapper mapper(TagStorage storage) {
        return new BaseImageMapper(DockerBaseImages.values(), storage);
    }
}
