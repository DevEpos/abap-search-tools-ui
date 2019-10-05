package com.devepos.adt.saat.search.model;

import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.devepos.adt.saat.internal.search.SearchType;

/**
 * Represents an entry in the search history
 *
 * @author stockbal
 */
public interface IObjectSearchQuery {

	/**
	 * Returns the query String of the history entry
	 *
	 * @return the executed query
	 */
	String getQuery();

	/**
	 * Sets the query string
	 *
	 * @param query the query to be executed
	 */
	void setQuery(String query);

	/**
	 * Returns the search type of the executed query
	 *
	 * @return the search type
	 */
	SearchType getSearchType();

	/**
	 * Sets the search type of the query
	 *
	 * @param searchType
	 */
	void setSearchType(SearchType searchType);

	/**
	 * Returns the project destination Id of the history entry
	 *
	 * @return the destination Id
	 */
	String getDestinationId();

	/**
	 * Sets the destination id
	 *
	 * @param destinationId the destination id
	 */
	void setDestinationId(String destinationId);

	/**
	 * Returns <code>true</code> if the search in was executed with active "AND"
	 * search
	 *
	 * @return <code>true</code> if "AND" was activated
	 */
	boolean isAndSearchActive();

	/**
	 * Activates/Deactivates the "AND" search option
	 *
	 * @param andSearchActive
	 */
	void setAndSearchActice(boolean andSearchActive);

	/**
	 * Returns <code>true</code> if a history entry should be created for the query
	 *
	 * @return
	 */
	boolean createHistoryEntry();

	/**
	 * If <code>true</code> is passed a history entry should be created after the
	 * search was executed
	 *
	 * @param createHistory
	 */
	void setCreateHistory(boolean createHistory);

	/**
	 * Returns <code>true</code> if the search view should be updated after the
	 * search
	 *
	 * @return
	 */
	boolean shouldUpdateView();

	/**
	 * If <code>true</code> is passed the UI should be updated with the parameters
	 * of this object query
	 *
	 * @param updateView
	 */
	void setUpdateView(boolean updateView);

	/**
	 * Returns <code>true</code> if the API state should be read for the found
	 * objects
	 *
	 * @return
	 */
	boolean shouldReadApiState();

	/**
	 * If <code>true</code> is passed the API state should be determined for the
	 * found results
	 *
	 * @param readApiState
	 */
	void setReadApiState(boolean readApiState);

	/**
	 * Returns <code>true</code> if all results for the query should be returned. If
	 * this parameter ist set the query paramater
	 * {@link QueryParameterName#MAX_ROWS} will have no effect
	 *
	 * @return
	 */
	boolean shouldReadAllEntries();

	/**
	 * If <code>true</code> is passed all available results for this query should be
	 * retrieved
	 *
	 * @param readAllEntries
	 */
	void setReadAllEntries(boolean readAllEntries);
}
