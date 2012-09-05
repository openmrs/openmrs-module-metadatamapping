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

import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;

/**
 *
 */
public interface ConceptPubSubService extends OpenmrsService {
	
	Concept getConcept(Integer id) throws APIException;
	
	Concept getConcept(String mapping) throws APIException;
	
	boolean isLocalConcept(Concept concept) throws APIException;
	
	Set<ConceptSource> getSubscribedSources() throws APIException;
	
	void addLocalMappingsToAllConcepts() throws APIException;
	
	void addLocalMappingToConcept(Concept concept) throws APIException;
	
	ConceptSource getLocalSource() throws APIException;
	
	ConceptSource createLocalSourceFromImplementationId() throws APIException;
	
}
