package com.devepos.adt.saat.search.model;

public interface IObjectSearchQueryListener {

	/**
	 * Called after an <code>ISearchQuery</code> has finished.
	 *
	 * @param query the result of the executed object search query
	 */
	void queryFinished(IObjectSearchQueryResult queryResult);
}
