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
package org.openmrs.module.web.extension.html;

import java.util.Map;

import org.openmrs.module.web.extension.AdministrationSectionExt;


/**
 * Adds links to the administration page.
 */
public class AdminList extends AdministrationSectionExt {

	/**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
     */
    @Override
    public Map<String, String> getLinks() {
	    return null;
    }

	/**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
     */
    @Override
    public String getTitle() {
	    return null;
    }
	
}
