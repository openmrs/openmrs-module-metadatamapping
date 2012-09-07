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
package org.openmrs.module.conceptpubsub.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import org.openmrs.module.conceptpubsub.ConceptPubSub;
import org.openmrs.module.conceptpubsub.api.adapter.ConceptAdapter;
import org.openmrs.module.conceptpubsub.api.impl.ConceptPubSubServiceImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptPubSubServiceTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptpubsub.ConceptPubSubService")
	private ConceptPubSubService service;
	
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
		
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.LOCAL_SOURCE_UUID_GP, localeSource.getUuid()));
	}
	
	@Test
	public void test() throws Exception {
		InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("messages.properties"), "UTF-8");
		BufferedReader buf = new BufferedReader(reader);
		String line = buf.readLine();
		while(line != null) {
			System.out.print(line);
			line = buf.readLine();
		}
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
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
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
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
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies fail if local source not configured
	 */
	@Test(expected = APIException.class)
	public void addLocalMappingToConcept_shouldFailIfLocalSourceNotConfigured() throws Exception {
		Context.clearSession();
		
		//given
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.LOCAL_SOURCE_UUID_GP, ""));
		Concept concept = conceptService.getConcept(3);
		
		//when
		service.addLocalMappingToConcept(concept);
		
		//then
		Assert.fail();
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
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
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
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
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
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
	 * @see ConceptPubSubServiceImpl#getConcept(String)
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
	 * @see ConceptPubSubServiceImpl#getConcept(String)
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
	 * @see ConceptPubSubServiceImpl#getConcept(String)
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
	 * @see ConceptPubSubServiceImpl#getLocalSource()
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
	 * @see ConceptPubSubServiceImpl#getLocalSource()
	 * @verifies fail if gp is not set
	 */
	@Test(expected = APIException.class)
	public void getLocalSource_shouldFailIfGpIsNotSet() throws Exception {
		Context.clearSession();
		
		//given
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.LOCAL_SOURCE_UUID_GP, ""));
		
		//when
		service.getLocalSource();
		
		//then
		Assert.fail();
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getSubscribedSources()
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
		
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.SUBSCRIBED_TO_SOURCE_UUIDS_GP, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		//when
		Set<ConceptSource> subscribedSources = service.getSubscribedSources();
		
		//then
		Assert.assertEquals(2, subscribedSources.size());
		Assert.assertTrue(subscribedSources.contains(source1));
		Assert.assertTrue(subscribedSources.contains(source2));
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getSubscribedSources()
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
	 * @see ConceptPubSubServiceImpl#isLocalConcept(Concept)
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
		
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.SUBSCRIBED_TO_SOURCE_UUIDS_GP, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		Concept concept = conceptService.getConcept(3);
		
		//when
		boolean localConcept = service.isLocalConcept(concept);
		
		//then
		Assert.assertTrue(localConcept);
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#isLocalConcept(Concept)
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
		
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.SUBSCRIBED_TO_SOURCE_UUIDS_GP, source1.getUuid()
		        + ", " + source2.getUuid()));
		
		Concept concept = conceptService.getConcept(3);
		conceptAdapter.addMapping(concept, source2, concept.getId().toString());
		
		//when
		boolean localConcept = service.isLocalConcept(concept);
		
		//then
		Assert.assertFalse(localConcept);
	}
}
