package com.devepos.adt.saat.internal;

/**
 * Provides information about a destination to an ABAP project
 *
 * @author stockbal
 */
public interface IDestinationProvider {
	/**
	 * Returns the destination to an ABAP project
	 *
	 * @return
	 */
	String getDestinationId();

	/**
	 * Sets a new destination Id
	 *
	 * @param destinationId
	 */
	void setDestinationId(String destinationId);

	/**
	 * Returns the system part from the destination
	 * 
	 * @return
	 */
	String getSystemId();
}
