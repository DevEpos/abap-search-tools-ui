package com.devepos.adt.saat.search.history;

import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Represents an entry in the object search history
 *
 * @author stockbal
 */
public interface IObjectSearchHistoryEntry {

	/**
	 * Returns the {@link IObjectSearchQuery} of this history entry
	 * 
	 * @return
	 */
	IObjectSearchQuery getQuery();

	/**
	 * Returns the result count of the executed query
	 * 
	 * @return
	 */
	int getResultCount();
}
