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

/**
 * A member in a {@link MetadataSet}.
 */
public class MetadataSetMember extends BaseOpenmrsMetadata {
	
	private Integer metadataSetMemberId;
	
	private MetadataSet metadataSet;
	
	private MetadataTermMapping metadataTermMapping;
	
	private Double sortWeight;
	
	/**
	 * Construct a new metadata set member.
	 * @see #MetadataSetMember(MetadataSet, MetadataTermMapping) 
	 */
	public MetadataSetMember() {
		// Initialize name so that it needs not be set as it is of little use for a metadata set member.
		setName("");
	}
	
	/**
	 * Construct a new metadata set member.
	 * @param metadataSet set this member belongs to, may not be null
	 * @param metadataTermMapping member metadata term mapping, may not be null
	 */
	public MetadataSetMember(MetadataSet metadataSet, MetadataTermMapping metadataTermMapping) {
		this();
		ArgUtil.notNull(metadataSet, "metadataSet");
		ArgUtil.notNull(metadataTermMapping, "metadataTermMapping");
		this.metadataSet = metadataSet;
		this.metadataTermMapping = metadataTermMapping;
	}
	
	/**
	 * Delegates to {@link #getMetadataSetMemberId()}
	 * @return locally unique identifier for the object
	 */
	@Override
	public Integer getId() {
		return getMetadataSetMemberId();
	}
	
	/**
	 * Delegates to {@link #setMetadataSetMemberId(Integer)}
	 * @param id locally unique identifier for the object
	 */
	@Override
	public void setId(Integer id) {
		setMetadataSetMemberId(id);
	}
	
	/**
	 * @return locally unique identifier for the object
	 */
	public Integer getMetadataSetMemberId() {
		return metadataSetMemberId;
	}
	
	/**
	 * @param metadataSetMemberId locally unique identifier for the object
	 */
	public void setMetadataSetMemberId(Integer metadataSetMemberId) {
		this.metadataSetMemberId = metadataSetMemberId;
	}
	
	/**
	 * @return set this member belongs to, is never null
	 */
	public MetadataSet getMetadataSet() {
		return metadataSet;
	}
	
	/**
	 * @param metadataSet set this member belongs to, may not be null
	 */
	public void setMetadataSet(MetadataSet metadataSet) {
		ArgUtil.notNull(metadataSet, "metadataSet");
		this.metadataSet = metadataSet;
	}
	
	/**
	 * @return member metadata term mapping, is never null
	 */
	public MetadataTermMapping getMetadataTermMapping() {
		return metadataTermMapping;
	}
	
	/**
	 * @param metadataTermMapping member metadata term mapping, may not be null
	 */
	public void setMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		ArgUtil.notNull(metadataTermMapping, "metadataTermMapping");
		this.metadataTermMapping = metadataTermMapping;
	}
	
	/**
	 * @return sort weight used in sorting, may be null
	 */
	public Double getSortWeight() {
		return sortWeight;
	}
	
	/**
	 * @param sortWeight sort weight used in sorting, may be null
	 */
	public void setSortWeight(Double sortWeight) {
		this.sortWeight = sortWeight;
	}
}
