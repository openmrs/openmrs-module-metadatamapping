package org.openmrs.module.metadatamapping.api;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping.MetadataReference;

/**
 * Convenience builder for {@link MetadataTermMappingSearchCriteria}.
 */
public class MetadataTermMappingSearchCriteriaBuilder {
	
	private boolean includeAll = false;
	
	private Integer firstResult = 0;
	
	private Integer maxResults;
	
	private MetadataSource metadataSource;
	
	private OpenmrsMetadata referredObject;
	
	private MetadataReference referredObjectReference;
	
	/**
	 * @param includeAll include retired term mappings
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
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
	 * @param referredObject only get term mappings that refer to this metadata object
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setReferredObject(OpenmrsMetadata referredObject) {
		if (referredObjectReference != null) {
			throw new IllegalStateException("referredObject can not be set if referredObjectReference has been set");
		}
		this.referredObject = referredObject;
		return this;
	}
	
	/**
	 * @param referredObjectReference only get term mappings that refer to this metadata object
	 * @return this builder
	 */
	public MetadataTermMappingSearchCriteriaBuilder setReferredObjectReference(MetadataReference referredObjectReference) {
		if (referredObject != null) {
			throw new IllegalStateException("referredObjectReference can not be set if referredObject has been set");
		}
		this.referredObjectReference = referredObjectReference;
		return this;
	}
	
	/**
	 * @return search criteria with the values set to this builder
	 */
	public MetadataTermMappingSearchCriteria build() {
		if (referredObject != null) {
			return new MetadataTermMappingSearchCriteria(includeAll, firstResult, maxResults, metadataSource, referredObject);
		} else {
			return new MetadataTermMappingSearchCriteria(includeAll, firstResult, maxResults, metadataSource,
			        referredObjectReference);
		}
	}
}
