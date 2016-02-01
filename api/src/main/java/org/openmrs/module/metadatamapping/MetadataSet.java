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
import org.openmrs.module.metadatamapping.util.ArgUtil;
import org.openmrs.module.metadatamapping.util.StateUtil;

/**
 * A group or set of metadata terms mappings that relate to each other.
 */
public class MetadataSet extends BaseOpenmrsMetadata {
	
	private Integer metadataSetId;
	
	private MetadataSource metadataSource;
	
	private String code;
	
	/**
	 * Construct a new metadata set.
	 * @see #MetadataSet(MetadataSource, String)
	 */
	public MetadataSet() {
	}
	
	/**
	 * Construct a new metadata set.
	 * @param metadataSource source of this set, may not be null
	 * @param metadataSetCode code of this set, may not be null
	 */
	public MetadataSet(MetadataSource metadataSource, String metadataSetCode) {
		ArgUtil.notNull(metadataSource, "metadataSource");
		ArgUtil.notNull(metadataSetCode, "metadataSetCode");
		setMetadataSource(metadataSource);
		setCode(metadataSetCode);
		this.metadataSource = metadataSource;
		this.code = metadataSetCode;
	}
	
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
	
	/**
	 * @return source of this set, is never null
	 */
	public MetadataSource getMetadataSource() {
		return metadataSource;
	}
	
	/**
	 * @param metadataSource source of this set, may not be null
	 * @throws IllegalStateException when already set   
	 */
	public void setMetadataSource(MetadataSource metadataSource) {
		StateUtil.mustNotChangeIfSet(this.metadataSource, metadataSource, "metadataSource may not be changed");
		ArgUtil.notNull(metadataSource, "metadataSource");
		this.metadataSource = metadataSource;
	}
	
	/**
	 * @return code of this set within {@link #getMetadataSource()}, is never null
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * @param code code of this set within {@link #getMetadataSource()}, may not be null
	 */
	public void setCode(String code) {
		ArgUtil.notNull(code, "code");
		this.code = code;
	}
}
