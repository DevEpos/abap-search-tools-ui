package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devepos.adt.saat.internal.search.ui.ObjectSearchResultPage;

/**
 * Handler for opening the ABAP Object Search + in the eclipse search dialog
 *
 * @author stockbal
 */
public class OpenObjectSearchDialogHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow activeWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		final SearchDialog dialog = new SearchDialog(activeWindow, ObjectSearchResultPage.DIALOG_ID);
		dialog.setBlockOnOpen(false);
		dialog.open();
		dialog.setBlockOnOpen(true);
		return null;
	}

}
