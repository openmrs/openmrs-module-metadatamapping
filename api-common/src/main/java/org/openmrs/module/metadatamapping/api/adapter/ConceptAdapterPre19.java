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

import java.util.Collection;
import java.util.Iterator;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

/**
 * Provides support for the Concept class pre OpenMRS 1.9.
 */
@Component("metadatamapping.ConceptAdapterPre19")
public class ConceptAdapterPre19 implements ConceptAdapter {
	
	@Override
	public void addMapping(Concept concept, ConceptSource source, String code) {
		final ConceptMap map = new ConceptMap();
		map.setSource(source);
		map.setSourceCode(code);
		
		concept.addConceptMapping(map);
		
		Context.getConceptService().saveConcept(concept);
	}
	
	@Override
	public boolean hasMappingToSource(Concept concept, ConceptSource source) {
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getSource())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasMapping(Concept concept, ConceptSource source, String code) {
		for (ConceptMap map : concept.getConceptMappings()) {
			if (source.equals(map.getSource())) {
				if (code.equals(map.getSourceCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void retireMapping(Concept concept, ConceptSource source, String code) {
		//Cannot retire in pre-1.9
	}
	
	@Override
	public void unretireMapping(Concept concept, ConceptSource source, String code) {
		//Cannot unretire in pre-1.9
	}

    @Override
    public void purgeMapping(Concept concept, ConceptSource source, String code) {
    	Collection<ConceptMap> maps = concept.getConceptMappings();
    	if (maps == null) {
    		return;
    	}
    	
    	Iterator<ConceptMap> it = maps.iterator();
    	while(it.hasNext()) {
    		ConceptMap map = it.next();
    		if (source.equals(map.getSource())) {
    			if (code.equals(map.getSourceCode())) {
    				it.remove();
    				break;
    			}
    		}
    	}
    }
}
