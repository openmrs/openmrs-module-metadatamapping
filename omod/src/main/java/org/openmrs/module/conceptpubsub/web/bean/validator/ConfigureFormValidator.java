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
package org.openmrs.module.conceptpubsub.web.bean.validator;

import org.openmrs.module.conceptpubsub.ConceptPubSub;
import org.openmrs.module.conceptpubsub.web.bean.ConfigureForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * A validator for {@link ConfigureForm} objects
 */
@Component(ConceptPubSub.MODULE_ID + ".ConfigureFormValidator")
public class ConfigureFormValidator implements Validator {
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
		return ConfigureForm.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		ConfigureForm configureForm = (ConfigureForm) target;
		
		if (Boolean.TRUE.equals(configureForm.getAddLocalMappings())) {
			ValidationUtils.rejectIfEmpty(errors, "conceptSourceUuid", "conceptpubsub.error.emptyConceptSource");
		}
		
	}	
}
