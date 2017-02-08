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
package org.openmrs.module.metadatamapping.api;

import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
import org.openmrs.module.metadatamapping.api.wrapper.ConceptAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The service.
 */
public interface MetadataMappingService {
	
	/**
	 * Creates a local concept source from the implementation Id.
	 * <p>
	 * The local concept source is in a format 'implementationId-dict'. The '-dict' postfix is defined in
	 * {@link MetadataMapping#LOCAL_SOURCE_NAME_POSTFIX}.
	 * 
	 * @return the local concept source
	 * @throws APIException if the local concept source could not be created
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	ConceptSource createLocalConceptSourceFromImplementationId();
	
	/**
	 * Returns a configured local concept source.
	 * <p>
	 * The local concept source is read from the {@link MetadataMapping#GP_LOCAL_SOURCE_UUID} global
	 * property.
	 * 
	 * @return the local concept source
	 * @throws APIException if the local concept source is not configured
	 * @should return local concept source if gp set
	 * @should fail if gp is not set
	 */
	@Authorized()
	ConceptSource getLocalConceptSource();
	
	/**
	 * Returns true if local concept source is configured.
	 * 
	 * @return true if configured
	 */
	@Authorized()
	boolean isLocalConceptSourceConfigured();
	
	/**
	 * Returns true if local mappings should be added to concept on export.
	 * 
	 * @return true if should add local mappings to concept on export
	 */
	@Authorized()
	boolean isAddLocalMappingToConceptOnExport();
	
	/**
	 * Adds local mapping to the given concept.
	 * <p>
	 * A mapping in a format 'localSource:conceptId' is added to a concept if there is no other
	 * mapping to the local source in the concept.
	 * <p>
	 * The concept is saved at the end.
	 * <p>
	 * It delegates to
	 * {@link ConceptAdapter#addMapping(Concept, ConceptSource, String)}
	 * 
	 * @param concept concept to map
	 * @throws APIException if the local source is not configured
	 * @should add mapping if not found
	 * @should not add mapping if found
	 * @should fail if local source not configured
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void addLocalMappingToConcept(Concept concept);
	
	/**
	 * Adds local mappings to all concepts in the system.
	 * <p>
	 * It iterates over all concept and calls {@link #addLocalMappingToConcept(Concept)}.
	 * 
	 * @throws APIException reserved for future use
	 * @should delegate for all concepts
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void addLocalMappingToAllConcepts();
	
	/**
	 * Returns sources to which you are subscribed.
	 * 
	 * @return the set of sources or the empty set if nothing found
	 * @throws APIException reserved for future use
	 * @should return set if gp defined
	 * @should return empty set if gp not defined
	 */
	@Authorized()
	Set<ConceptSource> getSubscribedConceptSources();
	
	/**
	 * Adds the given source to the subscribed concept sources list.
	 * 
	 * @param conceptSource source to add
	 * @return true if added or false if already there
	 * @should add subscribed concept source
	 * @should return false if subscribed concept source present
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	boolean addSubscribedConceptSource(ConceptSource conceptSource);
	
	/**
	 * Removes the given source from the subscribed concept sources list.
	 * 
	 * @param conceptSource source to remove
	 * @return true if removed or false if not present
	 * @should remove subscribed concept source
	 * @should return false if subscribed concept source not present
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	boolean removeSubscribedConceptSource(ConceptSource conceptSource);
	
	/**
	 * Determines if the given concept is local.
	 * <p>
	 * A concept is local if it does not contain a source returned by
	 * {@link #getSubscribedConceptSources()}.
	 * 
	 * @param concept concept to check
	 * @return true if local
	 * @throws APIException reserved for future use
	 * @should return true if local
	 * @should return false if not local
	 */
	@Authorized()
	boolean isLocalConcept(Concept concept);
	
