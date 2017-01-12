package org.openmrs.module.metadatamapping.deploy;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for Metadata Set Member
 */
@Handler(supports = { MetadataSetMember.class })
public class MetadataSetMemberDeployHandler extends AbstractObjectDeployHandler<MetadataSetMember> {
	
	@Autowired
	@Qualifier("metadatamapping.MetadataMappingService")
	private MetadataMappingService metadataMappingService;
	
	@Override
	public MetadataSetMember fetch(String s) {
		return metadataMappingService.getMetadataSetMemberByUuid(s);
	}
	
	@Override
	public MetadataSetMember save(MetadataSetMember metadataSetMember) {
		return metadataMappingService.saveMetadataSetMember(metadataSetMember);
	}
	
	@Override
	public void uninstall(MetadataSetMember metadataSetMember, String s) {
		metadataMappingService.retireMetadataSetMember(metadataSetMember, s);
	}
}
