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
package org.openmrs.module.conceptpubsub.api.adapter;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.conceptpubsub.ConceptPubSubConfigurationPost19;

/**
 * Provides support for the Concept class post OpenMRS 1.9.
 * <p>
 * It's registered in {@link ConceptPubSubConfigurationPost19} when running on OpenMRS 1.9 and
 * later.
 */
public class ConceptAdapterPost19 implements ConceptAdapter {
	
	@Override
	public void addMappingToConceptIfNotPresent(final Concept concept, final ConceptSource source, final String code) {
		boolean foundSource = false;
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getConceptReferenceTerm().getConceptSource())) {
				foundSource = true;
				break;
			}
		}
		
		if (!foundSource) {
			final ConceptService conceptService = Context.getConceptService();
			
			ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
			if (term == null) {
				term = new ConceptReferenceTerm();
				term.setConceptSource(source);
				term.setCode(code);
				
				conceptService.saveConceptReferenceTerm(term);
			}
			
			final ConceptMap map = new ConceptMap();
			map.setConceptReferenceTerm(term);
			
			ConceptMapType mapType = conceptService.getConceptMapTypeByName("SAME-AS");
			map.setConceptMapType(mapType);
			
			concept.addConceptMapping(map);
			
			conceptService.saveConcept(concept);
		}
	}
}
