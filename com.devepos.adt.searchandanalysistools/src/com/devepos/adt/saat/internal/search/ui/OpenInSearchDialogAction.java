package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

public class OpenInSearchDialogAction extends Action {
	private final ObjectSearchResultPage page;

	public OpenInSearchDialogAction(final ObjectSearchResultPage searchPage) {
		super(Messages.ObjectSearchResultPage_OpenInSearchDialog_xtol, Action.AS_PUSH_BUTTON);
		this.page = searchPage;
		setToolTipText(Messages.ObjectSearchResultPage_OpenInSearchDialog_xtol);
		setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.SEARCH));
		setChecked(true);
	}

	@Override
	public void run() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final SearchDialog dialog = new SearchDialog(window, this.page.getID());
		dialog.setBlockOnOpen(false);
		dialog.open();
		if (dialog.getSelectedPage() instanceof ObjectSearchPage) {
			final ObjectSearchPage searchDialog = (ObjectSearchPage) dialog.getSelectedPage();
			searchDialog.setInputFromSearchRequest(this.page.getSearchRequest());
		}
		dialog.setBlockOnOpen(true);
	}
}
