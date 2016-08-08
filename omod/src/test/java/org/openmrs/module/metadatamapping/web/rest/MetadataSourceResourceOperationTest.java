package org.openmrs.module.metadatamapping.web.rest;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test different REST api operations on {@link org.openmrs.module.metadatamapping.MetadataSource}.
 */
@SuppressWarnings("Duplicates")
public class MetadataSourceResourceOperationTest extends MainResourceControllerTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Test
	public void create_shouldPersist() throws Exception {
		// given
		SimpleObject postData = new SimpleObject().add("name", "my-source");
		
		// when
		SimpleObject postResponseData = deserialize(handle(newPostRequest(getURI(), postData)));
		
		// then
		assertNotNull(postResponseData);
		Object newUuid = PropertyUtils.getProperty(postResponseData, "uuid");
		assertNotNull(newUuid);
		
		SimpleObject getResponseData = deserialize(handle(newGetRequest(getURI() + "/" + newUuid)));
		assertNotNull(getResponseData);
		assertEquals("my-source", PropertyUtils.getProperty(getResponseData, "name"));
	}
	
	@Test
	public void update_shouldPersist() throws Exception {
		// given
		SimpleObject postData = new SimpleObject().add("description", "This is the new description");
		
		// when
		MockHttpServletResponse postResponse = handle(newPostRequest(getURI() + "/" + getUuid(), postData));
		
		// then
		assertNotNull(postResponse);
		SimpleObject getResponseData = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertNotNull(getResponseData);
		assertEquals("This is the new description", PropertyUtils.getProperty(getResponseData, "description"));
	}
	
	@Test
	public void delete_shouldRetire() throws Exception {
		// given
		SimpleObject unRetired = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.FALSE, PropertyUtils.getProperty(unRetired, "retired"));
		
		// when
		MockHttpServletResponse deleteResponse = handle(newDeleteRequest(getURI() + "/" + getUuid()));
		
		// then
		assertNotNull(deleteResponse);
		SimpleObject retired = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.TRUE, PropertyUtils.getProperty(retired, "retired"));
	}
	
	@Test
	public void search_shouldMatchByName() throws Exception {
		// given
		// test data
		
		// when
		// NOTE: Space characters as part of request url may seem dangerous but here we assume
		// than in an actual http request the client would take of url escaping, if necessary.
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("name", "Integration Test Metadata Source 2");
		SimpleObject results = deserialize(handle(request));
		
		// then
		Object object = ResourceTestUtils.getExactlyOneObjectFromSearchResponse(results);
		assertEquals("9cace0bd-6f2a-4cc3-a26d-6fa292f1f2c1", PropertyUtils.getProperty(object, "uuid"));
	}
	
	@Test
	public void search_shouldIncludeRetiredInResultsWhenRequested() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		
		// when
		SimpleObject responseData = deserialize(handle(request));
		
		// then
		assertNotNull(responseData);
		List<Object> hits = responseData.get("results");
		assertThat(hits, hasItem(propertyValueEqualsTrue("retired")));
	}
	
	@Test
	public void search_shouldSupportPaging() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		request.setParameter("v", "full");
		request.setParameter("limit", "2");
		request.setParameter("startIndex", "0");
		// when
		List<SimpleObject> hitsFirstPage = deserialize(handle(request)).get("results");
		// then
		assertEquals(2, hitsFirstPage.size());
		assertEquals("Integration Test Metadata Source 1", PropertyUtils.getProperty(hitsFirstPage.get(0), "name"));
		assertEquals("Integration Test Metadata Source 2", PropertyUtils.getProperty(hitsFirstPage.get(1), "name"));
		
		// given
		request.setParameter("startIndex", "2");
		// when
		List<SimpleObject> hitsSecondPage = deserialize(handle(request)).get("results");
		// then
		assertEquals(1, hitsSecondPage.size());
		assertEquals("Integration Test Metadata Source 3", PropertyUtils.getProperty(hitsSecondPage.get(0), "name"));
	}
	
	@Override
	public String getURI() {
		return "/metadatamapping/source";
	}
	
	@Override
	public String getUuid() {
		return "df29a160-0add-4598-8ac2-b11a9eb3cdb8";
	}
	
	@Override
	public long getAllCount() {
		return 2;
	}
	
	/**
	 * Use this as a workaround as standard Hamcrest hasProperty(..., equalTo(...)) does not seem to work with
	 * a SimpleObject.
	 *
	 * @param property name of property
	 * @return true if the property value equals to true
	 */
	static Matcher<Object> propertyValueEqualsTrue(final String property) {
		return new BaseMatcher<Object>() {
			
			@Override
			public boolean matches(Object item) {
				try {
					return Boolean.TRUE.equals(PropertyUtils.getProperty(item, property));
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public void describeTo(Description description) {
				description.appendText("propertyValueEqualsTrue(").appendValue(property).appendText(")");
			}
		};
	}
}
