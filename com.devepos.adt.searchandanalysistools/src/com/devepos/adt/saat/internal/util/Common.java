package com.devepos.adt.saat.internal.util;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Helper class for common stuff
 * 
 * @author stockbal
 *
 */
public class Common {

	/**
	 * Retrieves the active Editor Input or <code>null</code>
	 * 
	 * @return
	 */
	public static IEditorInput getActiveEditorInput() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		
		IEditorPart activePart = activePage.getActiveEditor();
		if (activePart == null) {
			return null;
		}

		return activePart.getEditorInput();
	}
}
