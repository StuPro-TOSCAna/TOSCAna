package org.opentosca.toscana.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MainTest {
    private Main main;
    @Before
    public void setUp(){
        main = new Main();
    }
    @Test
    public void testOne(){
        assertEquals("Hello!",main.sayHello());

    }

}
