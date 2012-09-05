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
package org.openmrs.module.conceptpubsub;

import org.openmrs.module.conceptpubsub.api.adapter.ConceptAdapterPost19;
import org.openmrs.module.conceptpubsub.api.impl.ConceptPubSubServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class ConceptPubSubConfigurationPost19 {
	
	@Autowired
	private ConceptPubSubServiceImpl conceptPubSubService;
	
	@Bean
	public ConceptAdapterPost19 ConceptAdapterPost19() {
		ConceptAdapterPost19 conceptAdapter = new ConceptAdapterPost19();
		
		conceptPubSubService.setConceptAdapter(conceptAdapter);
		
		return conceptAdapter;
	}
	
}
