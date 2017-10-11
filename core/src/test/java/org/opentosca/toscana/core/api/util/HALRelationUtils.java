package org.opentosca.toscana.core.api.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HALRelationUtils {
	public static void validateRelations(
		JSONArray linkArray,
		Map<String, String> relations,
		Object... replacementParams
	) throws JSONException {
		assertTrue(linkArray.length() == relations.size());
		for (Map.Entry<String, String> entry : relations.entrySet()) {
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
