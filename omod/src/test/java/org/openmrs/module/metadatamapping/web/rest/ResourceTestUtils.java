package org.openmrs.module.metadatamapping.web.rest;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceTestUtils {
	
	static Object getExactlyOneObjectFromSearchResponse(SimpleObject responseData) {
		assertNotNull(responseData);
		List<SimpleObject> results = responseData.get("results");
		assertEquals(1, results.size(), "response should contain exactly one search result");
		return results.get(0);
	}
}
