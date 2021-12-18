package com.devepos.adt.saat.internal.search.favorites;

import java.util.List;

import com.devepos.adt.base.util.IModificationProvider;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchRequest;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;

/**
 * Manages the favorites of the object search
 *
 * @author stockbal
 */
public interface IObjectSearchFavorites extends IModificationProvider<IObjectSearchFavorite> {

  /**
   * Adds the given entry to the search favorites
   *
   * @param entry
   */
  void addFavorite(IObjectSearchFavorite entry);

  /**
   * Adds a new favorite entry for the given parameters
   *
   * @param searchRequest the search request
   * @see ObjectSearchRequest
   */
  void addFavorite(ObjectSearchRequest searchRequest);

  /**
   * Removes the given history entry from the history
   *
   * @param historyEntry the history entry to be removed
   */
  void removeFavorite(IObjectSearchFavorite favorite);

  /**
   * Returns the favorites of the object search
   *
   * @return
   */
  List<IObjectSearchFavorite> getFavorites();

  /**
   * Sets the favorites of the object search
   *
   * @param favorites the {@link List} of search favorites
   */
  void setFavorites(List<IObjectSearchFavorite> favorites);

  /**
   * Returns <code>true</code> if there is at least one history entry, otherwise
   * <code>false</code>
   *
   * @return <code>true</code> if there are history entries
   */
  boolean hasEntries();

  /**
   * Returns <code>true</code> if there is favorite with the given description
   *
   * @param destinationId the destination id of the project
   * @param searchType    the object type of the search
   * @param description   the description of a favorite
   * @return <code>true</code> if there is favorite with the given description
   */
  boolean contains(String destinationId, String searchType, String description);
}
