package com.devepos.adt.saat.search.model;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;

/**
 * Represents the result of an object search query
 *
 * @author stockbal
 */
public interface IObjectSearchQueryResult {

	/**
	 * Returns the {@link IObjectSearchQuery} that procured this query result
	 *
	 * @return
	 */
	IObjectSearchQuery getQuery();

	/**
	 * If <code>true</code> a history should be created for this query
	 *
	 * @return
	 */
	boolean createHistoryEntry();

	/**
	 * Returns the found results of the executed query
	 *
	 * @return the query results
	 */
	IAdtObjectReferenceElementInfo[] getResults();

	/**
	 * Returns the defined maximum row count for this query
	 *
	 * @return the defined maximum row count
	 */
	int getMaxResultCount();
}
