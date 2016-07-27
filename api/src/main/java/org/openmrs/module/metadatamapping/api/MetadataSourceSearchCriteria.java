package org.openmrs.module.metadatamapping.api;

/**
 * Search criteria for {@link MetadataMappingService#getMetadataSources(MetadataSourceSearchCriteria)}}. Use the
 * convenience builder {@link MetadataSourceSearchCriteriaBuilder}.
 * @since 1.2
 */
public class MetadataSourceSearchCriteria {
	
	private boolean includeAll;
	
	private Integer firstResult;
	
	private Integer maxResults;
	
	private String sourceName;
	
	/**
	 * Prefer using {@link MetadataSourceSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param sourceName only get a term mapping with this name (note that names are unique)
	 */
	public MetadataSourceSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults, String sourceName) {
		this.includeAll = includeAll;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.sourceName = sourceName;
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
	 * @return only get a source with this name (note that names are unique)
	 */
	public String getSourceName() {
		return sourceName;
	}
}
