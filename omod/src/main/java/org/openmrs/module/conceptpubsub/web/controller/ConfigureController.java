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
package org.openmrs.module.conceptpubsub.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.conceptpubsub.ConceptPubSub;
import org.openmrs.module.conceptpubsub.api.ConceptPubSubService;
import org.openmrs.module.conceptpubsub.web.bean.ConfigureForm;
import org.openmrs.module.conceptpubsub.web.bean.validator.ConfigureFormValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller(ConceptPubSub.MODULE_ID + ".ConfigureController")
public class ConfigureController {
	
	public static final String CONFIGURE_PATH = ConceptPubSub.MODULE_PATH + "/configure";
	
	@Autowired
	private ConfigureFormValidator validator;
	
	@ModelAttribute("conceptSources")
	public List<ConceptSource> getConceptSources() {
		return Context.getConceptService().getAllConceptSources();
	}
	
	@RequestMapping(value = CONFIGURE_PATH, method = RequestMethod.GET)
	public void configureGet(Model model) {
		AdministrationService adminService = Context.getAdministrationService();
		ConfigureForm configureForm = new ConfigureForm();
		
		String conceptSourceUuid = adminService.getGlobalProperty(ConceptPubSub.GP_LOCAL_SOURCE_UUID);
		configureForm.setConceptSourceUuid(conceptSourceUuid);
		
		String addLocalMappingsString = Context.getAdministrationService().getGlobalProperty(
		    ConceptPubSub.GP_ADD_LOCAL_MAPPINGS, "true");
		configureForm
		        .setAddLocalMappings(Boolean.valueOf((StringUtils.isNotBlank(addLocalMappingsString) ? addLocalMappingsString
		                : "true")));
		
		model.addAttribute(configureForm);
	}
	
	@RequestMapping(value = CONFIGURE_PATH, method = RequestMethod.POST)
	public String configurePost(ConfigureForm configureForm, Errors errors, Model model, HttpSession session,
	                            HttpServletRequest request) {
		validator.validate(configureForm, errors);
		if (!errors.hasErrors()) {
			Context.getService(ConceptPubSubService.class).setLocalConceptSource(configureForm.getConceptSourceUuid());
			
			saveGlobalProperty(ConceptPubSub.GP_ADD_LOCAL_MAPPINGS, configureForm.getAddLocalMappings().toString());
			
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, ConceptPubSub.MODULE_ID + ".configure.saved");
			
			return "redirect:" + CONFIGURE_PATH + ".form";
		}
		
		return CONFIGURE_PATH;
	}
	
	/**
	 * Saves a global property with the given name to the database.
	 */
	private void saveGlobalProperty(String name, String value) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty property = administrationService.getGlobalPropertyObject(name);
		if (property == null) {
			property = new GlobalProperty(name, value);
		} else {
			property.setPropertyValue(value);
		}
		administrationService.saveGlobalProperty(property);
	}
	
}
