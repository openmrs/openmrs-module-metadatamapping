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
package org.openmrs.module.metadatamapping.api.db.hibernate.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Retires/unretires/purges local mappings with concepts.
 */
@Component("metadatamapping.LocalMappingHibernateInterceptor")
public class LocalMappingHibernateInterceptor extends EmptyInterceptor implements ApplicationContextAware {
	
	private static final long serialVersionUID = 1L;
	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object, java.io.Serializable,
	 *      java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (!(entity instanceof Concept)) {
			return;
		}
		
		Concept concept = (Concept) entity;
		
		MetadataMappingService service = Context.getService(MetadataMappingService.class);
		
		FlushMode flushMode = setFlushMode(FlushMode.MANUAL);
		if (service.isLocalConceptSourceConfigured()) {
			service.markLocalMappingRetiredInConcept(concept);
		}
		setFlushMode(flushMode);
		
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
	        String[] propertyNames, Type[] types) {
		if (!(entity instanceof Concept)) {
			return false;
		}
		
		Concept concept = (Concept) entity;
		
		MetadataMappingService service = Context.getService(MetadataMappingService.class);
		
		FlushMode flushMode = setFlushMode(FlushMode.MANUAL);
		if (service.isLocalConceptSourceConfigured()) {
			if (concept.isRetired()) {
				service.markLocalMappingRetiredInConcept(concept);
			} else {
				service.markLocalMappingUnretiredInConcept(concept);
			}
		}
		setFlushMode(flushMode);
		
		return false;
	}
	
	private FlushMode setFlushMode(FlushMode flushMode) {
		//We need to get sessionFactory lazily here, because when the interceptor is instantiated Hibenate is not yet ready to work.
		FlushMode previousFlushMode = getCurrentSession().getFlushMode();
		getCurrentSession().setFlushMode(flushMode);
		return previousFlushMode;
	}
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.hibernate.Session getCurrentSession() {
		SessionFactory sessionFactory = (SessionFactory) applicationContext.getBean("sessionFactory");
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
