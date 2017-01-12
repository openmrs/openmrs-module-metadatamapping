package org.openmrs.module.metadatamapping.deploy;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSource;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataTermMapping;

public class MetadataTermMappingDeployHandlerTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataDeployService deployService;
	
	@Autowired
	private MetadataMappingService metadataMappingService;
	
	@Test
	public void integration() {
		
		// Install a metadatasource to use
		MetadataSource source = deployService.installObject(metadataSource("New name", "New desc", "obj-uuid"));
		
		// Check installing new
		deployService.installObject(metadataTermMapping(source, "code", Location.class.getName(), "metadata-uuid",
		    "term-uuid"));
		
		MetadataTermMapping created = metadataMappingService.getMetadataTermMappingByUuid("term-uuid");
		Assert.assertThat(created.getMetadataSource(), is(source));
		Assert.assertThat(created.getCode(), is("code"));
		Assert.assertThat(created.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(created.getMetadataUuid(), is("metadata-uuid"));
		Assert.assertThat(created.getUuid(), is("term-uuid"));
		
		// Check updating existing
		deployService.installObject(metadataTermMapping(source, "updated-code", PatientIdentifierType.class.getName(),
		    "updated-metadata-uuid", "term-uuid"));
		MetadataTermMapping updated = metadataMappingService.getMetadataTermMappingByUuid("term-uuid");
		Assert.assertThat(updated.getId(), Matchers.is(created.getId()));
		Assert.assertThat(updated.getCode(), Matchers.is("updated-code"));
		Assert.assertThat(created.getMetadataClass(), is(PatientIdentifierType.class.getName()));
		Assert.assertThat(created.getMetadataUuid(), is("updated-metadata-uuid"));
		
		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(MetadataTermMapping.class, "term-uuid"), "Testing");
		
		Assert.assertThat(metadataMappingService.getMetadataTermMappingByUuid("term-uuid").isRetired(), Matchers.is(true));
		
		// Check re-install unretires
		deployService.installObject(metadataTermMapping(source, "unretired-code", Location.class.getName(),
		    "unretired-metadata-uuid", "term-uuid"));
		
		MetadataSource unretired = metadataMappingService.getMetadataSourceByUuid("obj-uuid");
		Assert.assertThat(updated.getCode(), Matchers.is("unretired-code"));
		Assert.assertThat(created.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(created.getMetadataUuid(), is("unretired-metadata-uuid"));
		Assert.assertThat(unretired.isRetired(), Matchers.is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
		
		// Check everything can be persisted
		Context.flushSession();
		
	}
}
