package org.opentosca.toscana.cli;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CliMainTest extends BaseCliTest {

    @Test
    public void statusTest() {
        String string = "teststring";
        when(apiController.showStatus()).thenReturn(string);
        CliMain main = new CliMain();
        main.setApiController(apiController);
        main.main(new String[]{"status"});
        assertEquals(string, getResult().split("\n")[0]);
    }
}
