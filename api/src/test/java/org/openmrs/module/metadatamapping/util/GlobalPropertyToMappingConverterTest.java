package org.openmrs.module.metadatamapping.util;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.metadatamapping.MetadataMappingMatchers.hasMappedClass;
import static org.openmrs.module.metadatamapping.MetadataMappingMatchers.hasMappedUuid;

public class GlobalPropertyToMappingConverterTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataMappingService metadataMappingService;
	
	private MetadataSource source;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("globalPropertyToMappingConverterDataset.xml");
		source = metadataMappingService.getMetadataSource(1);
	}
	
	@Test
	@Verifies(value = "do nothing, if mapping is not missing", method = "convert(String)")
	public void convert_shouldDoNothingIfMappingAlreadyExists() {
		//given
		//test dataset
		assertThat(metadataMappingService.getMetadataTermMapping(source, "emr.exitFromInpatientEncounterType"),
		    is(notNullValue()));
		
		//when
		getEncounterTypeConverter().convert("emr.exitFromInpatientEncounterType");
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source,
		    "emr.exitFromInpatientEncounterType");
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "create empty mapping if no global property", method = "convert(String)")
	public void convert_shouldCreateEmptyMappingIfThereIsNoGlobalProperty() {
		//given
		//test dataset
		assertThat(metadataMappingService.getMetadataTermMapping(source, "provider.exitTest"), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert("emr.exitTest");
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, "emr.exitTest");
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "create empty mapping if empty global property", method = "convert(String)")
	public void convert_shouldCreateEmptyMappingIfThereIsEmptyGlobalProperty() {
		//given
		//test dataset
		assertThat(metadataMappingService.getMetadataTermMapping(source, "provider.unknownProviderUuid"), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert("provider.unknownProviderUuid");
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source,
		    "provider.unknownProviderUuid");
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "map object from global property", method = "convert(String)")
	public void convert_shouldMapObjectFromGlobalProperty() {
		//given
		//test dataset
		assertThat(metadataMappingService.getMetadataTermMapping(source, "emr.checkInEncounterType"), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert("emr.checkInEncounterType");
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, "emr.checkInEncounterType");
		assertThat(termMapping, hasMappedUuid("55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b"));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	public GlobalPropertyToMappingConverter<EncounterType> getEncounterTypeConverter() {
		return new GlobalPropertyToMappingConverter<EncounterType>(
		                                                           source) {
			
			@Override
			public EncounterType getMetadataByUuid(String uuid) {
				return Context.getEncounterService().getEncounterTypeByUuid(uuid);
			}
		};
	}
}
