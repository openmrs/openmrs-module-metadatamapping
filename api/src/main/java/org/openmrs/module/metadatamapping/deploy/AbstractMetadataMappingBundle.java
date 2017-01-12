package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatamapping.MetadataSource;

import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataSource;
import static org.openmrs.module.metadatamapping.MetadataMappingConstructors.metadataTermMapping;

public abstract class AbstractMetadataMappingBundle extends AbstractMetadataBundle {
	
	protected void install(MetadataSourceDescriptor d) {
		install(metadataSource(d.name(), d.description(), d.uuid()));
	}
	
	protected void install(MetadataTermMappingDescriptor d) {
		install(metadataTermMapping(MetadataUtils.existing(MetadataSource.class, d.metadataSource().uuid()), d.code(), d
		        .metadataClass(), d.metadataUuid(), d.uuid()));
	}
	
}
