package com.devepos.adt.saat.search.favorites;

import java.util.List;

import com.devepos.adt.saat.IModificationProvider;
import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Manages the favorites of the object search
 *
 * @author stockbal
 */
public interface IObjectSearchFavorites extends IModificationProvider {

	/**
	 * Adds the given entry to the search favorites
	 *
	 * @param entry
	 */
	void addFavorite(IObjectSearchFavorite entry);

	/**
	 * Adds a new favorite entry for the given parameters
	 *
	 * @param query                the executed {@link IObjectSearchQuery}
	 * @param description          the description for the favorite
	 * @param isProjectIndependent <code>true</code> if the favorite entry should be
	 *                             project independent
	 * @see                        IObjectSearchQuery
	 */
	void addFavorite(IObjectSearchQuery query, String description, boolean isProjectIndependent);

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
	 * @param  destinationId the destination id of the project
	 * @param  searchType    the object type of the search
	 * @param  description   the description of a favorite
	 * @return               <code>true</code> if there is favorite with the given
	 *                       description
	 */
	boolean contains(String destinationId, SearchType searchType, String description);
}
