package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.util.FileHelper;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KubernetesManualCreatorTest extends BaseUnitTest {
    private static String testManualCorrectFile;

    @BeforeClass
    public static void setUp() {
        File manual = new File("src/test/resources/kubernetes/", "kubernetes_manual_guide_test.md");
        testManualCorrectFile = FileHelper.readFileToString(manual);
    }

    @Test
    public void testCorrectFile() throws IOException {
        assertEquals(testManualCorrectFile, KubernetesManualCreator.createManual("test-app", "test-app.yaml"));
    }
}
