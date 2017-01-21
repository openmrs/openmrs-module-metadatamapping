package org.openmrs.module.metadatamapping.web.rest;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.openmrs.module.metadatamapping.web.rest.MetadataSourceResourceOperationTest.propertyValueEqualsTrue;

public class MetadataSetResourceOperationTest extends MainResourceControllerTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Override
	public String getURI() {
		return "/metadatamapping/metadataset";
	}
	
	@Override
	public String getUuid() {
		return "2fb06283-befc-4273-9448-2fcbbe4c99d5";
	}
	
	@Override
	public long getAllCount() {
		return 3;
	}
	
	@Test
	public void create_shouldPersist() throws Exception {
		//given
		SimpleObject requestBody = new SimpleObject();
		
		//when
		SimpleObject postResponseBody = deserialize(handle(newPostRequest(getURI(), requestBody)));
		
		//then
		assertNotNull(postResponseBody);
		Object newUuid = PropertyUtils.getProperty(postResponseBody, "uuid");
		assertNotNull(newUuid);
		
		SimpleObject getResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + newUuid)));
		assertNotNull(getResponseBody);
	}
	
	@Test
	public void update_shouldPersist() throws Exception {
		//given
		String uuid = "b3f4aa58-ab02-4379-ae61-ec2e15c29c1e";
		String testname = "testname";
		SimpleObject requestBody = new SimpleObject().add("name", testname).add("uuid", uuid);
		
		//when
		SimpleObject postResponseBody = deserialize(handle(newPostRequest(getURI() + "/" + uuid, requestBody)));
		
		//then
		assertNotNull(postResponseBody);
		Object newUuid = PropertyUtils.getProperty(postResponseBody, "uuid");
		assertNotNull(newUuid);
		assertThat((String) newUuid, is(uuid));
		
		SimpleObject getResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + newUuid)));
		assertNotNull(getResponseBody);
		
		assertThat((String) getResponseBody.get("name"), is(testname));
	}
	
	@Test
	public void delete_shouldRetire() throws Exception {
		// given
		SimpleObject unRetired = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.FALSE, PropertyUtils.getProperty(unRetired, "retired"));
		
		// when
		MockHttpServletResponse deleteResponse = handle(newDeleteRequest(getURI() + "/" + getUuid()));
		
		// then
		Assert.assertNotNull(deleteResponse);
		SimpleObject retired = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(Boolean.TRUE, PropertyUtils.getProperty(retired, "retired"));
	}
	
	@Test
	public void search_shouldIncludeRetiredInResultsWhenRequested() throws Exception {
		// given
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		
		// when
		SimpleObject responseData = deserialize(handle(request));
		
		// then
		Assert.assertNotNull(responseData);
		List<Object> hits = responseData.get("results");
		assertThat(hits, hasItem(propertyValueEqualsTrue("retired")));
	}
	
	@Test
	public void addMember_shouldPersist() throws Exception {
		//given
		String uuid = "2fb06283-befc-4273-9448-2fcbbe4c99d5";
		SimpleObject requestBody = new SimpleObject().add("metadataClass", "org.openmrs.Location").add("metadataUuid",
		    "f5abf471-13b3-4db9-9871-792990f610bf");
		
		SimpleObject getListResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + uuid + "/members")));
		assertNotNull(getListResponseBody);
		ArrayList<?> results = getListResponseBody.get("results");
		assertThat(results.size(), is(5));
		
		//when
		SimpleObject postResponseBody = deserialize(handle(newPostRequest(getURI() + "/" + uuid + "/members", requestBody)));
		
		//then
		assertNotNull(postResponseBody);
		Object newUuid = PropertyUtils.getProperty(postResponseBody, "uuid");
		assertNotNull(newUuid);
		
		SimpleObject newGetListResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + uuid + "/members")));
		assertNotNull(newGetListResponseBody);
		ArrayList<?> newResults = newGetListResponseBody.get("results");
		assertThat(newResults.size(), is(6));
	}
	
	@Test
	public void deleteMember_shouldRetire() throws Exception {
		//given
		String uuid = "2fb06283-befc-4273-9448-2fcbbe4c99d5";
		
		SimpleObject getListResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + uuid + "/members")));
		assertNotNull(getListResponseBody);
		ArrayList<?> results = getListResponseBody.get("results");
		assertThat(results.size(), is(5));
		
		//when
		MockHttpServletResponse deleteResponse = handle(newDeleteRequest(getURI() + "/" + uuid
		        + "/members/b0c99f16-14b8-49b2-8d14-1e7447ad6aa9"));
		Assert.assertNotNull(deleteResponse);
		
		//then
		SimpleObject newGetListResponseBody = deserialize(handle(newGetRequest(getURI() + "/" + uuid + "/members")));
		assertNotNull(newGetListResponseBody);
		ArrayList<?> newResults = newGetListResponseBody.get("results");
		assertThat(newResults.size(), is(4));
		
		SimpleObject retired = deserialize(handle(newGetRequest(getURI() + "/" + uuid
		        + "/members/b0c99f16-14b8-49b2-8d14-1e7447ad6aa9")));
		assertEquals(Boolean.TRUE, PropertyUtils.getProperty(retired, "retired"));
	}
	
	@Test
	@ExpectedException(ConversionException.class)
	public void updateMember_shouldNotAllowUpdatingMappedObject() throws Exception {
		//given
		String uuid = "2fb06283-befc-4273-9448-2fcbbe4c99d5";
		SimpleObject postRequestBody = new SimpleObject().add("metadataClass", "org.openmrs.Encounter");
		
		//when
		deserialize(handle(newPostRequest(getURI() + "/" + uuid + "/members/b0c99f16-14b8-49b2-8d14-1e7447ad6aa9",
		    postRequestBody)));
		
		//then expect exception
	}
}
