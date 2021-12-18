package com.devepos.adt.saat.internal.menu;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.menus.CommandContributionItem;

import com.devepos.adt.base.ui.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.handlers.OpenInDbBrowserHandler;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Factory for creating items for menus
 *
 * @author stockbal
 */
public class SaatMenuItemFactory {

  /**
   * Adds "Open in DB Browser" command to the given {@link IMenuManager}
   *
   * @param mgr           the menu manager for the command
   * @param skipSelscreen if <code>true</code> the command will be added with a
   *                      parameter value of <code>'true</code> otherwise
   *                      <code>'false'</code>
   */
  public static void addOpenInDbBrowserCommand(final IMenuManager mgr,
      final boolean skipSelscreen) {
    addOpenInDbBrowserCommand(mgr, null, skipSelscreen);
  }

  /**
   * Adds "Open in DB Browser" command to the given {@link IMenuManager}
   *
   * @param mgr           the menu manager for the command
   * @param groupId       the id of the menu group for the command
   * @param skipSelscreen if <code>true</code> the command will be added with a
   *                      parameter value of <code>'true</code> otherwise
   *                      <code>'false'</code>
   */
  public static void addOpenInDbBrowserCommand(final IMenuManager mgr, final String groupId,
      final boolean skipSelscreen) {
    MenuItemFactory.addCommandItem(mgr, groupId, ICommandConstants.OPEN_IN_DB_BROWSER,
        SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.DB_BROWSER_DATA_PREVIEW),
        skipSelscreen ? Messages.ObjectSearch_OpenInDbBrowserAndSkip_xmit
            : Messages.ObjectSearch_OpenInDbBrowser_xmit, new String[][] { {
                OpenInDbBrowserHandler.PARAM_SKIP_SELSCREEN, String.valueOf(skipSelscreen) } });
  }

  /**
   * Adds a {@link CommandContributionItem} for the given <code>commandId</code>
   * to the supplied {@link IMenuManager} which opens the CDS Analyzer View. <br>
   *
   * @param mgr       the {@link IMenuManager} to which the command should be
   *                  added
   * @param groupId   the group name where the command should be inserted in the
   *                  menu or <code>null</code>
   * @param commandId the fully qualified id of the command
   */
  public static void addCdsAnalyzerCommandItem(final IMenuManager mgr, final String groupId,
      final String commandId) {
    String label = null;
    String imageId = null;

    switch (commandId) {
    case ICommandConstants.CDS_TOP_DOWN_ANALYSIS:
      label = Messages.AdtObjectMenu_CdsTopDownAnalysis_xmit;
      imageId = IImages.TOP_DOWN;
      break;
    case ICommandConstants.USED_ENTITIES_ANALYSIS:
      label = Messages.AdtObjectMenu_CdsUsedEntitiesAnalysis_xmit;
      imageId = IImages.USAGE_ANALYZER;
      break;
    case ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS:
      label = Messages.AdtObjectMenu_CdsWhereUsedAnalysis_xmit;
      imageId = IImages.WHERE_USED_IN;
      break;
    case ICommandConstants.FIELD_ANALYSIS:
      label = Messages.AdtObjectMenu_FieldAnalysis_xmit;
      imageId = IImages.FIELD_ANALYSIS;
      break;
    }

    Assert.isTrue(label != null && imageId != null);
    MenuItemFactory.addCommandItem(mgr, groupId, commandId, SearchAndAnalysisPlugin.getDefault()
        .getImageDescriptor(imageId), label, null);
  }
}
