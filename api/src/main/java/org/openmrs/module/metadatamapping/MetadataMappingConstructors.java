package org.openmrs.module.metadatamapping;

public class MetadataMappingConstructors {
	
	/**
	 * Constructs a Metadata Source
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static MetadataSource metadataSource(String name, String description, String uuid) {
		MetadataSource obj = new MetadataSource();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}
	
	public static MetadataTermMapping metadataTermMapping(MetadataSource metadataSource, String code, String metadataClass,
	        String metadataUuid, String uuid) {
		MetadataTermMapping obj = new MetadataTermMapping();
		obj.setMetadataSource(metadataSource);
		obj.setCode(code);
		obj.setMetadataClass(metadataClass);
		obj.setMetadataUuid(metadataUuid);
		obj.setUuid(uuid);
		return obj;
	}

}

