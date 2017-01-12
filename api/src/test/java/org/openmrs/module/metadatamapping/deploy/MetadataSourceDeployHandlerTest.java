package org.openmrs.module.metadatamapping.deploy;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSource;

public class MetadataSourceDeployHandlerTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataDeployService deployService;
	
	@Autowired
	private MetadataMappingService metadataMappingService;
	
	@Test
	public void integration() {
		
		// Check installing new
		deployService.installObject(metadataSource("New name", "New desc", "obj-uuid"));
		
		MetadataSource created = metadataMappingService.getMetadataSourceByUuid("obj-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));
		
		// Check updating existing
		deployService.installObject(metadataSource("Updated name", "Updated desc", "obj-uuid"));
		MetadataSource updated = metadataMappingService.getMetadataSourceByUuid("obj-uuid");
		Assert.assertThat(updated.getId(), Matchers.is(created.getId()));
		Assert.assertThat(updated.getName(), Matchers.is("Updated name"));
		Assert.assertThat(updated.getDescription(), Matchers.is("Updated desc"));
		
		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(MetadataSource.class, "obj-uuid"), "Testing");
		
		Assert.assertThat(metadataMappingService.getMetadataSourceByUuid("obj-uuid").isRetired(), Matchers.is(true));
		
		// Check re-install unretires
		deployService.installObject(metadataSource("Unretired name", "Unretired desc", "obj-uuid"));
		
		MetadataSource unretired = metadataMappingService.getMetadataSourceByUuid("obj-uuid");
		Assert.assertThat(unretired.getName(), Matchers.is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), Matchers.is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), Matchers.is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
		
		// Check everything can be persisted
		Context.flushSession();
		
	}
}
