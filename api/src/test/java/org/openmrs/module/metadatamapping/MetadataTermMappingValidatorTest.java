package org.openmrs.module.metadatamapping;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class MetadataTermMappingValidatorTest {
    @Test
    public void validate_shouldRejectNonLoadedClass(){
        MetadataTermMapping mapping = new MetadataTermMapping();
        mapping.setMetadataClass("org.api.openmrs.Monster");

        Errors errors = new BindException(mapping, "mapping");
        new MetadataTermMappingValidator().validate(mapping, errors);
        assertTrue(errors.hasFieldErrors("metadataClass"));
    }

    @Test
    public void validate_shouldPassValidationWhenClassIsLoaded(){
        MetadataTermMapping mapping = new MetadataTermMapping();
        mapping.setMetadataClass("org.openmrs.Location");

        Errors errors = new BindException(mapping, "mapping");
        new MetadataTermMappingValidator().validate(mapping, errors);
        assertFalse(errors.hasFieldErrors("metadataClass"));
    }
}
