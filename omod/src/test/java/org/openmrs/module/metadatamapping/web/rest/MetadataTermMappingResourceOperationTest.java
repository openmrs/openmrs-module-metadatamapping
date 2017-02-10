package org.openmrs.module.metadatamapping.web.rest;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.validation.ValidationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test different REST api operations on {@link org.openmrs.module.metadatamapping.MetadataTermMapping}.
 */
@SuppressWarnings("Duplicates")
public class MetadataTermMappingResourceOperationTest extends MainResourceControllerTest {
	
	@Resource
	private LocationService locationService;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Test
	public void create_shouldPersist() throws Exception {
		// given
		SimpleObject postData = new SimpleObject().add("code", "term-123").add("name", "Test Term Mapping 123").add(
		    "metadataSource", "df29a160-0add-4598-8ac2-b11a9eb3cdb8").add("metadataClass", "org.openmrs.Drug").add(
		    "metadataUuid", "3cfcf118-931c-46f7-8ff6-7b876f0d4202");
		
		// when
		SimpleObject postResponseData = deserialize(handle(newPostRequest(getURI(), postData)));
		
		// then
		assertNotNull(postResponseData);
		Object newUuid = PropertyUtils.getProperty(postResponseData, "uuid");
		assertNotNull(newUuid);
		
		SimpleObject getResponseData = deserialize(handle(newGetRequest(getURI() + "/" + newUuid)));
		assertNotNull(getResponseData);
		assertEquals("term-123", PropertyUtils.getProperty(getResponseData, "code"));
		assertEquals("Test Term Mapping 123", PropertyUtils.getProperty(getResponseData, "name"));
		assertEquals("org.openmrs.Drug", PropertyUtils.getProperty(getResponseData, "metadataClass"));
		assertEquals("3cfcf118-931c-46f7-8ff6-7b876f0d4202", PropertyUtils.getProperty(getResponseData, "metadataUuid"));
		Object metadataSource = getResponseData.get("metadataSource");
		assertEquals("df29a160-0add-4598-8ac2-b11a9eb3cdb8", PropertyUtils.getProperty(metadataSource, "uuid"));
	}
	
	@Test(expected = ValidationException.class)
	@Ignore("Will this load classes from modules which are not in config.xml? like org.openmrs.module.providermanagement.Provider")
	public void create_shouldGetValidationErrorWhenInvalidClass() throws Exception {
		// given
		SimpleObject postData = new SimpleObject().add("code", "term-123").add("name", "Test Term Mapping 123").add(
		    "metadataSource", "df29a160-0add-4598-8ac2-b11a9eb3cdb8").add("metadataClass", "NOTCLAZZ").add("metadataUuid",
		    "3cfcf118-931c-46f7-8ff6-7b876f0d4202");
		
		// when
		SimpleObject postResponseData = deserialize(handle(newPostRequest(getURI(), postData)));
		
		//then expect exception
	}
	
	@Test
	public void update_shouldPersist() throws Exception {
		// given
		SimpleObject postData = new SimpleObject().add("description", "This is the new term mapping description").add(
		    "metadataClass", "org.openmrs.Drug").add("metadataUuid", "3cfcf118-931c-46f7-8ff6-7b876f0d4202");
		
		// when
		MockHttpServletResponse postResponse = handle(newPostRequest(getURI() + "/" + getUuid(), postData));
		
		// then
		assertNotNull(postResponse);
		
		SimpleObject getResponseData = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertNotNull(getResponseData);
		assertEquals("This is the new term mapping description", PropertyUtils.getProperty(getResponseData, "description"));
		assertEquals("org.openmrs.Drug", PropertyUtils.getProperty(getResponseData, "metadataClass"));
		assertEquals("3cfcf118-931c-46f7-8ff6-7b876f0d4202", PropertyUtils.getProperty(getResponseData, "metadataUuid"));
	}
	
	@Test
	public void delete_shouldRetire() throws Exception {
		// given
		SimpleObject unRetiredTermMapping = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.FALSE, PropertyUtils.getProperty(unRetiredTermMapping, "retired"));
		
		// when
		MockHttpServletResponse deleteResponse = handle(newDeleteRequest(getURI() + "/" + getUuid()));
		
		// then
		assertNotNull(deleteResponse);
		
