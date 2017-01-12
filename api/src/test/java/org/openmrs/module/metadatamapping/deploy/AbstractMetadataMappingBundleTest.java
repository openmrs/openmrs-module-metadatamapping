package org.openmrs.module.metadatamapping.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class AbstractMetadataMappingBundleTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private TestMetadataMappingBundle bundle;
	
	@Test
	public void shouldInstallMetadataSourceAndMapping() throws Exception {
		
		bundle.install();
		
		MetadataSource createdSource = MetadataUtils.possible(MetadataSource.class, "be1a1868-8d00-4e59-a602-e02cbf4a3772");
		Assert.assertThat(createdSource, notNullValue());
		Assert.assertThat(createdSource.getName(), is("some-name"));
		Assert.assertThat(createdSource.getDescription(), is("some-description"));
		
		MetadataTermMapping createdMapping = MetadataUtils.possible(MetadataTermMapping.class,
		    "ffdb71ff-84ff-410d-9c56-45f794e5ce87");
		Assert.assertThat(createdMapping, notNullValue());
		Assert.assertThat(createdMapping.getCode(), is("some-code"));
		Assert.assertThat(createdMapping.getMetadataClass(), is("Location.class"));
		Assert.assertThat(createdMapping.getMetadataUuid(), is("some-uuid"));
	}
	
	@Component
	public static class TestMetadataMappingBundle extends AbstractMetadataMappingBundle {
		
		private static MetadataSourceDescriptor source = new MetadataSourceDescriptor() {
			
			public String name() {
				return "some-name";
			}
			
			public String description() {
				return "some-description";
			}
			
			public String uuid() {
				return "be1a1868-8d00-4e59-a602-e02cbf4a3772";
			}
		};
		
		private static MetadataTermMappingDescriptor term = new MetadataTermMappingDescriptor() {
			
			public MetadataSourceDescriptor metadataSource() {
				return source;
			}
			
			public String code() {
				return "some-code ";
			}
			
			public String metadataClass() {
				return "Location.class";
			}
			
			public String metadataUuid() {
				return "some-uuid";
			}
			
			public String uuid() {
				return "ffdb71ff-84ff-410d-9c56-45f794e5ce87";
			}
		};
		
		@Override
		public void install() throws Exception {
			install(source);
			install(term);
		}
	}
	
}
