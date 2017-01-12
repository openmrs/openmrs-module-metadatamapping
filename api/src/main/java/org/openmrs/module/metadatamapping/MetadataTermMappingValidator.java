package org.openmrs.module.metadatamapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = { MetadataTermMapping.class }, order = 50)
public class MetadataTermMappingValidator implements Validator {
	
	public static final String ERROR_INFO = "metadata class must be loaded class implementing OpenmrsMetadata";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public boolean supports(Class<?> aClass) {
		return MetadataTermMapping.class.isAssignableFrom(aClass);
	}
	
	@Override
	public void validate(Object o, Errors errors) {
		if (o == null || !(o instanceof MetadataTermMapping)) {
			throw new IllegalArgumentException("The parameter obj should not be null and must be of type"
			        + MetadataTermMapping.class);
		} else {
			MetadataTermMapping metadataTermMapping = (MetadataTermMapping) o;
			try {
				Class<?> aClass = getClass().getClassLoader().loadClass(metadataTermMapping.getMetadataClass());
				if (!OpenmrsMetadata.class.isAssignableFrom(aClass)) {
					errors.rejectValue("metadataClass", ERROR_INFO);
				}
			}
			catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
