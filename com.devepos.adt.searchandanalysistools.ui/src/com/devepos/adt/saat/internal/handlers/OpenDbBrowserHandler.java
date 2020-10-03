package com.devepos.adt.saat.internal.handlers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.WorkbenchPart;

import com.devepos.adt.base.ui.project.ProjectUtil;
import com.devepos.adt.base.ui.util.AdtUIUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;

/**
 * Handler which just opens the DB Browser without any parameter
 *
 * @author stockbal
 */
public class OpenDbBrowserHandler extends DbBrowserCommandHandler {

	private IProject currentProject;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// get the current selection
		final ISelection selection = window.getSelectionService().getSelection();

		// get the current selected ADT project
		this.currentProject = ProjectUtil.getCurrentAbapProject();
		if (this.currentProject == null) {
			// open project chooser dialog
			this.currentProject = AbapProjectSelectionDialog.open(null, this.currentProject);
			if (this.currentProject == null) {
				return null;
			}
		}

		if (!ProjectUtil.ensureLoggedOnToProject(this.currentProject).isOK()) {
			// without successful logon the feature availability cannot be tested
			return null;
		}

		if (!isFeatureAvailable(this.currentProject)) {
			showFeatureNotAvailableDialog(this.currentProject);
			return null;
		}

		final WorkbenchPart part = (WorkbenchPart) AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
			.openEditorAndStartTransaction(this.currentProject, "ZDBBR", true, //$NON-NLS-1$
				Stream.of(new String[][] { { "ADT", String.valueOf(true) } }) //$NON-NLS-1$
					.collect(Collectors.toMap(data -> data[0], data -> data[1])));

		AdtUIUtil.overrideSapGuiPartTitle(part, this.currentProject, Messages.DbBrowser_xtit, Messages.DbBrowser_xtit,
			SearchAndAnalysisPlugin.getDefault().getImage(IImages.DB_BROWSER_DATA_PREVIEW));
		return null;
	}

}
