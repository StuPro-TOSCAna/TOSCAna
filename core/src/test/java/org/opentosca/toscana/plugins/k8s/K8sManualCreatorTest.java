package org.opentosca.toscana.plugins.k8s;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.testutils.CategoryAwareJUnitRunner;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.core.util.FileHelper;
import org.opentosca.toscana.plugins.kubernetes.KubernetesManualCreator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(CategoryAwareJUnitRunner.class)
@TestCategory(TestCategories.FAST)
public class K8sManualCreatorTest {
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
