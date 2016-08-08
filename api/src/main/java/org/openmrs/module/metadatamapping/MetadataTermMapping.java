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
 * Maps a "term" withing a specific namespace or "source" to a local metadata object. {@link #getCode()} is the public 
 * identifier of this term withing the {@link #getMetadataSource()}.
 * @since 1.1
 */
public class MetadataTermMapping extends BaseOpenmrsMetadata {
	
	/**
	 * Reference to a metadata object, i.e. a tuple of a canonical class name and a uuid.
	 * @since 1.2
	 */
	public static class MetadataReference {
		
		private String referenceCanonicalClassName;
		
		private String referenceUuid;
		
		/**
		 * Construct a new reference.
		 * @param referenceCanonicalClassName canonical class name of referred object
		 * @param referenceUuid uuid of referred object
		 */
		public MetadataReference(String referenceCanonicalClassName, String referenceUuid) {
			ArgUtil.notNull(referenceCanonicalClassName, "metadataClass");
			ArgUtil.notNull(referenceUuid, "metadataUuid");
			this.referenceCanonicalClassName = referenceCanonicalClassName;
			this.referenceUuid = referenceUuid;
		}
		
		/**
		 * @return canonical class name of referred object
		 */
		public String getReferenceCanonicalClassName() {
			return referenceCanonicalClassName;
		}
		
		/**
		 * @return uuid of referred object
		 */
		public String getReferenceUuid() {
			return referenceUuid;
		}
	}
	
	private Integer metadataTermMappingId;
	
	private MetadataSource metadataSource;
	
	private String code;
	
	private String metadataClass;
	
	private String metadataUuid;
	
	/**
	 * Construct a new metadata term mapping.
	 * @see #MetadataTermMapping(MetadataSource, String, OpenmrsMetadata)
	 */
	public MetadataTermMapping() {
	}
	
	/**
	 * Construct a new metadata term mapping.
	 * @param metadataSource defines the namespace of this term, may not be null
	 * @param metadataTermCode code of this term within metadataSource, may not be null
	 * @param referredObject object this term maps to, may not be null, must have {@link OpenmrsMetadata#getUuid()}
	 * @since 1.1
	 */
	public MetadataTermMapping(MetadataSource metadataSource, String metadataTermCode, OpenmrsMetadata referredObject) {
		this();
		ArgUtil.notNull(metadataSource, "metadataSource");
		ArgUtil.notNull(metadataTermCode, "metadataTermCode");
		setMetadataSource(metadataSource);
		setCode(metadataTermCode);
		setMappedObject(referredObject);
	}
	
	/**
	 * Construct a new metadata term mapping without reference to an OpenmrsMetadata object.
	 * @param metadataSource defines the namespace of this term, may not be null
	 * @param metadataTermCode code of this term within metadataSource, may not be null
	 * @since 1.1
	 */
	public MetadataTermMapping(MetadataSource metadataSource, String metadataTermCode) {
		this();
		ArgUtil.notNull(metadataSource, "metadataSource");
		ArgUtil.notNull(metadataTermCode, "metadataTermCode");
		setMetadataSource(metadataSource);
		setCode(metadataTermCode);
	}
	
	/**
	 * Delegates to {@link #getMetadataTermMappingId()}
	 * @return locally unique identifier for the object
	 */
	@Override
	public Integer getId() {
		return getMetadataTermMappingId();
	}
	
	/**
	 * Delegates to {@link #setMetadataTermMappingId(Integer)}
	 * @param id locally unique identifier for the object
	 */
	@Override
	public void setId(Integer id) {
		setMetadataTermMappingId(id);
	}
	
	/**
	 * @return locally unique identifier for the object
	 */
	public Integer getMetadataTermMappingId() {
		return metadataTermMappingId;
	}
	
	/**
	 * @param metadataTermMappingId locally unique identifier for the object
	 */
	public void setMetadataTermMappingId(Integer metadataTermMappingId) {
		this.metadataTermMappingId = metadataTermMappingId;
	}
	
	/**
	 * @return source of this term, is never null
	 */
	public MetadataSource getMetadataSource() {
		return metadataSource;
	}
	
	/**
	 * @param metadataSource source of this term, may not be null
	 * @throws IllegalStateException when already set   
	 */
	public void setMetadataSource(MetadataSource metadataSource) {
		ArgUtil.notNull(metadataSource, "metadataSource");
		this.metadataSource = metadataSource;
	}
	
	/**
	 * @return code of this term within {@link #getMetadataSource()}, is never null
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * @param code code of this term within {@link #getMetadataSource()}, may not be null
	 */
	public void setCode(String code) {
		ArgUtil.notNull(code, "code");
		this.code = code;
	}
	
	/**
	 * @return canonical class name of the mapped object
	 */
	public String getMetadataClass() {
		return metadataClass;
	}
	
	/**
	 * @return universally unique id of the mapped object
	 */
	public String getMetadataUuid() {
		return metadataUuid;
	}
	
	/**
	 * @param mappedObject metadata object this term maps to, may not be null, must have {@link 
	 * OpenmrsMetadata#getUuid()}
	 */
	public void setMappedObject(OpenmrsMetadata mappedObject) {
		ArgUtil.notNull(mappedObject, "mappedObject");
		ArgUtil.notNull(mappedObject.getUuid(), "mappedObject.uuid");
		setMetadataClass(mappedObject.getClass().getCanonicalName());
		setMetadataUuid(mappedObject.getUuid());
	}
	
	/**
	 * @param mappedObjectReference metadata object this term maps to, may not be null
	 */
	public void setMappedObject(MetadataReference mappedObjectReference) {
		ArgUtil.notNull(mappedObjectReference, "mappedObject");
		setMetadataClass(mappedObjectReference.getReferenceCanonicalClassName());
		setMetadataUuid(mappedObjectReference.getReferenceUuid());
	}
	
	/**
	 * Private as we prefer the convenience method {@link #setMappedObject(OpenmrsMetadata)}
	 * @param metadataClass canonical class name of the mapped object
	 */
	private void setMetadataClass(String metadataClass) {
		this.metadataClass = metadataClass;
	}
	
	/**
	 * Private as we prefer the convenience method {@link #setMappedObject(OpenmrsMetadata)}
	 * @param metadataUuid uuid of the mapped object
	 */
	private void setMetadataUuid(String metadataUuid) {
		this.metadataUuid = metadataUuid;
	}
}
