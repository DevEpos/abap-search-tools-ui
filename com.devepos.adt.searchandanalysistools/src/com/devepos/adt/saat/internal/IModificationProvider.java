package com.devepos.adt.saat.internal;

/**
 * Provider for modifications
 *
 * @author stockbal
 */
public interface IModificationProvider {
	/**
	 * Adds the given modification listener to the object search favorites
	 *
	 * @param listener the listener to be added
	 */
	void addModificationListener(IModificationListener listener);

	/**
	 * Removes the given modification listener from the object search favorites
	 *
	 * @param listener the listener to be removed
	 */
	void removeModificationListener(IModificationListener listener);
}
