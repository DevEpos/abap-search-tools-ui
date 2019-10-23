package com.devepos.adt.saat.internal.handlers;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.ris.search.ui.AdtRepositorySearchServiceUIFactory;
import com.sap.adt.ris.search.ui.IAdtRepositorySearchServiceUIParameters;
import com.sap.adt.ris.search.ui.IAdtRepositorySearchServiceUIResult;
import com.sap.adt.ris.search.ui.usagereferences.AdtRisUsageReferencesSearchQuery;
import com.sap.adt.ris.search.ui.usagereferences.AdtRisUsageReferencesSearchQueryParameters;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Command handler for executing a Where Used Query for an ABAP Development
 * Object
 *
 * @author stockbal
 */
public class RunWhereUsedQueryHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// open search dialog to choose ABAP Development object
		final IAdtRepositorySearchServiceUIParameters parameters = AdtRepositorySearchServiceUIFactory
			.createAdtRepositorySearchServiceUIParameters();
		parameters.setTitle(Messages.RunWhereUsedQueryHandler_openObjectDialog_xtit);
		final IAdtRepositorySearchServiceUIResult result = AdtRepositorySearchServiceUIFactory
			.createAdtRepositorySearchServiceUI()
			.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), parameters);

		if (result == null) {
			return null;
		}

		final IAdtObjectReference selectedAdtObjRef = result.getFirstSelectedObjectReference();
		final IProject project = result.getSelectedProject();

		final AdtRisUsageReferencesSearchQueryParameters usageSearchParameters = new AdtRisUsageReferencesSearchQueryParameters(
			project, URI.create(selectedAdtObjRef.getUri()));
		final AdtRisUsageReferencesSearchQuery searchQuery = new AdtRisUsageReferencesSearchQuery(usageSearchParameters);
		NewSearchUI.runQueryInBackground(searchQuery);

		/*
		 * If there is no active page in the workbench window the search view will not
		 * be brought to the front so it has to be done manually
		 */
		final IWorkbenchPage activeSearchPage = SearchPlugin.getActivePage();
		final IWorkbenchPart activeSearchView = activeSearchPage.getActivePart();
		if (activeSearchPage != null && activeSearchView != null && activeSearchPage.isPartVisible(activeSearchView)) {
			activeSearchPage.bringToTop(activeSearchView);
		}
		return null;
	}

}
