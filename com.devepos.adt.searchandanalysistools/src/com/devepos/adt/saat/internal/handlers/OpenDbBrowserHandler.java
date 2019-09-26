package com.devepos.adt.saat.internal.handlers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.WorkbenchPart;

import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;

/**
 * Handler which just opens the DB Browser without any parameter
 *
 * @author stockbal
 */
public class OpenDbBrowserHandler extends AbstractHandler {

	private IProject currentProject;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// get the current selection
		final ISelection selection = window.getSelectionService().getSelection();

		// get the current selected ADT project
		this.currentProject = ProjectUtil.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);

		final WorkbenchPart part = (WorkbenchPart) AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
			.openEditorAndStartTransaction(this.currentProject, "ZDBBR", true,
				Stream.of(new String[][] { { "ADT", String.valueOf(true) } })
					.collect(Collectors.toMap(data -> data[0], data -> data[1])));

		AdtUtil.overrideSapGuiPartTitle(part, this.currentProject, "DB Browser", "DB Browser", IImages.DB_BROWSER_DATA_PREVIEW); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

}
