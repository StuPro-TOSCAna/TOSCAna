package org.opentosca.toscana.plugins.kubernetes;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ResourceFileCreatorTest extends BaseUnitTest {

    @Test
    public void testReplicationControllerCreation() throws MalformedURLException {
        ResourceFileCreator resourceFileCreator
            = new ResourceFileCreator(Arrays.asList(Arrays.asList(TestEffectiveModels.getMinimalDockerApplication())));
        HashMap<String, String> result = null;
        try {
            result = resourceFileCreator.create();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }

        String service = result.get("test-service");
        String deployment = result.get("test-deployment");
        Yaml yaml = new Yaml();
        serviceTest((Map) yaml.load(service));
        deploymentTest((Map) yaml.load(deployment));
    }

    private void deploymentTest(Map map) {
        String appName = "test";
        Map metadata = (Map) map.get("metadata");
        // test the metadata
        assertEquals("test-deployment", metadata.get("name"));
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
        assertEquals("nfode/simpletaskapp:v1", container.get("image"));
        assertEquals("simpletaskapp", container.get("name"));
    }

    private void metadataTest(String name, Map templateMetadata) {
        assertEquals(name, templateMetadata.get("name"));
        assertEquals(name, ((Map) templateMetadata.get("labels")).get("app"));
    }

    private void serviceTest(Map map) {
        String appName = "test";

        // test the metadata
        String serviceName = "test-service";
        metadataTest(serviceName, (Map) map.get("metadata"));

        //test the spec
        Map spec = (Map) map.get("spec");
        assertEquals(appName, ((Map) spec.get("selector")).get("app"));
    }
}
