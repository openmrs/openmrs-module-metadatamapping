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
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.api.adapter.ConceptAdapter;
import org.openmrs.module.metadatamapping.api.impl.MetadataMappingServiceImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
	private ConceptAdapter conceptAdapter;
	
	private ConceptSource localeSource;
	
	@Before
	public void setupLocalSource() {
		localeSource = new ConceptSource();
		localeSource.setName("my-dict");
		conceptService.saveConceptSource(localeSource);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, localeSource.getUuid()));
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
		conceptAdapter.addMapping(concept, localeSource, "3");
		Concept retiredConcept1 = conceptService.getConcept(3);
		conceptService.retireConcept(retiredConcept1, "to test...");
		conceptAdapter.addMapping(retiredConcept1, localeSource, "3");
		Concept retiredConcept2 = conceptService.getConcept(5);
		conceptService.retireConcept(retiredConcept2, "to test...");
		conceptAdapter.addMapping(retiredConcept2, localeSource, "3");
		
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
		conceptAdapter.addMapping(retiredConcept1, localeSource, "3");
		Concept retiredConcept2 = conceptService.getConcept(5);
		conceptService.retireConcept(retiredConcept2, "to test...");
		conceptAdapter.addMapping(retiredConcept2, localeSource, "3");
		Concept retiredConcept3 = conceptService.getConcept(4);
		conceptService.retireConcept(retiredConcept3, "to test...");
		conceptAdapter.addMapping(retiredConcept3, localeSource, "3");
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
	 * @see MetadataMappingServiceImpl#getLocalSource()
	 * @verifies return local source if gp set
	 */
	@Test
	public void getLocalSource_shouldReturnLocalSourceIfGpSet() throws Exception {
		//given
		
		//when
		ConceptSource source = service.getLocalSource();
		
		//then
		Assert.assertEquals(localeSource, source);
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getLocalSource()
	 * @verifies fail if gp is not set
	 */
	@Test(expected = APIException.class)
	public void getLocalSource_shouldFailIfGpIsNotSet() throws Exception {
		Context.clearSession();
		
		//given
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, ""));
		
		//when
		service.getLocalSource();
		
		//then
		Assert.fail();
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getSubscribedSources()
	 * @verifies return set if gp defined
	 */
	@Test
	public void getSubscribedSources_shouldReturnSetIfGpDefined() throws Exception {
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
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		
		//then
		Assert.assertEquals(2, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
		Assert.assertTrue(subscribedSources.contains(source2));
	}
	
	/**
	 * @see MetadataMappingServiceImpl#getSubscribedSources()
	 * @verifies return empty set if gp not defined
	 */
	@Test
	public void getSubscribedSources_shouldReturnEmptySetIfGpNotDefined() throws Exception {
		//given
		
		//when
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		
		//then
		Assert.assertEquals(0, subscribedSources.size());
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
	 * @see MetadataMappingService#addSubscribedSource(ConceptSource)
	 * @verifies add subscribed source
	 */
	@Test
	public void addSubscribedSource_shouldAddSubscribedSource() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		Assert.assertEquals(0, subscribedSources.size());
		
		//when
		boolean added = service.addSubscribedSource(source1);
		
		//then
		Assert.assertTrue(added);
		
		subscribedSources = service.getSubscribedSources();
		
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
	}
	
	/**
	 * @see MetadataMappingService#addSubscribedSource(ConceptSource)
	 * @verifies return false if subscribed source present
	 */
	@Test
	public void addSubscribedSource_shouldReturnFalseIfSubscribedSourcePresent() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		
		service.addSubscribedSource(source1);
		
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
		
		//when
		boolean added = service.addSubscribedSource(source1);
		
		//then
		Assert.assertFalse(added);
		
		subscribedSources = service.getSubscribedSources();
		
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
	}
	
	/**
	 * @see MetadataMappingService#removeSubscribedSource(ConceptSource)
	 * @verifies remove subscribed source
	 */
	@Test
	public void removeSubscribedSource_shouldRemoveSubscribedSource() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		service.addSubscribedSource(source1);
		service.addSubscribedSource(source2);
		
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		Assert.assertEquals(2, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
		Assert.assertTrue(subscribedSources.contains(source2));
		
		//when
		boolean removed = service.removeSubscribedSource(source1);
		
		//then
		Assert.assertTrue(removed);
		
		subscribedSources = service.getSubscribedSources();
		
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source2));
	}
	
	/**
	 * @see MetadataMappingService#removeSubscribedSource(ConceptSource)
	 * @verifies return false if subscribed source not present
	 */
	@Test
	public void removeSubscribedSource_shouldReturnFalseIfSubscribedSourceNotPresent() throws Exception {
		//given
		ConceptSource source1 = new ConceptSource();
		source1.setName("their-dict");
		conceptService.saveConceptSource(source1);
		ConceptSource source2 = new ConceptSource();
		source2.setName("their-2nd-dict");
		conceptService.saveConceptSource(source2);
		
		service.addSubscribedSource(source1);
		
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
		
		//when
		boolean removed = service.removeSubscribedSource(source2);
		
		//then
		Assert.assertFalse(removed);
		
		subscribedSources = service.getSubscribedSources();
		
		Assert.assertEquals(1, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
	}
}
