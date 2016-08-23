package org.openmrs.module.metadatamapping.api;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.metadatamapping.MetadataSource;

/**
 * Search criteria for {@link MetadataMappingService#getMetadataTermMappings(MetadataTermMappingSearchCriteria)}. Use the
 * convenience builder {@link MetadataTermMappingSearchCriteriaBuilder}.
 * @since 1.2
 */
public class MetadataTermMappingSearchCriteria extends MetadataSearchCriteria {
	
	private Boolean mapped;
	
	private MetadataSource metadataSource;
	
	private String metadataTermCode;
	
	private String metadataTermName;
	
	private String metadataUuid;
	
	private String metadataClass;
	
	private OpenmrsMetadata referredObject;
	
	/**
	 * Prefer using {@link MetadataTermMappingSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param metadataSource only get term mappings from this source
	 * @param metadataTermCode only get term mappings with this code
	 * @param metadataTermName only get a term mapping with this name (note that names are unique)
	 * @param referredObject only get term mappings that refer to this metadata object
	 */
	public MetadataTermMappingSearchCriteria(boolean includeAll, Boolean mapped, Integer firstResult, Integer maxResults,
	    MetadataSource metadataSource, String metadataTermCode, String metadataTermName, OpenmrsMetadata referredObject) {
		super(includeAll, firstResult, maxResults);
		this.mapped = mapped;
		this.metadataSource = metadataSource;
		this.metadataTermCode = metadataTermCode;
		this.metadataTermName = metadataTermName;
		this.referredObject = referredObject;
	}
	
	/**
	 * Prefer using {@link MetadataTermMappingSearchCriteriaBuilder} instead. Every parameter is optional.
	 * @param includeAll include retired term mappings
	 * @param firstResult start from this result (numbered from <tt>0</tt>)
	 * @param maxResults get a maximum of this many results
	 * @param metadataSource only get term mappings from this source
	 * @param metadataTermCode only get term mappings with this code
	 * @param metadataTermName only get a term mapping with this name (note that names are unique)
	 * @param metadataClass only get term mapping with this metadataClass
	 * @param metadataUuid only get term mapping with this metadatauuid
	 */
	public MetadataTermMappingSearchCriteria(boolean includeAll, Boolean mapped, Integer firstResult, Integer maxResults,
	    MetadataSource metadataSource, String metadataTermCode, String metadataTermName, String metadataClass,
	    String metadataUuid) {
		super(includeAll, firstResult, maxResults);
		this.mapped = mapped;
		this.metadataSource = metadataSource;
		this.metadataTermCode = metadataTermCode;
		this.metadataTermName = metadataTermName;
		this.metadataClass = metadataClass;
		this.metadataUuid = metadataUuid;
	}
	
	/**
	 * @return defined or undefined mappings
	 */
	public Boolean getMapped() {
		return mapped;
	}
	
	/**
	 * @return only get term mappings from this source
	 */
	public MetadataSource getMetadataSource() {
		return metadataSource;
	}
	
	/**
	 * @return only get term mappings with this code
	 */
	public String getMetadataTermCode() {
		return metadataTermCode;
	}
	
	/**
	 * @return only get a term mapping with this name (note that names are unique)
	 */
	public String getMetadataTermName() {
		return metadataTermName;
	}
	
	/**
	 * @return only get term mappings that refer to this metadata object
	 */
	public OpenmrsMetadata getReferredObject() {
		return referredObject;
	}
	
	/**
	 *
	 * @return only get term mappings with this metadataUuid
	 */
	public String getMetadataUuid() {
		return metadataUuid;
	}
	
	/**
	 *
	 * @return only get term mappings with this metadataClass
	 */
	public String getMetadataClass() {
		return metadataClass;
	}
}
