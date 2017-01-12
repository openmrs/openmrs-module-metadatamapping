package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.metadatamapping.MetadataSetMember;

public abstract class MetadataSetMemberDescriptor extends MetadataDescriptor<MetadataSetMember> {
	
	public abstract MetadataSetDescriptor metadataSet();
	
	public abstract String metadataClass();
	
	public abstract String metadataUuid();
	
	public abstract Double sortWeight();
	
	@Override
	public String name() {
		return null;
	}
	
	@Override
	public String description() {
		return null;
	}
	
	@Override
	public Class<MetadataSetMember> getDescribedType() {
		return MetadataSetMember.class;
	}
	
}
