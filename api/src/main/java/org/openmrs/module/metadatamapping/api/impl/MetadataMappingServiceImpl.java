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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.GlobalProperty;
import org.openmrs.ImplementationId;
import org.openmrs.OpenmrsMetadata;
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
import org.openmrs.module.metadatamapping.api.db.MetadataMappingDAO;
import org.openmrs.module.metadatamapping.api.wrapper.ConceptAdapter;
import org.springframework.transaction.annotation.Transactional;

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
	public ConceptSource createLocalSourceFromImplementationId() {
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
	public ConceptSource getLocalSource() {
		final String sourceUuid = adminService.getGlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, "");
		
		if (StringUtils.isEmpty(sourceUuid)) {
			throw new APIException("Local concept source is not set in the " + MetadataMapping.GP_LOCAL_SOURCE_UUID
			        + " global property. Call createLocalSourceFromImplementationId to have it set automatically.");
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
	public boolean isLocalSourceConfigured() {
		final String sourceUuid = adminService.getGlobalProperty(MetadataMapping.GP_LOCAL_SOURCE_UUID, "");
		if (!StringUtils.isBlank(sourceUuid)) {
			final ConceptSource source = conceptService.getConceptSourceByUuid(sourceUuid);
			return (source != null);
		} else {
			return false;
		}
	}
	
	/**
	 * @see org.openmrs.module.metadatamapping.api.MetadataMappingService#isAddLocalMappingOnExport()
	 */
	@Override
	public boolean isAddLocalMappingOnExport() {
		String addLocalMappings = adminService.getGlobalProperty(MetadataMapping.GP_ADD_LOCAL_MAPPINGS, "");
		return Boolean.valueOf(addLocalMappings);
	}
	
	@Override
	@Transactional
	public void addLocalMappingToConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localSource = getLocalSource();
		if (!conceptAdapter.hasMappingToSource(concept, localSource)) {
			conceptAdapter.addMapping(concept, localSource, concept.getId().toString());
		}
	}
	
	@Override
	@Transactional
	public void markLocalMappingRetiredInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localSource = getLocalSource();
		conceptAdapter.retireMapping(concept, localSource, concept.getId().toString());
	}
	
	@Override
	@Transactional
	public void markLocalMappingUnretiredInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localSource = getLocalSource();
		conceptAdapter.unretireMapping(concept, localSource, concept.getId().toString());
	}
	
	@Override
	@Transactional
	public void purgeLocalMappingInConcept(final Concept concept) {
		if (concept.getId() == null) {
			return;
		}
		
		final ConceptSource localSource = getLocalSource();
		conceptAdapter.purgeMapping(concept, localSource, concept.getId().toString());
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
	public Set<ConceptSource> getSubscribedSources() {
		final String sourceUuidsList = adminService.getGlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, "");
		
		if (StringUtils.isBlank(sourceUuidsList)) {
			return Collections.emptySet();
		}
		
		final String[] sourceUuids = sourceUuidsList.split(",");
		
		final Set<ConceptSource> subscribedSources = new HashSet<ConceptSource>();
		for (String sourceUuid : sourceUuids) {
			sourceUuid = sourceUuid.trim();
			final ConceptSource source = conceptService.getConceptSourceByUuid(sourceUuid);
			if (source != null) {
				subscribedSources.add(source);
			}
		}
		return Collections.unmodifiableSet(subscribedSources);
	}
	
	@Transactional
	@Override
	public boolean addSubscribedSource(ConceptSource conceptSource) {
		Set<ConceptSource> subscribedSources = new HashSet<ConceptSource>(getSubscribedSources());
		if (!subscribedSources.add(conceptSource)) {
			return false;
		}
		
		updateSubscribedSourcesGlobalProperty(subscribedSources);
		
		return true;
	}
	
	@Transactional
	@Override
	public boolean removeSubscribedSource(ConceptSource conceptSource) {
		Set<ConceptSource> subscribedSources = new HashSet<ConceptSource>(getSubscribedSources());
		if (!subscribedSources.remove(conceptSource)) {
			return false;
		}
		
		updateSubscribedSourcesGlobalProperty(subscribedSources);
		
		return true;
	}
	
	private void updateSubscribedSourcesGlobalProperty(Set<ConceptSource> subscribedSources) {
		GlobalProperty sourceUuidsGP = adminService.getGlobalPropertyObject(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS);
		
		if (sourceUuidsGP == null) {
			sourceUuidsGP = new GlobalProperty(MetadataMapping.GP_SUBSCRIBED_TO_SOURCE_UUIDS, "");
		}
		
		Set<String> sourceUuids = new HashSet<String>();
		for (ConceptSource subscribedSource : subscribedSources) {
			sourceUuids.add(subscribedSource.getUuid());
		}
		
		sourceUuidsGP.setPropertyValue(StringUtils.join(sourceUuids, ","));
		
		adminService.saveGlobalProperty(sourceUuidsGP);
	}
	
	@Override
	public boolean isLocalConcept(final Concept concept) {
		final Set<ConceptSource> subscribedSources = getSubscribedSources();
		
		if (concept.getConceptMappings() != null) {
			for (ConceptMap map : concept.getConceptMappings()) {
				if (subscribedSources.contains(map.getSource())) {
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
	public List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject) {
		return dao.getMetadataTermMappings(referredObject);
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
	@Transactional(readOnly = true)
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource) {
		return dao.getMetadataTermMappings(metadataSource);
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
	public MetadataSet getMetadataSetByUuid(String metadataSetUuid) {
		return dao.getMetadataSetByUuid(metadataSetUuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public MetadataSet getMetadataSet(MetadataSource metadataSource, String metadataSetCode) {
		return dao.getMetadataSet(metadataSource, metadataSetCode);
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
	@Transactional(readOnly = true)
	public List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, int firstResult, int maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		return dao.getMetadataSetMembers(metadataSet, firstResult, maxResults, retiredHandlingMode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetadataSetMember> getMetadataSetMembers(String metadataSourceName, String metadataSetCode, int firstResult,
	        int maxResults, RetiredHandlingMode retiredHandlingMode) {
		return dao.getMetadataSetMembers(metadataSourceName, metadataSetCode, firstResult, maxResults, retiredHandlingMode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet, int firstResult,
	        int maxResults) {
		return dao.getMetadataSetItems(type, metadataSet, firstResult, maxResults);
	}
	
	@Override
	@Transactional(readOnly = true)
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, String metadataSourceName,
	        String metadataSetCode, int firstResult, int maxResults) {
		return dao.getMetadataSetItems(type, metadataSourceName, metadataSetCode, firstResult, maxResults);
	}
	
	@Override
	@Transactional
	public MetadataSetMember retireMetadataSetMember(MetadataSetMember metadataSetMember, String reason) {
		// Required values have already been set by the injected BaseRetireHandler.
		return dao.saveMetadataSetMember(metadataSetMember);
	}
}
