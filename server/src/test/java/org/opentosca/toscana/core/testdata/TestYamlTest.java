package org.opentosca.toscana.core.testdata;

import java.io.File;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.parse.CsarParseService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 Contains integration tests for our yaml models used to test our application
 */
public class TestYamlTest extends BaseSpringTest {

    @Autowired
    private CsarParseService parser;

    @Test
    public void testMinimalDocker() throws Exception {
        File minimalDocker = new File(TestCsars.YAML_DIR, "valid/minimal-docker/minimal-docker.yaml");
        parser.parse(minimalDocker);

    }
}
