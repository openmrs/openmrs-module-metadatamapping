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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataSourceSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteria;
import org.openmrs.module.metadatamapping.api.db.MetadataMappingDAO;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;
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
		final Criteria criteria = getCurrentSession().createCriteria(Concept.class);
		criteria.addOrder(Order.asc("conceptId"));
		criteria.setMaxResults(maxResults);
		criteria.setFirstResult(firstResult);
		
		@SuppressWarnings("unchecked")
		final List<Concept> list = criteria.list();
		return list;
	}
	
	@Override
	public MetadataSource saveMetadataSource(MetadataSource metadataSource) {
		getCurrentSession().saveOrUpdate(metadataSource);
		return metadataSource;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MetadataSource> getMetadataSources(MetadataSourceSearchCriteria searchCriteria) {
		Criteria criteria = getCurrentSession().createCriteria(MetadataSource.class);
		
		if (!searchCriteria.isIncludeAll()) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		if (searchCriteria.getSourceName() != null) {
			criteria.add(Restrictions.eq("name", searchCriteria.getSourceName()));
		}
		
		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.asc("id"));
		
		if (searchCriteria.getFirstResult() != null) {
			criteria.setFirstResult(searchCriteria.getFirstResult());
		}
		if (searchCriteria.getMaxResults() != null) {
			criteria.setMaxResults(searchCriteria.getMaxResults());
		}
		
		return criteria.list();
	}
	
	@Override
	public MetadataSource getMetadataSource(Integer metadataSourceId) {
		return (MetadataSource) getCurrentSession().get(MetadataSource.class, metadataSourceId);
	}
	
	@Override
	public MetadataSource getMetadataSourceByName(String metadataSourceName) {
		Criteria criteria = getCurrentSession().createCriteria(MetadataSource.class);
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
		return (MetadataTermMapping) getCurrentSession().get(MetadataTermMapping.class, metadataTermMappingId);
	}
	
	@Override
	public <T extends OpenmrsObject> T getByUuid(Class<T> openmrsObjectClass, String uuid) {
		return internalGetByUuid(openmrsObjectClass, uuid);
	}
	
	@Override
	@SuppressWarnings(value = "unchecked")
	public List<MetadataTermMapping> getMetadataTermMappings(MetadataTermMappingSearchCriteria searchCriteria) {
		Criteria criteria = getCurrentSession().createCriteria(MetadataTermMapping.class);
		
		// Filtering on metadataClass should be redundant as uuids should be globally unique but better be on the safe
		// side.
		if (searchCriteria.getReferredObject() != null) {
			criteria.add(Restrictions.eq("metadataUuid", searchCriteria.getReferredObject().getUuid()));
			criteria.add(Restrictions.eq("metadataClass", searchCriteria.getReferredObject().getClass().getCanonicalName()));
		} else if (searchCriteria.getReferredObjectReference() != null) {
			criteria.add(Restrictions.eq("metadataUuid", searchCriteria.getReferredObjectReference().getReferenceUuid()));
			criteria.add(Restrictions.eq("metadataClass", searchCriteria.getReferredObjectReference()
			        .getReferenceCanonicalClassName()));
		}
		
		if (!searchCriteria.isIncludeAll()) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		if (searchCriteria.getMetadataSource() != null) {
			criteria.add(Restrictions.eq("metadataSource", searchCriteria.getMetadataSource()));
		}
		
		if (searchCriteria.getMetadataTermCode() != null) {
			criteria.add(Restrictions.eq("code", searchCriteria.getMetadataTermCode()));
		}
		
		if (searchCriteria.getMetadataTermName() != null) {
			criteria.add(Restrictions.eq("name", searchCriteria.getMetadataTermName()));
		}
		
		// Set ordering so as to ensure a consistent ordering of the results on consecutive invocations
		criteria.addOrder(Order.asc("metadataSource"));
		criteria.addOrder(Order.asc("metadataTermMappingId"));
		
		if (searchCriteria.getFirstResult() != null) {
			criteria.setFirstResult(searchCriteria.getFirstResult());
		}
		if (searchCriteria.getMaxResults() != null) {
			criteria.setMaxResults(searchCriteria.getMaxResults());
		}
		
		return criteria.list();
	}
	
	@Override
	public MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode) {
		Criteria criteria = getCurrentSession().createCriteria(MetadataTermMapping.class);
		criteria.add(Restrictions.eq("metadataSource", metadataSource));
		criteria.add(Restrictions.eq("code", metadataTermCode));
		return (MetadataTermMapping) criteria.uniqueResult();
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
			if (metadataItem != null) {
				metadataItems.add(metadataItem);
			}
		}
		return metadataItems;
	}
	
	private MetadataTermMapping internalSaveMetadataTermMapping(MetadataTermMapping metadataTermMapping) {
		getCurrentSession().saveOrUpdate(metadataTermMapping);
		return metadataTermMapping;
	}
	
	@SuppressWarnings(value = "unchecked")
	private <T extends OpenmrsObject> T internalGetByUuid(Class<T> openmrsObjectClass, String uuid) {
		Criteria criteria = getCurrentSession().createCriteria(openmrsObjectClass);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (T) criteria.uniqueResult();
	}
	
	private Criteria createSourceMetadataTermCriteria(String metadataSourceName, Class<?> metadataClass,
	        String metadataTermCode) {
		Criteria criteria = getCurrentSession().createCriteria(MetadataTermMapping.class).add(
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
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 *
	 * @return the current hibernate session.
	 */
	private org.hibernate.Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session) method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}
