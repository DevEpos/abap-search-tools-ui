package com.devepos.adt.saat.internal.util;

import java.net.URI;

import org.eclipse.core.resources.IProject;

import com.devepos.adt.saat.IDestinationProvider;
import com.sap.adt.project.IProjectProvider;
import com.sap.adt.tools.core.AdtObjectReference;
import com.sap.adt.tools.core.IAdtObjectReference;

/**
 * Adapter Factory which is necessary to get the correct types for executing a
 * Where-Used list
 *
 * @author stockbal
 */
public class AdtWhereUsedAdapterFactory {

	/**
	 * Adapts an EMF ADT Object Reference to the ADT Object reference
	 *
	 * @param  adtObjectRef EMF ADT Object Reference
	 * @return
	 */
	public static IAdtObjectReference adaptToNonEmfAdtObjectRef(
		final com.sap.adt.tools.core.model.adtcore.IAdtObjectReference adtObjectRef) {
		if (adtObjectRef == null) {
			return null;
		}
		final String uriString = adtObjectRef.getUri();
		final URI refUri = uriString != null ? URI.create(uriString) : null;
		return new AdtObjectReference(refUri, adtObjectRef.getName(), adtObjectRef.getType(), null, adtObjectRef.getPackageName(),
			adtObjectRef.getDescription());
	}

	/**
	 * Adapts destination provider to project provider
	 *
	 * @param  adtObjectRef EMF ADT Object Reference
	 * @return              the adapted project provider
	 */
	public static IProjectProvider adaptToProjectProvider(
		final com.sap.adt.tools.core.model.adtcore.IAdtObjectReference adtObjectRef) {
		if (adtObjectRef == null || !(adtObjectRef instanceof IDestinationProvider)) {
			return null;
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
			.getProviderForDestination(((IDestinationProvider) adtObjectRef).getDestinationId());
		if (projectProvider != null && projectProvider.hasProject()) {
			return new ProjectProvider(projectProvider.getProject());
		}
		return null;
	}

	/*
	 * Simple implementation of a project provider
	 */
	private static final class ProjectProvider implements IProjectProvider {

		private final IProject project;

		public ProjectProvider(final IProject project) {
			this.project = project;
		}

		@Override
		public IProject getProject() {
			return this.project;
		}

	}
}
