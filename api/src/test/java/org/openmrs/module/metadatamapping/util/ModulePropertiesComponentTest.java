package org.openmrs.module.metadatamapping.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptSource;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class ModulePropertiesComponentTest extends BaseModuleContextSensitiveTest {
	
	private ModuleProperties moduleProperties;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("modulePropertiesComponentTestDataset.xml");
		
		moduleProperties = new ModuleProperties() {
			
			@Override
			public String getMetadataSourceName() {
				return "org.openmrs.module.emrapi";
			}
		};
		
		//module properties is manually created so services are not injected
		moduleProperties.setAdministrationService(Context.getAdministrationService());
		moduleProperties.setConceptService(Context.getConceptService());
		moduleProperties.setMetadataMappingService(Context.getService(MetadataMappingService.class));
	}
	
	@Test
	public void shouldFetchConceptSourceByUuid() {
		// this concept source is in the standard test data set
		ConceptSource source = moduleProperties.getConceptSourceByCode("emr.someConceptSource");
		Assertions.assertNotNull(source);
		Assertions.assertEquals("Some Standardized Terminology", source.getName());
		
	}
	
	@Test
	public void shouldFetchLocationByUuid() {
		// this location is in the standard test data set
		Location location = moduleProperties.getEmrApiMetadataByCode(Location.class, "emr.unknownLocation");
		Assertions.assertNotNull(location);
		Assertions.assertEquals("Unknown Location", location.getName());
	}
	
	@Test
	public void shouldFetchProviderByUuid() {
		// this location is in the standard test data set
		Provider provider = moduleProperties.getEmrApiMetadataByCode(Provider.class, "emr.unknownProvider");
		Assertions.assertNotNull(provider);
		Assertions.assertEquals("Test", provider.getIdentifier());
	}
	
	@Test
	public void shouldFetchFormByUuid() {
		Form form = moduleProperties.getEmrApiMetadataByCode(Form.class, "emr.unknownForm");
		Assertions.assertNotNull(form);
		Assertions.assertEquals("Basic Form", form.getName());
	}
}
