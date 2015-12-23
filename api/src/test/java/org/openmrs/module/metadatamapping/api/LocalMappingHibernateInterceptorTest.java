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
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.api.db.hibernate.interceptor.LocalMappingHibernateInterceptor;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link LocalMappingHibernateInterceptor}
 */
public class LocalMappingHibernateInterceptorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService service;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService adminService;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	private ConceptSource localConceptSource;
	
	@Before
	public void setupLocalConceptSource() {
		localConceptSource = new ConceptSource();
		localConceptSource.setName("my-dict");
		conceptService.saveConceptSource(localConceptSource);
		
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, localConceptSource
		        .getUuid()));
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
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(id.toString(), localConceptSource);
		Assert.assertFalse(term.isRetired());
		
		//when
		conceptService.purgeConcept(concept);
		
		//then
		term = conceptService.getConceptReferenceTermByCode(id.toString(), localConceptSource);
		Assert.assertTrue(term.isRetired());
	}
	
	@Test
	public void shouldRetireConceptReferenceTermIfConceptRetired() {
		//given
		Concept concept = conceptService.getConcept(3);
		
		service.addLocalMappingToConcept(concept);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode("3", localConceptSource);
		Assert.assertFalse(term.isRetired());
		
		//when
		conceptService.retireConcept(concept, "Testing...");
		
		//then
		term = conceptService.getConceptReferenceTermByCode("3", localConceptSource);
		Assert.assertTrue(term.isRetired());
	}
	
	@Test
	public void shouldUnretireConceptReferenceTermIfConceptUnretired() {
		//given
		Concept concept = conceptService.getConcept(3);
		conceptService.retireConcept(concept, "Testing...");
		
		service.addLocalMappingToConcept(concept);
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode("3", localConceptSource);
		Assert.assertTrue(term.isRetired());
		
		//when
		concept.setRetired(false);
		concept.setRetiredBy(null);
		concept.setRetireReason(null);
		conceptService.saveConcept(concept);
		
		//then
		term = conceptService.getConceptReferenceTermByCode("3", localConceptSource);
		Assert.assertFalse(term.isRetired());
	}
}
