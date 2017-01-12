package org.openmrs.module.metadatamapping;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class MetadataTermMappingValidatorTest {
	
	@Test
	@Ignore("Will this load classes from modules which are not in config.xml? like org.openmrs.module.providermanagement.Provider")
	public void validate_shouldRejectNonLoadedClass() {
		MetadataTermMapping mapping = new MetadataTermMapping();
		mapping.setMetadataClass("org.api.openmrs.Monster");
		
		Errors errors = new BindException(mapping, "mapping");
		new MetadataTermMappingValidator().validate(mapping, errors);
		assertTrue(errors.hasFieldErrors("metadataClass"));
	}
	
	@Test
	public void validate_shouldPassValidationWhenClassIsLoaded() {
		MetadataTermMapping mapping = new MetadataTermMapping();
		mapping.setMetadataClass("org.openmrs.Location");
		
		Errors errors = new BindException(mapping, "mapping");
		new MetadataTermMappingValidator().validate(mapping, errors);
		assertFalse(errors.hasFieldErrors("metadataClass"));
	}
}
