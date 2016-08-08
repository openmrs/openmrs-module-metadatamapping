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

import org.openmrs.BaseOpenmrsMetadata;

/**
 * A group or set of metadata terms mappings that relate to each other. MetadataSet doesn't directly contain members, but its members refer to it.
 * To obtain {@link MetadataSetMember}'s use {@link org.openmrs.module.metadatamapping.api.MetadataMappingService#getMetadataSetMembers(MetadataSet, int, int, RetiredHandlingMode)}
 */
public class MetadataSet extends BaseOpenmrsMetadata {
	
	private Integer metadataSetId;
	
	/**
	 * Delegates to {@link #getMetadataSetId()}
	 * @return locally unique identifier for the object
	 */
	@Override
	public Integer getId() {
		return getMetadataSetId();
	}
	
	/**
	 * Delegates to {@link #setMetadataSetId(Integer)}
	 * @param id locally unique identifier for the object
	 */
	@Override
	public void setId(Integer id) {
		setMetadataSetId(id);
	}
	
	/**
	 * @return locally unique identifier for the object
	 */
	public Integer getMetadataSetId() {
		return metadataSetId;
	}
	
	/**
	 * @param metadataSetId locally unique identifier for the object
	 */
	public void setMetadataSetId(Integer metadataSetId) {
		this.metadataSetId = metadataSetId;
	}
	
}
