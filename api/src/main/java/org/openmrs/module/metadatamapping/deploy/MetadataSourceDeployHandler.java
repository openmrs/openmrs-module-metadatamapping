package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for Metadata Source
 */
@Handler(supports = { MetadataSource.class })
public class MetadataSourceDeployHandler extends AbstractObjectDeployHandler<MetadataSource> {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService metadataMappingService;
	
	@Override
	public MetadataSource fetch(String s) {
		return metadataMappingService.getMetadataSourceByUuid(s);
	}
	
	@Override
	public MetadataSource save(MetadataSource metadataSource) {
		return metadataMappingService.saveMetadataSource(metadataSource);
	}
	
	@Override
	public void uninstall(MetadataSource metadataSource, String s) {
		metadataMappingService.retireMetadataSource(metadataSource, s);
	}
}
