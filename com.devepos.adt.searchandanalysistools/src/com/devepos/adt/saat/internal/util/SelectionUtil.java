package com.devepos.adt.saat.internal.util;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Utility for reading selections from the eclipse workbench window
 *
 * @author stockbal
 */
public class SelectionUtil {

	/**
	 * Retrieves the selection from the current workbench.<br>
	 * The prio levels for getting the selection is the following
	 * <ol>
	 * <li>Active window</li>
	 * <li>Active Page</li>
	 * <li>Active Part</li>
	 * </ol>
	 *
	 * @return the found {@link ISelection}
	 */
	public static final ISelection getSelection() {
		final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWindow == null) {
			return null;
		}
		ISelection selection = getSelectionFromActiveWindow(activeWindow);
		if (selection != null) {
			return selection;
		}
		final IWorkbenchPage activePage = activeWindow.getActivePage();
		if (activePage == null) {
			return null;
		}
		selection = getSelectionFromActivePage(activePage);
		if (selection != null) {
			return selection;
		}
		final IWorkbenchPart activePart = activePage.getActivePart();
		if (activePart == null) {
			return null;
		}
		selection = getSelectionFromActivePart(activePart);
		if (selection != null) {
			return selection;
		}

		return null;
	}

	private static final ISelection getSelectionFromActiveWindow(final IWorkbenchWindow window) {
		return window.getSelectionService().getSelection();
	}

	private static final ISelection getSelectionFromActivePage(final IWorkbenchPage page) {
		return page.getSelection();

	}

	private static final ISelection getSelectionFromActivePart(final IWorkbenchPart part) {
		final IWorkbenchSite site = part.getSite();
		return site != null ? getSelectionFromActivePartSite(site) : null;
	}

	private static final ISelection getSelectionFromActivePartSite(final IWorkbenchSite site) {
		final ISelectionProvider selectionProvider = site.getSelectionProvider();
		if (selectionProvider == null) {
			return null;
		}
		return selectionProvider.getSelection();

	}
}
