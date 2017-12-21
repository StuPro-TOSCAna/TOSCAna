package org.opentosca.toscana.model.nodedefinition;

import org.opentosca.toscana.model.node.Dbms;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.model.nodedefinition.DbmsDefinition.ROOT_PASSWORD_PROPERTY;

public class PropertyLinkerTest {

    private Dbms dbms1;
    private Dbms dbms2;

    @Before
    public void setUp() {
        dbms1 = Dbms.builder("dbms1").build();
        dbms2 = Dbms.builder("dbms2").build();
        dbms2.link(ROOT_PASSWORD_PROPERTY, dbms1, ROOT_PASSWORD_PROPERTY);
    }

    @Test
    public void linkerGetterTest() {
        String expected = "password";
        dbms1.setRootPassword(expected);
        String result = dbms2.getRootPassword().get();
        assertEquals(expected, result);
    }

    @Test
    public void linkerSetterTest() {
        String expected = "password";
        dbms2.setRootPassword(expected);
        String result = dbms1.getRootPassword().get();
        assertEquals(expected, result);
    }
}
