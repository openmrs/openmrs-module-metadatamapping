package org.openmrs.module.metadatamapping.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper for other modules to make migration from GP to mappings easier
 * @param <T> Class of mapped metadata object
 */
public abstract class GlobalPropertyToMappingConverter<T extends OpenmrsMetadata>{

    MetadataSource source;

    public GlobalPropertyToMappingConverter(MetadataSource source) {
        this.source = source;
    }

    abstract T getMetadataByUuid(String uuid);

    void saveIfMissing(String globalProperty){
        MetadataMappingService metadataMappingService = Context.getService(MetadataMappingService.class);
        MetadataTermMapping metadataTermMapping = metadataMappingService.getMetadataTermMapping(source, globalProperty);
        if(metadataTermMapping == null){
            T instance = null;

            String globalPropertyValue = Context.getAdministrationService().getGlobalProperty(globalProperty);
            if(StringUtils.isNotBlank(globalPropertyValue)){
                instance = getMetadataByUuid(globalPropertyValue);
            }

            if(instance == null){
                metadataTermMapping = new MetadataTermMapping(source, globalProperty, getMetadataClass());
            } else {
                metadataTermMapping = new MetadataTermMapping(source, globalProperty, instance);
            }
            metadataMappingService.saveMetadataTermMapping(metadataTermMapping);
        }
    }

    private String getMetadataClass(){
        Type superclass = getClass().getGenericSuperclass();
        String typeString = ((ParameterizedType)superclass).getActualTypeArguments()[0].toString();
        //cut off 'class ' prefix
        return typeString.substring("class ".length());
    }
}
