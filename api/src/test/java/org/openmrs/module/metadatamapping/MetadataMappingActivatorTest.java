package org.openmrs.module.metadatamapping;

import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MetadataMappingActivatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	UserService userService;
	
	@Test
	public void started_shouldRemoveDeprecatedMetadataMappingPrivilege() {
		final String metadataMapping = "Metadata Mapping";
		//given
		Privilege privilege = new Privilege(metadataMapping, metadataMapping);
		privilege = userService.savePrivilege(privilege);
		for (Role role : userService.getAllRoles()) {
			role.addPrivilege(privilege);
		}
		
		//when
		new MetadataMappingActivator().started();
		
		//then
		privilege = userService.getPrivilege(metadataMapping);
		
		assertThat(privilege, is(nullValue()));
	}
	
}
