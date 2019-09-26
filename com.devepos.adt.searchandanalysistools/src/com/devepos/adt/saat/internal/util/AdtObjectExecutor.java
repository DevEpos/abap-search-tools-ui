package com.devepos.adt.saat.internal.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.AdtObjectExecutionUtilFactory;

/**
 * Utility for executing ADT objects via "Run as..."
 *
 * @author stockbal
 */
@SuppressWarnings("restriction")
public class AdtObjectExecutor {

	/**
	 * Executes the ADT object with the given name
	 *
	 * @param project    project where the object should be executed in
	 * @param objectName the name of the object
	 * @param objectUri  the URI string of the object
	 * @param objectType the type of the object
	 */
	public static void executeObject(final IProject project, final IAdtObjectReference objectRef) {
		AdtObjectExecutionUtilFactory.getInstance()
			.executeObject(project,
				AdtObjectReferenceFactory.createReference(null, objectRef.getName(), objectRef.getType(), objectRef.getUri()),
				null, new NullProgressMonitor());
	}

}
