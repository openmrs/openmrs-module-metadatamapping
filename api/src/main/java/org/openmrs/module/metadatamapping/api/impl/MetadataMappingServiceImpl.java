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
package org.openmrs.module.metadatamapping.api.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.ImplementationId;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.api.MetadataSetSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataSourceSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataSourceSearchCriteriaBuilder;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteriaBuilder;
import org.openmrs.module.metadatamapping.api.db.MetadataMappingDAO;
import org.openmrs.module.metadatamapping.api.wrapper.ConceptAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The service implementation.
 */
public class MetadataMappingServiceImpl extends BaseOpenmrsService implements MetadataMappingService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private AdministrationService adminService;
	
	private ConceptService conceptService;
	
	private MetadataMappingDAO dao;
	
	private ConceptAdapter conceptAdapter;
	
	private int batchSize = 1000;
	
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
	
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	public void setConceptAdapter(ConceptAdapter conceptAdapter) {
		this.conceptAdapter = conceptAdapter;
	}
	
	public void setDao(MetadataMappingDAO dao) {
		this.dao = dao;
	}
	
	@Override
	@Transactional
	public ConceptSource createLocalConceptSourceFromImplementationId() {
		ImplementationId implementationId = adminService.getImplementationId();
		
		if (implementationId == null) {
			throw new APIException("Implementation id is not set");
		}
		
		final ConceptSource source = new ConceptSource();
		source.setName(implementationId.getImplementationId() + MetadataMapping.LOCAL_SOURCE_NAME_POSTFIX);
		source.setDescription(MetadataMapping.LOCAL_SOURCE_DESCRIPTION_PREFIX + implementationId.getImplementationId());
		
		conceptService.saveConceptSource(source);
		
		setLocalConceptSource(source);
		
		return source;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ConceptSource getLocalConceptSource() {
		final String sourceUuid = adminService.getGlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, "");
		
		if (StringUtils.isEmpty(sourceUuid)) {
			throw new APIException("Local concept source is not set in the " + MetadataMapping.GP_LOCAL_SOURCE_UUID
			        + " global property. Call createLocalConceptSourceFromImplementationId to have it set automatically.");
		} else {
			final ConceptSource source = conceptService.getConceptSourceByUuid(sourceUuid);
			
			if (source == null) {
				throw new APIException("Local concept source [" + sourceUuid + "] set in the "
				        + MetadataMapping.GP_LOCAL_SOURCE_UUID
				        + " global property does not exist. Set the global property to " + "an existing concept source.");
			}
			
			return source;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isLocalConceptSourceConfigured() {
		final String sourceUuid = adminService.getGlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, "");
		if (!StringUtils.isBlank(sourceUuid)) {
			final ConceptSource source = conceptService.getConceptSourceByUuid(sourceUuid);
			return (source != null);
		} else {
			return false;
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatamapping.api.MetadataMappingService#isAddLocalMappingToConceptOnExport()
	 */
	@Override
	public boolean isAddLocalMappingToConceptOnExport() {
		String addLocalMappings = adminService.getGlobalProperty(MetadataMapping.GP_ADD_LOCAL_MAPPINGS, "");
		return Boolean.valueOf(addLocalMappings);
	}
	
	@Override
	@Transactional
	public void addLocalMappingToConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localConceptSource = getLocalConceptSource();
		if (!conceptAdapter.hasMappingToSource(concept, localConceptSource)) {
			conceptAdapter.addMapping(concept, localConceptSource, concept.getId().toString());
		}
	}
	
	@Override
	@Transactional
	public void markLocalMappingRetiredInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localConceptSource = getLocalConceptSource();
		conceptAdapter.retireMapping(concept, localConceptSource, concept.getId().toString());
	}
	
	@Override
	@Transactional
	public void markLocalMappingUnretiredInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localConceptSource = getLocalConceptSource();
		conceptAdapter.unretireMapping(concept, localConceptSource, concept.getId().toString());
	}
	
	@Override
	@Transactional
	public void purgeLocalMappingInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localConceptSource = getLocalConceptSource();
		conceptAdapter.purgeMapping(concept, localConceptSource, concept.getId().toString());
	}
	
	@Override
	@Transactional
	public void addLocalMappingToAllConcepts() {
		int position = 0;
		List<Concept> concepts = dao.getConcepts(position, batchSize);
		while (true) {
			for (Concept concept : concepts) {
				addLocalMappingToConcept(concept);
				if (concept.isRetired()) {
					markLocalMappingRetiredInConcept(concept);
				}
			}
			
			if (concepts.size() == batchSize) {
				position += batchSize;
				concepts = dao.getConcepts(position, batchSize);
			} else {
				break;
			}
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<ConceptSource> getSubscribedConceptSources() {
		final String sourceUuidsList = adminService.getGlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, "");
		
		if (StringUtils.isBlank(sourceUuidsList)) {
			return Collections.emptySet();
		}
		
		final String[] sourceUuids = sourceUuidsList.split(",");
		
		final Set<ConceptSource> subscribedConceptSources = new HashSet<ConceptSource>();
		for (String sourceUuid : sourceUuids) {
			sourceUuid = sourceUuid.trim();
			final ConceptSource source = conceptService.getConceptSourceByUuid(sourceUuid);
			if (source != null) {
				subscribedConceptSources.add(source);
			}
		}
		return Collections.unmodifiableSet(subscribedConceptSources);
	}
	
	@Transactional
	@Override
	public boolean addSubscribedConceptSource(ConceptSource conceptSource) {
		Set<ConceptSource> subscribedConceptSources = new HashSet<ConceptSource>(getSubscribedConceptSources());
		if (!subscribedConceptSources.add(conceptSource)) {
			return false;
		}
		
		updateSubscribedConceptSourcesGlobalProperty(subscribedConceptSources);
		
		return true;
	}
	
	@Transactional
	@Override
	public boolean removeSubscribedConceptSource(ConceptSource conceptSource) {
		Set<ConceptSource> subscribedConceptSources = new HashSet<ConceptSource>(getSubscribedConceptSources());
		if (!subscribedConceptSources.remove(conceptSource)) {
			return false;
		}
		
		updateSubscribedConceptSourcesGlobalProperty(subscribedConceptSources);
		
		return true;
	}
	
	private void updateSubscribedConceptSourcesGlobalProperty(Set<ConceptSource> subscribedConceptSources) {
		GlobalProperty sourceUuidsGP = adminService.getGlobalPropertyObject(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS);
		
		if (sourceUuidsGP == null) {
			sourceUuidsGP = new GlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, "");
		}
		
		Set<String> sourceUuids = new HashSet<String>();
		for (ConceptSource subscribedConceptSource : subscribedConceptSources) {
			sourceUuids.add(subscribedConceptSource.getUuid());
		}
		
		sourceUuidsGP.setPropertyValue(StringUtils.join(sourceUuids, ","));
		
		adminService.saveGlobalProperty(sourceUuidsGP);
	}
	
	@Override
	public boolean isLocalConcept(final Concept concept) {
		final Set<ConceptSource> subscribedConceptSources = getSubscribedConceptSources();
		
		if (concept.getConceptMappings() != null) {
			for (ConceptMap map : concept.getConceptMappings()) {
				if (subscribedConceptSources.contains(map.getConceptReferenceTerm().getConceptSource())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public Concept getConcept(final String mapping) {
		if (StringUtils.isBlank(mapping)) {
			throw new IllegalArgumentException("Mapping must not be blank");
		}
		
		final String[] split = mapping.split(":");
		if (split.length == 1) {
			try {
				final Integer id = Integer.valueOf(split[0]);
				return getConcept(id);
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Mapping '" + mapping + "' has format 'id'. The id '" + split[0]
				        + "' must be an integer.", e);
			}
		} else if (split.length == 2) {
			final String source = split[0];
			final String code = split[1];
			
			try {
				Integer.parseInt(code);
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Mapping '" + mapping + "' has format 'source:id'. The id '" + split[0]
				        + "' must be an integer.", e);
			}
			
			return Context.getConceptService().getConceptByMapping(code, source);
		} else {
			throw new IllegalArgumentException("Mapping '" + mapping + "' must contain only one ':'");
		}
	}
	
	@Override
	public Concept getConcept(final Integer id) {
		return conceptService.getConcept(id);
	}
	
	@Override
	public void setLocalConceptSource(ConceptSource conceptSource) {
		adminService.saveGlobalProperty(new GlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, conceptSource.getUuid()));
	}
	
	@Override
	@Transactional
	public MetadataSource saveMetadataSource(MetadataSource metadataSource) {
		return dao.saveMetadataSource(metadataSource);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSource> getMetadataSources(boolean includeRetired) {
		return dao.getMetadataSources(new MetadataSourceSearchCriteriaBuilder().setIncludeAll(includeRetired).build());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSource> getMetadataSources(MetadataSourceSearchCriteria searchCriteria) {
		return dao.getMetadataSources(searchCriteria);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSource getMetadataSource(Integer metadataSourceId) {
		return dao.getMetadataSource(metadataSourceId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSource getMetadataSourceByUuid(String metadataSourceUuid) {
		return dao.getByUuid(MetadataSource.class, metadataSourceUuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSource getMetadataSourceByName(String metadataSourceName) {
		return dao.getMetadataSourceByName(metadataSourceName);
	}
	
	@Override
	@Transactional
	public MetadataSource retireMetadataSource(MetadataSource metadataSource, String reason) {
		// Required values are already set by the injected BaseRetireHandler.
		return dao.saveMetadataSource(metadataSource);
	}
	
	@Override
	@Transactional
	public MetadataTermMapping mapMetadataItem(String referredObjectUuid, String referredObjectClassName, String sourceName,
	        String mappingCode) {
		
		MetadataMappingService service = Context.getService(MetadataMappingService.class);
		MetadataSource source = getMetadataSourceByName(sourceName);
		
		if (source == null) {
			throw new IllegalArgumentException("No source with name " + sourceName);
		}
		
		// create mapping if necessary
		MetadataTermMapping mapping = getMetadataTermMapping(source, mappingCode);
		if (mapping == null) {
			mapping = new MetadataTermMapping();
			mapping.setMetadataSource(source);
			mapping.setCode(mappingCode);
		}
		
		// update & save
		mapping.setMetadataUuid(referredObjectUuid);
		mapping.setMetadataClass(referredObjectClassName);
		service.saveMetadataTermMapping(mapping);
		return mapping;
	}
	
	@Override
	@Transactional
	public MetadataTermMapping mapMetadataItem(OpenmrsMetadata referredObject, String sourceName, String mappingCode) {
		if (referredObject == null) {
			throw new IllegalArgumentException("Referred object is null");
		}
		return mapMetadataItem(referredObject.getUuid(), referredObject.getClass().getCanonicalName(), sourceName,
		    mappingCode);
	}
	
	@Override
	@Transactional
	public <T extends OpenmrsMetadata> MetadataTermMapping mapMetadataItems(List<T> referredObjects, String sourceName,
	        String mappingCode) {
		
		if (referredObjects == null || referredObjects.size() == 0) {
			throw new IllegalArgumentException("List of objects to map null or empty");
		}
		
		MetadataMappingService service = Context.getService(MetadataMappingService.class);
		MetadataSource source = getMetadataSourceByName(sourceName);
		
		if (source == null) {
			throw new IllegalArgumentException("No source with name " + sourceName);
		}
		
		MetadataTermMapping mapping = getMetadataTermMapping(source, mappingCode);
		
		// create new set
		if (mapping == null) {
			
			MetadataSet set = new MetadataSet();
			service.saveMetadataSet(set);
			
			for (OpenmrsMetadata obj : referredObjects) {
				service.saveMetadataSetMember(new MetadataSetMember(obj, set));
			}
			
			mapping = new MetadataTermMapping();
			mapping.setMetadataSource(source);
			mapping.setCode(mappingCode);
			mapping.setMappedObject(set);
			service.saveMetadataTermMapping(mapping);
			return mapping;
		}
		// find and modify existing set
		else {
			MetadataSet existingSet = getMetadataItem(MetadataSet.class, sourceName, mappingCode);
			List<MetadataSetMember> existingSetMembers = getMetadataSetMembers(existingSet, RetiredHandlingMode.ONLY_ACTIVE);
			
			List<String> existingSetMemberUuids = mapToMemberUuids(existingSetMembers);
			List<String> newSetMemberUuids = mapToUuids(referredObjects);
			
			// remove any items that are no longer there
			for (MetadataSetMember member : existingSetMembers) {
				if (!newSetMemberUuids.contains(member.getMetadataUuid())) {
					service.retireMetadataSetMember(member, "removed from set");
				}
			}
			
			// add any missing items
			for (OpenmrsMetadata obj : referredObjects) {
				if (!existingSetMemberUuids.contains(obj.getUuid())) {
					service.saveMetadataSetMember(new MetadataSetMember(obj, existingSet));
				}
			}
			
		}
		
		return mapping;
	}
	
	private List<String> mapToMemberUuids(List<MetadataSetMember> members) {
		
		List<String> memberUuids = new ArrayList<String>();
		
		if (members == null) {
			return memberUuids;
		} else {
			for (MetadataSetMember member : members) {
				memberUuids.add(member.getMetadataUuid());
			}
		}
		
		return memberUuids;
	}
	
	private <T extends OpenmrsMetadata> List<String> mapToUuids(List<T> metadata) {
		
		List<String> uuids = new ArrayList<String>();
		
		if (metadata == null) {
			return uuids;
		} else {
			for (OpenmrsObject member : metadata) {
				uuids.add(member.getUuid());
			}
		}
		
		return uuids;
	}
	
	@Override
	@Transactional
	public MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		return dao.saveMetadataTermMapping(metadataTermMapping);
	}
	
	@Override
	@Transactional
	public Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings) {
		return dao.saveMetadataTermMappings(metadataTermMappings);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId) {
		return dao.getMetadataTermMapping(metadataTermMappingId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataTermMapping getMetadataTermMappingByUuid(String metadataTermMappingUuid) {
		return dao.getByUuid(MetadataTermMapping.class, metadataTermMappingUuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataTermMappingSearchCriteria searchCriteria) {
		return dao.getMetadataTermMappings(searchCriteria);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject) {
		MetadataTermMappingSearchCriteria searchCriteria = new MetadataTermMappingSearchCriteriaBuilder().setIncludeAll(
		    false).setReferredObject(referredObject).build();
		return dao.getMetadataTermMappings(searchCriteria);
	}
	
	@Override
	@Transactional
	public MetadataTermMapping retireMetadataTermMapping(MetadataTermMapping metadataTermMapping, String reason) {
		// Required values are already set by the injected BaseRetireHandler.
		return dao.saveMetadataTermMapping(metadataTermMapping);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode) {
		return dao.getMetadataTermMapping(metadataSource, metadataTermCode);
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(String metadataSourceName, String metadataTermCode) {
		return dao.getMetadataTermMapping(dao.getMetadataSourceByName(metadataSourceName), metadataTermCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource) {
		MetadataTermMappingSearchCriteria searchCriteria = new MetadataTermMappingSearchCriteriaBuilder().setIncludeAll(
		    false).setMetadataSource(metadataSource).build();
		return dao.getMetadataTermMappings(searchCriteria);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode) {
		return dao.getMetadataItem(type, metadataSourceName, metadataTermCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName) {
		return dao.getMetadataItems(type, metadataSourceName);
	}
	
	@Override
	@Transactional
	public MetadataSet saveMetadataSet(MetadataSet metadataSet) {
		return dao.saveMetadataSet(metadataSet);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSet getMetadataSet(Integer metadataSetId) {
		return dao.getMetadataSet(metadataSetId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSet> getMetadataSets(MetadataSetSearchCriteria criteria) {
		return dao.getMetadataSet(criteria);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSet getMetadataSetByUuid(String metadataSetUuid) {
		return dao.getMetadataSetByUuid(metadataSetUuid);
	}
	
	@Override
	@Transactional
	public MetadataSet retireMetadataSet(MetadataSet metadataSet, String reason) {
		// Required values on metadata set have already been set by the injected BaseRetireHandler.
		
		int firstResult = 0;
		final int CHUNK_SIZE = 100;
		while (true) {
			List<MetadataSetMember> metadataSetMembers = dao.getMetadataSetMembers(metadataSet, firstResult, CHUNK_SIZE,
			    RetiredHandlingMode.ONLY_ACTIVE);
			
			for (MetadataSetMember metadataSetMember : metadataSetMembers) {
				metadataSetMember.setRetired(true);
				metadataSetMember.setRetiredBy(metadataSet.getRetiredBy());
				metadataSetMember.setRetireReason(reason);
				metadataSetMember.setDateRetired(metadataSet.getDateRetired());
				
				dao.saveMetadataSetMember(metadataSetMember);
			}
			
			if (metadataSetMembers.size() < CHUNK_SIZE) {
				break;
			} else {
				firstResult = firstResult + CHUNK_SIZE;
			}
		}
		
		return dao.saveMetadataSet(metadataSet);
	}
	
	@Override
	@Transactional
	public MetadataSetMember saveMetadataSetMember(MetadataSetMember metadataSetMember) {
		return dao.saveMetadataSetMember(metadataSetMember);
	}
	
	@Override
	@Transactional
	public MetadataSetMember saveMetadataSetMember(MetadataSet metadataSet, OpenmrsMetadata metadata) {
		MetadataSetMember setMember = new MetadataSetMember(metadata, metadataSet);
		return saveMetadataSetMember(setMember);
	}
	
	@Override
	@Transactional
	public Collection<MetadataSetMember> saveMetadataSetMembers(Collection<MetadataSetMember> metadataSetMembers) {
		return dao.saveMetadataSetMembers(metadataSetMembers);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSetMember getMetadataSetMember(Integer metadataSetMemberId) {
		return dao.getMetadataSetMember(metadataSetMemberId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSetMember getMetadataSetMemberByUuid(String metadataSetMemberUuid) {
		return dao.getByUuid(MetadataSetMember.class, metadataSetMemberUuid);
	}
	
	@Override
	public List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, RetiredHandlingMode retiredHandlingMode) {
		return this.getMetadataSetMembers(metadataSet, null, null, retiredHandlingMode);
	}
	
	@Override
	public List<MetadataSetMember> getMetadataSetMembers(String metadataSetUuid, RetiredHandlingMode retiredHandlingMode) {
		return this.getMetadataSetMembers(metadataSetUuid, null, null, retiredHandlingMode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSetMember> getMetadataSetMembers(String metadataSetUuid, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		return dao.getMetadataSetMembers(metadataSetUuid, firstResult, maxResults, retiredHandlingMode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		return dao.getMetadataSetMembers(metadataSet, firstResult, maxResults, retiredHandlingMode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet,
	        Integer firstResult, Integer maxResults) {
		return dao.getMetadataSetItems(type, metadataSet, firstResult, maxResults);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet) {
		return dao.getMetadataSetItems(type, metadataSet);
	}
	
	@Override
	public <T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, MetadataSetMember setMember) {
		if (setMember == null) {
			return null;
		} else {
			return dao.getByUuid(type, setMember.getMetadataUuid());
		}
	}
	
	@Override
	@Transactional
	public MetadataSetMember retireMetadataSetMember(MetadataSetMember metadataSetMember, String reason) {
		// Required values have already been set by the injected BaseRetireHandler.
		return dao.saveMetadataSetMember(metadataSetMember);
	}
}
