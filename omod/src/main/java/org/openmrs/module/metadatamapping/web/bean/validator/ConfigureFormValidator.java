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
package org.openmrs.module.metadatamapping.web.bean.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.web.bean.ConfigureForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * A validator for {@link ConfigureForm} objects
 */
@Component(MetadataMapping.MODULE_ID + ".ConfigureFormValidator")
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
			ValidationUtils.rejectIfEmpty(errors, "conceptSourceUuid", "metadatamapping.error.emptyConceptSource");
		}
		
		if (!StringUtils.isBlank(configureForm.getConceptSourceUuid())) {
			ConceptSource source = Context.getService(ConceptService.class).getConceptSourceByUuid(
			    configureForm.getConceptSourceUuid());
			if (source == null) {
				errors.rejectValue("conceptSourceUuid", "metadatamapping.error.missingConceptSource");
			}
		}
		
	}
}
