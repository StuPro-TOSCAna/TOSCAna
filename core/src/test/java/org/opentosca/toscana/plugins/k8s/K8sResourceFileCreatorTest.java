package org.opentosca.toscana.plugins.k8s;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.testutils.CategoryAwareJUnitRunner;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.core.util.FileHelper;
import org.opentosca.toscana.plugins.kubernetes.KubernetesResourceFileCreator;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class K8sResourceFileCreatorTest extends BaseJUnitTest {
    public static KubernetesResourceFileCreator creator;
    public static String correctResourceFile;

    @BeforeClass
    public static void setUp() {
        creator = new KubernetesResourceFileCreator();
        File file = new File("src/test/resources/kubernetes/", "k8s_test_resource.yaml");
        correctResourceFile = FileHelper.readFileToString(file);
    }

    @Test
    public void basicTest() throws JsonProcessingException {
        assertEquals(correctResourceFile, creator.createResourceFileAsString("test-app"));
    }
}
