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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The DAO.
 */
@Component("metadatamapping.MetadataMappingDAO")
public class MetadataMappingDAO {
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	/**
	 * Allows to iterate over concepts in batches.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @return the list of concepts
	 */
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
}
