package com.devepos.adt.saat.internal.search.model;

import com.devepos.adt.saat.internal.search.IObjectSearchProvider;
import com.devepos.adt.saat.internal.search.ObjectSearchProvider;
import com.devepos.adt.saat.internal.ui.IStatusUpdater;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryListener;
import com.devepos.adt.saat.search.model.QueryManager;

/**
 * Internal implementation for the object search engine
 *
 * @author stockbal
 */
public class InternalSearchEngine {
	private static InternalSearchEngine INSTANCE;
	private final QueryManager queryManager;
	private final IObjectSearchProvider searchProvider;

	private InternalSearchEngine() {
		this.queryManager = new QueryManager();
		this.searchProvider = new ObjectSearchProvider();
	}

	/**
	 * Retrieves an instance of the internal search engine
	 *
	 * @return
	 */
	public static InternalSearchEngine getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new InternalSearchEngine();
		}
		return INSTANCE;
	}

	/**
	 * Return the {@link QueryManager}
	 *
	 * @return
	 */
	public QueryManager getQueryManager() {
		return this.queryManager;
	}

	/**
	 * Runs the given {@link IObjectSearchQuery}
	 *
	 * @param query the query to be executed
	 */
	public void runQuery(final IObjectSearchQuery query) {
		final String destinationId = query.getDestinationId();
		// get ABAP project for destination id
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (!projectProvider.ensureLoggedOn()) {
			return;
		}

		this.searchProvider.runSearch(query, projectProvider);
	}

	/**
	 * Runs the given {@link IObjectSearchQuery}
	 *
	 * @param query          the query to be executed
	 * @param searchListener the listener to be notified after the search has
	 *                       finished
	 * @param statusUpdater  the status updater which should be used for status
	 *                       updates during the query execution
	 * @param useJob         if <code>false</code> the caller should make sure it
	 *                       runs in a backend Job
	 */
	public void runQuery(final IObjectSearchQuery query, final IObjectSearchQueryListener searchListener,
		final IStatusUpdater statusUpdater, final boolean useJob) {

		final String destinationId = query.getDestinationId();
		// get ABAP project for destination id
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (!projectProvider.ensureLoggedOn()) {
			return;
		}

		this.searchProvider.runSearch(query, projectProvider, searchListener, statusUpdater, useJob);
	}
}
