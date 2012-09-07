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
package org.openmrs.module.conceptpubsub.aop;

import java.lang.reflect.Method;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.conceptpubsub.api.ConceptPubSubService;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * Automatically retires/unretires/purges local mappings with concepts.
 */
public class ConceptServiceAOP implements MethodBeforeAdvice {
	
	/**
	 * @see org.springframework.aop.MethodBeforeAdvice#before(java.lang.reflect.Method,
	 *      java.lang.Object[], java.lang.Object)
	 */
	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		if (!(target instanceof ConceptService) || args.length == 0 || !(args[0] instanceof Concept)) {
			return;
		}
		
		Concept concept = (Concept) args[0];
		ConceptPubSubService service = Context.getService(ConceptPubSubService.class);
		
		if ("saveConcept".equals(method.getName())) {
			if (concept.isRetired()) {
				service.retireLocalMappingInConcept(concept);
			} else {
				service.unretireLocalMappingInConcept(concept);
			}
		} else if ("retireConcept".equals(method.getName())) {
			service.retireLocalMappingInConcept(concept);
		} else if ("unretireConcept".equals(method.getName())) {
			service.unretireLocalMappingInConcept(concept);
		} else if ("purgeConcept".equals(method.getName())) {
			service.purgeLocalMappingInConcept(concept);
		}
	}
	
}
