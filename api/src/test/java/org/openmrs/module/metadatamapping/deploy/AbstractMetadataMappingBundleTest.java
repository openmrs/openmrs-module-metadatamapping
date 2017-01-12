package org.openmrs.module.metadatamapping.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
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
		
		MetadataTermMapping createdMapping1 = MetadataUtils.possible(MetadataTermMapping.class,
		    "ffdb71ff-84ff-410d-9c56-45f794e5ce87");
		Assert.assertThat(createdMapping1, notNullValue());
		Assert.assertThat(createdMapping1.getCode(), is("some-code"));
		Assert.assertThat(createdMapping1.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(createdMapping1.getMetadataUuid(), is("some-location-uuid"));
		
		MetadataSet createdSet = MetadataUtils.possible(MetadataSet.class, "1aa2a231-9498-414b-bd56-c357785889e1");
		Assert.assertThat(createdSet, notNullValue());
		
		MetadataSetMember createdMember1 = MetadataUtils.possible(MetadataSetMember.class,
		    "04f92bae-662c-40a6-9093-9b9e433d2673");
		Assert.assertThat(createdMember1, notNullValue());
		Assert.assertThat(createdMember1.getMetadataSet().getUuid(), is("1aa2a231-9498-414b-bd56-c357785889e1"));
		Assert.assertThat(createdMember1.getMetadataClass(), is(Location.class.getName()));
		Assert.assertThat(createdMember1.getMetadataUuid(), is("another-location-uuid"));
		Assert.assertThat(createdMember1.getSortWeight(), is(2.0));
		
		MetadataSetMember createdMember2 = MetadataUtils.possible(MetadataSetMember.class,
		    "1914a562-26a8-4b3e-95de-fdf174a392f7");
		Assert.assertThat(createdMember2, notNullValue());
		Assert.assertThat(createdMember2.getMetadataSet().getUuid(), is("1aa2a231-9498-414b-bd56-c357785889e1"));
		Assert.assertThat(createdMember2.getMetadataClass(), is(PatientIdentifierType.class.getName()));
		Assert.assertThat(createdMember2.getMetadataUuid(), is("identifier-type-uuid"));
		Assert.assertThat(createdMember2.getSortWeight(), is(3.0));
		
		MetadataTermMapping createdMapping2 = MetadataUtils.possible(MetadataTermMapping.class,
		    "96a11abc-2b90-4dc9-b201-bab5b3259e07");
		Assert.assertThat(createdMapping2, notNullValue());
		Assert.assertThat(createdMapping2.getCode(), is("another-code"));
		Assert.assertThat(createdMapping2.getMetadataClass(), is(MetadataSet.class.getName()));
		Assert.assertThat(createdMapping2.getMetadataUuid(), is(createdSet.getUuid()));
		
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
		
		private static MetadataTermMappingDescriptor term1 = new MetadataTermMappingDescriptor() {
			
			public MetadataSourceDescriptor metadataSource() {
				return source;
			}
			
			public String code() {
				return "some-code ";
			}
			
			public String metadataClass() {
				return Location.class.getName();
			}
			
			public String metadataUuid() {
				return "some-location-uuid";
			}
			
			public String uuid() {
				return "ffdb71ff-84ff-410d-9c56-45f794e5ce87";
			}
		};
		
		private static MetadataSetDescriptor set = new MetadataSetDescriptor() {
			
			public String uuid() {
				return "1aa2a231-9498-414b-bd56-c357785889e1";
			}
		};
		
		private static MetadataSetMemberDescriptor member1 = new MetadataSetMemberDescriptor() {
			
			public MetadataSetDescriptor metadataSet() {
				return set;
			}
			
			public String metadataClass() {
				return Location.class.getName();
			}
			
			public String metadataUuid() {
				return "another-location-uuid";
			}
			
			public Double sortWeight() {
				return 2.0;
			}
			
			public String uuid() {
				return "04f92bae-662c-40a6-9093-9b9e433d2673";
			}
		};
		
		private static MetadataSetMemberDescriptor member2 = new MetadataSetMemberDescriptor() {
			
			public MetadataSetDescriptor metadataSet() {
				return set;
			}
			
			public String metadataClass() {
				return PatientIdentifierType.class.getName();
			}
			
			public String metadataUuid() {
				return "identifier-type-uuid";
			}
			
			public Double sortWeight() {
				return 3.0;
			}
			
			public String uuid() {
				return "1914a562-26a8-4b3e-95de-fdf174a392f7";
			}
		};
		
		private static MetadataTermMappingDescriptor term2 = new MetadataTermMappingDescriptor() {
			
			public MetadataSourceDescriptor metadataSource() {
				return source;
			}
			
			public String code() {
				return "another-code ";
			}
			
			public String metadataClass() {
				return MetadataSet.class.getName();
			}
			
			public String metadataUuid() {
				return set.uuid();
			}
			
			public String uuid() {
				return "96a11abc-2b90-4dc9-b201-bab5b3259e07";
			}
		};
		
		@Override
		public void install() throws Exception {
			install(source);
			install(term1);
			install(set);
			install(member1);
			install(member2);
			install(term2);
		}
	}
	
}
