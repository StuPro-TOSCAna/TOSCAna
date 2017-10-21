package org.opentosca.toscana.core.parse;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.TestData;
import org.opentosca.toscana.core.csar.Csar;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class CsarParserImplTest extends BaseSpringTest {

    @Autowired
    CsarParser csarParser;
    @Autowired
    TestData testData;

    @Test
    public void parse() throws Exception {
        Csar csar = testData.getCsar(TestData.CSAR_YAML_VALID_SIMPLETASK);
        TServiceTemplate serviceTemplate = csarParser.parse(csar);
        assertNotNull(serviceTemplate);
    }

}
