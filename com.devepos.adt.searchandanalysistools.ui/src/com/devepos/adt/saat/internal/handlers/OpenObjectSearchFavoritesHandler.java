package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devepos.adt.saat.internal.search.ObjectSearchEngine;
import com.devepos.adt.saat.internal.search.favorites.ManageSearchFavoritesDialog;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;;

/**
 * Handler for managing the favorites of the ABAP Object Search +
 *
 * @author stockbal
 */
public class OpenObjectSearchFavoritesHandler extends AbstractHandler {

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final SelectionDialog favoriteDialog = new ManageSearchFavoritesDialog(HandlerUtil
        .getActiveShell(event));
    if (favoriteDialog.open() == Window.OK) {
      final Object[] chosenEntries = favoriteDialog.getResult();
      if (chosenEntries != null && chosenEntries.length == 1) {
        final IObjectSearchFavorite favorite = (IObjectSearchFavorite) chosenEntries[0];
        if (favorite.isProjectIndependent()) {
          ObjectSearchEngine.openFavoriteInSearchDialog(favorite);
        } else {
          ObjectSearchEngine.runSearchFromFavorite(favorite);
        }
      }
    }
    return null;
  }
}
