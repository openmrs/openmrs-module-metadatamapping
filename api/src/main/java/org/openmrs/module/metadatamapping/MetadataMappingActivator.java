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

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

import java.util.List;

/**
 * Performs startup and shutdown operations.
 */
public class MetadataMappingActivator extends BaseModuleActivator {
	
	@Override
	public void started() {
		removeDeprecatedMetadataMappingPrivilege();
	}
	
	/**
	 * @deprecated since 1.2.0
	 */
	private void removeDeprecatedMetadataMappingPrivilege() {
		UserService userService = Context.getUserService();
		
		Privilege privilege = userService.getPrivilege("Metadata Mapping");
		if (privilege != null) {
			List<Role> roles = userService.getAllRoles();
			for (Role role : roles) {
				role.removePrivilege(privilege);
			}
			
			userService.purgePrivilege(privilege);
		}
	}
}
