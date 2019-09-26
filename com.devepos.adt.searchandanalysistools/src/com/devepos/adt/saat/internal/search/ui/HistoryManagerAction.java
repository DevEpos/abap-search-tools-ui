package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.IModificationListener;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.SearchEngine;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.favorites.NewSearchFavoriteDialog;
import com.devepos.adt.saat.internal.search.history.SearchHistorySelectionDialog;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQuery;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.history.IObjectSearchHistory;
import com.devepos.adt.saat.search.history.IObjectSearchHistoryEntry;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * An Action for managing the search history
 *
 * @author stockbal
 */
public class HistoryManagerAction extends Action implements IMenuCreator, IModificationListener {
	private Menu menu;

	public HistoryManagerAction() {
		super(Messages.ObjectSearch_HistoryAction_xtol, SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.SEARCH_HISTORY));
		setMenuCreator(this);
		final IObjectSearchHistory history = SearchAndAnalysisPlugin.getDefault().getHistory();
		setEnabled(true);
		history.addModificationListener(this);
	}

	@Override
	public void dispose() {
		if (this.menu != null) {
			this.menu.dispose();
		}
		SearchAndAnalysisPlugin.getDefault().getHistory().removeModificationListener(this);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		boolean enable = enabled;
		if (enable && !SearchAndAnalysisPlugin.getDefault().getHistory().hasEntries()) {
			enable = false;
		}
		super.setEnabled(enable);
	}

	@Override
	public Menu getMenu(final Control parent) {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.menu = new Menu(parent);
		final IObjectSearchHistory history = SearchAndAnalysisPlugin.getDefault().getHistory();

		final IAction clearHistoryAction = new Action(Messages.ObjectSearch_ClearHistory_xmit) {
			@Override
			public void run() {
				SearchAndAnalysisPlugin.getDefault().getHistory().clear();
			}

		};
		final IAction showHistoryAction = new Action(Messages.ObjectSearch_ManageHistoryEntries_xmit) {
			@Override
			public void run() {
				openHistorySelectionDialog();
			}
		};
		final IAction addToFavorites = new Action(Messages.ObjectSearch_AddToFavorites_xmit) {
			@Override
			public void run() {
				openCreateFavoriteDialog();
			}
		};
		addToFavorites.setEnabled(history.hasActiveEntry());

		for (final IObjectSearchHistoryEntry historyEntry : history.getEntries()) {
			final IAction historyAction = new Action(historyEntry.toString(), IAction.AS_RADIO_BUTTON) {
				@Override
				public void run() {
					runSearch(historyEntry);
				}
			};
			switch (historyEntry.getQuery().getSearchType()) {
			case CDS_VIEW:
				historyAction.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.CDS_VIEW));
				break;
			case DB_TABLE_VIEW:
				historyAction.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TABLE_DEFINITION));
				break;
			}
			historyAction.setChecked(history.isActiveEntry(historyEntry));
			addActionToMenu(this.menu, historyAction);
		}
		final Separator separator = new Separator();
		separator.fill(this.menu, -1);
		addActionToMenu(this.menu, clearHistoryAction);
		addActionToMenu(this.menu, addToFavorites);
		addActionToMenu(this.menu, showHistoryAction);

		return this.menu;
	}

	private void runSearch(final IObjectSearchHistoryEntry historyEntry) {
		final IObjectSearchQuery searchQuery = new ObjectSearchQuery(historyEntry.getQuery().getQuery(),
			historyEntry.getQuery().getSearchType(), historyEntry.getQuery().getDestinationId());
		searchQuery.setUpdateView(true);
		searchQuery.setReadApiState(true);
		searchQuery.setAndSearchActice(historyEntry.getQuery().isAndSearchActive());
		SearchEngine.runObjectSearch(searchQuery);
		SearchAndAnalysisPlugin.getDefault().getHistory().setActiveEntry(historyEntry);
	}

	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}

	@Override
	public void runWithEvent(final Event event) {
		openHistorySelectionDialog();
	}

	@Override
	public void modified(final ModificationKind kind) {
		switch (kind) {
		case ADDED:
			setEnabled(true);
			break;
		case CLEARED:
			setEnabled(false);
		default:
			break;
		}
	}

	/*
	 * Open a dialog to create a favorite for a search
	 */
	private void openCreateFavoriteDialog() {
		final IObjectSearchHistoryEntry activeEntry = SearchAndAnalysisPlugin.getDefault().getHistory().getActiveEntry();
		final NewSearchFavoriteDialog createDialog = new NewSearchFavoriteDialog(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), activeEntry.getQuery());
		createDialog.open();
	}

	/*
	 * Open a dialog to manage all existing history entries
	 */
	private void openHistorySelectionDialog() {
		final SearchHistorySelectionDialog historyDialog = new SearchHistorySelectionDialog(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (historyDialog.open() == Window.OK) {
			final Object[] chosenEntries = historyDialog.getResult();
			if (chosenEntries != null && chosenEntries.length == 1) {
				runSearch((IObjectSearchHistoryEntry) chosenEntries[0]);
			}
		}

	}

	/*
	 * Adds the given action to the given menu
	 */
	private void addActionToMenu(final Menu parent, final IAction action) {
		final ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}