package org.openmrs.module.metadatamapping.web.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.openmrs.module.webservices.rest.SimpleObject;

class ResourceTestUtils {
	
	static Object getExactlyOneObjectFromSearchResponse(SimpleObject responseData) {
		assertNotNull(responseData);
		List<SimpleObject> results = responseData.get("results");
		assertEquals("response should contain exactly one search result", 1, results.size());
		return results.get(0);
	}
}
