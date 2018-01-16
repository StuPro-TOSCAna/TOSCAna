package org.opentosca.toscana.core.parse.converter.linker;

import java.io.File;
import java.util.Set;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.operation.OperationVariable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToscaFunctionIT extends BaseIntegrationTest {

    private final static File BASE_PATH = new File("src/integration/resources/converter/functions");

    private final static File GET_INPUT = new File(BASE_PATH, "get_input.yaml");
    private final static File GET_PROPERTY = new File(BASE_PATH, "get_property.yaml");
    private final static File GET_PROPERTY_SELF = new File(BASE_PATH, "get_property_self.yaml");
    private final static File GET_PROPERTY_IN_INTERFACE = new File(BASE_PATH, "get_property_in-interface.yaml");

    @Test
    public void getInputTest() {
        EffectiveModel model = new EffectiveModel(GET_INPUT);
        Database database = (Database) model.getNodeMap().get("my_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }

    @Test
    public void getPropertyTest() {
        EffectiveModel model = new EffectiveModel(GET_PROPERTY);
        Database database = (Database) model.getNodeMap().get("my_second_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }

    @Test
    public void getPropertySelfTest() {
        EffectiveModel model = new EffectiveModel(GET_PROPERTY_SELF);
        Database database = (Database) model.getNodeMap().get("my_db");
        assertEquals("my_user_name", database.getDatabaseName());
    }

    @Test
    public void getPropertyInInterfaceTest() {
        EffectiveModel model = new EffectiveModel(GET_PROPERTY_IN_INTERFACE);
        Database database = (Database) model.getNodeMap().get("my_db");
        Set<OperationVariable> inputs = database.getStandardLifecycle().getConfigure().get().getInputs();
        OperationVariable input = inputs.stream().findFirst().orElse(null);
        assertEquals("my_user_name", input.getValue().get());
    }
}
