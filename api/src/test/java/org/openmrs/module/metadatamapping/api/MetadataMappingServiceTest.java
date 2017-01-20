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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
import org.openmrs.module.metadatamapping.api.impl.MetadataMappingServiceImpl;
import org.openmrs.module.metadatamapping.api.wrapper.ConceptAdapter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
		MetadataSourceSearchCriteriaBuilder searchCriteriaBuilder = new MetadataSourceSearchCriteriaBuilder();
		
		searchCriteriaBuilder.setIncludeAll(false);
		List<MetadataSource> nonRetiredSources = service.getMetadataSources(searchCriteriaBuilder.build());
		
		searchCriteriaBuilder.setIncludeAll(true);
		List<MetadataSource> allSources = service.getMetadataSources(searchCriteriaBuilder.build());
		
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
	@Verifies(value = "throw exception if no matching source", method = "mapMetadataItem")
	@ExpectedException(IllegalArgumentException.class)
	public void mapMetadataItem_shouldThrowExceptionIfNoMatchingSource() {
		service.mapMetadataItem(locationService.getLocation(1), "non-existing-source", "some-mapping");
	}
	
	@Test
	@Verifies(value = "throw exception if referredObject is null", method = "mapMetadataItem")
	@ExpectedException(IllegalArgumentException.class)
	public void mapMetadataItem_shouldThrowExceptionIfReferredObjectIsNull() {
		service.mapMetadataItem(null, "Integration Test Metadata Source 1", "some-mapping");
	}
	
	@Test
	@Verifies(value = "create new metadata term mapping", method = "mapMetadataItem")
	public void mapMetadataItem_shouldCreateNewMetadataTermMapping() {
		
		Location xanadu = locationService.getLocation(1);
		MetadataTermMapping newMapping = service.mapMetadataItem(xanadu, "Integration Test Metadata Source 1",
		    "some-mapping");
		
		Assert.assertNotNull(newMapping.getUuid());
		Assert.assertThat(newMapping.getMetadataSource().getName(), is("Integration Test Metadata Source 1"));
		Assert.assertThat(newMapping.getCode(), is("some-mapping"));
		Assert.assertThat(newMapping.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(newMapping.getMetadataUuid(), is(xanadu.getUuid()));
	}
	
	@Test
	@Verifies(value = "update existing metadata term mapping", method = "mapMetadataItem")
	public void mapMetadataItem_shouldUpdateExistingMetadataTermMapping() {
		
		Location xanadu = locationService.getLocation(1);
		MetadataTermMapping updatedMapping = service
		        .mapMetadataItem(xanadu, "Integration Test Metadata Source 1", "mdt-nnl"); // update the existing "neverland" mappiung to point to "xanadu"
		
		Assert.assertThat(updatedMapping.getUuid(), is("2d93cda0-1316-4ed1-82ff-47f78068efaa"));
		Assert.assertThat(updatedMapping.getMetadataSource().getName(), is("Integration Test Metadata Source 1"));
		Assert.assertThat(updatedMapping.getCode(), is("mdt-nnl"));
		Assert.assertThat(updatedMapping.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(updatedMapping.getMetadataUuid(), is(xanadu.getUuid()));
	}
	
	@Test
	@Verifies(value = "throw exception if no matching source", method = "mapMetadataItems")
	@ExpectedException(IllegalArgumentException.class)
	public void mapMetadataItems_shouldThrowExceptionIfNoMatchingSource() {
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		metadataList.add(locationService.getLocation(1));
		service.mapMetadataItems(metadataList, "non-existing-source", "some-mapping");
	}
	
	@Test
	@Verifies(value = "throw exception if referredObjects is null", method = "mapMetadataItems")
	@ExpectedException(IllegalArgumentException.class)
	public void mapMetadataItems_shouldThrowExceptionIfReferredObjectsIsNull() {
		service.mapMetadataItems(null, "Integration Test Metadata Source 1", "some-mapping");
	}
	
	@Test
	@Verifies(value = "throw exception if referredObjects is empty list", method = "mapMetadataItems")
	@ExpectedException(IllegalArgumentException.class)
	public void mapMetadataItems_shouldThrowExceptionIfReferredObjectsIsEmptyList() {
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		service.mapMetadataItems(metadataList, "Integration Test Metadata Source 1", "some-mapping");
	}
	
	@Test
	@Verifies(value = "should create new metadata mapping to metadata set ", method = "mapMetadataItem")
	public void mapMetadataItems_shouldCreateNewMetadataMappingToMetadataSet() {
		
		Location location1 = locationService.getLocation(1);
		Location location2 = locationService.getLocation(2);
		
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		metadataList.add(location1);
		metadataList.add(location2);
		MetadataTermMapping newMapping = service.mapMetadataItems(metadataList, "Integration Test Metadata Source 1",
		    "some-mapping");
		
		Assert.assertNotNull(newMapping.getUuid());
		Assert.assertThat(newMapping.getMetadataSource().getName(), is("Integration Test Metadata Source 1"));
		Assert.assertThat(newMapping.getCode(), is("some-mapping"));
		Assert.assertThat(newMapping.getMetadataClass(), is(MetadataSet.class.getName()));
		
		MetadataSet metadataSet = service.getMetadataItem(MetadataSet.class, "Integration Test Metadata Source 1",
		    "some-mapping");
		Assert.assertNotNull(metadataSet);
		
		List<Location> locations = service.getMetadataSetItems(Location.class, metadataSet);
		Assert.assertNotNull(locations);
		Assert.assertThat(locations.size(), is(2));
		Assert.assertTrue(locations.contains(location1));
		Assert.assertTrue(locations.contains(location2));
	}
	
	@Test
	@Verifies(value = "should throw exception if existing metadata mapping is not set", method = "mapMetadataItem")
	@ExpectedException(InvalidMetadataTypeException.class)
	public void mapMetadataItems_shouldThrowExceptionIfExistingMetadataMappingIsNotSet() {
		
		Location xanadu = locationService.getLocation(1);
		Location neverland = locationService.getLocation(2);
		
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		metadataList.add(xanadu);
		metadataList.add(neverland);
		service.mapMetadataItems(metadataList, "Integration Test Metadata Source 1", "mdt-xan");
	}
	
	@Test
	@Verifies(value = "should add new items to metadata set", method = "mapMetadataItem")
	public void mapMetadataItems_shouldAddNewItemsToMetadataSet() {
		
		Location location1 = locationService.getLocation(1);
		Location location2 = locationService.getLocation(2);
		Location location5 = locationService.getLocation(5);
		
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		metadataList.add(location1);
		metadataList.add(location2);
		metadataList.add(location5);
		
		MetadataTermMapping existingMapping = service.mapMetadataItems(metadataList, "Integration Test Metadata Source 1",
		    "location-set");
		Assert.assertThat(existingMapping.getUuid(), is("3bd2888a-80ea-496a-ada5-cf6e6c5c02b0"));
		
		Context.flushSession();
		
		MetadataSet metadataSet = service.getMetadataItem(MetadataSet.class, "Integration Test Metadata Source 1",
		    "location-set");
		
		Assert.assertNotNull(metadataSet);
		Assert.assertThat(metadataSet.getUuid(), is("efad9246-8346-4288-9d74-fd81dda3568b"));
		
		List<Location> locations = service.getMetadataSetItems(Location.class, metadataSet);
		Assert.assertNotNull(locations);
		Assert.assertThat(locations.size(), is(3));
		Assert.assertTrue(locations.contains(location1));
		Assert.assertTrue(locations.contains(location2));
		Assert.assertTrue(locations.contains(location5));
	}
	
	@Test
	@Verifies(value = "should retire items from metadata set", method = "mapMetadataItem")
	public void mapMetadataItems_shouldRetireItemsFromMetadataSet() {
		
		Location location2 = locationService.getLocation(2);
		
		// create list with only location 2
		List<OpenmrsMetadata> metadataList = new ArrayList<OpenmrsMetadata>();
		metadataList.add(location2);
		
		MetadataTermMapping existingMapping = service.mapMetadataItems(metadataList, "Integration Test Metadata Source 1",
		    "location-set");
		Assert.assertThat(existingMapping.getUuid(), is("3bd2888a-80ea-496a-ada5-cf6e6c5c02b0"));
		
		MetadataSet metadataSet = service.getMetadataItem(MetadataSet.class, "Integration Test Metadata Source 1",
		    "location-set");
		Assert.assertNotNull(metadataSet);
		Assert.assertThat(metadataSet.getUuid(), is("efad9246-8346-4288-9d74-fd81dda3568b"));
		
		List<Location> members = service.getMetadataSetItems(Location.class, metadataSet);
		Assert.assertNotNull(members);
		Assert.assertThat(members.size(), is(1));
		Assert.assertTrue(members.contains(location2));
		
		// confirm the old mapping (for location 5) is there but retired
		MetadataSetMember retiredMember = service.getMetadataSetMemberByUuid("58c0cf9d-c883-45e3-884e-92fc2e73566c");
		Assert.assertTrue(retiredMember.isRetired());
	}
	
	@Test
	@Verifies(value = "save valid new object", method = "saveMetadataTermMapping(MetadataTermMapping)")
	public void saveMetadataTermMapping_shouldSaveValidNewObject() {
		// given
		MetadataSource metadataSource = new MetadataSource();
		metadataSource.setName("my-source");
		
		MetadataTermMapping metadataTermMapping = new MetadataTermMapping(metadataSource, "my code", "org.openmrs.Drug");
		metadataTermMapping.setName("some term");
		
		Assert.assertNull(metadataTermMapping.getId());
		
		// when
		metadataTermMapping = service.saveMetadataTermMapping(metadataTermMapping);
		
		// then
		Assert.assertNotNull(metadataTermMapping.getId());
		Assert.assertNotNull(metadataTermMapping.getUuid());
	}
	
	@Test
	@Verifies(value = "save mapping without metadataUuid", method = "saveMetadataTermMapping(MetadataTermMapping)")
	public void saveMetadataTermMapping_shouldSaveMappingWithoutReferredObject() {
		// given
		MetadataSource metadataSource = new MetadataSource();
		metadataSource.setName("my-source");
		
		MetadataTermMapping metadataTermMapping = new MetadataTermMapping(metadataSource, "my code without metadataUuid",
		        "org.openmrs.Drug");
		metadataTermMapping.setName("some term without metadataUuid");
		
		Assert.assertNull(metadataTermMapping.getId());
		
		// when
		metadataTermMapping = service.saveMetadataTermMapping(metadataTermMapping);
		
		// then
		Assert.assertNotNull(metadataTermMapping.getId());
		Assert.assertNotNull(metadataTermMapping.getMetadataClass());
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
		Assert.assertEquals(9, termMappings.size());
		
		// given
		searchCriteriaBuilder.setIncludeAll(true);
		// when
		termMappings = service.getMetadataTermMappings(searchCriteriaBuilder.build());
		// then
		Assert.assertEquals(11, termMappings.size());
		
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
		Assert.assertEquals("mdt-väi", termMappings.get(2).getCode());
		
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
	@SuppressWarnings("deprecation")
	@Verifies(value = "return unretired term mappings referring to object", method = "getMetadataTermMappings"
	        + "(OpenmrsMetadata)")
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
		Assert.assertEquals(5, metadataTermMappings.size());
		
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
	@Verifies(value = "return retired metadata item for unretired term", method = "getMetadataItem(Class, String, "
	        + "String)")
	public void getMetadataItem_shouldReturnRetiredMetadataItemForUnretiredTerm() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSource(1);
		MetadataTermMapping neverNeverLandTermMapping = service.getMetadataTermMapping(metadataSource, "mdt-nnl");
		Assert.assertFalse(neverNeverLandTermMapping.isRetired());
		
		// when
		Location neverNeverLand = service.getMetadataItem(Location.class, metadataSource.getName(),
		    neverNeverLandTermMapping.getCode());
		
		// then
		Assert.assertNotNull("requesting a retired metadata item returns not null", neverNeverLand);
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
	@Verifies(value = "return metadata items of terms matching type", method = "getMetadataItems(Class," + " String)")
	public void getMetadataItems_shouldReturnMetadataItemsOfTermsMatchingType() {
		// given
		// data in the test data set, and the following
		MetadataSource metadataSource = service.getMetadataSourceByUuid("9cace0bd-6f2a-4cc3-a26d-6fa292f1f2c1");
		
		// when
		List<Location> locations = service.getMetadataItems(Location.class, metadataSource.getName());
		List<Drug> drugs = service.getMetadataItems(Drug.class, metadataSource.getName());
		
		// then
		Assert.assertEquals(2, locations.size());
		Assert.assertEquals(2, drugs.size());
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
	
	@Test
	@Verifies(value = "save valid new object", method = "saveMetadataSet(MetadataSet)")
	public void saveMetadataSet_shouldSaveValidNewObject() {
		// given
		MetadataSet metadataSet = new MetadataSet();
		Assert.assertNull(metadataSet.getId());
		
		// when
		metadataSet = service.saveMetadataSet(metadataSet);
		
		// then
		Assert.assertNotNull(metadataSet.getId());
		Assert.assertNotNull(metadataSet.getUuid());
	}
	
	@Test
	@Verifies(value = "fail if code is not unique within source", method = "saveMetadataSet(MetadataSet)")
	public void saveMetadataSet_shouldFailIfCodeIsNotUniqueWithinSource() {
		// This case only serves a documentation purpose: the constraint is enforced in the database schema
		// and can not be verified by this integration test.
	}
	
	@Test
	@Verifies(value = "return a retired set", method = "getMetadataSet(MetadataSource, String)")
	public void getMetadataSet_shouldReturnARetiredSet() {
		// given
		// data in the test data set
		
		// when
		MetadataSet retiredMetadataSet = service.getMetadataSetByUuid("b3f4aa58-ab02-4379-ae61-ec2e15c29c1e");
		
		// then
		Assert.assertNotNull(retiredMetadataSet);
		Assert.assertTrue(retiredMetadataSet.isRetired());
	}
	
	@Test
	@Verifies(value = "return matching metadata set", method = "getMetadataSetByUuid(String)")
	public void getMetadataSetByUuid_shouldReturnMatchingMetadataSet() {
		// given
		// data in the test data set
		
		// when
		MetadataSet locationsSet = service.getMetadataSetByUuid("2fb06283-befc-4273-9448-2fcbbe4c99d5");
		
		// then
		Assert.assertNotNull("getMetadataSetByUuid returned a set object", locationsSet);
	}
	
	@Test
	@Verifies(value = "retire and set info", method = "retireMetadataSet(MetadataSet, String)")
	public void retireMetadataSet_shouldRetireAndSetInfo() {
		// given
		MetadataSet metadataSet = service.getMetadataSet(1);
		
		// when
		metadataSet = service.retireMetadataSet(metadataSet, "testing the retire method");
		
		// then
		Assert.assertTrue(metadataSet.isRetired());
		Assert.assertNotNull(metadataSet.getRetiredBy());
		Assert.assertEquals("testing the retire method", metadataSet.getRetireReason());
	}
	
	@Test
	@Verifies(value = "retire members", method = "retireMetadataSet(MetadataSet, String)")
	public void retireMetadataSet_shouldRetireMembers() {
		// given
		MetadataSet metadataSet = service.getMetadataSet(1);
		Assert.assertFalse(metadataSet.isRetired());
		for (MetadataSetMember metadataSetMember : service.getMetadataSetMembers(metadataSet, 0, 1000,
		    RetiredHandlingMode.INCLUDE_RETIRED)) {
			Assert.assertFalse(metadataSetMember.isRetired());
		}
		
		// when
		metadataSet = service.retireMetadataSet(metadataSet, "testing the retire method");
		clearHibernateCache();
		
		// then
		Assert.assertTrue(metadataSet.isRetired());
		for (MetadataSetMember metadataSetMember : service.getMetadataSetMembers(metadataSet, 0, 1000,
		    RetiredHandlingMode.INCLUDE_RETIRED)) {
			Assert.assertTrue(metadataSetMember.isRetired());
			Assert.assertNotNull(metadataSetMember.getRetiredBy());
			Assert.assertEquals("testing the retire method", metadataSetMember.getRetireReason());
		}
	}
	
	@Test
	@Verifies(value = "get members in desired order 1", method = "getMetadataSetMembers(MetadataSet, int, int, "
	        + "RetiredHandlingMode)")
	public void getMetadataSetMembers_shouldGetMembersInDesiredOrder1() {
		new TestCase_getMetadataSetMembers() {
			
			@Override
			protected List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults) {
				return service.getMetadataSetMembers(metadataSet, firstResult, maxResults, RetiredHandlingMode.ONLY_ACTIVE);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "get members in desired order 2", method = "getMetadataSetMembers(String, int, int, RetiredHandlingMode)")
	public void getMetadataSetMembers_shouldGetMembersInDesiredOrder2() {
		new TestCase_getMetadataSetMembers() {
			
			@Override
			protected List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults) {
				return service.getMetadataSetMembers(metadataSet.getUuid(), firstResult, maxResults,
				    RetiredHandlingMode.ONLY_ACTIVE);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "respect retire fetch mode 1", method = "getMetadataSetMembers(MetadataSet, int, int, "
	        + "RetiredHandlingMode)")
	public void getMetadataSetMembers_shouldRespectRetireFetchMode1() throws Exception {
		new TestCase_getMetadataSetMembersInRetireModes() {
			
			@Override
			protected List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
			        RetiredHandlingMode retiredHandlingMode) {
				return service.getMetadataSetMembers(metadataSet, firstResult, maxResults, retiredHandlingMode);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "respect retire fetch mode2", method = "getMetadataSetMembers(String, String, int, int, "
	        + "RetiredHandlingMode)")
	public void getMetadataSetMembers_shouldRespectRetireFetchMode2() throws Exception {
		new TestCase_getMetadataSetMembersInRetireModes() {
			
			@Override
			protected List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
			        RetiredHandlingMode retiredHandlingMode) {
				return service.getMetadataSetMembers(metadataSet.getUuid(), firstResult, maxResults, retiredHandlingMode);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "get unretired metadata items of unretired terms matching type in sort weight order 1", method = "getMetadataSetItems(Class, MetadataSet, int, int)")
	public void getMetadataSetItems_shouldGetUnretiredMetadataItemsOfUnretiredTermsMatchingTypeInSortWeightOrder1() {
		new TestCase_getMetadataSetItems() {
			
			@Override
			<T extends OpenmrsMetadata> List<T> getItems(Class<T> type, MetadataSet metadataSet, Integer firstResult,
			        Integer maxResults) {
				return service.getMetadataSetItems(type, metadataSet, firstResult, maxResults);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "get unretired metadata items of unretired terms matching type in sort weight order 2", method = "getMetadataSetItems(Class, String, String, int, int)")
	public void getMetadataSetItems_shouldGetUnretiredMetadataItemsOfUnretiredTermsMatchingTypeInSortWeightOrder2() {
		new TestCase_getMetadataSetItems() {
			
			@Override
			<T extends OpenmrsMetadata> List<T> getItems(Class<T> type, MetadataSet metadataSet, Integer firstResult,
			        Integer maxResults) {
				return service.getMetadataSetItems(type, metadataSet, firstResult, maxResults);
			}
		}.run();
	}
	
	@Test
	@Verifies(value = "throw IllegalArgumentException if set does not exist", method = "getMetadataSetItems(Class, MetadataSet, int, int)")
	@ExpectedException(IllegalArgumentException.class)
	public void getMetadataSetItems_shouldThrowExceptionIfMetadataSetDoesNotExist() throws Exception {
		// given
		
		// when
		List<Location> locations = service.getMetadataSetItems(Location.class, null, 0, 1000);
		
		// then
		// expect exception thrown
	}
	
	@Test
	@Verifies(value = "return unretired metadata item for unretired set member", method = "getMetadataItem(Class, MetadataSetMember)")
	public void getMetadataSetItem_shouldReturnUnretiredMetadataItemForUnretiredSetMember() throws Exception {
		// given
		MetadataSetMember member = service.getMetadataSetMember(2);
		// when
		Location metadataItem = service.getMetadataItem(Location.class, member);
		// then
		Assert.assertNotNull(metadataItem);
		assertThat(member.getMetadataUuid(), is(metadataItem.getUuid()));
	}
	
	@Test
	@Verifies(value = "return retired metadata item for unretired set member", method = "getMetadataItem(Class, MetadataSetMember)")
	public void getMetadataSetItem_shouldReturnRetiredMetadataItemForUnretiredSetMember() throws Exception {
		// given
		MetadataSetMember member = service.getMetadataSetMember(4);
		Assert.assertThat(member.isRetired(), is(false));
		// when
		Location metadataItem = service.getMetadataItem(Location.class, member);
		// then
		Assert.assertNotNull(metadataItem);
		assertThat(member.getMetadataUuid(), is(metadataItem.getUuid()));
	}
	
	@Test
	@Verifies(value = "return unretired metadata item for retired set member", method = "getMetadataItem(Class, MetadataSetMember)")
	public void getMetadataSetItem_shouldReturnUnretiredMetadataItemForRetiredSetMember() throws Exception {
		// given
		MetadataSetMember member = service.getMetadataSetMember(5);
		Assert.assertThat(member.isRetired(), is(true));
		// when
		Location metadataItem = service.getMetadataItem(Location.class, member);
		// then
		Assert.assertNotNull(metadataItem);
		assertThat(member.getMetadataUuid(), is(metadataItem.getUuid()));
	}
	
	@Test
	@Verifies(value = "return null for non existent set member", method = "getMetadataItem(Class, MetadataSetMember)")
	public void getMetadataSetItem_shouldReturnNullForNonExistentSetMember() throws Exception {
		// given
		// test dataset
		
		// when
		Location metadataItem = service.getMetadataItem(Location.class, null);
		// then
		Assert.assertNull(metadataItem);
	}
	
	@Test
	@Verifies(value = "retire and set info", method = "retireMetadataSetMember(MetadataSetMember, String)")
	public void retireMetadataSetMember_shouldRetireAndSetInfo() {
		// given
		MetadataSetMember member = service.getMetadataSetMember(1);
		
		// when
		member = service.retireMetadataSetMember(member, "testing the retire method");
		
		// then
		Assert.assertTrue(member.isRetired());
		Assert.assertNotNull(member.getRetiredBy());
		Assert.assertEquals("testing the retire method", member.getRetireReason());
	}
	
	@Test
	public void getMetadataSetMembers_shouldGetAllMembersBySet() {
		
		// given
		MetadataSet set = service.getMetadataSet(4);
		
		Assert.assertThat(service.getMetadataSetMembers(set, RetiredHandlingMode.INCLUDE_RETIRED).size(), is(2));
		Assert.assertThat(service.getMetadataSetMembers(set, RetiredHandlingMode.ONLY_ACTIVE).size(), is(1));
	}
	
	@Test
	public void getMetadataSetMembers_shouldGetAllMembersBySetUuid() {
		Assert.assertThat(service.getMetadataSetMembers("efad9246-8346-4288-9d74-fd81dda3568b",
		    RetiredHandlingMode.INCLUDE_RETIRED).size(), is(2));
		Assert.assertThat(service.getMetadataSetMembers("efad9246-8346-4288-9d74-fd81dda3568b",
		    RetiredHandlingMode.ONLY_ACTIVE).size(), is(1));
	}
	
	private abstract class TestCase_getMetadataSetMembers {
		
		abstract List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults);
		
		void run() {
			// given
			MetadataSet metadataSet = service.getMetadataSet(1);
			
			// when
			List<MetadataSetMember> members = getMembers(metadataSet, null, null);
			
			// then
			Assert.assertEquals(5, members.size());
			
			Iterator<MetadataSetMember> memberIterator = members.iterator();
			Assert.assertEquals("e1e2cc7d-dfb4-4e26-85b1-727666ff066d", memberIterator.next().getUuid());
			Assert.assertEquals("0bc57eff-3088-460d-880b-56988d02851b", memberIterator.next().getUuid());
			Assert.assertEquals("f75d45fb-f478-438a-970c-1a6b4f61f503", memberIterator.next().getUuid());
			Assert.assertEquals("b0c99f16-14b8-49b2-8d14-1e7447ad6aa9", memberIterator.next().getUuid());
			Assert.assertEquals("e9bed2b0-2828-44b3-a499-e3a307600197", memberIterator.next().getUuid());
		}
	}
	
	private abstract class TestCase_getMetadataSetMembersInRetireModes {
		
		abstract List<MetadataSetMember> getMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
		        RetiredHandlingMode retiredHandlingMode);
		
		void run() {
			// given
			MetadataSet metadataSet = service.getMetadataSet(3);
			
			// when
			List<MetadataSetMember> membersWithRetired = getMembers(metadataSet, 0, 1000,
			    RetiredHandlingMode.INCLUDE_RETIRED);
			List<MetadataSetMember> membersOnlyActive = getMembers(metadataSet, 0, 1000, RetiredHandlingMode.ONLY_ACTIVE);
			
			// then
			Assert.assertEquals(2, membersWithRetired.size());
			Iterator<MetadataSetMember> memberWithRetiredIterator = membersWithRetired.iterator();
			Assert.assertEquals("r2d180c6-d5fb-4202-b1a6-80a06273c158", memberWithRetiredIterator.next().getMetadataUuid());
			Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8", memberWithRetiredIterator.next().getMetadataUuid());
			
			Assert.assertEquals(1, membersOnlyActive.size());
			Iterator<MetadataSetMember> memberOnlyActiveIterator = membersOnlyActive.iterator();
			Assert.assertEquals("r2d180c6-d5fb-4202-b1a6-80a06273c158", memberOnlyActiveIterator.next().getMetadataUuid());
		}
	}
	
	private abstract class TestCase_getMetadataSetItems {
		
		abstract <T extends OpenmrsMetadata> List<T> getItems(Class<T> type, MetadataSet metadataSet, Integer firstResult,
		        Integer maxResults);
		
		void run() {
			// given
			MetadataSet metadataSet = service.getMetadataSet(1);
			
			// when
			List<Location> locations = getItems(Location.class, metadataSet, 0, 1000);
			
			// then
			Assert.assertEquals(3, locations.size());
			Iterator<Location> locationIterator = locations.iterator();
			Assert.assertEquals("Xanadu", locationIterator.next().getName());
			Assert.assertEquals("Pohjola", locationIterator.next().getName());
			Assert.assertEquals("Väinölä", locationIterator.next().getName());
		}
	}
}
