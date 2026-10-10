package org.openmrs.module.metadatamapping.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class MetadataSetResourceRepresentationTest extends BaseDelegatingResourceTest<MetadataSetResource, MetadataSet> {
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("metadataMappingInMemoryTestDataSet.xml");
	}
	
	@Override
	public MetadataSet newObject() {
		return Context.getService(MetadataMappingService.class).getMetadataSetByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "";
	}
	
	@Override
	public String getUuidProperty() {
		return "2fb06283-befc-4273-9448-2fcbbe4c99d5";
	}
	
}
