package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.module.metadatadeploy.descriptor.Descriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.metadatamapping.MetadataSet;

public abstract class MetadataSetDescriptor extends MetadataDescriptor<MetadataSet> {
	
	@Override
	public String name() {
		return null;
	}
	
	@Override
	public String description() {
		return null;
	}
	
	/**
	 * @see Descriptor#getDescribedType()
	 */
	@Override
	public Class<MetadataSet> getDescribedType() {
		return MetadataSet.class;
	}
	
}