	/**
	 * Returns a concept by mapping in a format (1) 'source:code' or (2) 'conceptId'.
	 * <p>
	 * It delegates to {@link ConceptService#getConceptByMapping(String, String)} in case (1) and to
	 * {@link #getConcept(Integer)} in case (2).
	 * 
	 * @param mapping mapping or identifier of the concept
	 * @return the concept or null if not found
	 * @throws APIException reserved for future use
	 * @should return non retired if retired also found by mapping
	 * @should return retired if no other found by mapping
	 * @should delegate if id provided
	 * @should return null if nothing found
	 */
	@Authorized()
	Concept getConcept(String mapping);
	
	/**
	 * Delegates to {@link ConceptService#getConcept(Integer)}.
	 * <p>
	 * It is a convenience method in case id is passed as an integer and not a string.
	 * 
	 * @param id identifier of the concept
	 * @return the concept or null if not found
	 * @throws APIException reserved for future use
	 * @should return non retired
	 * @should return retired
	 * @should return null if not found
	 */
	@Authorized()
	Concept getConcept(Integer id);
	
	/**
	 * Purges a local mapping if present in the concept.
	 * 
	 * @param concept purge the local mapping of this concept
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void purgeLocalMappingInConcept(Concept concept);
	
	/**
	 * Unretires a local mapping if present in the concept.
	 * 
	 * @param concept unretire a local mapping for this concept
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void markLocalMappingUnretiredInConcept(Concept concept);
	
	/**
	 * Retires a local mapping if present in the concept.
	 * 
	 * @param concept retire a local mapping for this concept
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void markLocalMappingRetiredInConcept(Concept concept);
	
	/**
	 * Sets the local concept source to the source with the given uuid.
	 * 
	 * @see MetadataMapping#GP_ADD_LOCAL_MAPPINGS
	 * @see #createLocalConceptSourceFromImplementationId()
	 * @param conceptSource concept source to set
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	void setLocalConceptSource(ConceptSource conceptSource);
	
	/**
	 * Save a new metadata source or update an existing one.
	 * @param metadataSource object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSource saveMetadataSource(MetadataSource metadataSource);
	
	/**
	 * Get metadata sources.
	 * @param includeRetired if true, will return also retired metadata sources
	 * @return list of metadata source
	 * @since 1.1
	 * @should respect includeRetired flag
	 * @deprecated use {@link #getMetadataSources(MetadataSourceSearchCriteria)} instead
	 */
	@Authorized()
	@Deprecated
	List<MetadataSource> getMetadataSources(boolean includeRetired);
	
	/**
	 * Find all the metadata sources that match the given criteria.
	 * @param searchCriteria find sources matching these criteria
	 * @return list of sources
	 * @since 1.2
	 * @should return sources matching every criteria
	 */
	@Authorized()
	List<MetadataSource> getMetadataSources(MetadataSourceSearchCriteria searchCriteria);
	
