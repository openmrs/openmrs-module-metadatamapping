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
package org.openmrs.module.conceptpubsub.api.db.hibernate.interceptor;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.conceptpubsub.ConceptPubSub;
import org.openmrs.module.conceptpubsub.api.ConceptPubSubService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link LocalMappingHibernateInterceptor} in post 1.9.
 */
public class LocalMappingHibernateInterceptorPost19Test extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptpubsub.ConceptPubSubService")
	private ConceptPubSubService service;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService adminService;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	private ConceptSource localeSource;
	
	@Before
	public void setupLocalSource() {
		localeSource = new ConceptSource();
		localeSource.setName("my-dict");
		conceptService.saveConceptSource(localeSource);
		
		adminService.saveGlobalProperty(new GlobalProperty(ConceptPubSub.GP_LOCAL_SOURCE_UUID, localeSource.getUuid()));
	}
	
	@Test
	public void shouldRetireConceptReferenceTermIfConceptPurged() {
		//given
		Concept concept = new Concept();
		concept.setConceptClass(conceptService.getConceptClass(1));
		concept.setDatatype(conceptService.getConceptDatatype(1));
		concept.addName(new ConceptName("my-dict-concept", Locale.ENGLISH));
		conceptService.saveConcept(concept);
		Integer id = concept.getId();
		
		service.addLocalMappingToConcept(concept);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(id.toString(), localeSource);
		Assert.assertFalse(term.isRetired());
		
		//when
		conceptService.purgeConcept(concept);
		
		//then
		term = conceptService.getConceptReferenceTermByCode(id.toString(), localeSource);
		Assert.assertTrue(term.isRetired());
	}
	
	@Test
	public void shouldRetireConceptReferenceTermIfConceptRetired() {
		//given
		Concept concept = conceptService.getConcept(3);
		
		service.addLocalMappingToConcept(concept);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode("3", localeSource);
		Assert.assertFalse(term.isRetired());
		
		//when
		conceptService.retireConcept(concept, "Testing...");
		
		//then
		term = conceptService.getConceptReferenceTermByCode("3", localeSource);
		Assert.assertTrue(term.isRetired());
	}
	
	@Test
	public void shouldUnretireConceptReferenceTermIfConceptUnretired() {
		//given
		Concept concept = conceptService.getConcept(3);
		conceptService.retireConcept(concept, "Testing...");
		
		service.addLocalMappingToConcept(concept);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode("3", localeSource);
		Assert.assertTrue(term.isRetired());
		
		//when
		concept.setRetired(false);
		concept.setRetiredBy(null);
		concept.setRetireReason(null);
		conceptService.saveConcept(concept);
		
		//then
		term = conceptService.getConceptReferenceTermByCode("3", localeSource);
		Assert.assertFalse(term.isRetired());
	}
}
