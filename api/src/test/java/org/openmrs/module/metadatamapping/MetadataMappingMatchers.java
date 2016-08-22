package org.openmrs.module.metadatamapping;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

public class MetadataMappingMatchers {
	
	public static Matcher<MetadataTermMapping> hasMappedUuid(String uuid) {
		return new FeatureMatcher<MetadataTermMapping, String>(
		                                                       equalTo(uuid), "metadata uuid", "metadata uuid") {
			
			@Override
			protected String featureValueOf(MetadataTermMapping actual) {
				return actual.getMetadataUuid();
			}
		};
	}
	
	public static Matcher<MetadataTermMapping> hasMappedClass(String className) {
		return new FeatureMatcher<MetadataTermMapping, String>(
		                                                       equalTo(className), "metadataClass", "metadataClass") {
			
			@Override
			protected String featureValueOf(MetadataTermMapping actual) {
				return actual.getMetadataClass();
			}
		};
	}
}
