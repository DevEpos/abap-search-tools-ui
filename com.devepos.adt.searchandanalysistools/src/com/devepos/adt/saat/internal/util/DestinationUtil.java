package com.devepos.adt.saat.internal.util;

/**
 * Util class for destinaion handling of ABAP projects
 *
 * @author stockbal
 */
public final class DestinationUtil {

	/**
	 * Retrieves the system id from the given destination id
	 *
	 * @param  destinationId a destination id to an ABAP project
	 * @return               the system id from the given destination id
	 */
	public static String getSystemId(final String destinationId) {
		if (destinationId == null) {
			return "";
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.hasProject()) {
			return destinationId;
		}
		return projectProvider.getDestinationData().getSystemConfiguration().getSystemId();
	}
}
