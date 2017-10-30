package org.opentosca.toscana.plugins.model;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.junit.Before;
import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class DockerAppTest extends BaseSpringTest {
    static TServiceTemplate tServiceTemplate;
    static KubernetesPlugin plugin;
    @Autowired
    CsarParseService csarParser;
    @Autowired
    private TestCsars testCsars;

    @Before
    public void setUp() {
        plugin = new KubernetesPlugin();
    }

    @Test
    public void validNodeTemplate() throws InvalidDockerAppException, FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        tServiceTemplate = csarParser.parse(csar);
        DockerApp dockerApp = new DockerApp(tServiceTemplate.getTopologyTemplate().getNodeTemplates().get("simpleTaskApp"));
        dockerApp.parseNodeTemplate();
        String[] validTag = new String[]{"tag for docker image", "simple-task-app"};
        String[] validIdentifier = new String[]{"identifier of docker container", "simple-task-app-1"};
        List<String> dependencies = new LinkedList<>();
        dependencies.add("simple-task-app/Dockerfile");
        dependencies.add("simple-task-app/createdb.sql");
        dependencies.add("simple-task-app/index.php");
        dependencies.add("simple-task-app/mysql-credentials.php");

        assertArrayEquals(validTag, dockerApp.getTag());
        assertArrayEquals(validIdentifier, dockerApp.getIdentifier());
        assertArrayEquals(dependencies.toArray(), dockerApp.getDependencies().toArray());
    }

    @Test(expected = InvalidDockerAppException.class)
    public void missingDependencies() throws FileNotFoundException, InvalidCsarException, InvalidDockerAppException {
        Csar csar = testCsars.getCsar(TestCsars.CSAR_YAML_INVALID_DEPENDENCIES_MISSING);
        tServiceTemplate = csarParser.parse(csar);
        DockerApp dockerApp = new DockerApp(tServiceTemplate.getTopologyTemplate().getNodeTemplates().get("simpleTaskApp"));
        dockerApp.parseNodeTemplate();
    }

    // TODO add test for missing attributes

}
