package com.devepos.adt.saat.internal.search.history;

import com.devepos.adt.saat.search.history.IObjectSearchHistoryEntry;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Represents an entry in the object search history
 *
 * @author stockbal
 */
public class ObjectSearchHistoryEntry implements IObjectSearchHistoryEntry {
	private final IObjectSearchQuery query;
	private final int resultCount;

	public ObjectSearchHistoryEntry(final IObjectSearchQuery query, final int resultCount) {
		this.query = query;
		this.resultCount = resultCount;
	}

	@Override
	public IObjectSearchQuery getQuery() {
		return this.query;
	}

	@Override
	public int getResultCount() {
		return this.resultCount;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof IObjectSearchHistoryEntry)) {
			return super.equals(object);
		}
		return this.query.equals(((IObjectSearchHistoryEntry) object).getQuery());
	}

	@Override
	public String toString() {
		return String.format("%s - %d Result(s)", this.query.toString(), getResultCount());
	}

}
