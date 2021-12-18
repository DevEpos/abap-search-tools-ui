package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.FeatureTester;

/**
 * Abstract Handler for Commands concerning the DB Browser Application (SAP GUI
 * Transaction)
 *
 * @author stockbal
 */
public abstract class DbBrowserCommandHandler extends AbstractHandler {

  /**
   * Returns <code>true</code> if the DB Browser feature is available
   *
   * @param project an ABAP project
   * @return <code>true</code> if the DB Browser feature is available
   */
  protected boolean isFeatureAvailable(final IProject project) {
    if (project == null) {
      return false;
    }
    return FeatureTester.isSapGuiDbBrowserAvailable(project);
  }

  /**
   * Shows a message dialog to signal the User that the DB Browser Feature is not
   * available in the given project
   *
   * @param project an ABAP project
   */
  protected void showFeatureNotAvailableDialog(final IProject project) {
    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        Messages.Dialog_InfoTitle_xmsg, NLS.bind(Messages.DbBrowser_featureIsNotAvailable_xmsg,
            DestinationUtil.getDestinationId(project)));
  }

}
