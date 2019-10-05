package com.devepos.adt.saat.search.favorites;

import com.devepos.adt.saat.internal.search.SearchType;

/**
 * An object query that was classified as a favorite
 *
 * @author stockbal
 */
public interface IObjectSearchFavorite {

	/**
	 * Returns the query String of the favorite entry
	 *
	 * @return the executed query String
	 */
	String getQuery();

	/**
	 * Sets the query of the favorite
	 *
	 * @param query the query
	 */
	void setQuery(String query);

	/**
	 * Returns the description of this favorite
	 *
	 * @return
	 */
	String getDescription();

	/**
	 * Sets the description of the favorite
	 *
	 * @param description the description
	 */
	void setDescription(String description);

	/**
	 * Returns the search type of the executed query
	 *
	 * @return the search type
	 */
	SearchType getSearchType();

	/**
	 * Sets the {@link SearchType} of the favorite
	 *
	 * @param searchType the search type
	 */
	void setSearchType(SearchType searchType);

	/**
	 * Returns the project destination Id of the history entry. If the destination
	 * is is <code>null</code> the query is project independent
	 *
	 * @return the destination Id
	 */
	String getDestinationId();

	/**
	 * Sets the destination Id of the favorite
	 *
	 * @param destinationId the destination id
	 */
	void setDestinationId(String destinationId);

	/**
	 * Returns <code>true</code> if the query is project independent
	 *
	 * @return <code>true</code> if it is project independent
	 */
	boolean isProjectIndependent();

	/**
	 * If <code>true</code> the favorite will be set to project independent
	 *
	 * @param isProjectIndependent
	 */
	void setProjectIndependent(boolean isProjectIndependent);

	/**
	 * Returns <code>true</code> if the search in was executed with active "AND"
	 * search
	 *
	 * @return <code>true</code> if "AND" was activated
	 */
	boolean isAndSearchActive();

	/**
	 * If <code>true</code> the AND search will be set for the favorite
	 *
	 * @param isAndSearchActive
	 */
	void setAndSearchActive(boolean isAndSearchActive);
}
