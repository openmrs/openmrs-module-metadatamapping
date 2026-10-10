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
package org.openmrs.module.metadatamapping.api.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.MetadataSetSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataSourceSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteria;
import org.openmrs.module.metadatamapping.api.db.MetadataMappingDAO;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Hibernate DAO implementation.
 */
@Component("metadatamapping.MetadataMappingDAO")
public class HibernateMetadataMappingDAO implements MetadataMappingDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * @see MetadataMappingDAO#getConcepts(int, int)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Concept> getConcepts(final int firstResult, final int maxResults) {
		final Query<Concept> query = getCurrentSession().createQuery("from Concept c order by c.conceptId asc", Concept.class);
		query.setMaxResults(maxResults);
		query.setFirstResult(firstResult);
		
		return query.list();
	}
	
	@Override
	public MetadataSource saveMetadataSource(MetadataSource metadataSource) {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), metadataSource);
	}
	
	@Override
	public List<MetadataSource> getMetadataSources(MetadataSourceSearchCriteria searchCriteria) {
		StringBuilder hql = new StringBuilder("from MetadataSource s where 1 = 1");
		Map<String, Object> params = new HashMap<String, Object>();
		
		if (!searchCriteria.isIncludeAll()) {
			hql.append(" and s.retired = false");
		}
		
		if (searchCriteria.getSourceName() != null) {
			hql.append(" and s.name = :name");
			params.put("name", searchCriteria.getSourceName());
		}
		
		hql.append(" order by s.name asc, s.metadataSourceId asc");
		
		Query<MetadataSource> query = createQuery(hql.toString(), MetadataSource.class, params);
		if (searchCriteria.getFirstResult() != null) {
			query.setFirstResult(searchCriteria.getFirstResult());
		}
		if (searchCriteria.getMaxResults() != null) {
			query.setMaxResults(searchCriteria.getMaxResults());
		}
		
		return query.list();
	}
	
	@Override
	public MetadataSource getMetadataSource(Integer metadataSourceId) {
		return getCurrentSession().find(MetadataSource.class, metadataSourceId);
	}
	
	@Override
	public MetadataSource getMetadataSourceByName(String metadataSourceName) {
		return getCurrentSession().createQuery("from MetadataSource s where s.name = :name", MetadataSource.class)
		        .setParameter("name", metadataSourceName).uniqueResult();
	}
	
	@Override
	public MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		return internalSaveMetadataTermMapping(metadataTermMapping);
	}
	
	@Override
	public Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings) {
		List<MetadataTermMapping> savedMetadataTermMappings = new LinkedList<MetadataTermMapping>();
		for (MetadataTermMapping metadataTermMapping : metadataTermMappings) {
			savedMetadataTermMappings.add(internalSaveMetadataTermMapping(metadataTermMapping));
		}
		return savedMetadataTermMappings;
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId) {
		return getCurrentSession().find(MetadataTermMapping.class, metadataTermMappingId);
	}
	
	@Override
	public <T extends OpenmrsObject> T getByUuid(Class<T> openmrsObjectClass, String uuid) {
		return internalGetByUuid(openmrsObjectClass, uuid);
	}
	
	@Override
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataTermMappingSearchCriteria searchCriteria) {
		StringBuilder hql = new StringBuilder("from MetadataTermMapping m where 1 = 1");
		Map<String, Object> params = new HashMap<String, Object>();
		
		// Filtering on metadataClass should be redundant as uuids should be globally unique but better be on the safe
		// side.
		if (searchCriteria.getReferredObject() != null) {
			hql.append(" and m.metadataUuid = :referredUuid and m.metadataClass = :referredClass");
			params.put("referredUuid", searchCriteria.getReferredObject().getUuid());
			params.put("referredClass", searchCriteria.getReferredObject().getClass().getCanonicalName());
		}
		
		if (searchCriteria.getMetadataUuid() != null) {
			hql.append(" and m.metadataUuid = :metadataUuid");
			params.put("metadataUuid", searchCriteria.getMetadataUuid());
		}
		
		if (searchCriteria.getMetadataClass() != null) {
			hql.append(" and m.metadataClass = :metadataClass");
			params.put("metadataClass", searchCriteria.getMetadataClass());
		}
		
		if (!searchCriteria.isIncludeAll()) {
			hql.append(" and m.retired = false");
		}
		
		if (searchCriteria.getMapped() != null) {
			if (searchCriteria.getMapped()) {
				hql.append(" and m.metadataUuid is not null");
			} else {
				hql.append(" and m.metadataUuid is null");
			}
		}
		
		if (searchCriteria.getMetadataSource() != null) {
			hql.append(" and m.metadataSource = :metadataSource");
			params.put("metadataSource", searchCriteria.getMetadataSource());
		}
		
		if (searchCriteria.getMetadataTermCode() != null) {
			hql.append(" and m.code = :code");
			params.put("code", searchCriteria.getMetadataTermCode());
		}
		
		if (searchCriteria.getMetadataTermName() != null) {
			hql.append(" and m.name = :name");
			params.put("name", searchCriteria.getMetadataTermName());
		}
		
		// Set ordering so as to ensure a consistent ordering of the results on consecutive invocations
		hql.append(" order by m.metadataSource.metadataSourceId asc, m.metadataTermMappingId asc");
		
		Query<MetadataTermMapping> query = createQuery(hql.toString(), MetadataTermMapping.class, params);
		if (searchCriteria.getFirstResult() != null) {
			query.setFirstResult(searchCriteria.getFirstResult());
		}
		if (searchCriteria.getMaxResults() != null) {
			query.setMaxResults(searchCriteria.getMaxResults());
		}
		
		return query.list();
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode) {
		return getCurrentSession()
		        .createQuery("from MetadataTermMapping m where m.metadataSource = :metadataSource and m.code = :code",
		            MetadataTermMapping.class).setParameter("metadataSource", metadataSource)
		        .setParameter("code", metadataTermCode).uniqueResult();
	}
	
	@Override
	public <T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode) {
		Query<MetadataTermMapping> query = createSourceMetadataTermQuery(metadataSourceName, null, metadataTermCode);
		MetadataTermMapping metadataTermMapping = query.uniqueResult();
		
		T metadataItem = null;
		if (metadataTermMapping != null) {
			if (!type.getCanonicalName().equals(metadataTermMapping.getMetadataClass())) {
				throw new InvalidMetadataTypeException("requested type " + type + " of metadata term mapping "
				        + metadataTermMapping.getUuid() + " refers to type " + metadataTermMapping.getMetadataClass());
			}
			metadataItem = internalGetByUuid(type, metadataTermMapping.getMetadataUuid());
		}
		return metadataItem;
	}
	
	@Override
	public <T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName) {
		List<T> metadataItems = new LinkedList<T>();
		Query<MetadataTermMapping> metadataTermQuery = createSourceMetadataTermQuery(metadataSourceName, type, null);
		for (MetadataTermMapping metadataTermMapping : metadataTermQuery.list()) {
			T metadataItem = internalGetByUuid(type, metadataTermMapping.getMetadataUuid());
			if (metadataItem != null) {
				metadataItems.add(metadataItem);
			}
		}
		return metadataItems;
	}
	
	@Override
	public MetadataSet saveMetadataSet(MetadataSet metadataSet) {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), metadataSet);
	}
	
	@Override
	public MetadataSet getMetadataSet(Integer metadataSetId) {
		return getCurrentSession().find(MetadataSet.class, metadataSetId);
	}
	
	@Override
	public List<MetadataSet> getMetadataSet(MetadataSetSearchCriteria searchCriteria) {
		String hql = "from MetadataSet s";
		if (!searchCriteria.isIncludeAll()) {
			hql += " where s.retired = false";
		}
		Query<MetadataSet> query = getCurrentSession().createQuery(hql, MetadataSet.class);
		
		if (searchCriteria.getFirstResult() != null) {
			query.setFirstResult(searchCriteria.getFirstResult());
		}
		if (searchCriteria.getMaxResults() != null) {
			query.setMaxResults(searchCriteria.getMaxResults());
		}
		return query.list();
	}
	
	@Override
	public MetadataSet getMetadataSetByUuid(String metadataSetUuid) {
		return internalGetByUuid(MetadataSet.class, metadataSetUuid);
	}
	
	@Override
	public MetadataSetMember saveMetadataSetMember(MetadataSetMember metadataSetMember) {
		return internalSaveMetadataSetMember(metadataSetMember);
	}
	
	@Override
	public Collection<MetadataSetMember> saveMetadataSetMembers(Collection<MetadataSetMember> metadataSetMembers) {
		List<MetadataSetMember> savedMetadataSetMembers = new LinkedList<MetadataSetMember>();
		for (MetadataSetMember metadataSetMember : metadataSetMembers) {
			savedMetadataSetMembers.add(internalSaveMetadataSetMember(metadataSetMember));
		}
		return savedMetadataSetMembers;
	}
	
	@Override
	public MetadataSetMember getMetadataSetMember(Integer metadataSetMemberId) {
		return getCurrentSession().find(MetadataSetMember.class, metadataSetMemberId);
	}
	
	@Override
	public List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		String hql = "from MetadataSetMember m where m.metadataSet = :metadataSet";
		if (RetiredHandlingMode.ONLY_ACTIVE.equals(retiredHandlingMode)) {
			hql += " and m.retired = false";
		}
		hql += " order by m.sortWeight desc";
		
		Query<MetadataSetMember> query = getCurrentSession().createQuery(hql, MetadataSetMember.class);
		query.setParameter("metadataSet", metadataSet);
		
		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}
		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}
		return query.list();
	}
	
	@Override
	public List<MetadataSetMember> getMetadataSetMembers(String metadataSetUuid, Integer firstResult, Integer maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		MetadataSet metadataSet = getMetadataSetByUuid(metadataSetUuid);
		return getMetadataSetMembers(metadataSet, firstResult, maxResults, retiredHandlingMode);
	}
	
	@Override
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet,
	        Integer firstResult, Integer maxResults) {
		return internalGetMetadataSetItems(type, metadataSet, firstResult, maxResults);
	}
	
	@Override
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet) {
		return internalGetMetadataSetItems(type, metadataSet, null, null);
	}
	
	private MetadataTermMapping internalSaveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), metadataTermMapping);
	}
	
	private MetadataSetMember internalSaveMetadataSetMember(MetadataSetMember metadataSetMember) {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), metadataSetMember);
	}
	
	private <T extends OpenmrsObject> T internalGetByUuid(Class<T> openmrsObjectClass, String uuid) {
		return HibernateUtil.getUniqueEntityByUUID(sessionFactory, openmrsObjectClass, uuid);
	}
	
	private Query<MetadataTermMapping> createSourceMetadataTermQuery(String metadataSourceName, Class<?> metadataClass,
	        String metadataTermCode) {
		StringBuilder hql = new StringBuilder(
		        "select m from MetadataTermMapping m join m.metadataSource s where m.retired = false");
		Map<String, Object> params = new HashMap<String, Object>();
		if (metadataClass != null) {
			hql.append(" and m.metadataClass = :metadataClass");
			params.put("metadataClass", metadataClass.getCanonicalName());
		}
		if (metadataTermCode != null) {
			hql.append(" and m.code = :code");
			params.put("code", metadataTermCode);
		}
		
		hql.append(" and s.name = :sourceName");
		params.put("sourceName", metadataSourceName);
		
		return createQuery(hql.toString(), MetadataTermMapping.class, params);
	}
	
	private <T> Query<T> createQuery(String hql, Class<T> resultClass, Map<String, Object> params) {
		Query<T> query = getCurrentSession().createQuery(hql, resultClass);
		for (Map.Entry<String, Object> param : params.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}
		return query;
	}
	
	private <T extends OpenmrsMetadata> List<T> internalGetMetadataSetItems(Class<T> type, MetadataSet metadataSet,
	        Integer firstResult, Integer maxResults) {
		if (metadataSet == null) {
			throw new IllegalArgumentException("To obtain MetadataSet items, reference to MetadataSet must be given");
		}
		
		Query<String> memberQuery = getCurrentSession().createQuery(
		    "select member.metadataUuid from MetadataSetMember member"
		            + " where member.retired = false and member.metadataSet = :metadataSet"
		            + " and member.metadataUuid in (select item.uuid from " + type.getName() + " item"
		            + " where item.uuid = member.metadataUuid and item.retired = false)"
		            + " order by member.sortWeight desc", String.class);
		memberQuery.setParameter("metadataSet", metadataSet);
		if (firstResult != null) {
			memberQuery.setFirstResult(firstResult);
		}
		if (maxResults != null) {
			memberQuery.setMaxResults(maxResults);
		}
		
		List<String> itemUuids = memberQuery.list();
		
		List<T> items = new LinkedList<T>();
		for (String itemUuid : itemUuids) {
			T item = getByUuid(type, itemUuid);
			if (item != null) {
				items.add(item);
			}
		}
		return items;
	}
}
