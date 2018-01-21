package org.opentosca.toscana.plugins.kubernetes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.plugins.kubernetes.model.Pod;
import org.opentosca.toscana.plugins.kubernetes.model.TestNodeStacks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ResourceFileCreatorTest extends BaseUnitTest {
    private String appName = "my-app";
    private String appServiceName = "my-app-service";
    private String appDeploymentName = "my-app-deployment";

    @Test
    public void testReplicationControllerCreation() {
        ResourceFileCreator resourceFileCreator = new ResourceFileCreator(Pod.getPods(TestNodeStacks.getLampNodeStacks(log)));

        HashMap<String, String> result = null;
        try {
            result = resourceFileCreator.create();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }

        String service = result.get(appServiceName);
        String deployment = result.get(appDeploymentName);
        Yaml yaml = new Yaml();
        serviceTest((Map) yaml.load(service));
        deploymentTest((Map) yaml.load(deployment));
    }

    private void deploymentTest(Map map) {
        Map metadata = (Map) map.get("metadata");
        // test the metadata
        assertEquals(appDeploymentName.replaceAll("_", "-"), metadata.get("name"));
        assertEquals(appName, ((Map) metadata.get("labels")).get("app"));

        // test the spec
        Map spec = (Map) map.get("spec");
        assertEquals(appName, ((Map) ((Map) spec.get("selector")).get("matchLabels")).get("app"));
        Map template = (Map) spec.get("template");

        // test the metadata
        metadataTest(appName, (Map) template.get("metadata"));

        // test the containers
        ArrayList<Map> containers = (ArrayList<Map>) ((Map) template.get("spec")).get("containers");
        Map container = containers.get(0);
        assertEquals(appName, container.get("image"));
        assertEquals(appName, container.get("name"));
    }

    private void metadataTest(String name, Map templateMetadata) {
        assertEquals(name, templateMetadata.get("name"));
        assertEquals(name, ((Map) templateMetadata.get("labels")).get("app"));
    }

    private void serviceTest(Map map) {
        // test the metadata
        metadataTest(appServiceName.replaceAll("_", "-"), (Map) map.get("metadata"));

        //test the spec
        Map spec = (Map) map.get("spec");
        assertEquals(appName, ((Map) spec.get("selector")).get("app"));
    }
}
