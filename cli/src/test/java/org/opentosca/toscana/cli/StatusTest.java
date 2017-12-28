package org.opentosca.toscana.cli;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatusTest extends BaseCliTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void statusTest() {
        when(apiController.showStatus()).thenReturn("string");
        CliMain main = new CliMain();
        main.setApiController(apiController);
        main.main(new String[]{"status"});
        verify(apiController).showStatus();
    }

    // TODO fix this
//    @Test
//    public void statusNotWorkingTest() {
//        apiController = new ApiController("http://127.0.0.1:8084/");
//        //when(apiController.showStatus()).thenReturn("string");
//        CliMain main = new CliMain();
//        main.setApiController(apiController);
//        exit.expectSystemExitWithStatus(1);
//        main.main(new String[]{"status"});
//        //verify(apiController).showStatus();
//    }
}
