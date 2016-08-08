package org.openmrs.module.metadatamapping.api;

/**
 * Search criteria for {@link MetadataMappingService#getMetadataSources(MetadataSourceSearchCriteria)}}. Use the
 * convenience builder {@link MetadataSourceSearchCriteriaBuilder}.
 * @since 1.2
 */
public class MetadataSourceSearchCriteria extends MetadataSearchCriteria {
	
	private String sourceName;
	
	/**
	 * Prefer using {@link MetadataSourceSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param sourceName only get a term mapping with this name (note that names are unique)
	 */
	public MetadataSourceSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults, String sourceName) {
		super(includeAll, firstResult, maxResults);
		this.sourceName = sourceName;
	}
	
	/**
	 * @return only get a source with this name (note that names are unique)
	 */
	public String getSourceName() {
		return sourceName;
	}
}
