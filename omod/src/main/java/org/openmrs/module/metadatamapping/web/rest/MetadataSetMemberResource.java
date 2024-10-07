package org.openmrs.module.metadatamapping.web.rest;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Date;
import java.util.List;

@SubResource(parent = MetadataSetResource.class, path = "members", supportedClass = MetadataSetMember.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })
public class MetadataSetMemberResource extends DelegatingSubResource<MetadataSetMember, MetadataSet, MetadataSetResource> {
	
	@Override
	public MetadataSet getParent(MetadataSetMember instance) {
		return instance.getMetadataSet();
	}
	
	@Override
	public void setParent(MetadataSetMember instance, MetadataSet parent) {
		instance.setMetadataSet(parent);
	}
	
	@Override
	public PageableResult doGetAll(MetadataSet parent, RequestContext context) throws ResponseException {
		Integer firstResult = context.getStartIndex();
		if (firstResult == null) {
			firstResult = 0;
		}
		
		Integer maxResults = context.getLimit();
		if (maxResults == null) {
			maxResults = 10;
		}
		
		RetiredHandlingMode mode = RetiredHandlingMode.ONLY_ACTIVE;
		if (context.getIncludeAll()) {
			mode = RetiredHandlingMode.INCLUDE_RETIRED;
		}
		
		boolean hasMore = false;
		List<MetadataSetMember> results = getService().getMetadataSetMembers(parent, firstResult, maxResults + 1, mode);
		if (results.size() > maxResults) {
			hasMore = true;
			results = results.subList(0, maxResults);
		}
		return new AlreadyPaged<MetadataSetMember>(context, results, hasMore);
	}
	
	@Override
	public MetadataSetMember getByUniqueId(String uniqueId) {
		return getService().getMetadataSetMemberByUuid(uniqueId);
	}
	
	@Override
	protected void delete(MetadataSetMember delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isRetired()) {
			return;
		}
		delegate.setRetired(true);
		delegate.setRetiredBy(Context.getAuthenticatedUser());
		delegate.setDateRetired(new Date());
		delegate.setRetireReason(reason);
		save(delegate);
	}
	
	@Override
	public MetadataSetMember newDelegate() {
		return new MetadataSetMember();
	}
	
	@Override
	public MetadataSetMember save(MetadataSetMember delegate) {
		return getService().saveMetadataSetMember(delegate);
	}
	
	@Override
	public void purge(MetadataSetMember delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("purge not supported");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("metadataSet", Representation.REF);
			description.addProperty("metadataClass");
			description.addProperty("metadataUuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				description.addProperty("auditInfo");
			}
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	@RepHandler(RefRepresentation.class)
	public SimpleObject asRef(MetadataSetMember delegate) throws ConversionException {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		if (delegate.isRetired())
			rep.addProperty("retired");
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	private MetadataMappingService getService() {
		return Context.getService(MetadataMappingService.class);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("metadataUuid");
		description.addRequiredProperty("metadataClass");
		description.addProperty("name");
		description.addProperty("description");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("description");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty().example("uuid"));
			model.property("metadataSet", new RefProperty("#/definitions/MetadatamappingMetadatasetGetRef"));
			model.property("metadataClass", new StringProperty());
			model.property("metadataUuid", new StringProperty());
			model.property("name", new StringProperty());
			model.property("description", new StringProperty());
			model.property("retired", new BooleanProperty());
		}
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("metadataUuid", new StringProperty())
		        .property("metadataClass", new StringProperty()).property("name", new StringProperty()).property(
		            "description", new StringProperty());
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getUPDATEModel(rep);
		return model.property("name", new StringProperty()).property("description", new StringProperty());
	}
}
