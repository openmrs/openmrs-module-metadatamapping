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
package org.openmrs.module.metadatamapping.api.adapter;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataMappingConfigurationPost19;

/**
 * Provides support for the Concept class post OpenMRS 1.9.
 * <p>
 * It's registered in {@link MetadataMappingConfigurationPost19} when running on OpenMRS 1.9 and
 * later.
 */
public class ConceptAdapterPost19 implements ConceptAdapter {
	
	@Override
	public void addMapping(Concept concept, ConceptSource source, String code) {
		final ConceptService conceptService = Context.getConceptService();
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
		if (term == null) {
			term = new ConceptReferenceTerm();
			term.setConceptSource(source);
			term.setCode(code);
			
			conceptService.saveConceptReferenceTerm(term);
		}
		
		term.setRetired(concept.isRetired());
		
		final ConceptMap map = new ConceptMap();
		map.setConceptReferenceTerm(term);
		
		ConceptMapType mapType = conceptService.getConceptMapTypeByName("SAME-AS");
		map.setConceptMapType(mapType);
		
		concept.addConceptMapping(map);
		
		conceptService.saveConcept(concept);
	}
	
	@Override
	public boolean hasMappingToSource(Concept concept, ConceptSource source) {
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getConceptReferenceTerm().getConceptSource())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasMapping(Concept concept, ConceptSource source, String code) {
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getConceptReferenceTerm().getConceptSource())) {
				if (code.equals(map.getConceptReferenceTerm().getCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void retireMapping(Concept concept, ConceptSource source, String code) {
		if (hasMapping(concept, source, code)) {
			final ConceptService conceptService = Context.getConceptService();
			ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
			if (!term.isRetired()) {
				conceptService.retireConceptReferenceTerm(term, "Retired with concept: " + concept.getUuid());
			}
		}
	}
	
	@Override
	public void unretireMapping(Concept concept, ConceptSource source, String code) {
		if (hasMapping(concept, source, code)) {
			final ConceptService conceptService = Context.getConceptService();
			ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
			if (term.isRetired()) { 
				conceptService.unretireConceptReferenceTerm(term);
			}
		}
	}
	
	@Override
	public void purgeMapping(Concept concept, ConceptSource source, String code) {
		if (hasMapping(concept, source, code)) {
			final ConceptService conceptService = Context.getConceptService();
			ConceptReferenceTerm term = conceptService.getConceptReferenceTermByCode(code, source);
			conceptService.purgeConceptReferenceTerm(term);
		}
	}
}
