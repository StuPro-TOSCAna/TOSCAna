package org.opentosca.toscana.core.parse.converter.servicemodel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.parse.graphconverter.BaseEntity;
import org.opentosca.toscana.core.parse.graphconverter.ScalarEntity;
import org.opentosca.toscana.core.parse.graphconverter.SequenceEntity;
import org.opentosca.toscana.core.parse.graphconverter.ServiceModel;
import org.opentosca.toscana.model.EntityId;

import com.google.common.collect.Lists;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ServiceModelIT extends BaseIntegrationTest {

    private final static File BASE_PATH = new File("src/integration/resources/converter/tosca_elements");

    private final static File REPOSITORY = new File(BASE_PATH, "repository.yaml");
    private final static File CREDENTIAL = new File(BASE_PATH, "credential.yaml");
    private final static File INPUT = new File(BASE_PATH, "input.yaml");
    private final static File OUTPUT = new File(BASE_PATH, "output.yaml");
    private final static File INTERFACE = new File(BASE_PATH, "interface.yaml");
    private final static File CAPABILITY = new File(BASE_PATH, "capability.yaml");
    private final static File REQUIREMENT = new File(BASE_PATH, "requirement.yaml");
    private final static File NODE = new File(BASE_PATH, "node.yaml");

    private final static Map<File, ServiceModel> modelMap = new HashMap<>();

    private File currentFile;

    @Test
    public void repositoryTest() throws Exception {
        currentFile = REPOSITORY;
        assertEquals("https://first-url.com/", get("repositories", "first_repo", "url"));
        assertEquals("test-description", get("repositories", "second_repo", "description"));
        assertEquals("https://second-url.com/", get("repositories", "second_repo", "url"));
        assertEquals("test-token", get("repositories", "second_repo", "credential", "token"));
    }

    @Test
    public void credentialTest() throws Exception {
        currentFile = CREDENTIAL;
        assertEquals("test-protocol", get("repositories", "first_repo", "credential", "protocol"));
        assertEquals("test-token_type", get("repositories", "first_repo", "credential", "token_type"));
        assertEquals("test-token", get("repositories", "first_repo", "credential", "token"));
        assertEquals("test-user", get("repositories", "first_repo", "credential", "user"));
        assertEquals("value1", get("repositories", "first_repo", "credential", "keys", "key1"));
    }

    @Test
    public void inputTest() throws Exception {
        currentFile = INPUT;
        assertEquals("string", get("topology_template", "inputs", "test_input", "type"));
        assertEquals("test-value", get("topology_template", "inputs", "test_input", "value"));
        assertEquals("test-description", get("topology_template", "inputs", "test_input", "description"));
        assertEquals("true", get("topology_template", "inputs", "test_input", "required"));
        assertEquals("test-default", get("topology_template", "inputs", "test_input", "default"));
    }

    @Test
    public void outputTest() throws Exception {
        currentFile = OUTPUT;
        assertEquals("string", get("topology_template", "outputs", "test_output", "type"));
        assertEquals("test-value", get("topology_template", "outputs", "test_output", "value"));
        assertEquals("test-description", get("topology_template", "outputs", "test_output", "description"));
        assertEquals("true", get("topology_template", "outputs", "test_output", "required"));
        assertEquals("test-default", get("topology_template", "outputs", "test_output", "default"));
    }

    @Test
    public void interfaceTest() throws Exception {
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
    public void capabilityTest() throws Exception {
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
    public void requirementTest() throws Exception {
        currentFile = REQUIREMENT;
        Optional<BaseEntity> fulfiller = getModel().getEntity(Lists.newArrayList("topology_template",
            "node_templates", "test-node", "requirements", "test-requirement1", "node"));
        assertTrue(fulfiller.isPresent());
        Optional<BaseEntity> fulfiller2 = getModel().getEntity(Lists.newArrayList("topology_template",
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
    public void nodeTest() throws Exception {
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

    private String get(String... context) throws Exception {
        ServiceModel model = getModel();
        BaseEntity entity = model.getEntityOrThrow(new EntityId(Lists.newArrayList(context)));
        return ((ScalarEntity) entity).get(); 
    }

    private List<String> getList(String... context) throws Exception {
        ServiceModel model = getModel();
        BaseEntity entity = model.getEntityOrThrow(new EntityId(Lists.newArrayList(context)));
        return ((SequenceEntity) entity).get();
    }

    private ServiceModel getModel() throws Exception {
        ServiceModel model = modelMap.get(currentFile);
        if (model == null) {
            model = new ServiceModel(currentFile);
            modelMap.put(currentFile, model);
        }
        return model;
    }
}
