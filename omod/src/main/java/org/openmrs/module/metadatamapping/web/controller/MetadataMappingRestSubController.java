package org.openmrs.module.metadatamapping.web.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + METADATA_MAPPING_REST_NAMESPACE)
public class MetadataMappingRestSubController extends MainSubResourceController {
	
	@Override
	public String getNamespace() {
		return RestConstants.VERSION_1 + METADATA_MAPPING_REST_NAMESPACE;
	}
	
}
