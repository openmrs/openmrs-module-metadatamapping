package org.openmrs.module.metadatamapping.web.rest;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Test different representations of {@link MetadataSource} on the REST api.
 */
public class MetadataSourceResourceRepresentationTest extends BaseDelegatingResourceTest<MetadataSourceResource, MetadataSource> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Override
	public MetadataSource newObject() {
		return Context.getService(MetadataMappingService.class).getMetadataSourceByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Integration Test Metadata Source 3";
	}
	
	@Override
	public String getUuidProperty() {
		return "055aeb3a-da49-439e-a735-da28f26d9118";
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
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
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
	}
}
