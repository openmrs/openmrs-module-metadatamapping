package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for Metadata Term Mapping
 */
@Handler(supports = { MetadataTermMapping.class })
public class MetadataTermMappingDeployHandler extends AbstractObjectDeployHandler<MetadataTermMapping> {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService metadataMappingService;
	
	@Override
	public MetadataTermMapping fetch(String s) {
		return metadataMappingService.getMetadataTermMappingByUuid(s);
	}
	
	@Override
	public MetadataTermMapping save(MetadataTermMapping metadataTermMapping) {
		return metadataMappingService.saveMetadataTermMapping(metadataTermMapping);
	}
	
	@Override
	public void uninstall(MetadataTermMapping metadataTermMapping, String s) {
		metadataMappingService.retireMetadataTermMapping(metadataTermMapping, s);
	}
}
