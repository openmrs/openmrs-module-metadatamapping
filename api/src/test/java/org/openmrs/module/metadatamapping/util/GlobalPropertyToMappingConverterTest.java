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
		String globalPropertyKey = "emr.exitFromInpatientEncounterType";
		assertThat(metadataMappingService.getMetadataTermMapping(source, globalPropertyKey), is(notNullValue()));
		
		//when
		getEncounterTypeConverter().convert(globalPropertyKey);
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, globalPropertyKey);
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "create empty mapping if no global property", method = "convert(String)")
	public void convert_shouldCreateEmptyMappingIfThereIsNoGlobalProperty() {
		//given
		//test dataset
		String globalPropertyKey = "emr.exitTest";
		assertThat(metadataMappingService.getMetadataTermMapping(source, globalPropertyKey), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert(globalPropertyKey);
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, globalPropertyKey);
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "create empty mapping if empty global property", method = "convert(String)")
	public void convert_shouldCreateEmptyMappingIfThereIsEmptyGlobalProperty() {
		//given
		//test dataset
		String globalPropertyKey = "provider.unknownProviderUuid";
		assertThat(metadataMappingService.getMetadataTermMapping(source, globalPropertyKey), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert(globalPropertyKey);
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, globalPropertyKey);
		assertThat(termMapping, hasMappedUuid(null));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "map object from global property", method = "convert(String)")
	public void convert_shouldMapObjectFromGlobalProperty() {
		//given
		//test dataset
		String globalPropertyKey = "emr.checkInEncounterType";
		assertThat(metadataMappingService.getMetadataTermMapping(source, globalPropertyKey), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert(globalPropertyKey);
		
		//then
		MetadataTermMapping termMapping = metadataMappingService.getMetadataTermMapping(source, globalPropertyKey);
		assertThat(termMapping, hasMappedUuid("55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b"));
		assertThat(termMapping, hasMappedClass("org.openmrs.EncounterType"));
	}
	
	@Test
	@Verifies(value = "replace global property value with migration info if migrated", method = "convert(String)")
	public void convert_shouldReplaceGlobalPropertyValueWithMigrationInfo() {
		//given
		//test dataset
		String globalPropertyKey = "emr.checkInEncounterType";
		assertThat(metadataMappingService.getMetadataTermMapping(source, globalPropertyKey), is(nullValue()));
		
		//when
		getEncounterTypeConverter().convert(globalPropertyKey);
		
		//then
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyKey);
		assertThat(globalProperty, is(String.format(GlobalPropertyToMappingConverter.MIGRATION_INFO_TMPL, source.getName(),
		    globalPropertyKey)));
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
