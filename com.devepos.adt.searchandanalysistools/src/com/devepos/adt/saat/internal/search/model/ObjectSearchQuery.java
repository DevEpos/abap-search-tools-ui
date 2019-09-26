package com.devepos.adt.saat.internal.search.model;

import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Implementation of {@link IObjectSearchQuery}
 *
 * @author stockbal
 */
public class ObjectSearchQuery implements IObjectSearchQuery {

	private String query;
	private String destinationId;
	private boolean andSearchActive;
	private SearchType searchType;
	private boolean createHistoryEntry;
	private boolean updateView;
	private boolean readApiState;
	private boolean readAllEntries;

	public ObjectSearchQuery() {
		this(null, SearchType.CDS_VIEW, null);
	}

	public ObjectSearchQuery(final String query, final SearchType searchType, final String destinationId) {
		this.query = query;
		this.searchType = searchType;
		this.destinationId = destinationId;
		this.updateView = false;
		this.readApiState = false;
		this.readAllEntries = false;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof IObjectSearchQuery)) {
			return super.equals(object);
		}
		final IObjectSearchQuery otherEntry = (IObjectSearchQuery) object;
		return this.query.equalsIgnoreCase(otherEntry.getQuery())
			&& this.destinationId.equalsIgnoreCase(otherEntry.getDestinationId())
			&& this.andSearchActive == otherEntry.isAndSearchActive();
	}

	@Override
	public String toString() {
		final String[] destination = getDestinationId().split("_");
		return String.format("[%s-%s] '%s' (%s)", destination[0], destination[1], getQuery(), getSearchType());
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public String getDestinationId() {
		return this.destinationId;
	}

	@Override
	public boolean isAndSearchActive() {
		return this.andSearchActive;
	}

	@Override
	public SearchType getSearchType() {
		return this.searchType;
	}

	@Override
	public boolean createHistoryEntry() {
		return this.createHistoryEntry;
	}

	@Override
	public boolean shouldUpdateView() {
		return this.updateView;
	}

	@Override
	public boolean shouldReadApiState() {
		return this.readApiState;
	}

	@Override
	public boolean shouldReadAllEntries() {
		return this.readAllEntries;
	}

	@Override
	public void setQuery(final String query) {
		this.query = query;
	}

	@Override
	public void setSearchType(final SearchType searchType) {
		this.searchType = searchType;
	}

	@Override
	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public void setAndSearchActice(final boolean andSearchActive) {
		this.andSearchActive = andSearchActive;
	}

	@Override
	public void setCreateHistory(final boolean createHistory) {
		this.createHistoryEntry = createHistory;
	}

	@Override
	public void setUpdateView(final boolean updateView) {
		this.updateView = updateView;
	}

	@Override
	public void setReadApiState(final boolean readApiState) {
		this.readApiState = readApiState;
	}

	@Override
	public void setReadAllEntries(final boolean readAllEntries) {
		this.readAllEntries = readAllEntries;
	}

}
