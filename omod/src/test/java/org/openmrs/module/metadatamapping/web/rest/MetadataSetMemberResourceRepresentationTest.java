package org.openmrs.module.metadatamapping.web.rest;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class MetadataSetMemberResourceRepresentationTest extends BaseDelegatingResourceTest<MetadataSetMemberResource, MetadataSetMember> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Override
	public MetadataSetMember newObject() {
		return Context.getService(MetadataMappingService.class).getMetadataSetMemberByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return null;
	}
	
	@Override
	public String getUuidProperty() {
		return "f75d45fb-f478-438a-970c-1a6b4f61f503";
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		validateCommonRepresentationProps();
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		validateCommonRepresentationProps();
		assertPropPresent("auditInfo");
	}
	
	private void validateCommonRepresentationProps() {
		assertPropEquals("name", getObject().getName());
		assertPropEquals("metadataClass", getObject().getMetadataClass());
		assertPropEquals("metadataUuid", getObject().getMetadataUuid());
		assertPropPresent("metadataSet");
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
	}
}
