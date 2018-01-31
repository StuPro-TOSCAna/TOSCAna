package org.opentosca.toscana.core.parse.converter;

import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.operation.OperationVariable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.parse.TestTemplates.Functions.GET_INPUT;
import static org.opentosca.toscana.core.parse.TestTemplates.Functions.GET_PROPERTY;
import static org.opentosca.toscana.core.parse.TestTemplates.Functions.GET_PROPERTY_IN_INPUT;
import static org.opentosca.toscana.core.parse.TestTemplates.Functions.GET_PROPERTY_IN_INTERFACE;
import static org.opentosca.toscana.core.parse.TestTemplates.Functions.GET_PROPERTY_SELF;

public class IntrinsicFunctionResolverTest extends BaseUnitTest {

    @Test
    public void getInputTest() {
        EffectiveModel model = new EffectiveModelFactory().create(GET_INPUT, logMock());
        Database database = (Database) model.getNodeMap().get("my_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }

    @Test
    public void getPropertyTest() {
        EffectiveModel model = new EffectiveModelFactory().create(GET_PROPERTY, logMock());
        Database database = (Database) model.getNodeMap().get("my_second_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }

    @Test
    public void getPropertySelfTest() {
        EffectiveModel model = new EffectiveModelFactory().create(GET_PROPERTY_SELF, logMock());
        Database database = (Database) model.getNodeMap().get("my_db");
        assertEquals("my_user_name", database.getDatabaseName());
    }

    @Test
    public void getNestedPropertyTest() {
        EffectiveModel model = new EffectiveModelFactory().create(GET_PROPERTY_IN_INTERFACE, logMock());
        Database database = (Database) model.getNodeMap().get("my_db");
        Set<OperationVariable> inputs = database.getStandardLifecycle().getConfigure().get().getInputs();
        OperationVariable input = inputs.stream().findFirst().orElse(null);
        assertEquals("my_user_name", input.getValue().get());
    }

    /**
     Tests whether names of linked properties are correct
     */
    @Test
    public void correctPropertyNameTest() {
        EffectiveModel model = new EffectiveModelFactory().create(GET_PROPERTY_IN_INPUT, logMock());
        Database myApp = (Database) model.getNodeMap().get("my_db");
        OperationVariable input = myApp.getStandardLifecycle().getConfigure().get().getInputs().iterator().next();
        assertEquals("correct-name", input.getKey());
        assertEquals("test-string", input.getValue().get());
    }
}
