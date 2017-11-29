package org.opentosca.toscana.core.parse;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.model.EffectiveModel;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 Does not run completely decoupled: Uses the winery parser internally.
 In case of failure, make sure the parser works as expected.
 */
public class ModelConverterTest extends BaseJUnitTest {

    private final static String PATH = "src/test/resources/converter";

    private EffectiveModel getModel(String templateName) throws MultiException {
        Reader reader = new Reader();
        TServiceTemplate serviceTemplate = reader.parse(PATH, templateName + ".yaml");
        return new ModelConverter().convert(serviceTemplate);
    }

    @Test
    public void convertName() throws MultiException {
        EffectiveModel model = getModel("name");
        assertEquals(1, model.getNodes().size());
    }

}
