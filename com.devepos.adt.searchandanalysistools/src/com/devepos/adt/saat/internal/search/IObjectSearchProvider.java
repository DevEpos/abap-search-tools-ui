package com.devepos.adt.saat.internal.search;

import com.devepos.adt.saat.internal.ui.IStatusUpdater;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryListener;
import com.devepos.adt.saat.search.model.QueryManager;

/**
 * An <code>ISearchResultProvider</code> is used to provide results for an
 * object search
 *
 * @author stockbal
 */
public interface IObjectSearchProvider {

	/**
	 * Runs a search with the given parameters
	 *
	 * @param query           the search query string
	 * @param projectProvider the {@link IAbapProjectProvider} to be used for the
	 *                        search
	 */
	void runSearch(IObjectSearchQuery query, IAbapProjectProvider projectProvider);

	/**
	 * Runs a search with the given parameters.<br>
	 * Only the passed <code>searchListener</code> and <code>statusUpdate</code>
	 * will be notified during the search, regardless of the registered listeners
	 * and {@link QueryManager}
	 *
	 * @param query           the query to be executed
	 * @param projectProvider the project provider to get the backend destination
	 *                        from
	 * @param searchListener  a listener to be notified when the search is finished
	 * @param statusUpdater   a status updater to be notified for status changes
	 *                        during the search or <code>null</code>
	 * @param useJob          if <code>false</code> the caller should make certain
	 *                        that this method is called in a backend Job
	 */
	void runSearch(IObjectSearchQuery query, IAbapProjectProvider projectProvider, IObjectSearchQueryListener searchListener,
		IStatusUpdater statusUpdater, boolean useJob);
}
