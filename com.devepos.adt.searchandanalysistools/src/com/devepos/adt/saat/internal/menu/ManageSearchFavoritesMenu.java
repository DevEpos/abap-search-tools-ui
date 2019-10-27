package com.devepos.adt.saat.internal.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ObjectSearchEngine;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.search.favorites.ExportFavoritesAction;
import com.devepos.adt.saat.internal.search.favorites.ImportFavoritesAction;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorites;

/**
 * Creates Menu for ABAP Object Search + Favorites
 *
 * @author stockbal
 */
public class ManageSearchFavoritesMenu extends CompoundContributionItem {

	private IObjectSearchFavorites favoriteManager;

	public ManageSearchFavoritesMenu() {
		this.favoriteManager = SearchAndAnalysisPlugin.getDefault().getFavoriteManager();
	}

	public ManageSearchFavoritesMenu(final String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final IContributionItem[] items = createMenuItems();
		return items;
	}

	private IContributionItem[] createMenuItems() {
		final List<IContributionItem> items = new ArrayList<>();
		if (!this.favoriteManager.hasEntries()) {
			final IAction noFavoritesAction = new Action(Messages.ObjectSearch_NoSearchFavorites_xmit) {
			};
			noFavoritesAction.setEnabled(false);
			items.add(new ActionContributionItem(noFavoritesAction));
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
				items.add(new ActionContributionItem(favoriteAction));
			}
		}
		items.add(new Separator());
		items.add(new ActionContributionItem(new ImportFavoritesAction()));
		final Action exportAction = new ExportFavoritesAction();
		exportAction.setEnabled(this.favoriteManager.hasEntries());
		items.add(new ActionContributionItem(exportAction));

		return items.toArray(new IContributionItem[items.size()]);
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

}