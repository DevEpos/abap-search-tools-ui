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
import org.eclipse.ui.dialogs.SelectionDialog;

import com.devepos.adt.saat.IModificationListener;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.SearchEngine;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.favorites.ExportFavoritesAction;
import com.devepos.adt.saat.internal.search.favorites.ImportFavoritesAction;
import com.devepos.adt.saat.internal.search.favorites.ManageSearchFavoritesDialog;
import com.devepos.adt.saat.internal.search.favorites.NewSearchFavoriteDialog;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQuery;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorite;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorites;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * An Action for managing the opening/managing the searches favorites
 *
 * @author stockbal
 */
public class SearchFavoritesAction extends Action implements IMenuCreator, IModificationListener {
	private Menu menu;
	private final IAbapProjectProvider projectProvider;
	private final IObjectSearchFavorites favoriteManager;

	public SearchFavoritesAction(final IAbapProjectProvider projectProvider) {
		super(Messages.ObjectSearch_SearchFavoritesAction_xtol, SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.FAVORITES));
		this.projectProvider = projectProvider;
		setMenuCreator(this);
		this.favoriteManager = SearchAndAnalysisPlugin.getDefault().getFavoriteManager();
	}

	@Override
	public void dispose() {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.favoriteManager.removeModificationListener(this);
	}

	@Override
	public Menu getMenu(final Control parent) {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.menu = new Menu(parent);

		final IAction organizeFavoritesAction = new Action(Messages.ObjectSearch_OrganizeFavorites_xmit) {
			@Override
			public void run() {
				openOrganizeFavoritesDialog();
			}
		};
		organizeFavoritesAction.setEnabled(this.favoriteManager.hasEntries());

		final IAction createFavoriteAction = new Action(Messages.ObjectSearch_CreateFavoriteFromLastSearch_xmit) {
			@Override
			public void run() {
				createNewFavorite();
			}

		};
		createFavoriteAction.setEnabled(SearchAndAnalysisPlugin.getDefault().getHistory().hasActiveEntry());

		if (!this.favoriteManager.hasEntries()) {
			final IAction noFavoritesAction = new Action(Messages.ObjectSearch_NoSearchFavorites_xmit) {
			};
			noFavoritesAction.setEnabled(false);
			addActionToMenu(this.menu, noFavoritesAction);
		} else {
			for (final IObjectSearchFavorite favorite : this.favoriteManager.getFavorites()) {
				final IAction favoriteAction = new Action(favorite.toString()) {
					@Override
					public void run() {
						runSearch(favorite);
					}
				};
				switch (favorite.getSearchType()) {
				case CDS_VIEW:
					favoriteAction.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.CDS_VIEW));
					break;
				case DB_TABLE_VIEW:
					favoriteAction.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TABLE_DEFINITION));
					break;
				}
				addActionToMenu(this.menu, favoriteAction);
			}
		}
		Separator separator = new Separator();
		separator.fill(this.menu, -1);

		addActionToMenu(this.menu, createFavoriteAction);
		addActionToMenu(this.menu, organizeFavoritesAction);

		separator = new Separator();
		separator.fill(this.menu, -1);
		addActionToMenu(this.menu, new ImportFavoritesAction());
		final Action exportAction = new ExportFavoritesAction();
		exportAction.setEnabled(this.favoriteManager.hasEntries());
		addActionToMenu(this.menu, exportAction);

		return this.menu;
	}

	/*
	 * Executes a new object search for the given favorite
	 */
	private void runSearch(final IObjectSearchFavorite favorite) {
		String destinationId = null;
		if (favorite.isProjectIndependent()) {
			destinationId = this.projectProvider.getDestinationId();
		} else {
			destinationId = favorite.getDestinationId();
		}
		final IObjectSearchQuery searchQuery = new ObjectSearchQuery(favorite.getQuery(), favorite.getSearchType(),
			destinationId);
		searchQuery.setAndSearchActice(favorite.isAndSearchActive());
		searchQuery.setReadApiState(true);
		searchQuery.setCreateHistory(true);
		searchQuery.setUpdateView(true);
		SearchEngine.runObjectSearch(searchQuery);
	}

	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}

	@Override
	public void runWithEvent(final Event event) {
		openOrganizeFavoritesDialog();
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
	 * Opens a dialog to organize all the favorites
	 */
	private void openOrganizeFavoritesDialog() {
		if (!this.favoriteManager.hasEntries()) {
			return;
		}
		final SelectionDialog historyDialog = new ManageSearchFavoritesDialog(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (historyDialog.open() == Window.OK) {
			final Object[] chosenEntries = historyDialog.getResult();
			if (chosenEntries != null && chosenEntries.length == 1) {
				runSearch((IObjectSearchFavorite) chosenEntries[0]);
			}
		}
	}

	/*
	 * Creates new favorite from the current history entry
	 */
	private void createNewFavorite() {
		new NewSearchFavoriteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			SearchAndAnalysisPlugin.getDefault().getHistory().getActiveEntry().getQuery()).open();
	}

	/*
	 * Adds the given action to the given menu
	 */
	private void addActionToMenu(final Menu parent, final IAction action) {
		final ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}