	/**
	 * Get metadata source with the given id.
	 * @param metadataSourceId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	@Authorized()
	MetadataSource getMetadataSource(Integer metadataSourceId);
	
	/**
	 * Get metadata source with the given uuid. 
	 * @param metadataSourceUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	@Authorized()
	MetadataSource getMetadataSourceByUuid(String metadataSourceUuid);
	
	/**
	 * Get metadata source with the given name. 
	 * @param metadataSourceName uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	@Authorized()
	MetadataSource getMetadataSourceByName(String metadataSourceName);
	
	/**
	 * Retire the object and set required info via an AOP injected method.
	 * @param metadataSource object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSource retireMetadataSource(MetadataSource metadataSource, String reason);
	
	/**
	 * Creates or updates a metadata mapping for an object
	 *
	 * @param referredObjectUuid uuid of object to map
	 * @param referredObjectClassName class name of object to map
	 * @param sourceName name of source
	 * @param mappingCode name of mapping
	 * @return created or updated MetadataTermMapping
	 * @should create new MetadataTermMapping if none exists within the existing source with the given mappingName
	 * @should update existing MetadataTermMapping if a mapping with that name already exists within the given source
	 * @should fail if no source with the give sourceName
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataTermMapping mapMetadataItem(String referredObjectUuid, String referredObjectClassName, String sourceName,
	        String mappingCode);
	
	/**
	 * Creates or updates a metadata mapping for an object
	 *
	 * @param referredObject object to map
	 * @param sourceName name of source
	 * @param mappingCode name of mapping
	 * @return created or updated MetadataTermMapping
	 * @should create new MetadataTermMapping if none exists within the existing source with the given mappingName
	 * @should update existing MetadataTermMapping if a mapping with that name already exists within the given source
	 * @should fail if no source with the give sourceName
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataTermMapping mapMetadataItem(OpenmrsMetadata referredObject, String sourceName, String mappingCode);
	
	/**
	 * Creates or updates a metadata mapping for a set of objects
	 * @param referredObjects objects to map
	 * @param sourceName name of source
	 * @param mappingCode name of mapping
	 * @return created up updated MetadataTermMapping
	 * @should create new MetadataSet, MetadataSetMembers, and MetadataTermMapping if none exists within the existing source with the given mapping name
	 * @should if existing MetadataTermMapping, should add any objects in referredObjects that aren't currently items in MetadataSet
	 * @should if existing MetadateTermMapping, should remove any objects in referredObjects that aren't currently items in MetadataSet
	 * @should fail if no source with given sourceName
	 * @should fail if existing MetadataTermMapping and that mapping does not point to a MetadataSet
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	<T extends OpenmrsMetadata> MetadataTermMapping mapMetadataItems(List<T> referredObjects, String sourceName,
	        String mappingCode);
	
	/**
	 * Save a new metadata term mapping or update an existing one.
	 * @param metadataTermMapping object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 * @should fail if code is not unique within source
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping);
	
	/**
	 * Batch save for metadata terms mappings.
	 * @param metadataTermMappings collection of metadata term mappings to save
	 * @return collections of saved metadata term mappings
	 * @since 1.1
	 * @see #saveMetadataTermMapping(MetadataTermMapping)
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings);
	
	/**
	 * Get metadata term mapping with the given id.
	 * @param metadataTermMappingId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	@Authorized()
	MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId);
	
	/**
	 * Get metadata term mapping with the given uuid. 
	 * @param metadataTermMappingUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return matching metadata term mapping
	 */
	@Authorized()
	MetadataTermMapping getMetadataTermMappingByUuid(String metadataTermMappingUuid);
	
	/**
	 * Find all the metadata term mappings that match the given criteria.
	 * @param searchCriteria find term mappings matching these criteria
	 * @return list of metadata term mappings
	 * @since 1.2
	 * @should return term mappings matching every criteria
	 */
	@Authorized()
	List<MetadataTermMapping> getMetadataTermMappings(MetadataTermMappingSearchCriteria searchCriteria);
	
