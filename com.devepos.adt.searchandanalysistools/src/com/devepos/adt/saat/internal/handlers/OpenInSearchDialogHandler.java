package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devepos.adt.saat.internal.search.ui.ObjectSearchPage;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchResultPage;

public class OpenInSearchDialogHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final SearchView activeSearchView = getSearchView(event);
		if (activeSearchView == null) {
			return null;
		}
		final ISearchResultPage resultPage = activeSearchView.getActivePage();
		if (resultPage != null && resultPage instanceof ObjectSearchResultPage) {
			final ObjectSearchResultPage objectSearchResultPage = (ObjectSearchResultPage) resultPage;
			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			final SearchDialog dialog = new SearchDialog(window, objectSearchResultPage.getSearchDialogId());
			dialog.setBlockOnOpen(false);
			dialog.open();
			if (dialog.getSelectedPage() instanceof ObjectSearchPage) {
				final ObjectSearchPage searchDialog = (ObjectSearchPage) dialog.getSelectedPage();
				searchDialog.setInputFromSearchQuery(objectSearchResultPage.getSearchQuery());
			}
			dialog.setBlockOnOpen(true);
		}
		return null;
	}

	protected SearchView getSearchView(final ExecutionEvent event) {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof SearchView) {
			return (SearchView) activePart;
		}
		return null;
	}

}
