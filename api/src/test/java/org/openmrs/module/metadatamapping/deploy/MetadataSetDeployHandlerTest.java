package org.openmrs.module.metadatamapping.deploy;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSet;

public class MetadataSetDeployHandlerTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataDeployService deployService;
	
	@Autowired
	private MetadataMappingService metadataMappingService;
	
	@Test
	public void integration() {
		
		// check installing new
		deployService.installObject(metadataSet("some-uuid"));
		assertNotNull(metadataMappingService.getMetadataSetByUuid("some-uuid"));
		
		// check uninstall retires
		deployService.uninstallObject(metadataMappingService.getMetadataSetByUuid("some-uuid"), "test");
		assertTrue(metadataMappingService.getMetadataSetByUuid("some-uuid").isRetired());
		
		// check that re-install unretired
		deployService.installObject(metadataSet("some-uuid"));
		assertFalse(metadataMappingService.getMetadataSetByUuid("some-uuid").isRetired());
		
		// Check everything can be persisted
		Context.flushSession();
		
	}
	
}