	/**
	 * Find all the unretired metadata term mappings that refer to the given metadata object.
	 * @param referredObject find term mappings that refer to this object
	 * @return list of matching metadata term mappings
	 * @since 1.1
	 * @should return unretired term mappings referring to object
	 * @deprecated Use {@link #getMetadataTermMappings(MetadataTermMappingSearchCriteria)} instead
	 */
	@Authorized()
	@Deprecated
	List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject);
	
	/**
	 * Retire the object and set required info via an AOP injected method.
	 * @param metadataTermMapping object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataTermMapping retireMetadataTermMapping(MetadataTermMapping metadataTermMapping, String reason);
	
	/**
	 * Get a specific metadata term mapping from a specific source. 
	 * @param metadataSource source of the term
	 * @param metadataTermCode code of the term
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return a retired term mapping
	 */
	@Authorized()
	MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode);
	
	/**
	 * Get a specific metadata term mapping from a specific source.
	 * @param metadataSourceName name of source of the term
	 * @param metadataTermCode code of the term
	 * @return object or null, if does not exist
	 * @since 1.2
	 * @should return a retired term mapping
	 */
	@Authorized()
	MetadataTermMapping getMetadataTermMapping(String metadataSourceName, String metadataTermCode);
	
	/**
	 * Get all unretired metadata term mappings in the source.
	 * @param metadataSource source of the terms
	 * @return list of terms
	 * @since 1.1
	 * @should return only unretired term mappings
	 * @deprecated Use {@link #getMetadataTermMappings(MetadataTermMappingSearchCriteria)} instead
	 */
	@Authorized()
	@Deprecated
	List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource);
	
	/**
	 * Get metadata item referred to by the given metadata term mapping
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param metadataTermCode metadata term code
	 * @param <T> type of the metadata item
	 * @return metadata item or null, if not found or if either the metadata term mapping or the metadata item itself are 
	 * retired
	 * @throws InvalidMetadataTypeException when the requested type does not match the type of the metadata item
	 * referred to by the metadata term mapping
	 * @since 1.1
	 * @should return unretired metadata item for unretired term
	 * @should return retired metadata item for unretired term
	 * @should not return unretired metadata item for retired term
	 * @should fail on type mismatch
	 * @should return null if term does not exist
	 */
	@Authorized(MetadataMapping.PRIVILEGE_VIEW_METADATA)
	<T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode);
	
	/**
	 * Get metadata items of the given type that are referred to by any metadata term mappings in the given metadata source
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param <T> type of the metadata item
	 * @return list of matching metadata items
	 * @since 1.1
	 * @should return metadata items of terms matching type
	 * @should return nothing if source does not exist
	 */
	@Authorized(MetadataMapping.PRIVILEGE_VIEW_METADATA)
	<T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName);
	
	/**
	 * Save a new metadata set or update an existing one.
	 * @param metadataSet object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSet saveMetadataSet(MetadataSet metadataSet);
	
	/**
	 * Get metadata set with the given id.
	 * @param metadataSetId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.2
	 * @should return a retired set
	 */
	@Authorized()
	MetadataSet getMetadataSet(Integer metadataSetId);
	
	/**
	 * get metadata sets matching passed search criteria
	 * @param criteria
	 * @return
	 */
	@Authorized()
	List<MetadataSet> getMetadataSets(MetadataSetSearchCriteria criteria);
	
	/**
	 * Get metadata set with the given uuid.
	 * @param metadataSetUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.2
	 * @should return matching metadata set
	 */
	@Authorized()
	MetadataSet getMetadataSetByUuid(String metadataSetUuid);
	
	/**
	 * Retire the metadata set and its members and set required info via an AOP injected method.
	 * @param metadataSet object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.2
	 * @should retire and set info
	 * @should retire members
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSet retireMetadataSet(MetadataSet metadataSet, String reason);
	
	/**
	 * Save a new metadata set member or update an existing one.
	 * @param metadataSetMember object to save
	 * @return saved object
	 * @since 1.2
	 * @see #getMetadataSetMembers(MetadataSet, int, int, RetiredHandlingMode)
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSetMember saveMetadataSetMember(MetadataSetMember metadataSetMember);
	
	/**
	 * Save a new metadata set member or update an existing one.
	 * @param metadataSet set to update with entry
	 * @param metadata object to save
	 * @return saved object
	 * @since 1.2
	 * @see #getMetadataSetMembers(MetadataSet, int, int, RetiredHandlingMode)
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSetMember saveMetadataSetMember(MetadataSet metadataSet, OpenmrsMetadata metadata);
	
	/**
	 * Save a collection of new metadata set members or update an existing ones.
	 * @param metadataSetMembers collection of objects to save
	 * @return the same collection with saved objects
	 * @since 1.1
	 * @see #saveMetadataSetMember(MetadataSetMember)
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	Collection<MetadataSetMember> saveMetadataSetMembers(Collection<MetadataSetMember> metadataSetMembers);
	
	/**
	 * Get metadata set member with the given id.
	 * @param metadataSetMemberId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.2
	 */
	@Authorized()
	MetadataSetMember getMetadataSetMember(Integer metadataSetMemberId);
	
	/**
	 * Get metadata set member with the given uuid.
	 * @param metadataSetMemberUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.2
	 */
	@Authorized()
	MetadataSetMember getMetadataSetMemberByUuid(String metadataSetMemberUuid);
	
	/**
	 * Get members of a metadata set. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSet metadata set
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get members in desired order 1
	 * @should respect retire fetch mode 1
	 */
	@Authorized()
	List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get members of a metadata set. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSet metadata set
	 * @param firstResult zero based index of first result to get (null = 0)
	 * @param maxResults maximum number of results to get (null = get all)
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get members in desired order 1
	 * @should respect retire fetch mode 1
	 */
	@Authorized()
	List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get members of a metadata set with given uuid. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSetUuid metadata set uuid
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get members in desired order 1
	 * @should respect retire fetch mode 1
	 */
	@Authorized()
	List<MetadataSetMember> getMetadataSetMembers(String metadataSetUuid, RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get members of a metadata set with given uuid. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSetUuid metadata set uuid
	 * @param firstResult zero based index of first result to get (null = 0)
	 * @param maxResults maximum number of results to get (null = get all)
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get members in desired order 1
	 * @should respect retire fetch mode 1
	 */
	@Authorized()
	List<MetadataSetMember> getMetadataSetMembers(String metadataSetUuid, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get unretired metadata items in the set of specified type. If set members have {@link MetadataSetMember#getSortWeight()} set they will
	 * be ordered in ascending order according to said weight. Note that due to differences in database implementations,
	 * the order  will be unpredictable, if there are null sort weights in the set.
	 * @param type type of the metadata items
	 * @param metadataSet metadata set
	 * @param firstResult zero based index of first result to get
	 * @param maxResults maximum number of results to get
	 * @param <T> type of the metadata items
	 * @return list of items in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get unretired metadata items of unretired terms matching type in sort weight order 1
	 * @should throw IllegalArgumentException if set does not exist
	 */
	@Authorized(MetadataMapping.PRIVILEGE_VIEW_METADATA)
	<T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet, Integer firstResult,
	        Integer maxResults);
	
	/**
	 * Get unretired metadata items in the set of specified type. If set members have {@link MetadataSetMember#getSortWeight()} set they will
	 * be ordered in ascending order according to said weight. Note that due to differences in database implementations,
	 * the order  will be unpredictable, if there are null sort weights in the set.
	 * @param type type of the metadata items
	 * @param metadataSet metadata set
	 * @param <T> type of the metadata items
	 * @return list of items in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.2
	 * @should get unretired metadata items of unretired terms matching type in sort weight order 1
	 * @should throw IllegalArgumentException if set does not exist
	 */
	@Authorized(MetadataMapping.PRIVILEGE_VIEW_METADATA)
	<T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet);
	
	/**
	 * Get metadata item referred to by the given metadata term mapping
	 * @param type type of the metadata item
	 * @param setMember metadata set member referring to object
	 * @param <T> type of the metadata item
	 * @return metadata item or null, if not found or if set member itself is null
	 * @should return unretired metadata item for unretired set member
	 * @should return retired metadata item for unretired set member
	 * @should return unretired metadata item for retired set member
	 * @should return null for non existent set member
	 * @since 1.2
	 */
	@Authorized(MetadataMapping.PRIVILEGE_VIEW_METADATA)
	<T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, MetadataSetMember setMember);
	
	/**
	 * Retire the metadata set member and set required info via an AOP injected method.
	 * @param metadataSetMember object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.2
	 * @should retire and set info
	 */
	@Authorized(MetadataMapping.PRIVILEGE_MANAGE)
	MetadataSetMember retireMetadataSetMember(MetadataSetMember metadataSetMember, String reason);
}
