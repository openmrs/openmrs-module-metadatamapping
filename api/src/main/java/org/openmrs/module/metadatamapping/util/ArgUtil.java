package org.openmrs.module.metadatamapping.util;

/**
 * Utility methods for asserting method arguments
 */
public class ArgUtil {
	
	/**
	 * Fails if the parameter is null. 
	 * @param parameter parameter to check
	 * @param label label of the parameter (usually parameter name)
	 */
	public static void notNull(Object parameter, String label) {
		if (parameter == null) {
			fail(label + " is null");
		}
	}
	
	private static void fail(String message) {
		throw new IllegalArgumentException(message);
	}
}
