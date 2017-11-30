package org.opentosca.toscana.core.testdata;

import java.io.File;

import org.opentosca.toscana.core.BaseJUnitTest;

import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.junit.Test;

public class TestYamlTest extends BaseJUnitTest {
    @Test
    public void testMinimalDocker() throws MultiException {
        File minimalDocker = new File(TestCsars.YAML_DIR, "valid/minimal-docker/minimal-docker.yaml");
        parseFile(minimalDocker.getParent(), minimalDocker.getName());
    }

    /**
     Utility that parses a yaml file
     with the help of the winery yaml reader
     */
    private void parseFile(String path, String name) throws MultiException {
        Reader yamlReader = new Reader();
        yamlReader.parse(path, name);
    }
}
