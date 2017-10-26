package org.opentosca.toscana.plugins.k8s;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.testdata.TestContext;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin;
import org.opentosca.toscana.plugins.model.InvalidDockerAppException;

import org.assertj.core.util.Lists;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.opentosca.toscana.core.testdata.TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK;

public class K8sPluginTest extends BaseSpringTest {
    private static final Logger log = LoggerFactory.getLogger(K8sPluginTest.class);

    static TServiceTemplate tServiceTemplate;
    static KubernetesPlugin plugin;
    @Autowired
    CsarParseService csarParser;
    @Autowired
    Preferences preferences;
    @Autowired
    TestContext testContext;
    @Autowired
    private TestCsars testCsars;

    @Before
    public void setUp() {

        plugin = new KubernetesPlugin();
    }

    @Test
    public void validTSerciceTemplate() throws InvalidDockerAppException, FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        tServiceTemplate = csarParser.parse(csar);
        plugin.getDockerApp(tServiceTemplate);
    }

    @Test(expected = InvalidDockerAppException.class)
    public void dockerAppMissing() throws FileNotFoundException, InvalidCsarException, InvalidDockerAppException {
        Csar csar = testCsars.getCsar(TestCsars.CSAR_YAML_INVALID_DOCKERAPP_MISSING);
        tServiceTemplate = csarParser.parse(csar);
        plugin.getDockerApp(tServiceTemplate);
    }

    @Test
    public void transformTest() throws Exception {
        //TODO refactor this monster
        TransformationContext transformationContext = testContext.getContext(CSAR_YAML_VALID_DOCKER_SIMPLETASK, new Platform("test", "bla"));
        List<String> expectedTransformationRoot = Lists.newArrayList("simple-task-app","simple-task-app_resource.yaml","Readme.md");
        assertNotNull(transformationContext);
        plugin.transform(transformationContext);
        File transformationFileRootPath = new File(preferences.getDataDir(), "simple-taskcsar/transformations/test/");
        List<String> result = new ArrayList<>();
        for (String s : transformationFileRootPath.list()) {
            result.add(s);
        }
        assertTrue(expectedTransformationRoot.containsAll(result));
        
        List<String> expectetDockerPathFiles = Lists.newArrayList("index.php", "mysql-credentials.php", "createdb.sql", "Dockerfile");
        File transformationDockerFilesPath = new File(preferences.getDataDir(), "simple-taskcsar/transformations/test/simple-task-app/");
        result.clear();
        for (String s : transformationDockerFilesPath.list()) {
            result.add(s);
        }
        assertTrue(expectetDockerPathFiles.containsAll(result));
    }
}
