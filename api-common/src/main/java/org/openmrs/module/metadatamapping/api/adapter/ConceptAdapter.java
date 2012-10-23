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
import org.openmrs.ConceptSource;


/**
 * Provides support for the Concept class from different OpenMRS versions.
 */
public interface ConceptAdapter {

	void addMapping(Concept concept, ConceptSource source, String code);
	
	boolean hasMappingToSource(Concept concept, ConceptSource source);
	
	boolean hasMapping(Concept concept, ConceptSource source, String code);
	
	void retireMapping(Concept concept, ConceptSource source, String code);
	
	void unretireMapping(Concept concept, ConceptSource source, String code);
	
	void purgeMapping(Concept concept, ConceptSource source, String code);
}
