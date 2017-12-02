package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.util.FileHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KubernetesResourceFileCreatorTest extends BaseUnitTest {
    private static KubernetesResourceFileCreator creator;
    private static String correctResourceFile;

    @BeforeClass
    public static void setUp() {
        creator = new KubernetesResourceFileCreator();
        File file = new File("src/test/resources/kubernetes/", "kubernetes_test_resource.yaml");
        correctResourceFile = FileHelper.readFileToString(file);
    }

    @Test
    public void basicTest() throws JsonProcessingException {
        assertEquals(correctResourceFile, creator.createResourceFileAsString("test-app"));
    }
}