		SimpleObject retiredTermMapping = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.TRUE, PropertyUtils.getProperty(retiredTermMapping, "retired"));
	}
	
	@Test
	public void search_shouldMatchWithCodeAndSourceUuid() throws Exception {
		// given
		// test data
		
		// when
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("code", "drug-tri");
		request.setParameter("source", "9cace0bd-6f2a-4cc3-a26d-6fa292f1f2c1");
		SimpleObject results = deserialize(handle(request));
		
		// then
		Object object = ResourceTestUtils.getExactlyOneObjectFromSearchResponse(results);
		assertEquals("f03b3f7c-e2af-4428-8bdf-c1361f03d6ef", PropertyUtils.getProperty(object, "uuid"));
	}
	
	@Test
	public void search_shouldMatchWithName() throws Exception {
		// given
		// test data
		
		// when
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("name", "Triomune-30");
		SimpleObject results = deserialize(handle(request));
		
		// then
		Object object = ResourceTestUtils.getExactlyOneObjectFromSearchResponse(results);
		assertEquals("f03b3f7c-e2af-4428-8bdf-c1361f03d6ef", PropertyUtils.getProperty(object, "uuid"));
	}
	
	@Test
	public void search_shouldMatchWithCodeAndSourceName() throws Exception {
		// given
		// test data
		
		// when
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("code", "mdt-xan");
		request.setParameter("source", "Integration Test Metadata Source 2");
		SimpleObject results = deserialize(handle(request));
		
		// then
		Object object = ResourceTestUtils.getExactlyOneObjectFromSearchResponse(results);
		assertEquals("08bbe6b9-6240-4e9b-92ab-e3e6c07a0d2c", PropertyUtils.getProperty(object, "uuid"));
	}
	
	/**
	 * Test all the search parameters not already tested by other test cases.
	 */
	@Test
	public void search_shouldSupportAllSearchParameters() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("v", "full");
		// when
		List<SimpleObject> results = deserialize(handle(request)).get("results");
		// then
		assertEquals("default parameters", 9, results.size());
		
		// given
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals("include all", 11, results.size());
		
		// given
		request.setParameter("limit", "2");
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals(2, results.size());
		assertEquals("mdt-xan", PropertyUtils.getProperty(results.get(0), "code"));
		
		// given
		request.setParameter("startIndex", "2");
		request.setParameter("limit", "3");
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals(3, results.size());
		assertEquals("xyz", PropertyUtils.getProperty(results.get(0), "code"));
		assertEquals("61774ac8-ac82-4fd9-b496-fd2016a016f7", PropertyUtils.getProperty(results.get(2), "uuid"));
		
		// given
		Location neverNeverLand = locationService.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
		request.setParameter("metadataUuid", neverNeverLand.getUuid());
		request.setParameter("metadataClass", neverNeverLand.getClass().getCanonicalName());
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(false));
		request.setParameter("startIndex", "0");
		request.removeParameter("limit");
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals(2, results.size());
		assertEquals("Integration Test Metadata Source 1", getMetadataSourceNameProperty(results.get(0)));
		assertEquals("mdt-nnl", PropertyUtils.getProperty(results.get(0), "code"));
		assertEquals("Integration Test Metadata Source 2", getMetadataSourceNameProperty(results.get(1)));
		assertEquals("mdt-nnl", PropertyUtils.getProperty(results.get(1), "code"));
		
		// given
		request.setParameter("source", "Integration Test Metadata Source 2");
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals(1, results.size());
		assertEquals("Integration Test Metadata Source 2", getMetadataSourceNameProperty(results.get(0)));
		assertEquals("mdt-nnl", PropertyUtils.getProperty(results.get(0), "code"));
		
		// given
		request.setParameter("source", "9cace0bd-6f2a-4cc3-a26d-6fa292f1f2c1");
		// when
		results = deserialize(handle(request)).get("results");
		// then
		assertEquals(1, results.size());
		assertEquals("Integration Test Metadata Source 2", getMetadataSourceNameProperty(results.get(0)));
		assertEquals("mdt-nnl", PropertyUtils.getProperty(results.get(0), "code"));
	}
	
	@Test
	public void search_shouldHandleUnknownSourceName() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("source", "Unknown Source Name");
		
		// when
		List<SimpleObject> results = deserialize(handle(request)).get("results");
		
		// then
		assertEquals(0, results.size());
	}
	
	@Test
	public void search_shouldHandleUnknownSourceUuid() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("source", "1234-unknown-uuid");
		
		// when
		List<SimpleObject> results = deserialize(handle(request)).get("results");
		
		// then
		assertEquals(0, results.size());
	}
	
	@Override
	public String getURI() {
		return "/metadatamapping/termmapping";
	}
	
	@Override
	public String getUuid() {
		return "ca6b1024-42e9-48f2-8656-3757527ddf8c";
	}
	
	@Override
	public long getAllCount() {
		return 9;
	}
	
	private String getMetadataSourceNameProperty(Object metadataTermResult) throws Exception {
		Object metadataSource = PropertyUtils.getProperty(metadataTermResult, "metadataSource");
		return (String) PropertyUtils.getProperty(metadataSource, "display");
	}
}
