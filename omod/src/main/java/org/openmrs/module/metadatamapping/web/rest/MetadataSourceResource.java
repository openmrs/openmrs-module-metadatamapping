package org.openmrs.module.metadatamapping.web.rest;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.api.MetadataSourceSearchCriteriaBuilder;
import org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE + "/source", supportedClass = MetadataSource.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class MetadataSourceResource extends MetadataDelegatingCrudResource<MetadataSource> {
	
	public static final String PARAM_SOURCE_NAME = "name";
	
	@Override
	public MetadataSource getByUniqueId(String uniqueId) {
		return getService().getMetadataSourceByUuid(uniqueId);
	}
	
	@Override
	public MetadataSource newDelegate() {
		return new MetadataSource();
	}
	
	@Override
	public MetadataSource save(MetadataSource delegate) {
		return getService().saveMetadataSource(delegate);
	}
	
	@Override
	public void purge(MetadataSource delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("purge not supported");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		// NOTE: description for ref representation is provided by MetaDataDelegatingCrudResource
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			// metadata
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			
			// links
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				// Relies on a getter method annotated with @PropertyGetter
				description.addProperty("auditInfo");
			}
		}
		
		return description;
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		return getPageableResult(context);
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return getPageableResult(context);
	}
	
	private PageableResult getPageableResult(RequestContext context) {
		MetadataSourceSearchCriteriaBuilder searchCriteriaBuilder = new MetadataSourceSearchCriteriaBuilder();
		
		if (context.getIncludeAll()) {
			searchCriteriaBuilder.setIncludeAll(true);
		}
		
		String metadataSourceName = context.getParameter(PARAM_SOURCE_NAME);
		if (StringUtils.isNotBlank(metadataSourceName)) {
			searchCriteriaBuilder.setSourceName(metadataSourceName);
		}
		
		Integer firstResult = context.getStartIndex();
		if (firstResult == null) {
			firstResult = 0;
		}
		Integer maxResults = context.getLimit();
		if (maxResults == null) {
			maxResults = 10;
		}
		
		boolean hasMore = false;
		searchCriteriaBuilder.setFirstResult(firstResult).setMaxResults(maxResults + 1).build();
		
		List<MetadataSource> metadataSources = getService().getMetadataSources(searchCriteriaBuilder.build());
		if (metadataSources.size() > maxResults) {
			hasMore = true;
			metadataSources = metadataSources.subList(0, maxResults);
		}
		
		return new AlreadyPaged<MetadataSource>(context, metadataSources, hasMore);
	}
	
	private MetadataMappingService getService() {
		return Context.getService(MetadataMappingService.class);
	}
}
