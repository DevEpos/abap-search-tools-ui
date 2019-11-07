package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ObjectSearchEngine;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.search.favorites.ExportFavoritesAction;
import com.devepos.adt.saat.internal.search.favorites.IObjectSearchFavorites;
import com.devepos.adt.saat.internal.search.favorites.ImportFavoritesAction;
import com.devepos.adt.saat.internal.search.favorites.ManageSearchFavoritesDialog;
import com.devepos.adt.saat.internal.search.favorites.NewSearchFavoriteDialog;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;

/**
 * An Action for managing the opening/managing the searches favorites
 *
 * @author stockbal
 */
public class SearchFavoritesAction extends Action implements IMenuCreator {
	private Menu menu;
	private final IObjectSearchFavorites favoriteManager;

	public SearchFavoritesAction() {
		super(Messages.ObjectSearch_SearchFavoritesAction_xtol,
			SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.FAVORITES));
		setMenuCreator(this);
		this.favoriteManager = SearchAndAnalysisPlugin.getDefault().getFavoriteManager();
	}

	@Override
	public void dispose() {
		if (this.menu != null) {
			this.menu.dispose();
		}
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

		final IAction createFavoriteAction = new Action(Messages.ObjectSearch_CreateFavoriteFromCurrentQuery_xmit) {
			@Override
			public void run() {
				createNewFavorite();
			}

		};

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
				if (SearchType.CDS_VIEW.name().equals(favorite.getSearchType())) {
					favoriteAction.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.CDS_VIEW));
				} else if (SearchType.DB_TABLE_VIEW.name().equals(favorite.getSearchType())) {
					favoriteAction
						.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TABLE_DEFINITION));
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
		if (favorite.isProjectIndependent()) {
			ObjectSearchEngine.openFavoriteInSearchDialog(favorite);
		} else {
			ObjectSearchEngine.runSearchFromFavorite(favorite);
		}
	}

	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}

	@Override
	public void runWithEvent(final Event event) {
		openOrganizeFavoritesDialog();
	}

	/*
	 * Opens a dialog to organize all the favorites
	 */
	private void openOrganizeFavoritesDialog() {
		final SelectionDialog favoritesDialog = new ManageSearchFavoritesDialog(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (favoritesDialog.open() == Window.OK) {
			final Object[] chosenEntries = favoritesDialog.getResult();
			if (chosenEntries != null && chosenEntries.length == 1) {
				runSearch((IObjectSearchFavorite) chosenEntries[0]);
			}
		}
	}

	/*
	 * Creates new favorite from the last executed object search query
	 */
	private void createNewFavorite() {
		final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (!(activePart instanceof SearchView)) {
			return;
		}
		final SearchView currentSearchView = (SearchView) activePart;
		final ISearchResultPage resultPage = currentSearchView.getActivePage();
		if (resultPage == null || !(resultPage instanceof ObjectSearchResultPage)) {
			return;
		}
		new NewSearchFavoriteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			((ObjectSearchResultPage) resultPage).getSearchQuery().getSearchRequest()).open();
	}

	/*
	 * Adds the given action to the given menu
	 */
	private void addActionToMenu(final Menu parent, final IAction action) {
		final ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}