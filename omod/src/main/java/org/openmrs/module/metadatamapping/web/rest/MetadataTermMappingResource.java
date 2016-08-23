package org.openmrs.module.metadatamapping.web.rest;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteria;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteriaBuilder;
import org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
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
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE + "/termmapping", supportedClass = MetadataTermMapping.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class MetadataTermMappingResource extends MetadataDelegatingCrudResource<MetadataTermMapping> {
	
	public static final String PARAM_TERM_CODE = "code";
	
	public static final String PARAM_TERM_NAME = "name";
	
	public static final String PARAM_SOURCE_NAME_OR_UUID = "source";
	
	public static final String PARAM_METADATA_CLASS = "metadataClass";
	
	public static final String PARAM_METADATA_UUID = "metadataUuid";
	
	public static final String PARAM_DEFINED = "mapped";
	
	@Override
	public MetadataTermMapping getByUniqueId(String uniqueId) {
		return getService().getMetadataTermMappingByUuid(uniqueId);
	}
	
	@Override
	public MetadataTermMapping newDelegate() {
		return new MetadataTermMapping();
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		return super.create(propertiesToCreate, context);
	}
	
	@Override
	public MetadataTermMapping save(MetadataTermMapping delegate) {
		return getService().saveMetadataTermMapping(delegate);
	}
	
	@Override
	public void purge(MetadataTermMapping delegate, RequestContext context) throws ResponseException {
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
			
			description.addProperty("metadataSource", Representation.REF);
			description.addProperty("code");
			description.addProperty("metadataClass");
			description.addProperty("metadataUuid");
			
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
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("code");
		description.addRequiredProperty("metadataSource");
		description.addRequiredProperty("metadataClass");
		
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("metadataUuid");
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("code");
		description.addProperty("description");
		description.addProperty("name");
		description.addProperty("metadataUuid");
		description.addProperty("metadataClass");
		
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
		MetadataTermMappingSearchCriteriaBuilder searchCriteriaBuilder = new MetadataTermMappingSearchCriteriaBuilder();
		
		if (context.getIncludeAll()) {
			searchCriteriaBuilder.setIncludeAll(true);
		}
		
		String metadataSourceNameOrUuid = context.getParameter(PARAM_SOURCE_NAME_OR_UUID);
		if (StringUtils.isNotBlank(metadataSourceNameOrUuid)) {
			MetadataSource metadataSource = getService().getMetadataSourceByUuid(metadataSourceNameOrUuid);
			if (metadataSource == null) {
				metadataSource = getService().getMetadataSourceByName(metadataSourceNameOrUuid);
			}
			
			if (metadataSource == null) {
				// NOTE: Short circuit to empty search result when given metadata source does not exist.
				return new AlreadyPaged<MetadataTermMapping>(context, Collections.<MetadataTermMapping> emptyList(), false);
			} else {
				searchCriteriaBuilder.setMetadataSource(metadataSource);
			}
		}
		
		String metadataTermCode = context.getParameter(PARAM_TERM_CODE);
		if (StringUtils.isNotBlank(metadataTermCode)) {
			searchCriteriaBuilder.setMetadataTermCode(metadataTermCode);
		}
		
		String metadataTermName = context.getParameter(PARAM_TERM_NAME);
		if (StringUtils.isNotBlank(metadataTermName)) {
			searchCriteriaBuilder.setMetadataTermName(metadataTermName);
		}
		
		String metadataClassName = context.getParameter(PARAM_METADATA_CLASS);
		if (StringUtils.isNotBlank(metadataClassName)) {
			searchCriteriaBuilder.setMetadataClass(metadataClassName);
		}
		
		String metadataUuid = context.getParameter(PARAM_METADATA_UUID);
		if (StringUtils.isNotBlank(metadataUuid)) {
			searchCriteriaBuilder.setMetadataUuid(metadataUuid);
		}
		
		String isReturnOnlyDefined = context.getParameter(PARAM_DEFINED);
		if (StringUtils.isNotBlank(isReturnOnlyDefined)) {
			if (isReturnOnlyDefined.toLowerCase().equals("true")) {
				searchCriteriaBuilder.setMapped(Boolean.TRUE);
			} else if (isReturnOnlyDefined.toLowerCase().equals("false")) {
				searchCriteriaBuilder.setMapped(Boolean.FALSE);
			}
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
		searchCriteriaBuilder.setFirstResult(firstResult).setMaxResults(maxResults + 1);
		
		List<MetadataTermMapping> metadataTermMappings = getService().getMetadataTermMappings(searchCriteriaBuilder.build());
		if (metadataTermMappings.size() > maxResults) {
			hasMore = true;
			metadataTermMappings = metadataTermMappings.subList(0, maxResults);
		}
		
		return new AlreadyPaged<MetadataTermMapping>(context, metadataTermMappings, hasMore);
	}
	
	private MetadataMappingService getService() {
		return Context.getService(MetadataMappingService.class);
	}
}
