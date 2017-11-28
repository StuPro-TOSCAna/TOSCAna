// TODO might get deleted soon: TransformationContext no longer provides TServiceTemplate
//package org.opentosca.toscana.plugins.kubernetes;

//import java.io.BufferedWriter;
//import java.util.List;

//import org.opentosca.toscana.core.BaseSpringTest;
//import org.opentosca.toscana.core.csar.Csar;
//import org.opentosca.toscana.core.parse.CsarParseService;
//import org.opentosca.toscana.core.plugin.PluginFileAccess;
//import org.opentosca.toscana.core.testdata.TestCsars;
//import org.opentosca.toscana.core.transformation.TransformationContext;

//import org.assertj.core.util.Lists;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;

//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.opentosca.toscana.core.testdata.TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK;

//public class KubernetesPluginTest extends BaseSpringTest {

//    private static KubernetesPlugin plugin;
//    @Autowired
//    TestCsars testCsars;

//    @Autowired
//    CsarParseService csarParseService;

//    @Before
//    public void setUp() {
//        plugin = new KubernetesPlugin();
//    }

//    @Test
//    public void transformationMockTest() throws Exception {
//        Csar csar = testCsars.getCsar(CSAR_YAML_VALID_DOCKER_SIMPLETASK);
//        TransformationContext context = mock(TransformationContext.class);
//        PluginFileAccess pluginFileAccess = mock(PluginFileAccess.class);
//        when(context.getPluginFileAccess()).thenReturn(pluginFileAccess);
//        when(context.getModel()).thenReturn(csarParseService.parse(csar));
//        BufferedWriter mock = mock(BufferedWriter.class);
//        when(pluginFileAccess.access(any(String.class))).thenReturn(mock);
//        when(mock.append(any(String.class))).thenReturn(mock);
//        plugin.transform(context);
//        verify(pluginFileAccess).access("/Readme.md");
//        verify(pluginFileAccess).access("/simple-task-app_resource.yaml");
//        List<String> expectedDockerPathFiles = Lists.newArrayList("index.php", "mysql-credentials.php", "createdb.sql", "Dockerfile");
//        for (String s : expectedDockerPathFiles) {
//            verify(pluginFileAccess).copy("simple-task-app/" + s);
//        }
//    }
//}
