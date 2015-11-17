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
package org.openmrs.module.metadatamapping.api.db;

import java.util.Collection;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;

/**
 * The DAO.
 */
public interface MetadataMappingDAO {
	
	/**
	 * Allows to iterate over concepts in batches.
	 *
	 * @param firstResult first result index
	 * @param maxResults maximum number of results
	 * @return the list of concepts
	 */
	List<Concept> getConcepts(final int firstResult, final int maxResults);
	
	/**
	 * Save a new metadata source or update an existing one.
	 * @param metadataSource object to save
	 * @return saved object
	 */
	MetadataSource saveMetadataSource(MetadataSource metadataSource);
	
	/**
	 * Get metadata source with the given id.
	 * @param metadataSourceId database id of the object
	 * @return object or null, if does not exist
	 */
	MetadataSource getMetadataSource(Integer metadataSourceId);
	
	/**
	 * Get metadata source with the given name. 
	 * @param metadataSourceName uuid of the object
	 * @return object or null, if does not exist
	 */
	MetadataSource getMetadataSourceByName(String metadataSourceName);
	
	/**
	 * Save a new metadata term mapping or update an existing one.
	 * @param metadataTermMapping object to save
	 * @return saved object
	 */
	MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping);
	
	/**
	 * Batch save for metadata term mappings.
	 * @param metadataTermMappings collection of metadata term mappings to save
	 * @return collection of saved metadata term mappings
	 * @see #saveMetadataTermMapping(MetadataTermMapping) 
	 */
	Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings);
	
	/**
	 * Get metadata term mapping with the given id.
	 * @param metadataTermMappingId database id of the object
	 * @return object or null, if does not exist
	 */
	MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId);
	
	/**
	 * Get database object with the given uuid. 
	 * @param openmrsObjectClass type of the object
	 * @param uuid uuid of the object
	 * @return object or null, if does not exist
	 */
	<T extends OpenmrsObject> T getByUuid(Class<T> openmrsObjectClass, String uuid);
	
	/**
	 * Find all the unretired metadata term mappings that refer to the given metadata object.
	 * @param referredObject find term mappings that refer to this object
	 * @return list of matching metadata term mappings
	 * @since 1.1
	 * @should return unretired terms referring to object
	 */
	List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject);
	
	/**
	 * Get a specific metadata term mapping from a specific source. 
	 * @param metadataSource source of the term
	 * @param metadataTermCode code of the term   
	 * @return object or null, if does not exist
	 */
	MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode);
	
	/**
	 * Get all unretired term mappings in the source.
	 * @param metadataSource source of the terms
	 * @return list of terms
	 */
	List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource);
	
	/**
	 * Get metadata item referred to by the given metadata term mapping
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param metadataTermCode metadata term code
	 * @param <T> type of the metadata item
	 * @return metadata item or null, if not found or if either the metadata term mapping or the metadata item itself are 
	 * retired
	 */
	<T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode);
	
	/**
	 * Get metadata items of the given type that are referred to by any metadata term mappings in the given metadata source
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param <T> type of the metadata item
	 * @return list of matching metadata items
	 */
	<T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName);
}
