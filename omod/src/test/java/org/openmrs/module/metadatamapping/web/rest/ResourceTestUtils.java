package org.openmrs.module.metadatamapping.web.rest;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class ResourceTestUtils {
	
	static Object getExactlyOneObjectFromSearchResponse(SimpleObject responseData) {
		assertNotNull(responseData);
		List<SimpleObject> results = responseData.get("results");
		assertEquals("response should contain exactly one search result", 1, results.size());
		return results.get(0);
	}
}
