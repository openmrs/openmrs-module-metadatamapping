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
package org.openmrs.module.metadatamapping.web.bean;

import org.openmrs.module.metadatamapping.web.controller.ConfigureController;

/**
 * A form backing object for {@link ConfigureController}
 */
public class ConfigureForm {
	
	private String conceptSourceUuid;
	
	private Boolean addLocalMappings;
	
	/**
	 * @param conceptSourceUuid the conceptSourceUuid to set
	 */
	public void setConceptSourceUuid(String conceptSourceUuid) {
		this.conceptSourceUuid = conceptSourceUuid;
	}
	
	/**
	 * @return the conceptSourceUuid
	 */
	public String getConceptSourceUuid() {
		return conceptSourceUuid;
	}
	
	/**
	 * @return the addLocalMappings
	 */
	public Boolean getAddLocalMappings() {
		return addLocalMappings;
	}
	
	/**
	 * @param addLocalMappings the addLocalMappings to set
	 */
	public void setAddLocalMappings(Boolean addLocalMappings) {
		this.addLocalMappings = addLocalMappings;
	}
	
}
