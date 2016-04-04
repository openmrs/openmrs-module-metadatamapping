package org.openmrs.module.metadatamapping.web.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE)
public class MetadataMappingRestController extends MainResourceController {
	
	public static final String METADATA_MAPPING_REST_NAMESPACE = "/metadatamapping";
	
	@Override
	public String getNamespace() {
		return RestConstants.VERSION_1 + METADATA_MAPPING_REST_NAMESPACE;
	}
	
}
