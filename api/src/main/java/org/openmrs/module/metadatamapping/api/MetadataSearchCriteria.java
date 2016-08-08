package org.openmrs.module.metadatamapping.api;

/**
 * Search criteria superclass to reduce repetitiveness in creating search criteria
 * @since 1.2
 */
abstract public class MetadataSearchCriteria {
	
	protected boolean includeAll;
	
	protected Integer firstResult;
	
	protected Integer maxResults;
	
	/**
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 */
	public MetadataSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults) {
		this.includeAll = includeAll;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
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
}
