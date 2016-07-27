package org.openmrs.module.metadatamapping.api;

/**
 * Convenience builder for {@link MetadataSourceSearchCriteria}.
 * @since 1.2
 */
public class MetadataSourceSearchCriteriaBuilder {
	
	private boolean includeAll;
	
	private Integer firstResult;
	
	private Integer maxResults;
	
	private String sourceName;
	
	/**
	 * @param includeAll include retired term mappings
	 * @return this builder
	 */
	public MetadataSourceSearchCriteriaBuilder setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
		return this;
	}
	
	/**
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @return this builder
	 */
	public MetadataSourceSearchCriteriaBuilder setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	
	/**
	 * @param maxResults get a maximum of this many results
	 * @return this builder
	 */
	public MetadataSourceSearchCriteriaBuilder setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	
	/**
	 * @param sourceName only get a source with this name (note that names are unique)
	 * @return this builder
	 */
	public MetadataSourceSearchCriteriaBuilder setSourceName(String sourceName) {
		this.sourceName = sourceName;
		return this;
	}
	
	/**
	 * @return search criteria with the values set to this builder
	 */
	public MetadataSourceSearchCriteria build() {
		return new MetadataSourceSearchCriteria(includeAll, firstResult, maxResults, sourceName);
	}
}
