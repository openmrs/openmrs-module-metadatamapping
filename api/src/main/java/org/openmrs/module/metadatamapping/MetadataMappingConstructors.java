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
	
	public static MetadataSet metadataSet(String uuid) {
		MetadataSet obj = new MetadataSet();
		obj.setUuid(uuid);
		return obj;
	}
	
	public static MetadataSetMember metadataSetMember(MetadataSet set, String metadataClass, String metadataUuid,
	        Double sortWeight, String uuid) {
		MetadataSetMember obj = new MetadataSetMember();
		obj.setMetadataSet(set);
		obj.setMetadataClass(metadataClass);
		obj.setMetadataUuid(metadataUuid);
		obj.setSortWeight(sortWeight);
		obj.setUuid(uuid);
		return obj;
	}
	
}
