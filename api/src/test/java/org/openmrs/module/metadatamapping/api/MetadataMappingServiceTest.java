/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatamapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
import org.openmrs.module.metadatamapping.api.impl.MetadataMappingServiceImpl;
import org.openmrs.module.metadatamapping.api.wrapper.ConceptAdapter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

public class MetadataMappingServiceTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService service;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService adminService;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService locationService;
	
	@Autowired
	@Qualifier("metadatamapping.ConceptAdapter")
	private ConceptAdapter conceptAdapter;
	
	private ConceptSource localConceptSource;
	
	@Before
	public void setupLocalConceptSource() throws Exception {
		localConceptSource = new ConceptSource();
		localConceptSource.setName("my-dict");
		conceptService.saveConceptSource(localConceptSource);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, localConceptSource
		        .getUuid()));
		
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	/**
	 * @see MetadataMappingServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies add mapping if not found
	 */
	@Test
	public void addLocalMappingToConcept_shouldAddMappingIfNotFound() throws Exception {
		//given
		Concept concept = conceptService.getConcept(3);
		int mapsCount = concept.getConceptMappings().size();
		
		//when
		service.addLocalMappingToConcept(concept);
		
		//then
		Assert.assertEquals(mapsCount + 1, concept.getConceptMappings().size());
	}
	
	/**
	 * @see MetadataMappingServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies not add mapping if found
	 */
	@Test
	public void addLocalMappingToConcept_shouldNotAddMappingIfFound() throws Exception {
		//given
		Concept concept = conceptService.getConcept(3);
		int mapsCount = concept.getConceptMappings().size();
		service.addLocalMappingToConcept(concept);
		Assert.assertEquals(mapsCount + 1, concept.getConceptMappings().size());
		
		//when
		service.addLocalMappingToConcept(concept);
		
		//then
		Assert.assertEquals(mapsCount + 1, concept.getConceptMappings().size());
	}
	
	/**
	 * @see MetadataMappingServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies fail if local source not configured
	 */
	@Test(expected = APIException.class)
	public void addLocalMappingToConcept_shouldFailIfLocalSourceNotConfigured() throws Exception {
		Context.clearSession();
		
		//given
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, ""));
		Concept concept = conceptService.getConcept(3);
		
		//when
		service.addLocalMappingToConcept(concept);
		
		//then
		Assert.fail();
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(Integer)
	 * @verifies return non retired
	 */
	@Test
	public void getConcept_shouldReturnNonRetired() throws Exception {
		//given
		Concept concept = conceptService.getConcept(3);
		concept.setRetired(false);
		conceptService.saveConcept(concept);
		
		//when
		Concept foundConcept = service.getConcept(3);
		
		//then
		Assert.assertEquals(concept, foundConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(Integer)
	 * @verifies return retired
	 */
	@Test
	public void getConcept_shouldReturnRetired() throws Exception {
		//given
		Concept concept = conceptService.getConcept(3);
		conceptService.retireConcept(concept, "to test...");
		
		//when
		Concept foundConcept = service.getConcept(3);
		
		//then
		Assert.assertEquals(concept, foundConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(Integer)
	 * @verifies return null if not found
	 */
	@Test
	public void getConcept_shouldReturnNullIfNotFound() throws Exception {
		//given
		Concept concept = conceptService.getConcept(1);
		Assert.assertNull(concept);
		
		//when
		Concept foundConcept = service.getConcept(1);
		
		//then
		Assert.assertNull(foundConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(String)
	 * @verifies return non retired if retired also found by mapping
	 */
	@Test
	public void getConcept_shouldReturnNonRetiredIfRetiredAlsoFoundByMapping() throws Exception {
		//given
		Concept concept = conceptService.getConcept(4);
		conceptAdapter.addMapping(concept, localConceptSource, "3");
		Concept retiredConcept1 = conceptService.getConcept(3);
		conceptService.retireConcept(retiredConcept1, "to test...");
		conceptAdapter.addMapping(retiredConcept1, localConceptSource, "3");
		Concept retiredConcept2 = conceptService.getConcept(5);
		conceptService.retireConcept(retiredConcept2, "to test...");
		conceptAdapter.addMapping(retiredConcept2, localConceptSource, "3");
		
		//when
		Concept foundConcept = service.getConcept("my-dict:3");
		
		//then
		Assert.assertEquals(concept, foundConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(String)
	 * @verifies return retired if no other found by mapping
	 */
	@Test
	public void getConcept_shouldReturnRetiredIfNoOtherFoundByMapping() throws Exception {
		//given
		Concept retiredConcept1 = conceptService.getConcept(3);
		conceptService.retireConcept(retiredConcept1, "to test...");
		conceptAdapter.addMapping(retiredConcept1, localConceptSource, "3");
		Concept retiredConcept2 = conceptService.getConcept(5);
		conceptService.retireConcept(retiredConcept2, "to test...");
		conceptAdapter.addMapping(retiredConcept2, localConceptSource, "3");
		Concept retiredConcept3 = conceptService.getConcept(4);
		conceptService.retireConcept(retiredConcept3, "to test...");
		conceptAdapter.addMapping(retiredConcept3, localConceptSource, "3");
		Set<Concept> retiredConcepts = new HashSet<Concept>();
		retiredConcepts.addAll(Arrays.asList(retiredConcept1, retiredConcept2, retiredConcept3));
		
		//when
		Concept foundConcept = service.getConcept("my-dict:3");
		
		//then
		Assert.assertNotNull(foundConcept);
		Assert.assertTrue(retiredConcepts.contains(foundConcept));
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getConcept(String)
	 * @verifies return null if nothing found
	 */
	@Test
	public void getConcept_shouldReturnNullIfNothingFound() throws Exception {
		//given
		
		//when
		Concept foundConcept = service.getConcept("non-exisitng-concept-source:1234");
		
		//then
		Assert.assertNull(foundConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getLocalConceptSource()
	 * @verifies return local source if gp set
	 */
	@Test
	public void getLocalConceptSource_shouldReturnLocalConceptSourceIfGpSet() throws Exception {
		//given
		
		//when
		ConceptSource source = service.getLocalConceptSource();
		
		//then
		Assert.assertEquals(localConceptSource, source);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getLocalConceptSource()
	 * @verifies fail if gp is not set
	 */
	@Test(expected = APIException.class)
	public void getLocalConceptSource_shouldFailIfGpIsNotSet() throws Exception {
		Context.clearSession();
		
		//given
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, ""));
		
		//when
		service.getLocalConceptSource();
		
		//then
		Assert.fail();
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getSubscribedConceptSources()
	 * @verifies return set if gp defined
	 */
	@Test
	public void getSubscribedConceptSources_shouldReturnSetIfGpDefined() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		//when
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		
		//then
		Assert.assertEquals(2, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
		Assert.assertTrue(subscribedConceptSources.contains(source2));
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getSubscribedConceptSources()
	 * @verifies return empty set if gp not defined
	 */
	@Test
	public void getSubscribedConceptSources_shouldReturnEmptySetIfGpNotDefined() throws Exception {
		//given
		
		//when
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		
		//then
		Assert.assertEquals(0, subscribedConceptSources.size());
	}
	
	/**
	 * @see MetadataMappingServiceImpl#isLocalConcept(Concept)
	 * @verifies return true if local
	 */
	@Test
	public void isLocalConcept_shouldReturnTrueIfLocal() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		Concept concept = conceptService.getConcept(3);
		
		//when
		boolean localConcept = service.isLocalConcept(concept);
		
		//then
		Assert.assertTrue(localConcept);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#isLocalConcept(Concept)
	 * @verifies return false if not local
	 */
	@Test
	public void isLocalConcept_shouldReturnFalseIfNotLocal() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		Concept concept = conceptService.getConcept(3);
		conceptAdapter.addMapping(concept, source2, concept.getId().toString());
		
		//when
		boolean localConcept = service.isLocalConcept(concept);
		
		//then
		Assert.assertFalse(localConcept);
	}
	
	/**
	 * @see MetadataMappingService# addSubscribedConceptSource(ConceptSource)
	 * @verifies add subscribed concept source
	 */
	@Test
	public void addSubscribedConceptSource_shouldAddSubscribedConceptSource() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		Assert.assertEquals(0, subscribedConceptSources.size());
		
		//when
		boolean added = service.addSubscribedConceptSource(source1);
		
		//then
		Assert.assertTrue(added);
		
		subscribedConceptSources = service.getSubscribedConceptSources();
		
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
	}
	
	/**
	 * @see MetadataMappingService# addSubscribedConceptSource(ConceptSource)
	 * @verifies return false if subscribed concept source present
	 */
	@Test
	public void addSubscribedConceptSource_shouldReturnFalseIfSubscribedConceptSourcePresent() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		
		service.addSubscribedConceptSource(source1);
		
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
		
		//when
		boolean added = service.addSubscribedConceptSource(source1);
		
		//then
		Assert.assertFalse(added);
		
		subscribedConceptSources = service.getSubscribedConceptSources();
		
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
	}
	
	/**
	 * @see MetadataMappingService#removeSubscribedConceptSource(ConceptSource)
	 * @verifies remove subscribed concept source
	 */
	@Test
	public void removeSubscribedConceptSource_shouldRemoveSubscribedConceptSource() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		service.addSubscribedConceptSource(source1);
		service.addSubscribedConceptSource(source2);
		
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		Assert.assertEquals(2, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
		Assert.assertTrue(subscribedConceptSources.contains(source2));
		
		//when
		boolean removed = service.removeSubscribedConceptSource(source1);
		
		//then
		Assert.assertTrue(removed);
		
		subscribedConceptSources = service.getSubscribedConceptSources();
		
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source2));
	}
	
	/**
	 * @see MetadataMappingService#removeSubscribedConceptSource(ConceptSource)
	 * @verifies return false if subscribed concept source not present
	 */
	@Test
	public void removeSubscribedConceptSource_shouldReturnFalseIfSubscribedConceptSourceNotPresent() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		service.addSubscribedConceptSource(source1);
		
		Set<ConceptSource> subscribedConceptSources = service.getSubscribedConceptSources();
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
		
		//when
		boolean removed = service.removeSubscribedConceptSource(source2);
		
		//then
		Assert.assertFalse(removed);
		
		subscribedConceptSources = service.getSubscribedConceptSources();
		
		Assert.assertEquals(1, subscribedConceptSources.size());
		Assert.assertTrue(subscribedConceptSources.contains(source1));
	}
	
	@Test
	@Verifies(value = "save valid new object", method = "saveMetadataSource(MetadataSource)")
	public void saveMetadataSource_shouldSaveValidNewObject() {
		// given
		MetadataSource metadataSource = new MetadataSource();
		metadataSource.setName("my-source");
		Assert.assertNull(metadataSource.getId());
		
		// when
		metadataSource = service.saveMetadataSource(metadataSource);
		
		// then
		Assert.assertNotNull(metadataSource.getId());
		Assert.assertNotNull(metadataSource.getUuid());
	}
	
	@Test
	@Verifies(value = "respect includeRetired flag", method = "MetadataMappingService#getMetadataSources(boolean)")
	public void getMetadataSources_shouldRespectIncludeRetiredFlag() {
		// given
		// data in the test data set
		
		// when
		List<MetadataSource> nonRetiredSources = service.getMetadataSources(false);
		List<MetadataSource> allSources = service.getMetadataSources(true);
		
		// then
		Assert.assertEquals(2, nonRetiredSources.size());
		for (MetadataSource metadataSource : nonRetiredSources) {
			Assert.assertFalse("metadata source " + metadataSource.getId() + " is not retired", metadataSource.isRetired());
		}
		
		Assert.assertEquals(3, allSources.size());
		allSources.removeAll(nonRetiredSources);
		Assert.assertEquals("after non-retired source have been removed, only retired sources remain", 1, allSources.size());
		for (MetadataSource metadataSource : allSources) {
			Assert.assertTrue("metadata source " + metadataSource.getId() + " is retired", metadataSource.isRetired());
		}
	}
	
	@Test
	@Verifies(value = "retire and set info", method = "retireMetadataSource(MetadataSource, String)")
	public void retireMetadataSource_shouldRetireAndSetInfo() {
		// given
		MetadataSource metadataSource = service.getMetadataSource(1);
		
		// when
		metadataSource = service.retireMetadataSource(metadataSource, "testing the retire method");
		
		// then
		Assert.assertTrue(metadataSource.isRetired());
		Assert.assertNotNull(metadataSource.getRetiredBy());
		Assert.assertEquals("testing the retire method", metadataSource.getRetireReason());
	}
	
	@Test
	@Verifies(value = "save valid new object", method = "saveMetadataTermMapping(MetadataTermMapping)")
	public void saveMetadataTermMapping_shouldSaveValidNewObject() {
		// given
		MetadataSource metadataSource = new MetadataSource();
		metadataSource.setName("my-source");
		
		Location location = new Location();
		location.setUuid("some-uuid");
		
		MetadataTermMapping metadataTermMapping = new MetadataTermMapping(metadataSource, "my code", location);
		metadataTermMapping.setName("some term");
		
		Assert.assertNull(metadataTermMapping.getId());
		
		// when
		metadataTermMapping = service.saveMetadataTermMapping(metadataTermMapping);
		
		// then
		Assert.assertNotNull(metadataTermMapping.getId());
		Assert.assertNotNull(metadataTermMapping.getUuid());
	}
	
	@Test
	@Verifies(value = "save mapping without a referred object", method = "saveMetadataTermMapping(MetadataTermMapping)")
	public void saveMetadataTermMapping_shouldSaveMappingWithoutReferredObject() {
		// given
		MetadataSource metadataSource = new MetadataSource();
		metadataSource.setName("my-source");
		
		MetadataTermMapping metadataTermMapping = new MetadataTermMapping(metadataSource,
		        "my code without a referred object");
		metadataTermMapping.setName("some term without a referred object");
		
		Assert.assertNull(metadataTermMapping.getId());
		
		// when
		metadataTermMapping = service.saveMetadataTermMapping(metadataTermMapping);
		
		// then
		Assert.assertNotNull(metadataTermMapping.getId());
		Assert.assertNull(metadataTermMapping.getMetadataClass());
		Assert.assertNull(metadataTermMapping.getMetadataUuid());
	}
	
	@Test
	@Verifies(value = "fail if code is not unique within source", method = "saveMetadataTermMapping(MetadataTermMapping)")
	public void saveMetadataTermMapping_shouldFailIfCodeIsNotUniqueWithinSource() throws Exception {
		// This case only serves a documentation purpose: the constraint is enforced in the database schema
		// and can not be verified by this integration test.
	}
	
	@Test
	@Verifies(value = "return matching metadata term mapping", method = "getMetadataTermMappingByUuid(String)")
	public void getMetadataTermMappingByUuid_shouldReturnMatchingMetadataTermMapping() {
		// given
		// data in the test data set
		
		// when
		MetadataTermMapping neverNeverLandTermMapping = service
		        .getMetadataTermMappingByUuid("2d93cda0-1316-4ed1-82ff-47f78068efaa");
		
		// then
		Assert.assertNotNull("getMetadataTermMappingByUuid returned a term object", neverNeverLandTermMapping);
		Assert.assertEquals("term name is as expected", "Location Never Never Land", neverNeverLandTermMapping.getName());
	}
	
	@Test
	@Verifies(value = "retire and set info", method = "retireMetadataTermMapping(MetadataTermMapping, String)")
	public void retireMetadataTermMapping_shouldRetireAndSetInfo() {
		// given
		MetadataTermMapping metadataTermMapping = service.getMetadataTermMapping(1);
		
		// when
		metadataTermMapping = service.retireMetadataTermMapping(metadataTermMapping, "testing the retire method");
		
		// then
		Assert.assertTrue(metadataTermMapping.isRetired());
		Assert.assertNotNull(metadataTermMapping.getRetiredBy());
		Assert.assertEquals("testing the retire method", metadataTermMapping.getRetireReason());
	}
	
	@Test
	@Verifies(value = "return term mappings matching every criteria", method = "getMetadataTermMappings"
	        + "(MetadataTermMappingSearchCriteria)")
	public void getMetadataTermMappings_shouldReturnTermMappingsMatchingEveryCriteria() {
		// given
		// data in the test data set, and the following
		MetadataTermMappingSearchCriteriaBuilder searchCriteriaBuilder = new MetadataTermMappingSearchCriteriaBuilder();
		// when
		List<MetadataTermMapping> termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(6, termMappings.size());
		
		// given
		searchCriteriaBuilder.setIncludeAll(true);
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(8, termMappings.size());
		
		// given
		searchCriteriaBuilder.setMaxResults(2);
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(2, termMappings.size());
		Assert.assertEquals("mdt-xan", termMappings.get(0).getCode());
		
		// given
		searchCriteriaBuilder.setFirstResult(2);
		searchCriteriaBuilder.setMaxResults(3);
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(3, termMappings.size());
		Assert.assertEquals("xyz", termMappings.get(0).getCode());
		Assert.assertEquals("mdt-nnl", termMappings.get(2).getCode());
		
		// given
		Location neverNeverLand = locationService.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
		searchCriteriaBuilder.setReferredObject(neverNeverLand);
		searchCriteriaBuilder.setIncludeAll(false);
		searchCriteriaBuilder.setFirstResult(0);
		searchCriteriaBuilder.setMaxResults(null);
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(2, termMappings.size());
		Assert.assertEquals("Integration Test Metadata Source 1", termMappings.get(0).getMetadataSource().getName());
		Assert.assertEquals("mdt-nnl", termMappings.get(0).getCode());
		Assert.assertEquals("Integration Test Metadata Source 2", termMappings.get(1).getMetadataSource().getName());
		Assert.assertEquals("mdt-nnl", termMappings.get(1).getCode());
		
		// given
		searchCriteriaBuilder.setMetadataSource(service.getMetadataSourceByName("Integration Test Metadata Source 2"));
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(1, termMappings.size());
		Assert.assertEquals("Integration Test Metadata Source 2", termMappings.get(0).getMetadataSource().getName());
		Assert.assertEquals("mdt-nnl", termMappings.get(0).getCode());
	}
	
	@Test
	@Verifies(value = "return unretired term mappings referring to object", method = "getMetadataTermMappings"
	        + "(OpenmrsMetadata)")
	@SuppressWarnings("deprecation")
	public void getMetadataTermMappings_shouldReturnUnretiredTermMappingsReferringToObject() {
		// given
		// data in the test data set, and the following
		Location neverNeverLand = locationService.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
		
		// when
		List<MetadataTermMapping> neverNeverLandTermMappings = service.getMetadataTermMappings(neverNeverLand);
		
		// then
		Assert.assertEquals(2, neverNeverLandTermMappings.size());
		
		// The test case makes an assumption on the order of the terms
		MetadataTermMapping termFromSource1 = neverNeverLandTermMappings.get(0);
		MetadataTermMapping termFromSource2 = neverNeverLandTermMappings.get(1);
		
		Assert.assertEquals(Integer.valueOf(1), termFromSource1.getMetadataSource().getId());
		Assert.assertEquals("mdt-nnl", termFromSource1.getCode());
		
		Assert.assertEquals(Integer.valueOf(2), termFromSource2.getMetadataSource().getId());
		Assert.assertEquals("mdt-nnl", termFromSource2.getCode());
	}
	
	@Test
	@Verifies(value = "return a retired term mapping", method = "getMetadataTermMapping(MetadataSource, String)")
	public void getMetadataTermMapping_shouldReturnARetiredTermMapping() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		
		// when
		MetadataTermMapping metadataTermMapping = service.getMetadataTermMapping(metadataSource, "xyz");
		
		// then
		Assert.assertNotNull(metadataTermMapping);
		Assert.assertTrue(metadataTermMapping.isRetired());
		Assert.assertEquals("xyz", metadataTermMapping.getCode());
	}
	
	@Test
	@Verifies(value = "return only unretired term mappings", method = "getMetadataTermMappings(MetadataSource)")
	@SuppressWarnings("deprecation")
	public void getMetadataTermMappings_shouldReturnOnlyUnretiredTermMappings() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		
		// when
		List<MetadataTermMapping> metadataTermMappings = service.getMetadataTermMappings(metadataSource);
		
		// then
		Assert.assertNotNull(metadataTermMappings);
		Assert.assertEquals(2, metadataTermMappings.size());
		
		for (MetadataTermMapping metadataTermMapping : metadataTermMappings) {
			Assert.assertFalse("MetadataTermMapping " + metadataTermMapping.getId() + " is not retired", metadataTermMapping
			        .isRetired());
		}
	}
	
	@Test
	@Verifies(value = "return unretired metadata item for unretired term", method = "getMetadataItem(Class, String, "
	        + "String)")
	public void getMetadataItem_shouldReturnUnretiredMetadataItemForUnretiredTerm() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		MetadataTermMapping xanaduTermMapping = service.getMetadataTermMapping(metadataSource, "mdt-xan");
		Assert.assertFalse(xanaduTermMapping.isRetired());
		
		// when
		Location xanadu = service.getMetadataItem(Location.class, metadataSource.getName(), xanaduTermMapping.getCode());
		
		// then
		Assert.assertNotNull(xanadu);
		Assert.assertFalse("metadata item is not retired", xanadu.isRetired());
		Assert.assertEquals("Xanadu", xanadu.getName());
	}
	
	@Test
	@Verifies(value = "not return retired metadata item for unretired term", method = "getMetadataItem(Class, String, "
	        + "String)")
	public void getMetadataItem_shouldNotReturnRetiredMetadataItemForUnretiredTerm() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		MetadataTermMapping neverNeverLandTermMapping = service.getMetadataTermMapping(metadataSource, "mdt-nnl");
		Assert.assertFalse(neverNeverLandTermMapping.isRetired());
		
		// when
		Location neverNeverLand = service.getMetadataItem(Location.class, metadataSource.getName(),
		    neverNeverLandTermMapping.getCode());
		
		// then
		Assert.assertNull("requesting a retired metadata item returns null", neverNeverLand);
	}
	
	@Test
	@Verifies(value = "not return unretired metadata item for retired term", method = "getMetadataItem(Class, String, "
	        + "String)")
	public void getMetadataItem_shouldNotReturnUnretiredMetadataItemForRetiredTerm() {
		// given
		// data in the test data set, and the following
		MetadataTermMapping retiredXanaduTermMapping = service
		        .getMetadataTermMappingByUuid("c8d56f38-682c-4460-af0b-4cfd5328bedb");
		Assert.assertTrue("metadata term is retired", retiredXanaduTermMapping.isRetired());
		Location xanaduGiven = locationService.getLocationByUuid(retiredXanaduTermMapping.getMetadataUuid());
		Assert.assertFalse("actual metadata item is not retired", xanaduGiven.isRetired());
		
		// when
		Location xanadu = service.getMetadataItem(Location.class, retiredXanaduTermMapping.getMetadataSource().getName(),
		    retiredXanaduTermMapping.getCode());
		
		// then
		Assert.assertNull("requesting a metadata item for a retired metadata term returns null", xanadu);
	}
	
	@Test
	@Verifies(value = "fail on type mismatch", method = "getMetadataItem(Class, String, String)")
	@ExpectedException(InvalidMetadataTypeException.class)
	public void getMetadataItem_shouldFailOnTypeMismatch() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		MetadataTermMapping xanaduTermMapping = service.getMetadataTermMapping(metadataSource, "mdt-xan");
		
		// when
		Drug xanadu = service.getMetadataItem(Drug.class, metadataSource.getName(), xanaduTermMapping.getCode());
		
		// then
		// should never get here as the method invocation should have failed
	}
	
	@Test
	@Verifies(value = "return null if term does not exist", method = "getMetadataItem(Class, String, String)")
	public void getMetadataItem_shouldReturnNullIfTermDoesNotExist() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		
		// when
		Location location = service.getMetadataItem(Location.class, metadataSource.getName(), "unknown-code");
		
		// then
		Assert.assertNull(location);
	}
	
	@Test
	@Verifies(value = "return unretired metadata items of unretired terms matching type", method = "getMetadataItems(Class,"
	        + " String)")
	public void getMetadataItems_shouldReturnUnretiredMetadataItemsOfUnretiredTermsMatchingType() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSourceByUuid("9cace0bd-6f2a-4cc3-a26d-6fa292f1f2c1");
		
		// when
		List<Location> locations = service.getMetadataItems(Location.class, metadataSource.getName());
		List<Drug> drugs = service.getMetadataItems(Drug.class, metadataSource.getName());
		
		// then
		Assert.assertEquals(1, locations.size());
		for (Location location : locations) {
			Assert.assertFalse(location.isRetired());
		}
		Assert.assertEquals(2, drugs.size());
		for (Drug drug : drugs) {
			Assert.assertFalse(drug.isRetired());
		}
	}
	
	@Test
	@Verifies(value = "return nothing if source does not exist", method = "getMetadataItems(Class, String)")
	public void getMetadataItems_shouldReturnNothingIfSourceDoesNotExist() {
		// given
		// data in the test data set
		
		// when
		List<Location> locations = service.getMetadataItems(Location.class, "unknown source");
		
		// then
		Assert.assertEquals(0, locations.size());
	}
}
