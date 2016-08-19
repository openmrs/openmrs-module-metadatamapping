package org.openmrs.module.metadatamapping.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptSource;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ModulePropertiesComponentTest extends BaseModuleContextSensitiveTest {
	
	private ModuleProperties moduleProperties;
	
	@Before
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
		Assert.assertNotNull(source);
		Assert.assertEquals("Some Standardized Terminology", source.getName());
		
	}
	
	@Test
	public void shouldFetchLocationByUuid() {
		// this location is in the standard test data set
		Location location = moduleProperties.getEmrApiMetadataByCode(Location.class, "emr.unknownLocation");
		Assert.assertNotNull(location);
		Assert.assertEquals("Unknown Location", location.getName());
	}
	
	@Test
	public void shouldFetchProviderByUuid() {
		// this location is in the standard test data set
		Provider provider = moduleProperties.getEmrApiMetadataByCode(Provider.class, "emr.unknownProvider");
		Assert.assertNotNull(provider);
		Assert.assertEquals("Test", provider.getIdentifier());
	}
	
	@Test
	public void shouldFetchFormByUuid() {
		Form form = moduleProperties.getEmrApiMetadataByCode(Form.class, "emr.unknownForm");
		Assert.assertNotNull(form);
		Assert.assertEquals("Basic Form", form.getName());
	}
}
