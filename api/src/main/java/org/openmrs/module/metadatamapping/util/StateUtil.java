package org.openmrs.module.metadatamapping.util;

/**
 * Utility methods for asserting object state
 */
public class StateUtil {
	
	/**
	 * Fails if the currentState is not null. 
	 * @param currentState currentState to check
	 * @param newState   
	 * @param failureMessage label for the currentState (for example, member name)
	 * @throws IllegalStateException when the condition fails   
	 */
	public static void mustNotChangeIfSet(Object currentState, Object newState, String failureMessage) {
		if (currentState != null && currentState != newState) {
			fail(failureMessage);
		}
	}
	
	private static void fail(String message) {
		throw new IllegalStateException(message);
	}
}
