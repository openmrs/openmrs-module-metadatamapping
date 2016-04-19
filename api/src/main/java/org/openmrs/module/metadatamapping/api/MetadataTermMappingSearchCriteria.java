package org.openmrs.module.metadatamapping.api;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping.MetadataReference;

/**
 * Search criteria for {@link MetadataMappingService#getMetadataTermMappings(MetadataTermMappingSearchCriteria)}. Use the
 * convenience builder {@link MetadataTermMappingSearchCriteriaBuilder}.
 * @since 1.1
 */
public class MetadataTermMappingSearchCriteria {
	
	private boolean includeAll;
	
	private Integer firstResult;
	
	private Integer maxResults;
	
	private MetadataSource metadataSource;
	
	private OpenmrsMetadata referredObject;
	
	private MetadataReference referredObjectReference;
	
	/**
	 * Prefer using {@link MetadataTermMappingSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param metadataSource only get term mappings from this source
	 * @param referredObject only get term mappings that refer to this metadata object
	 */
	public MetadataTermMappingSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults,
	    MetadataSource metadataSource, OpenmrsMetadata referredObject) {
		this.includeAll = includeAll;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.metadataSource = metadataSource;
		this.referredObject = referredObject;
	}
	
	/**
	 * Prefer using {@link MetadataTermMappingSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param metadataSource only get term mappings from this source
	 * @param referredObjectReference only get term mappings that refer to this metadata object
	 */
	public MetadataTermMappingSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults,
	    MetadataSource metadataSource, MetadataReference referredObjectReference) {
		this.includeAll = includeAll;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.metadataSource = metadataSource;
		this.referredObjectReference = referredObjectReference;
	}
	
	/**
	 * @return include retired term mappings
	 */
	public boolean isIncludeAll() {
		return includeAll;
	}
	
	/**
	 * @return start from this result (numbered from <tt>0</tt>)
	 */
	public Integer getFirstResult() {
		return firstResult;
	}
	
	/**
	 * @return get a maximum of this many results
	 */
	public Integer getMaxResults() {
		return maxResults;
	}
	
	/**
	 * @return only get term mappings from this source
	 */
	public MetadataSource getMetadataSource() {
		return metadataSource;
	}
	
	/**
	 * @return only get term mappings that refer to this metadata object
	 */
	public OpenmrsMetadata getReferredObject() {
		return referredObject;
	}
	
	/**
	 * @return only get term mappings that refer to this metadata object
	 */
	public MetadataReference getReferredObjectReference() {
		return referredObjectReference;
	}
}
