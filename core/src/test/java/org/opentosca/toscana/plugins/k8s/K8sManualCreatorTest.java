package org.opentosca.toscana.plugins.k8s;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.testutils.CategoryAwareJUnitRunner;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.core.util.FileHelper;
import org.opentosca.toscana.plugins.kubernetes.KubernetesManualCreator;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class K8sManualCreatorTest extends BaseJUnitTest {
    private static String testManualCorrectFile;

    @BeforeClass
    public static void setUp() {
        File manual = new File("src/test/resources/kubernetes/", "k8s_manual_guide_test.md");
        testManualCorrectFile = FileHelper.readFileToString(manual);
    }

    @Test
    public void testCorrectFile() throws IOException {
        assertEquals(testManualCorrectFile, KubernetesManualCreator.createManual("test-app", "test-app.yaml"));
    }
}
