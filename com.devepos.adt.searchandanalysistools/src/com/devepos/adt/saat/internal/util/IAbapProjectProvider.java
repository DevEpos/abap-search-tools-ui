package com.devepos.adt.saat.internal.util;

import org.eclipse.core.resources.IProject;

import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;

/**
 * Provides information about current ABAP project
 *
 * @author stockbal
 */
public interface IAbapProjectProvider {

	/**
	 * Opens the given object reference in the ABAP project
	 *
	 * @param objectReference
	 */
	void openObjectReference(final IAdtObjectReference objectReference);

	/**
	 * Returns <code>true</code> if the provider references a valid ABAP project
	 *
	 * @return <code>true</code> if a project reference exists
	 */
	boolean hasProject();

	/**
	 * Retrieves the original Project reference
	 *
	 * @return
	 */
	IProject getProject();

	/**
	 * Sets the project in the project provider
	 *
	 * @param project the project to be used
	 */
	void setProject(IProject project);

	/**
	 * Retrieves the name of the project
	 *
	 * @return
	 */
	String getProjectName();

	/**
	 * Retrieves the destination id of the project
	 *
	 * @return
	 */
	String getDestinationId();

	/**
	 * Returns the destination data of the ABAP Project
	 * 
	 * @return the destination data of the ABAP Project
	 */
	IDestinationData getDestinationData();

	/**
	 * Ensures that the current user is logged on to the project
	 *
	 * @return <code>true</code> if user is logged on to the project
	 */
	boolean ensureLoggedOn();

	/**
	 * Retrieve instance of the current ABAP Project
	 *
	 * @return
	 */
	IAbapProject getAbapProject();

	/**
	 * Creates a stateless system session with current project in the provider
	 *
	 * @return
	 */
	ISystemSession createStatelessSession();

	/**
	 * Opens the given object reference in a SAP GUI editor
	 *
	 * @param objectReference the ADT object reference to be opened
	 */
	void openObjectReferenceInSapGui(IAdtObjectReference objectReference);

	/**
	 * Creates a real copy of this project provider
	 *
	 * @return a copy of this project provider
	 */
	IAbapProjectProvider copy();
}
