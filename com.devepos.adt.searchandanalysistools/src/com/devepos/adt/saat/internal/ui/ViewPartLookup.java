package com.devepos.adt.saat.internal.ui;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.search.ui.ObjectExplorerView;
import com.devepos.adt.saat.internal.util.Logging;

/**
 * Looks up View parts
 *
 * @author stockbal
 */
public class ViewPartLookup {
	/**
	 * Retrieves the database object search view from the workspace
	 *
	 * @return
	 */
	public static ObjectExplorerView getDbObjectSearchView() {
		return getDbObjectSearchView(true);
	}

	/**
	 * Retrieves the database object search view from the workspace
	 *
	 * @param  createIfNotOpen if <code>true</code> the View is created if it is not
	 *                         yet open in the active workbench window
	 * @return
	 */
	public static ObjectExplorerView getDbObjectSearchView(final boolean createIfNotOpen) {
		ObjectExplorerView searchView = null;

		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return null;
		}

		try {
			IViewPart view = null;
			if (createIfNotOpen) {
				view = page.showView(ObjectExplorerView.VIEW_ID);
			} else {
				final IViewReference viewRef = page.findViewReference(ObjectExplorerView.VIEW_ID);
				if (viewRef != null) {
					view = viewRef.getView(false);
				}
			}
			if (view != null && view instanceof ObjectExplorerView) {
				searchView = (ObjectExplorerView) view;
			}
		} catch (final PartInitException e) {
			Logging.getLogger(ViewPartLookup.class).error(e);
		}
		return searchView;
	}

	/**
	 * Retrieves the CDS Analysis view from the workspace
	 *
	 * @return
	 */
	public static CdsAnalysis getCdsAnalysisView() {
		CdsAnalysis view = null;
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			final IViewPart viewPart = page.showView(CdsAnalysis.VIEW_ID);
			if (viewPart instanceof CdsAnalysis) {
				view = (CdsAnalysis) viewPart;
			}
		} catch (final PartInitException e) {
			Logging.getLogger(ViewPartLookup.class).error(e);
		}
		return view;
	}
}
