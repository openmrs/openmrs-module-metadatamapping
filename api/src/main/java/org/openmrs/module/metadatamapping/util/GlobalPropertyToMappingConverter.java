package org.openmrs.module.metadatamapping.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper for other modules to make migration from GP to mappings easier
 *
 * @param <T> Class of mapped metadata object
 */
public abstract class GlobalPropertyToMappingConverter<T extends OpenmrsMetadata> {
	
	protected static final String MIGRATION_INFO_TMPL = "This global property had been migrated to metadata mapping in source '%s' with code '&s'";
	
	private MetadataSource source;
	
	public GlobalPropertyToMappingConverter(MetadataSource source) {
		this.source = source;
	}
	
	public abstract T getMetadataByUuid(String uuid);
	
	/**
	 * Creates missing mappings for metadata necessary for module functioning. MetadataTermMapping codes match global properties keys.
	 * Note that if mapping already has been created, and global property is edited, these changes will not be reflected in module metadata
	 * @should do nothing, if mapping is not missing
	 * @should create new mapping without mapped object, if mapping is missing and there is no global property or it has value not matching any OpenmrsMetadata uuid
	 * @should create new mapping with mapped object, if mapping is missing and there is global property with uuid matching existing OpenmrsMetadata
	 * @Should replace global property value with migration info if migrated
	 * @param globalPropertyKey - global property to convert to metadata mapping
	 */
	public void convert(String globalPropertyKey) {
		MetadataMappingService metadataMappingService = Context.getService(MetadataMappingService.class);
		MetadataTermMapping metadataTermMapping = metadataMappingService.getMetadataTermMapping(source, globalPropertyKey);
		if (metadataTermMapping == null) {
			T instance = null;
			
			GlobalProperty globalProperty = Context.getAdministrationService().getGlobalPropertyObject(globalPropertyKey);
			if (globalProperty != null && StringUtils.isNotBlank(globalProperty.getPropertyValue())) {
				instance = getMetadataByUuid(globalProperty.getPropertyValue());
				globalProperty.setPropertyValue(String.format(MIGRATION_INFO_TMPL, source.getName(), globalPropertyKey));
				Context.getAdministrationService().saveGlobalProperty(globalProperty);
			}
			
			if (instance == null) {
				metadataTermMapping = new MetadataTermMapping(source, globalPropertyKey, getMetadataClass());
			} else {
				metadataTermMapping = new MetadataTermMapping(source, globalPropertyKey, instance);
			}
			metadataMappingService.saveMetadataTermMapping(metadataTermMapping);
		}
	}
	
	private String getMetadataClass() {
		Type superclass = getClass().getGenericSuperclass();
		String typeString = ((ParameterizedType) superclass).getActualTypeArguments()[0].toString();
		//cut off 'class ' prefix
		return typeString.substring("class ".length());
	}
}
