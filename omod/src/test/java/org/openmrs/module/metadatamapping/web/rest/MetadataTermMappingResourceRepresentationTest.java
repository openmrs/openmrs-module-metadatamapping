package org.openmrs.module.metadatamapping.web.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Test different representations of {@link MetadataTermMapping} on the REST api.
 */
public class MetadataTermMappingResourceRepresentationTest extends BaseDelegatingResourceTest<MetadataTermMappingResource, MetadataTermMapping> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Override
	public MetadataTermMapping newObject() {
		return Context.getService(MetadataMappingService.class).getMetadataTermMappingByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Location Xanadu";
	}
	
	@Override
	public String getUuidProperty() {
		return "ca6b1024-42e9-48f2-8656-3757527ddf8c";
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
	
	private void validateCommonRepresentationProps() throws Exception {
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
		
		assertPropPresent("metadataSource");
		SimpleObject metadataSource = getRepresentation().get("metadataSource");
		assertEquals(getObject().getMetadataSource().getUuid(), metadataSource.get("uuid"));
		assertEquals(getObject().getMetadataSource().getName(), metadataSource.get("display"));
		
		assertPropEquals("code", getObject().getCode());
		assertPropEquals("metadataClass", getObject().getMetadataClass());
		assertPropEquals("metadataUuid", getObject().getMetadataUuid());
	}
}
