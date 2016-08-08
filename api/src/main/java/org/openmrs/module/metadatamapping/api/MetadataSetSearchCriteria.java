package org.openmrs.module.metadatamapping.api;

import org.openmrs.module.metadatamapping.api.MetadataSearchCriteria;

public class MetadataSetSearchCriteria extends MetadataSearchCriteria {
	
	/**
	 * @param includeAll  include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults  get a maximum of this many results
	 */
	public MetadataSetSearchCriteria(boolean includeAll, Integer firstResult, Integer maxResults) {
		super(includeAll, firstResult, maxResults);
	}
}
