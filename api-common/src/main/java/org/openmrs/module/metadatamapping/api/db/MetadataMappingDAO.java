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

import org.openmrs.Concept;

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
}
