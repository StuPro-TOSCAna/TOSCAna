package org.opentosca.toscana.model.nodedefinition;

import org.opentosca.toscana.model.node.Dbms;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyLinkerTest {

    private Dbms dbms1;
    private Dbms dbms2;

    @Before
    public void setUp() {
        // TODO fix
//        dbms1 = new Dbms("dbms1");
//        dbms2 = new Dbms("dbms1");
//        dbms2.link(Dbms.ROOT_PASSWORD, dbms1, Dbms.ROOT_PASSWORD);
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
