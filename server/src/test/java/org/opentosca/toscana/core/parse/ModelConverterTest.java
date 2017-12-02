package org.opentosca.toscana.core.parse;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.converter.ModelConverter;
import org.opentosca.toscana.core.parse.converter.UnknownNodeTypeException;
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
public class ModelConverterTest extends BaseUnitTest {

    private final static Path PATH = Paths.get("src/test/resources/converter");

    private EffectiveModel getModel(String templateName) throws MultiException, UnknownNodeTypeException {
        Reader reader = Reader.getReader();
        TServiceTemplate serviceTemplate = reader.parse(PATH, Paths.get(templateName + ".yaml"));
        return new ModelConverter().convert(serviceTemplate);
    }

    @Test
    public void convertName() throws MultiException, UnknownNodeTypeException {
        EffectiveModel model = getModel("name");
        assertEquals(1, model.getNodes().size());
    }

}
