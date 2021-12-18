package com.devepos.adt.saat.internal.search.favorites;

import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;

/**
 * Utility class for {@link IObjectSearchFavorite}
 *
 * @author stockbal
 */
public final class ObjectSearchFavoritesUtil {

  /**
   * Returns a display for the given favorite, which can be used in Menus or
   * dialogues
   *
   * @param favorite a object search favorite
   * @return a display for the given favorite, which can be used in Menus or
   *         dialogues
   */
  public static String getFavoriteDisplayName(final IObjectSearchFavorite favorite) {
    final String systemId = String.format("[%s] ", favorite.isProjectIndependent() ? "?"
        : DestinationUtil.getSystemId(favorite.getDestinationId()));
    return String.format("%s%s - '%s'", systemId, favorite.getDescription(), String.format("%s %s",
        favorite.getObjectName(), favorite.getSearchFilter()).trim());
  }
}
