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
package org.openmrs.module.metadatamapping;

import org.openmrs.module.metadatamapping.api.adapter.ConceptAdapterPost19;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Instantiates beans for OpenMRS 1.9 and later.
 */
@Configuration(value = "metadatamapping.MetadataMappingConfigurationPost19")
public class MetadataMappingConfigurationPost19 {
	
	@Bean(name = "metadatamapping.ConceptAdapterPost19")
	@Primary
	public ConceptAdapterPost19 getConceptAdapterPost19() {
		try {
			OpenmrsClassLoader.getInstance().loadClass("org.openmrs.ConceptReferenceTerm");
		}
		catch (ClassNotFoundException e) {
			return null;
		}
		
		return new ConceptAdapterPost19();
	}
	
}
