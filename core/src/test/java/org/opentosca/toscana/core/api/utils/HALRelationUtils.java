package org.opentosca.toscana.core.api.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HALRelationUtils {
	private static Logger log = LoggerFactory.getLogger(HALRelationUtils.class);

	public static void validateRelations(
		JSONArray linkArray,
		Map<String, String> relations,
		Object... replacementParams
	) throws JSONException {
		assertTrue(linkArray.length() == relations.size());
		for (Map.Entry<String, String> entry : relations.entrySet()) {
			log.debug("Looking at Relation {} with expected URL {}",
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
			assertTrue("Could not find link in Relations of the json response: " + entry.getKey(), found);
		}
	}
}
