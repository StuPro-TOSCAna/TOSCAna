package org.opentosca.toscana.core.parse.converter.linker;

import java.io.File;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.CsarParseServiceImpl;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Database;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ToscaFunctionIT extends BaseIntegrationTest {

    private final static Logger logger = LoggerFactory.getLogger(ToscaFunctionIT.class.getName());

    private final static File BASE_PATH = new File("src/integration/resources/converter/linker");

    private final static File GET_INPUT = new File(BASE_PATH, "get_input.yaml");
    private final static File GET_PROPERTY = new File(BASE_PATH, "get_property.yaml");
    
    private CsarParseService parser;
    
    @Before
    public void setUp(){
        parser = new CsarParseServiceImpl();
    }

    @Test
    public void getInputTest() throws Exception {
        EffectiveModel model = parser.parse(GET_INPUT);
        Database database = (Database) model.getNodeMap().get("my_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }
    
    @Test
    public void getPropertyTest() throws Exception {
        EffectiveModel model = parser.parse(GET_PROPERTY);
        Database database = (Database) model.getNodeMap().get("my_second_db");
        assertEquals("my_db_name", database.getDatabaseName());
    }
}
