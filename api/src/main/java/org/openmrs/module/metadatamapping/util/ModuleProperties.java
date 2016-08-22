package org.openmrs.module.metadatamapping.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.VisitService;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Helper class that lets modules centralize their configuration details. See EmrProperties for an example.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class ModuleProperties {
	
	private static final Log log = LogFactory.getLog(ModuleProperties.class);
	
	/**
	 * services
	 */
	@Autowired
	protected MetadataMappingService metadataMappingService;
	
	@Autowired
	@Qualifier("conceptService")
	protected ConceptService conceptService;
	
	@Autowired
	@Qualifier("encounterService")
	protected EncounterService encounterService;
	
	@Autowired
	@Qualifier("visitService")
	protected VisitService visitService;
	
	@Autowired
	@Qualifier("orderService")
	protected OrderService orderService;
	
	@Autowired
	@Qualifier("adminService")
	protected AdministrationService administrationService;
	
	@Autowired
	@Qualifier("locationService")
	protected LocationService locationService;
	
	@Autowired
	@Qualifier("userService")
	protected UserService userService;
	
	@Autowired
	@Qualifier("patientService")
	protected PatientService patientService;
	
	@Autowired
	@Qualifier("personService")
	protected PersonService personService;
	
	@Autowired
	@Qualifier("providerService")
	protected ProviderService providerService;
	
	@Autowired
	@Qualifier("formService")
	protected FormService formService;
	
	/**
	 * setters for easy testing
	 */
	public void setMetadataMappingService(MetadataMappingService metadataMappingService) {
		this.metadataMappingService = metadataMappingService;
	}
	
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	public void setAdministrationService(AdministrationService administrationService) {
		this.administrationService = administrationService;
	}
	
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}
	
	/**
	 * Module should use single metadata source for all its mappings,
	 * source name returned by this method will be used to lookup metadata
	 */
	public abstract String getMetadataSourceName();
	
	protected ConceptSource getConceptSourceByCode(String mappingCode) {
		ConceptSource conceptSource = getEmrApiMetadataByCode(ConceptSource.class, mappingCode);
		if (conceptSource == null) {
			throw new IllegalStateException("Configuration required: " + mappingCode);
		}
		return conceptSource;
	}
	
	protected List<PatientIdentifierType> getPatientIdentifierTypesByCode(String code) {
		MetadataSet metadataSet = getEmrApiMetadataByCode(MetadataSet.class, code);
		return metadataMappingService.getMetadataSetItems(PatientIdentifierType.class, metadataSet);
	}
	
	protected String getEmrApiMetadataUuidByCode(String mappingCode) {
		return getEmrApiMetadataUuidByCode(mappingCode, true);
	}
	
	protected String getEmrApiMetadataUuidByCode(String mappingCode, boolean required) {
		MetadataTermMapping mapping = metadataMappingService.getMetadataTermMapping(getEmrApiMetadataSource(), mappingCode);
		if (mapping != null && mapping.getMetadataUuid() != null) {
			return mapping.getMetadataUuid();
		} else if (required) {
			throw new IllegalStateException("Configuration required: " + mappingCode);
		} else {
			return null;
		}
	}
	
	protected MetadataSource getEmrApiMetadataSource() {
		return metadataMappingService.getMetadataSourceByName(getMetadataSourceName());
	}
	
	protected <T extends OpenmrsMetadata> T getEmrApiMetadataByCode(Class<T> type, String code, boolean required) {
		T metadataItem = metadataMappingService.getMetadataItem(type, getMetadataSourceName(), code);
		if (required && metadataItem == null) {
			throw new IllegalStateException("Configuration required: " + code);
		} else {
			return metadataItem;
		}
	}
	
	protected <T extends OpenmrsMetadata> T getEmrApiMetadataByCode(Class<T> type, String code) {
		return getEmrApiMetadataByCode(type, code, true);
	}
	
	protected Concept getSingleConceptByMapping(ConceptSource conceptSource, String code) {
		List<Concept> candidates = conceptService.getConceptsByMapping(code, conceptSource.getName(), false);
		if (candidates.size() == 0) {
			throw new IllegalStateException("Configuration required: can't find a concept by mapping "
			        + conceptSource.getName() + ":" + code);
		} else if (candidates.size() == 1) {
			return candidates.get(0);
		} else {
			throw new IllegalStateException("Configuration required: found more than one concept mapped as "
			        + conceptSource.getName() + ":" + code);
		}
	}
	
	protected Integer getIntegerByGlobalProperty(String globalPropertyName) {
		String globalProperty = getGlobalProperty(globalPropertyName, true);
		try {
			return Integer.valueOf(globalProperty);
		}
		catch (Exception e) {
			throw new IllegalStateException("Global property " + globalPropertyName + " value of " + globalProperty
			        + " is not parsable as an Integer");
		}
	}
	
	protected String getGlobalProperty(String globalPropertyName, boolean required) {
		String globalProperty = administrationService.getGlobalProperty(globalPropertyName);
		if (required && StringUtils.isEmpty(globalProperty)) {
			throw new IllegalStateException("Configuration required: " + globalPropertyName);
		}
		return globalProperty;
	}
	
	protected Collection<Concept> getConceptsByGlobalProperty(String gpName) {
		String gpValue = getGlobalProperty(gpName, false);
		
		if (!org.springframework.util.StringUtils.hasText(gpValue)) {
			return Collections.emptyList();
		}
		
		List<Concept> result = new ArrayList<Concept>();
		
		String[] concepts = gpValue.split("\\,");
		for (String concept : concepts) {
			Concept foundConcept = conceptService.getConceptByUuid(concept);
			if (foundConcept == null) {
				String[] mapping = concept.split("\\:");
				if (mapping.length == 2) {
					foundConcept = conceptService.getConceptByMapping(mapping[0], mapping[1]);
				}
			}
			
			if (foundConcept != null) {
				result.add(foundConcept);
			} else {
				throw new IllegalStateException("Invalid configuration: concept '" + concept + "' defined in " + gpName
				        + " does not exist");
			}
		}
		
		return result;
	}
}
