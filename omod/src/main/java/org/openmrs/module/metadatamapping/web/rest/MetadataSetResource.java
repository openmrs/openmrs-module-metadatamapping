package org.openmrs.module.metadatamapping.web.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.api.MetadataSetSearchCriteria;
import org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE + "/metadataset", supportedClass = MetadataSet.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class MetadataSetResource extends MetadataDelegatingCrudResource<MetadataSet> {
	
	@Override
	public MetadataSet getByUniqueId(String uniqueId) {
		return getService().getMetadataSetByUuid(uniqueId);
	}
	
	@Override
	public MetadataSet newDelegate() {
		return new MetadataSet();
	}
	
	@Override
	public MetadataSet save(MetadataSet delegate) {
		return getService().saveMetadataSet(delegate);
	}
	
	@Override
	public void purge(MetadataSet delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("purge not supported");
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		Integer firstResult = context.getStartIndex();
		if (firstResult == null) {
			firstResult = 0;
		}
		
		Integer maxResults = context.getLimit();
		if (maxResults == null) {
			maxResults = 10;
		}
		
		boolean hasMore = false;
		List<MetadataSet> results = getService().getMetadataSets(
		    new MetadataSetSearchCriteria(context.getIncludeAll(), firstResult, maxResults));
		if (results.size() > maxResults) {
			hasMore = true;
			results = results.subList(0, maxResults);
		}
		return new AlreadyPaged<MetadataSet>(context, results, hasMore);
	}
	
	private MetadataMappingService getService() {
		return Context.getService(MetadataMappingService.class);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("name");
		description.addProperty("description");
		
		return description;
	}
	
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("members");
	}
}
