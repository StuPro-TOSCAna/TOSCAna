package org.opentosca.toscana.api.utils;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertSame;

public class HALRelationUtils {
    private static final Logger logger = LoggerFactory.getLogger(HALRelationUtils.class);

    public static void validateRelations(
        JSONArray linkArray,
        Map<String, String> relations,
        Object... replacementParams
    ) throws JSONException {
        assertSame(linkArray.length(), relations.size());
        for (Map.Entry<String, String> entry : relations.entrySet()) {
            logger.debug("Looking at Relation {} with expected URL {}",
                entry.getKey(), String.format(entry.getValue(),
                    replacementParams)
            );
            boolean found = false;
            for (int i = 0; i < relations.size(); i++) {
                JSONObject obj = linkArray.getJSONObject(i);
                found = obj.getString("rel").equals(entry.getKey());
                found = found && obj.getString("href").equals(String.format(entry.getValue(), replacementParams));
                if (found)
                    break;
            }
            System.err.println(entry.getKey() + ": " + found);
            //assertTrue("Could not find link in Relations of the json response: " + entry.getName(), );
        }
    }
}
