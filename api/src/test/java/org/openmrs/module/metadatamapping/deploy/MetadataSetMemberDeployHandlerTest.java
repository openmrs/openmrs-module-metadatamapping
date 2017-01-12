package org.openmrs.module.metadatamapping.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSet;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSetMember;

public class MetadataSetMemberDeployHandlerTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataDeployService deployService;
	
	@Autowired
	private MetadataMappingService metadataMappingService;
	
	@Test
	public void integration() {
		
		// install a set to use
		MetadataSet set = deployService.installObject(metadataSet("set-uuid"));
		
		// Check installing new
		deployService.installObject(metadataSetMember(set, Location.class.getName(), "location-uuid", 2.0, "member-uuid"));
		
		MetadataSetMember created = metadataMappingService.getMetadataSetMemberByUuid("member-uuid");
		assertThat(created.getMetadataSet(), is(set));
		assertThat(created.getMetadataClass(), is(Location.class.getName()));
		assertThat(created.getMetadataUuid(), is("location-uuid"));
		assertThat(created.getSortWeight(), is(2.0));
		assertThat(created.getUuid(), is("member-uuid"));
		
		// Check updating existing
		deployService.installObject(metadataSetMember(set, PatientIdentifierType.class.getName(), "identifier-type-uuid",
		    3.0, "member-uuid"));
		
		MetadataSetMember updated = metadataMappingService.getMetadataSetMemberByUuid("member-uuid");
		assertThat(updated.getMetadataSet(), is(set));
		assertThat(updated.getMetadataClass(), is(PatientIdentifierType.class.getName()));
		assertThat(updated.getMetadataUuid(), is("identifier-type-uuid"));
		assertThat(updated.getSortWeight(), is(3.0));
		
		// check uninstall retires
		deployService.uninstallObject(metadataMappingService.getMetadataSetMemberByUuid("member-uuid"), "test");
		
		assertTrue(metadataMappingService.getMetadataSetMemberByUuid("member-uuid").isRetired());
		
		// check re-install unretires
		deployService.installObject(metadataSetMember(set, Location.class.getName(), "unretired-location-uuid", 2.0,
		    "member-uuid"));
		MetadataSetMember unretired = metadataMappingService.getMetadataSetMemberByUuid("member-uuid");
		assertThat(unretired.getMetadataSet(), is(set));
		assertThat(unretired.getMetadataClass(), is(Location.class.getName()));
		assertThat(unretired.getMetadataUuid(), is("unretired-location-uuid"));
		assertThat(unretired.getSortWeight(), is(2.0));
		assertFalse(unretired.isRetired());
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
		
		// Check everything can be persisted
		Context.flushSession();
		
	}
	
}
