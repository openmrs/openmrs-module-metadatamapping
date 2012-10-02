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

import org.openmrs.api.context.Context;

/**
 * Contains constants used by the module.
 */
public class ConceptPubSub {
	
	public static final String MODULE_ID = "conceptpubsub";
	
	public static final String GP_LOCAL_SOURCE_UUID = MODULE_ID + ".localConceptSourceUuid";
	
	public static final String LOCAL_SOURCE_DESCRIPTION_PREFIX = "Source for concepts published by ";
	
	public static final String LOCAL_SOURCE_NAME_POSTFIX = "-dict";
	
	public static final String GP_SUBSCRIBED_TO_SOURCE_UUIDS = MODULE_ID + ".subscribedToConceptSourceUuids";
	
	public static final String MODULE_PATH = "/module/" + MODULE_ID;
	
	/**
	 * Global property name, specifies whether the concept mappings to the local dictionary should
	 * be created when exporting concepts
	 */
	public static final String GP_ADD_LOCAL_MAPPINGS = MODULE_ID + ".addLocalMappings";
	
	/**
	 * @return true if the user has chosen to add local mappings
	 */
	public static boolean isAddLocalMappings() {
		String addLocalMappings = Context.getAdministrationService().getGlobalProperty(GP_ADD_LOCAL_MAPPINGS);
		return Boolean.valueOf(addLocalMappings);
	}
	
}
