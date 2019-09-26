package com.devepos.adt.saat.internal.search.model;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryResult;

/**
 * Represents the result of an executed {@link IObjectSearchQuery}
 *
 * @author stockbal
 */
public class ObjectSearchQueryResult implements IObjectSearchQueryResult {

	private final IObjectSearchQuery query;
	private final int maxResultCount;
	private final IAdtObjectReferenceElementInfo[] results;

	public ObjectSearchQueryResult(final IObjectSearchQuery query, final int maxResultCount,
		final IAdtObjectReferenceElementInfo[] results) {
		this.query = query;
		this.maxResultCount = maxResultCount;
		this.results = results;
	}

	@Override
	public IObjectSearchQuery getQuery() {
		return this.query;
	}

	@Override
	public boolean createHistoryEntry() {
		return this.query.createHistoryEntry();
	}

	@Override
	public IAdtObjectReferenceElementInfo[] getResults() {
		return this.results;
	}

	@Override
	public int getMaxResultCount() {
		return this.maxResultCount;
	}

}
