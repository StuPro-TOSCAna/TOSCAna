package org.opentosca.toscana.retrofit;

import java.io.IOException;

import org.opentosca.toscana.retrofit.model.Transformation.TransformationState;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import okhttp3.ResponseBody;

/**
 Some API calls do only return the confirmation, but the server is still working on the response.
 This class wraps these calls so that they only return when the server has finished processing.
 This is realized by polling the server state.
 */
public class BlockingToscanaApi extends ToscanaApi {

    public static final int POLLING_INTERVAL_MS = 700;

    public BlockingToscanaApi(String url) {
        super(url);
    }

    /**
     Start specified transformation in a blocking fashion. Returns only after finished processing.
     */
    @Override
    public ResponseBody startTransformation(String csarName, String platform) throws IOException, TOSCAnaServerException {
        ResponseBody response = super.startTransformation(csarName, platform);
        TransformationState state = getTransformation(csarName, platform).getState();
        if (state == TransformationState.INPUT_REQUIRED) {
            return response;
        } else {
            do {
                try {
                    Thread.sleep(POLLING_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                state = getTransformation(csarName, platform).getState();
            } while (!(state == TransformationState.DONE
                || state == TransformationState.ERROR));
            return response;
        }
    }
}
