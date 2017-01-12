package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = { MetadataSet.class })
public class MetadataSetDeployHandler extends AbstractObjectDeployHandler<MetadataSet> {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService metadataMappingService;
	
	@Override
	public MetadataSet fetch(String s) {
		return metadataMappingService.getMetadataSetByUuid(s);
	}
	
	@Override
	public MetadataSet save(MetadataSet metadataSet) {
		return metadataMappingService.saveMetadataSet(metadataSet);
	}
	
	@Override
	public void uninstall(MetadataSet metadataSet, String s) {
		metadataMappingService.retireMetadataSet(metadataSet, s);
	}
}
