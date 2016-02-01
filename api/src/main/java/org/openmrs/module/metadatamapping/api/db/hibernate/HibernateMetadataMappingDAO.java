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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.db.MetadataMappingDAO;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
import org.openmrs.module.metadatamapping.util.ArgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate DAO implementation.
 */
@Component("metadatamapping.MetadataMappingDAO")
public class HibernateMetadataMappingDAO implements MetadataMappingDAO {
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	/**
	 * @see MetadataMappingDAO#getConcepts(int, int)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Concept> getConcepts(final int firstResult, final int maxResults) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Concept.class);
		criteria.addOrder(Order.asc("conceptId"));
		criteria.setMaxResults(maxResults);
		criteria.setFirstResult(firstResult);
		
		@SuppressWarnings("unchecked")
		final List<Concept> list = criteria.list();
		return list;
	}
	
	@Override
	public MetadataSource saveMetadataSource(MetadataSource metadataSource) {
		sessionFactory.getCurrentSession().saveOrUpdate(metadataSource);
		return metadataSource;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MetadataSource> getMetadataSources(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataSource.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	@Override
	public MetadataSource getMetadataSource(Integer metadataSourceId) {
		return (MetadataSource) sessionFactory.getCurrentSession().get(MetadataSource.class, metadataSourceId);
	}
	
	@Override
	public MetadataSource getMetadataSourceByName(String metadataSourceName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataSource.class);
		criteria.add(Restrictions.eq("name", metadataSourceName));
		return (MetadataSource) criteria.uniqueResult();
	}
	
	@Override
	public MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		return internalSaveMetadataTermMapping(metadataTermMapping);
	}
	
	@Override
	public Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings) {
		for (MetadataTermMapping metadataTermMapping : metadataTermMappings) {
			internalSaveMetadataTermMapping(metadataTermMapping);
		}
		return metadataTermMappings;
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId) {
		return (MetadataTermMapping) sessionFactory.getCurrentSession()
		        .get(MetadataTermMapping.class, metadataTermMappingId);
	}
	
	@Override
	public <T extends OpenmrsObject> T getByUuid(Class<T> openmrsObjectClass, String uuid) {
		return internalGetByUuid(openmrsObjectClass, uuid);
	}
	
	@Override
	@SuppressWarnings(value = "unchecked")
	public List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataTermMapping.class);
		
		criteria.add(Restrictions.eq("metadataUuid", referredObject.getUuid()));
		// Filtering on metadataClass should be redundant as uuids should be globally unique but better be on the safe side.
		criteria.add(Restrictions.eq("metadataClass", referredObject.getClass().getCanonicalName()));
		criteria.add(Restrictions.eq("retired", false));
		
		// Set ordering so as to ensure a consistent ordering of the results on consecutive invocations
		criteria.addOrder(Order.asc("metadataSource"));
		criteria.addOrder(Order.asc("metadataTermMappingId"));
		
		return criteria.list();
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataTermMapping.class);
		criteria.add(Restrictions.eq("metadataSource", metadataSource));
		criteria.add(Restrictions.eq("code", metadataTermCode));
		return (MetadataTermMapping) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataTermMapping.class);
		criteria.add(Restrictions.eq("metadataSource", metadataSource));
		criteria.add(Restrictions.eq("retired", false));
		return criteria.list();
	}
	
	@Override
	public <T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode) {
		Criteria criteria = createSourceMetadataTermCriteria(metadataSourceName, null, metadataTermCode);
		MetadataTermMapping metadataTermMapping = (MetadataTermMapping) criteria.uniqueResult();
		
		T metadataItem = null;
		if (metadataTermMapping != null) {
			if (!type.getCanonicalName().equals(metadataTermMapping.getMetadataClass())) {
				throw new InvalidMetadataTypeException("requested type " + type + " of metadata term mapping "
				        + metadataTermMapping.getUuid() + " refers to type " + metadataTermMapping.getMetadataClass());
			}
			metadataItem = internalGetByUuid(type, metadataTermMapping.getMetadataUuid());
			if (metadataItem != null && metadataItem.isRetired()) {
				metadataItem = null;
			}
		}
		
		return metadataItem;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName) {
		List<T> metadataItems = new LinkedList<T>();
		Criteria metadataTermCriteria = createSourceMetadataTermCriteria(metadataSourceName, type, null);
		for (MetadataTermMapping metadataTermMapping : (List<MetadataTermMapping>) metadataTermCriteria.list()) {
			T metadataItem = internalGetByUuid(type, metadataTermMapping.getMetadataUuid());
			if (metadataItem != null && !metadataItem.isRetired()) {
				metadataItems.add(metadataItem);
			}
		}
		return metadataItems;
	}
	
	@Override
	public MetadataSet saveMetadataSet(MetadataSet metadataSet) {
		ArgUtil.notNull(metadataSet.getMetadataSource(), "metadataSet.metadataSource");
		sessionFactory.getCurrentSession().saveOrUpdate(metadataSet);
		return metadataSet;
	}
	
	@Override
	public MetadataSet getMetadataSet(Integer metadataSetId) {
		return (MetadataSet) sessionFactory.getCurrentSession().get(MetadataSet.class, metadataSetId);
	}
	
	@Override
	public MetadataSet getMetadataSetByUuid(String metadataSetUuid) {
		return internalGetByUuid(MetadataSet.class, metadataSetUuid);
	}
	
	@Override
	public MetadataSet getMetadataSet(MetadataSource metadataSource, String metadataSetCode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataSet.class);
		criteria.add(Restrictions.eq("metadataSource", metadataSource));
		criteria.add(Restrictions.eq("code", metadataSetCode));
		return (MetadataSet) criteria.uniqueResult();
	}
	
	@Override
	public MetadataSetMember saveMetadataSetMember(MetadataSetMember metadataSetMember) {
		return internalSaveMetadataSetMember(metadataSetMember);
	}
	
	@Override
	public Collection<MetadataSetMember> saveMetadataSetMembers(Collection<MetadataSetMember> metadataSetMembers) {
		for (MetadataSetMember metadataSetMember : metadataSetMembers) {
			internalSaveMetadataSetMember(metadataSetMember);
		}
		return metadataSetMembers;
	}
	
	@Override
	public MetadataSetMember getMetadataSetMember(Integer metadataSetMemberId) {
		return (MetadataSetMember) sessionFactory.getCurrentSession().get(MetadataSetMember.class, metadataSetMemberId);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, int firstResult, int maxResults,
	        RetiredHandlingMode retiredHandlingMode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataSetMember.class);
		criteria.addOrder(Order.asc("sortWeight"));
		
		if (RetiredHandlingMode.ONLY_ACTIVE.equals(retiredHandlingMode)) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.add(Restrictions.eq("metadataSet", metadataSet));
		
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		return criteria.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MetadataSetMember> getMetadataSetMembers(String metadataSourceName, String metadataSetCode, int firstResult,
	        int maxResults, RetiredHandlingMode retiredHandlingMode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataSetMember.class);
		criteria.addOrder(Order.asc("sortWeight"));
		
		if (RetiredHandlingMode.ONLY_ACTIVE.equals(retiredHandlingMode)) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria = criteria.createCriteria("metadataSet");
		criteria.add(Restrictions.eq("code", metadataSetCode));
		
		criteria = criteria.createCriteria("metadataSource");
		criteria.add(Restrictions.eq("name", metadataSourceName));
		
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		return criteria.list();
	}
	
	@Override
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet, int firstResult,
	        int maxResults) {
		return internalGetMetadataSetItems(type, metadataSet, null, null, firstResult, maxResults);
	}
	
	@Override
	public <T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, String metadataSourceName,
	        String metadataSetCode, int firstResult, int maxResults) {
		return internalGetMetadataSetItems(type, null, metadataSourceName, metadataSetCode, firstResult, maxResults);
	}
	
	private MetadataTermMapping internalSaveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		sessionFactory.getCurrentSession().saveOrUpdate(metadataTermMapping);
		return metadataTermMapping;
	}
	
	private MetadataSetMember internalSaveMetadataSetMember(MetadataSetMember metadataSetMember) {
		sessionFactory.getCurrentSession().saveOrUpdate(metadataSetMember);
		return metadataSetMember;
	}
	
	@SuppressWarnings(value = "unchecked")
	private <T extends OpenmrsObject> T internalGetByUuid(Class<T> openmrsObjectClass, String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(openmrsObjectClass);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (T) criteria.uniqueResult();
	}
	
	private Criteria createSourceMetadataTermCriteria(String metadataSourceName, Class<?> metadataClass,
	        String metadataTermCode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MetadataTermMapping.class).add(
		    Restrictions.eq("retired", false));
		if (metadataClass != null) {
			criteria.add(Restrictions.eq("metadataClass", metadataClass.getCanonicalName()));
		}
		if (metadataTermCode != null) {
			criteria.add(Restrictions.eq("code", metadataTermCode));
		}
		
		criteria = criteria.createCriteria("metadataSource").add(Restrictions.eq("name", metadataSourceName));
		
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends OpenmrsMetadata> List<T> internalGetMetadataSetItems(Class<T> type, MetadataSet metadataSet,
	        String metadataSourceName, String metadataSetCode, int firstResult, int maxResults) {
		if (metadataSet == null && (metadataSourceName == null || metadataSetCode == null)) {
			throw new IllegalArgumentException("either metadataSet must be given or metadataSourceName and "
			        + "metadataSetCode must be given");
		}
		
		Criteria memberCriteria = sessionFactory.getCurrentSession().createCriteria(MetadataSetMember.class, "member");
		memberCriteria.add(Restrictions.eq("member.retired", false));
		if (metadataSet != null) {
			memberCriteria.add(Restrictions.eq("member.metadataSet", metadataSet));
		} else {
			memberCriteria.createCriteria("member.metadataSet", "set").add(Restrictions.eq("set.code", metadataSetCode))
			        .createCriteria("set.metadataSource", "source").add(Restrictions.eq("source.name", metadataSourceName));
		}
		
		Criteria termMappingCriteria = memberCriteria.createCriteria("metadataTermMapping", "mapping");
		termMappingCriteria.add(Restrictions.eq("mapping.retired", false));
		termMappingCriteria.add(Restrictions.eq("mapping.metadataClass", type.getCanonicalName()));
		
		DetachedCriteria metadataItemSubQuery = DetachedCriteria.forClass(type, "item");
		metadataItemSubQuery.add(Restrictions.eqProperty("item.uuid", "mapping.metadataUuid"));
		metadataItemSubQuery.add(Restrictions.eq("item.retired", false));
		metadataItemSubQuery.setProjection(Projections.property("item.uuid"));
		
		memberCriteria.add(Subqueries.propertyIn("mapping.metadataUuid", metadataItemSubQuery));
		
		memberCriteria.setProjection(Projections.property("mapping.metadataUuid"));
		memberCriteria.setFirstResult(firstResult);
		memberCriteria.setMaxResults(maxResults);
		memberCriteria.addOrder(Order.asc("member.sortWeight"));
		
		List<String> itemUuids = memberCriteria.list();
		
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
