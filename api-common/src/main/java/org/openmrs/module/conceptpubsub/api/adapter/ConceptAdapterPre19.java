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
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

/**
 * Provides support for the Concept class pre OpenMRS 1.9.
 */
@Component("conceptpubsub.ConceptAdapterPre19")
public class ConceptAdapterPre19 implements ConceptAdapter {
	
	@Override
	public void addMappingToConcept(final Concept concept, final ConceptSource source) {
		boolean foundSource = false;
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getSource())) {
				foundSource = true;
				break;
			}
		}
		
		if (!foundSource) {
			final ConceptMap map = new ConceptMap();
			map.setSource(source);
			map.setSourceCode(concept.getId().toString());
			
			concept.addConceptMapping(map);
			
			Context.getConceptService().saveConcept(concept);
		}
	}
}
