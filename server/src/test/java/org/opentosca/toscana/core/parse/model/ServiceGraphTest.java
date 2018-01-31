package org.opentosca.toscana.core.parse.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EntityId;

import com.google.common.collect.Lists;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.CAPABILITY;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.CREDENTIAL;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.INPUT;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.INPUT_NO_VALUE;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.INTERFACE;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.NODE;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.OUTPUT;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.REPOSITORY;
import static org.opentosca.toscana.core.parse.TestTemplates.ToscaElements.REQUIREMENT;

public class ServiceGraphTest extends BaseUnitTest {

    private final static Map<File, ServiceGraph> graphMap = new HashMap<>();
    private File currentFile;

    @Test
    public void repositoryTest() {
        currentFile = REPOSITORY;
        assertEquals("https://first-url.com/", get("repositories", "first_repo", "url"));
        assertEquals("test-description", get("repositories", "second_repo", "description"));
        assertEquals("https://second-url.com/", get("repositories", "second_repo", "url"));
        assertEquals("test-token", get("repositories", "second_repo", "credential", "token"));
    }

    @Test
    public void credentialTest() {
        currentFile = CREDENTIAL;
        assertEquals("test-protocol", get("repositories", "first_repo", "credential", "protocol"));
        assertEquals("test-token_type", get("repositories", "first_repo", "credential", "token_type"));
        assertEquals("test-token", get("repositories", "first_repo", "credential", "token"));
        assertEquals("test-user", get("repositories", "first_repo", "credential", "user"));
        assertEquals("value1", get("repositories", "first_repo", "credential", "keys", "key1"));
    }

    @Test
    public void inputTest() {
        currentFile = INPUT;
        assertEquals("string", get("topology_template", "inputs", "test_input", "type"));
        assertEquals("test-value", get("topology_template", "inputs", "test_input", "value"));
        assertEquals("test-description", get("topology_template", "inputs", "test_input", "description"));
        assertEquals("true", get("topology_template", "inputs", "test_input", "required"));
        assertEquals("test-default", get("topology_template", "inputs", "test_input", "default"));
    }

    @Test
    public void outputTest() {
        currentFile = OUTPUT;
        assertEquals("string", get("topology_template", "outputs", "test_output", "type"));
        assertEquals("test-value", get("topology_template", "outputs", "test_output", "value"));
        assertEquals("test-description", get("topology_template", "outputs", "test_output", "description"));
        assertEquals("true", get("topology_template", "outputs", "test_output", "required"));
        assertEquals("test-default", get("topology_template", "outputs", "test_output", "default"));
    }

    @Test
    public void interfaceTest() {
        currentFile = INTERFACE;
        assertEquals("test-value1", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface1", "inputs", "test-input1"));
        assertEquals("test-description", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface1", "test-operation", "description"));
        assertEquals("test-description", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface1", "test-operation", "description"));
        assertEquals("test-implementation1", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface1", "test-operation", "implementation", "primary", "file"));
        assertEquals("test-value2", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface1", "test-operation", "inputs", "test-input2"));

        assertEquals("test-implementation2", get("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface2", "test-operation", "implementation", "primary", "file"));
        assertEquals(Lists.newArrayList("test-dependency1", "test-dependency2"), getList("topology_template", "node_templates", "test-node", "interfaces",
            "test-interface2", "test-operation", "implementation", "dependencies"));
    }

    @Test
    public void capabilityTest() {
        currentFile = CAPABILITY;
        assertEquals("test-property-value", get("topology_template", "node_templates", "test-node",
            "capabilities", "test-capability", "properties", "test-property-key"));
        assertEquals("test-property-value", get("topology_template", "node_templates", "test-node",
            "capabilities", "test-capability", "properties", "test-property-key"));
        assertEquals("test-attribute-value", get("topology_template", "node_templates", "test-node",
            "capabilities", "test-capability", "attributes", "test-attribute", "value"));
        assertEquals("test-description", get("topology_template", "node_templates", "test-node",
            "capabilities", "test-capability", "attributes", "test-attribute", "description"));
    }

    @Test
    public void requirementTest() {
        currentFile = REQUIREMENT;
        Optional<Entity> fulfiller = getGraph().getEntity(Lists.newArrayList("topology_template",
            "node_templates", "test-node", "requirements", "test-requirement1", "node"));
        assertTrue(fulfiller.isPresent());
        Optional<Entity> fulfiller2 = getGraph().getEntity(Lists.newArrayList("topology_template",
            "node_templates", "test-node", "requirements", "test-requirement2", "node"));
        assertTrue(fulfiller2.isPresent());
        assertEquals("DatabaseEndpoint", get("topology_template", "node_templates", "test-node", "requirements",
            "test-requirement2", "capability"));
        assertEquals("ConnectsTo", get("topology_template", "node_templates", "test-node", "requirements",
            "test-requirement2", "relationship"));
        assertEquals(Lists.newArrayList("1", "2"), getList("topology_template", "node_templates", "test-node", "requirements",
            "test-requirement2", "occurrences"));
    }

    @Test
    public void nodeTest() {
        currentFile = NODE;
        assertEquals("WebServer", get("topology_template", "node_templates", "test-node", "type"));
        assertEquals("test-property-value", get("topology_template", "node_templates", "test-node", "properties",
            "test-property-key"));
        assertEquals("test-attribute-value", get("topology_template", "node_templates", "test-node", "attributes",
            "test-attribute-key"));
        assertEquals("test-description", get("topology_template", "node_templates", "test-node", "description"));
        assertEquals("test-file", get("topology_template", "node_templates", "test-node", "artifacts",
            "test-artifact", "file"));
        assertEquals("test-artifact-description", get("topology_template", "node_templates", "test-node", "artifacts",
            "test-artifact", "description"));
        assertEquals("test-deploy-path", get("topology_template", "node_templates", "test-node", "artifacts",
            "test-artifact", "deploy_path"));
    }

    @Test
    public void allInputsSetTest() {
        currentFile = INPUT_NO_VALUE;
        ServiceGraph graph = getGraph();
        assertFalse(graph.requiredInputsSet());
        currentFile = INPUT;
        graph = getGraph();
        assertTrue(graph.requiredInputsSet());
    }

    private String get(String... context) {
        ServiceGraph graph = getGraph();
        Entity entity = graph.getEntityOrThrow(new EntityId(Lists.newArrayList(context)));
        return ((ScalarEntity) entity).getValue();
    }

    private List<String> getList(String... context) {
        ServiceGraph graph = getGraph();
        Entity entity = graph.getEntityOrThrow(new EntityId(Lists.newArrayList(context)));
        return ((SequenceEntity) entity).getValues();
    }

    private ServiceGraph getGraph() {
        ServiceGraph graph = graphMap.get(currentFile);
        if (graph == null) {
            graph = new ServiceGraph(currentFile, logMock());
            graphMap.put(currentFile, graph);
        }
        return graph;
    }
}
