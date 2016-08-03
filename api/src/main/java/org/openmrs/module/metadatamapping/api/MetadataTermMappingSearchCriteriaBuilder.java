package org.openmrs.module.metadatamapping.api;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatamapping.MetadataSource;

/**
 * Convenience builder for {@link MetadataTermMappingSearchCriteria}.
 */
public class MetadataTermMappingSearchCriteriaBuilder {
	
	private boolean includeAll = false;
	
	private Boolean mapped;
	
	private Integer firstResult = 0;
	
	private Integer maxResults;
	
	private MetadataSource metadataSource;
	
	private String metadataTermCode;
	
	private String metadataTermName;
	
	private String metadataUuid;
	
	private String metadataClass;
	
	private OpenmrsMetadata referredObject;
	
	/**
	 * @param includeAll include retired term mappings
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
		return this;
	}
	
	/**
	 * @param mapped return defined or undefined mappings (null by default (returns both defined and undefined), possible: true, false, null)
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMapped(Boolean mapped) {
		this.mapped = mapped;
		return this;
	}
	
	/**
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	
	/**
	 * @param maxResults get a maximum of this many results
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	
	/**
	 * @param metadataSource only get term mappings from this source
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMetadataSource(MetadataSource metadataSource) {
		this.metadataSource = metadataSource;
		return this;
	}
	
	/**
	 * @param metadataTermCode only get term mappings with this code
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMetadataTermCode(String metadataTermCode) {
		this.metadataTermCode = metadataTermCode;
		return this;
	}
	
	/**
	 * @param metadataTermName only get a term mapping with this name (note that names are unique)
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMetadataTermName(String metadataTermName) {
		this.metadataTermName = metadataTermName;
		return this;
	}
	
	/**
	 * @param referredObject only get term mappings that refer to this metadata object
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setReferredObject(OpenmrsMetadata referredObject) {
		this.referredObject = referredObject;
		return this;
	}
	
	/**
	 *
	 * @param metadataUuid only get term mappings with this metadataUuid
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMetadataUuid(String metadataUuid) {
		this.metadataUuid = metadataUuid;
		return this;
	}
	
	/**
	 *
	 * @param metadataClass only get term mappings with this metadataClass
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setMetadataClass(String metadataClass) {
		this.metadataClass = metadataClass;
		return this;
	}
	
	/**
	 * @return search criteria with the values set to this builder
	 */
	public MetadataTermMappingSearchCriteria build() {
		if (referredObject != null) {
			return new MetadataTermMappingSearchCriteria(includeAll, mapped, firstResult, maxResults, metadataSource,
			        metadataTermCode, metadataTermName, referredObject);
		} else {
			return new MetadataTermMappingSearchCriteria(includeAll, mapped, firstResult, maxResults, metadataSource,
			        metadataTermCode, metadataTermName, metadataClass, metadataUuid);
		}
	}
}
