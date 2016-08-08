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
import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatamapping.util.ArgUtil;

/**
 * A member in a {@link MetadataSet}. Similar to {@link MetadataTermMapping} it keeps reference to single metadata item
 */
public class MetadataSetMember extends BaseOpenmrsMetadata {
	
	private Integer metadataSetMemberId;
	
	private MetadataSet metadataSet;
	
	private String metadataUuid;
	
	private String metadataClass;
	
	private Double sortWeight;
	
	public MetadataSetMember() {
	}
	
	public MetadataSetMember(OpenmrsMetadata mappedObject) {
		setMappedObject(mappedObject);
	}
	
	public MetadataSetMember(OpenmrsMetadata mappedObject, MetadataSet metadataSet) {
		setMappedObject(mappedObject);
		this.metadataSet = metadataSet;
	}
	
	public void setMappedObject(OpenmrsMetadata mappedObject) {
		ArgUtil.notNull(mappedObject, "mappedObject");
		ArgUtil.notNull(mappedObject.getUuid(), "mappedObject.uuid");
		setMetadataClass(mappedObject.getClass().getCanonicalName());
		setMetadataUuid(mappedObject.getUuid());
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
	
	public String getMetadataClass() {
		return metadataClass;
	}
	
	public void setMetadataClass(String metadataClass) {
		this.metadataClass = metadataClass;
	}
	
	public String getMetadataUuid() {
		return metadataUuid;
	}
	
	public void setMetadataUuid(String metadataUuid) {
		this.metadataUuid = metadataUuid;
	}
}
