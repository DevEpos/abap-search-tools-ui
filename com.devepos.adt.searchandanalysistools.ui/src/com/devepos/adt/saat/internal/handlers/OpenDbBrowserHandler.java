package com.devepos.adt.saat.internal.handlers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
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
    window.getSelectionService().getSelection();

    // get the current selected ADT project
    currentProject = ProjectUtil.getCurrentAbapProject();
    if (currentProject == null) {
      // open project chooser dialog
      currentProject = AbapProjectSelectionDialog.open(null, currentProject);
      if (currentProject == null) {
        return null;
      }
    }

    if (!ProjectUtil.ensureLoggedOnToProject(currentProject).isOK()) {
      // without successful logon the feature availability cannot be tested
      return null;
    }

    if (!isFeatureAvailable(currentProject)) {
      showFeatureNotAvailableDialog(currentProject);
      return null;
    }

    final WorkbenchPart part = (WorkbenchPart) AdtSapGuiEditorUtilityFactory
        .createSapGuiEditorUtility()
        .openEditorAndStartTransaction(currentProject, "ZDBBR", true, //$NON-NLS-1$
            Stream.of(new String[][] { { "ADT", String.valueOf(true) } }) //$NON-NLS-1$
                .collect(Collectors.toMap(data -> data[0], data -> data[1])));

    AdtUIUtil.overrideSapGuiPartTitle(part, currentProject, Messages.DbBrowser_xtit,
        Messages.DbBrowser_xtit, SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.DB_BROWSER_DATA_PREVIEW));
    return null;
  }

}
