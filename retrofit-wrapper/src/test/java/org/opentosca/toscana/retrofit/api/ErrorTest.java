package org.opentosca.toscana.retrofit.api;

import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ErrorTest extends BaseToscanaApiTest {

    private ErrorTestParam param;

    @SuppressWarnings("unused")
    public ErrorTest(ErrorTestParam param, String name) {
        this.param = param;
    }

    @Test(expected = TOSCAnaServerException.class)
    public void executeTest() throws Exception {
        enqueError(param.code);
        param.callGenerator.excec(api);
    }

    private static class ErrorTestParam {
        private int code;
        private ErrorTestFunction callGenerator;

        ErrorTestParam(int code, ErrorTestFunction callGenerator) {
            this.code = code;
            this.callGenerator = callGenerator;
        }
    }

    private interface ErrorTestFunction {
        Object excec(ToscanaApi api) throws Exception;
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                new ErrorTestParam(
                    400,
                    (r -> r.downloadArtifactAsStream("test-csar", "platform"))
                ),
                "Artifact Download"
            },
            {
                new ErrorTestParam(
                    404,
                    (r -> r.getLogs("test-csar", "platform", 0L))
                ),
                "Log Retrieval"
            },
            {
                new ErrorTestParam(
                    404,
                    (r -> r.getCsarDetails("test-csar"))
                ),
                "Csar Details"
            },
            {
                new ErrorTestParam(
                    404,
                    (r -> r.getPlatformDetails("test"))
                ),
                "Platform Details"
            },
            {
                new ErrorTestParam(
                    404,
                    (r -> r.getTransformation("test-csar", "test"))
                ),
                "Transformation Details"
            },
            {
                new ErrorTestParam(
                    400,
                    (r -> {
                        r.uploadCsar("test-csar", new byte[1000]);
                        return new Object();
                    })
                ),
                "Csar Upload"
            }
        });
    }
}
