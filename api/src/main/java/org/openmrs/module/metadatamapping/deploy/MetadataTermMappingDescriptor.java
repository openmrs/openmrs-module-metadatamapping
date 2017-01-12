package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.metadatamapping.MetadataTermMapping;

public abstract class MetadataTermMappingDescriptor extends MetadataDescriptor<MetadataTermMapping> {
	
	public abstract MetadataSourceDescriptor metadataSource();
	
	public abstract String code();
	
	public abstract String metadataClass();
	
	public abstract String metadataUuid();
	
	@Override
	public String name() {
		return null;
	}
	
	@Override
	public String description() {
		return null;
	}
	
	@Override
	public Class<MetadataTermMapping> getDescribedType() {
		return MetadataTermMapping.class;
	}
}
