package com.devepos.adt.saat.internal.menu;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.handlers.OpenInDbBrowserHandler;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Factory for creating items for menus
 *
 * @author stockbal
 */
public class MenuItemFactory {

	/**
	 * Adds "Open in DB Browser" command to the given {@link IMenuManager}
	 *
	 * @param mgr           the menu manager for the command
	 * @param skipSelscreen if <code>true</code> the command will be added with a
	 *                      parameter value of <code>'true</code> otherwise
	 *                      <code>'false'</code>
	 */
	public static void addOpenInDbBrowserCommand(final IMenuManager mgr, final boolean skipSelscreen) {
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
	public static void addOpenInDbBrowserCommand(final IMenuManager mgr, final String groupId, final boolean skipSelscreen) {
		addCommandItem(mgr, groupId, ICommandConstants.OPEN_IN_DB_BROWSER, IImages.DB_BROWSER_DATA_PREVIEW,
			skipSelscreen ? Messages.ObjectSearch_OpenInDbBrowserAndSkip_xmit : Messages.ObjectSearch_OpenInDbBrowser_xmit,
			new String[][] { { OpenInDbBrowserHandler.PARAM_SKIP_SELSCREEN, String.valueOf(skipSelscreen) } });
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
	public static void addCdsAnalyzerCommandItem(final IMenuManager mgr, final String groupId, final String commandId) {
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
		addCommandItem(mgr, groupId, commandId, imageId, label, null);
	}

	/**
	 * Adds a {@link CommandContributionItem} for the given <code>commandId</code>
	 * to the supplied {@link IMenuManager}
	 *
	 * @param mgr       the {@link IMenuManager} to which the command should be
	 *                  added
	 * @param groupId   the group name where the command should be inserted in the
	 *                  menu or <code>null</code>
	 * @param commandId the fully qualified id of the command
	 * @param imageId   an image id from {@link IImages}
	 * @param label     the label of the menu entry
	 * @param params    an optional list of parameter key/value pairs for the
	 *                  command
	 */
	public static void addCommandItem(final IMenuManager mgr, final String groupId, final String commandId, final String imageId,
		final String label, final String[][] params) {
		final IContributionItem commandItem = createCommandContributionItem(groupId, commandId, imageId, label, true, params);
		if (groupId != null) {
			mgr.appendToGroup(groupId, commandItem);
		} else {
			mgr.add(commandItem);
		}
	}

	/**
	 * Adds a {@link CommandContributionItem} for the given <code>commandId</code>
	 * to the supplied {@link IToolBarManager}
	 *
	 * @param tbm            the toolbar manager instance
	 * @param groupId        the group id to which the command should be added
	 * @param commandId      a unique identifier of a command
	 * @param imageId        an id for an image registered in this plugin
	 * @param label          the label to be displayed for the command
	 * @param visibleEnabled if <code>true</code> the visibility of the contribution
	 *                       item will depend on the <code>enabled</code> state of
	 *                       the command
	 * @param params         an optional two dimensional array of command parameters
	 */
	public static void addCommandItem(final IToolBarManager tbm, final String groupId, final String commandId,
		final String imageId, final String label, final boolean visibleEnabled, final String[][] params) {

		final IContributionItem commandItem = createCommandContributionItem(groupId, commandId, imageId, label, visibleEnabled,
			params);

		if (groupId != null) {
			tbm.appendToGroup(groupId, commandItem);
		} else {
			tbm.add(commandItem);
		}
	}

	private static IContributionItem createCommandContributionItem(final String groupId, final String commandId,
		final String imageId, final String label, final boolean visibleEnabled, final String[][] params) {
		Map<String, String> paramMap = null;
		if (params != null) {
			paramMap = Stream.of(params).collect(Collectors.toMap(key -> key[0], data -> data[1]));
		}
		final CommandContributionItemParameter parameter = new CommandContributionItemParameter(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow(), commandId, commandId, paramMap,
			imageId != null ? SearchAndAnalysisPlugin.getDefault().getImageDescriptor(imageId) : null, null, null, label, null,
			null, CommandContributionItem.STYLE_PUSH, null, visibleEnabled);
		return new CommandContributionItem(parameter);
	}
}
