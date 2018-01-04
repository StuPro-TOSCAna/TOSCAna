package org.opentosca.toscana.cli;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class HelpTest extends BaseCliTest {
    private CliMain main;
    private String arg;
    private boolean expected;

    public HelpTest(String arg, Boolean expected) {
        this.arg = arg;
        this.expected = expected;
        main = new CliMain();
        ApiController apiController = mock(ApiController.class);
        main.setApiController(apiController);
    }

    @Parameterized.Parameters(name = "{index}: contains({0})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"csar", true},
            {"platform", true},
            {"status", true},
            {"transformation", true}
        });
    }

    @Test
    public void helpTest() {
        main.main(new String[]{"help", arg});
        String[] result = systemOutRule.getLog().split("\n");
        systemOutRule.clearLog();
        assertThat(result[0].contains("Usage: toscana " + arg), is(equalTo(expected)));
    }
}
