package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.internal.search.ui.ObjectExplorerView;
import com.devepos.adt.saat.internal.ui.ViewPartLookup;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAdtObject;

public class ShowInObjectSearchHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IProject project = AdtUtil.getCurrentAbapProject();
		final IAdtObject adtObjectFromEditor = AdtUtil.getAdtObjectFromSelection(true);
		if (adtObjectFromEditor == null) {
			return null;
		}

		final ObjectExplorerView dbObjectSearchView = ViewPartLookup.getDbObjectSearchView();
		if (dbObjectSearchView != null) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(dbObjectSearchView);
			dbObjectSearchView.showExplorer();
			dbObjectSearchView.setProject(project);
			dbObjectSearchView.showObject(adtObjectFromEditor.getName(), adtObjectFromEditor.getObjectType());
		}
		return null;
	}

}
