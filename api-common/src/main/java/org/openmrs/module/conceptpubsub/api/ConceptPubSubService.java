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
import org.openmrs.api.ConceptService;
import org.openmrs.module.conceptpubsub.ConceptPubSub;
import org.openmrs.module.conceptpubsub.api.adapter.ConceptAdapter;

/**
 * The service.
 */
public interface ConceptPubSubService {
	
	/**
	 * Creates a local concept source from the implementation Id.
	 * <p>
	 * The local source is in a format 'implementationId-dict'. The '-dict' postfix is defined in
	 * {@link ConceptPubSub#LOCAL_SOURCE_NAME_POSTFIX}.
	 * 
	 * @return the local source
	 * @throws APIException if the local source could not be created
	 * @should
	 */
	ConceptSource createLocalSourceFromImplementationId();
	
	/**
	 * Returns a configured local concept source.
	 * <p>
	 * The local source is read from the {@link ConceptPubSub#LOCAL_SOURCE_UUID_GP} global property.
	 * 
	 * @return the local source
	 * @throws APIException if the local source is not configured
	 * @should return local source if gp set
	 * @should fail if gp is not set
	 */
	ConceptSource getLocalSource();
	
	/**
	 * Adds local mapping to the given concept.
	 * <p>
	 * A mapping in a format 'localSource:concetpId' is added to a concept if there is no other
	 * mapping to the local source in the concept.
	 * <p>
	 * The concept is saved at the end.
	 * <p>
	 * It delegates to {@link ConceptAdapter#addMappingToConceptIfNotPresent(Concept, ConceptSource, String)}.
	 * 
	 * @param concept
	 * @throws APIException if the local source is not configured
	 * @should add mapping if not found
	 * @should not add mapping if found
	 * @should fail if local source not configured
	 */
	void addLocalMappingToConcept(Concept concept);
	
	/**
	 * Adds local mappings to all concepts in the system.
	 * <p>
	 * It iterates over all concept and calls {@link #addLocalMappingToConcept(Concept)}.
	 * 
	 * @throws APIException
	 * @should delegate for all concepts
	 */
	void addLocalMappingsToAllConcepts();
	
	/**
	 * Returns sources to which you are subscribed.
	 * 
	 * @return the set of sources or the empty set if nothing found
	 * @throws APIException
	 * @should return set if gp defined
	 * @should return empty set if gp not defined
	 */
	Set<ConceptSource> getSubscribedSources();
	
	/**
	 * Determines if the given concept is local.
	 * <p>
	 * A concept is local if it does not contain a source returned by
	 * {@link #getSubscribedSources()}.
	 * 
	 * @param the concept
	 * @return true if local
	 * @throws APIException
	 * @should return true if local
	 * @should return false if not local
	 */
	boolean isLocalConcept(Concept concept);
	
	/**
	 * Returns a concept by mapping in a format (1) 'source:code' or (2) 'conceptId'.
	 * <p>
	 * It delegates to {@link ConceptService#getConceptByMapping(String, String)} in case (1) and to
	 * {@link #getConcept(Integer)} in case (2).
	 * 
	 * @param mapping
	 * @return the concept or null if not found
	 * @throws APIException
	 * @should return non retired if retired also found by mapping
	 * @should return retired if no other found by mapping
	 * @should delegate if id provided
	 * @should return null if nothing found
	 */
	Concept getConcept(String mapping);
	
	/**
	 * Delegates to {@link ConceptService#getConcept(Integer)}.
	 * <p>
	 * It is a convenience method in case id is passed as an integer and not a string.
	 * 
	 * @param id
	 * @return the concept or null if not found
	 * @throws APIException
	 * @should return non retired
	 * @should return retired
	 * @should return null if not found
	 */
	Concept getConcept(Integer id);
	
}
