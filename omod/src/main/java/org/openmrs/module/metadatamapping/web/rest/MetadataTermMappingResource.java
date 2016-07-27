package org.openmrs.module.metadatamapping.web.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.MetadataTermMapping.MetadataReference;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.api.MetadataTermMappingSearchCriteriaBuilder;
import org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
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

@Resource(name = RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE
        + "/metadatatermmapping", supportedClass = MetadataTermMapping.class, supportedOpenmrsVersions = { "1.9.*",
        "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class MetadataTermMappingResource extends MetadataDelegatingCrudResource<MetadataTermMapping> {
	
	public static final String PARAM_TERM_CODE = "code";
	
	public static final String PARAM_TERM_NAME = "name";
	
	public static final String PARAM_SOURCE_NAME = "sourceName";
	
	public static final String PARAM_SOURCE_UUID = "sourceUuid";
	
	public static final String PARAM_REFERENCE_CLASS = "refClass";
	
	public static final String PARAM_REFERENCE_UUID = "refUuid";
	
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
			
			description.addProperty("mappedObject");
			
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
	
	@PropertyGetter("mappedObject")
	public SimpleObject getMappedObject(MetadataTermMapping delegate) {
		SimpleObject simpleObject = null;
		if (delegate.getMetadataClass() != null && delegate.getMetadataUuid() != null) {
			simpleObject = new SimpleObject();
			simpleObject.put("className", delegate.getMetadataClass());
			simpleObject.put("uuid", delegate.getMetadataUuid());
		}
		
		return simpleObject;
	}
	
	@PropertySetter("mappedObject")
	public void setMappedObject(MetadataTermMapping delegate, Object value) throws IllegalAccessException,
	        NoSuchMethodException, InvocationTargetException {
		String metadataClass = (String) PropertyUtils.getProperty(value, "className");
		String metadataUuid = (String) PropertyUtils.getProperty(value, "uuid");
		MetadataTermMapping.MetadataReference mappedObjectReference = new MetadataTermMapping.MetadataReference(
		        metadataClass, metadataUuid);
		delegate.setMappedObject(mappedObjectReference);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("code");
		description.addRequiredProperty("metadataSource");
		description.addRequiredProperty("name");
		
		description.addProperty("description");
		description.addProperty("mappedObject");
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("code");
		description.addProperty("description");
		description.addProperty("mappedObject");
		description.addProperty("name");
		
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
		
		String metadataSourceName = context.getParameter(PARAM_SOURCE_NAME);
		String metadataSourceUuid = context.getParameter(PARAM_SOURCE_UUID);
		if (StringUtils.isNotBlank(metadataSourceName) || StringUtils.isNotBlank(metadataSourceUuid)) {
			MetadataSource metadataSource;
			if (StringUtils.isNotBlank(metadataSourceName)) {
				metadataSource = getService().getMetadataSourceByName(metadataSourceName);
			} else {
				metadataSource = getService().getMetadataSourceByUuid(metadataSourceUuid);
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
		
		String referredObjectClassName = context.getParameter(PARAM_REFERENCE_CLASS);
		String referredObjectUuid = context.getParameter(PARAM_REFERENCE_UUID);
		if (StringUtils.isNotBlank(referredObjectClassName) && StringUtils.isNotBlank(referredObjectUuid)) {
			searchCriteriaBuilder.setReferredObjectReference(new MetadataReference(referredObjectClassName.trim(),
			        referredObjectUuid.trim()));
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
