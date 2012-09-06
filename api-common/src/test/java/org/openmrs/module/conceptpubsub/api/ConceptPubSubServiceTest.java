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

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
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
	
	/**
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies add mapping if not found
	 */
	@Test
	public void addLocalMappingToConcept_shouldAddMappingIfNotFound() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies not add mapping if found
	 */
	@Test
	public void addLocalMappingToConcept_shouldNotAddMappingIfFound() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#addLocalMappingToConcept(Concept)
	 * @verifies fail if local source not configured
	 */
	@Test
	public void addLocalMappingToConcept_shouldFailIfLocalSourceNotConfigured() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#createLocalSourceFromImplementationId()
	 * @verifies
	 */
	@Test
	public void createLocalSourceFromImplementationId_should() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
	 * @verifies return non retired
	 */
	@Test
	public void getConcept_shouldReturnNonRetired() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
	 * @verifies return retired
	 */
	@Test
	public void getConcept_shouldReturnRetired() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(Integer)
	 * @verifies return null if not found
	 */
	@Test
	public void getConcept_shouldReturnNullIfNotFound() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(String)
	 * @verifies return non retired if retired also found by mapping
	 */
	@Test
	public void getConcept_shouldReturnNonRetiredIfRetiredAlsoFoundByMapping() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(String)
	 * @verifies return retired if no other found by mapping
	 */
	@Test
	public void getConcept_shouldReturnRetiredIfNoOtherFoundByMapping() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(String)
	 * @verifies delegate if id provided
	 */
	@Test
	public void getConcept_shouldDelegateIfIdProvided() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getConcept(String)
	 * @verifies return null if nothing found
	 */
	@Test
	public void getConcept_shouldReturnNullIfNothingFound() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getLocalSource()
	 * @verifies return local source if gp set
	 */
	@Test
	public void getLocalSource_shouldReturnLocalSourceIfGpSet() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getLocalSource()
	 * @verifies fail if gp is not set
	 */
	@Test
	public void getLocalSource_shouldFailIfGpIsNotSet() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getSubscribedSources()
	 * @verifies return set if gp defined
	 */
	@Test
	public void getSubscribedSources_shouldReturnSetIfGpDefined() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#getSubscribedSources()
	 * @verifies return empty set if gp not defined
	 */
	@Test
	public void getSubscribedSources_shouldReturnEmptySetIfGpNotDefined() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#isLocalConcept(Concept)
	 * @verifies return true if local
	 */
	@Test
	public void isLocalConcept_shouldReturnTrueIfLocal() throws Exception {
		
	}
	
	/**
	 * @see ConceptPubSubServiceImpl#isLocalConcept(Concept)
	 * @verifies return false if not local
	 */
	@Test
	public void isLocalConcept_shouldReturnFalseIfNotLocal() throws Exception {
		
	}
}
