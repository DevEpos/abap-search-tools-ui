package com.devepos.adt.saat;

import com.devepos.adt.saat.internal.search.model.InternalSearchEngine;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQuery;
import com.devepos.adt.saat.internal.ui.IStatusUpdater;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryListener;
import com.devepos.adt.saat.search.model.QueryManager;

/**
 * Starting point for accessing the Object search API
 *
 * @author stockbal
 */
public class SearchEngine {

	/**
	 * Creates new empty object search query instance
	 * 
	 * @return
	 */
	public static IObjectSearchQuery createSearchQuery() {
		return new ObjectSearchQuery();
	}

	/**
	 * Runs a new object search for the given parameters
	 *
	 * @param searchQuery the search query
	 */
	public static void runObjectSearch(final IObjectSearchQuery searchQuery) {
		InternalSearchEngine.getInstance().runQuery(searchQuery);
	}

	/**
	 * Runs the given {@link IObjectSearchQuery}. <br>
	 * This method does notify the registered listeners in the {@link QueryManager}
	 * but the supplied the <code>searchListener</code> and
	 * <code>statusUpdater</code>
	 *
	 * @param searchQuery    the search query
	 * @param searchListener the listener to be notified after the search has
	 *                       finished
	 * @param statusUpdater  the status updater which should be used for status
	 *                       updates during the query execution
	 * @param useJob         if <code>false</code> the caller should make sure it
	 *                       runs in a backend Job
	 */
	public static void runObjectSearch(final IObjectSearchQuery searchQuery, final IObjectSearchQueryListener searchListener,
		final IStatusUpdater statusUpdater, final boolean useJob) {

		InternalSearchEngine.getInstance().runQuery(searchQuery, searchListener, statusUpdater, useJob);
	}

}
