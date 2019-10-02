package com.devepos.adt.saat.internal.search.ui;

import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;

/**
 * Search request for an Object Search. <br>
 * Holds all the data used in the object search query
 *
 * @author stockbal
 */
public class ObjectSearchRequest {
	private final String query;
	private String searchTerm;
	private String destinationId;
	private boolean andSearchActive;
	private SearchType searchType;
	private boolean readApiState;
	private boolean readAllEntries;
	private String parametersString;
	private Map<String, Object> parameters;
	private int maxResults;
	private IAbapProjectProvider projectProvider;

	public ObjectSearchRequest() {
		this.query = null;
		this.searchType = SearchType.CDS_VIEW;
		this.destinationId = null;
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
		return String.format("'%s' (%s) [%s-%s]", getQuery(), getSearchType(), destination[0], destination[1]);
	}

	public String getQuery() {
		String query = this.searchTerm == null || this.searchTerm.isEmpty() ? "" : this.searchTerm + " ";
		query += this.parametersString != null && !this.parametersString.isEmpty() ? this.parametersString : "";
		return query;
	}

	public String getDestinationId() {
		return this.projectProvider != null ? this.projectProvider.getDestinationId() : "";
	}

	public boolean isAndSearchActive() {
		return this.andSearchActive;
	}

	public SearchType getSearchType() {
		return this.searchType;
	}

	public boolean shouldReadApiState() {
		return this.readApiState;
	}

	public boolean shouldReadAllEntries() {
		return this.readAllEntries;
	}

	public void setSearchType(final SearchType searchType) {
		this.searchType = searchType;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setAndSearchActice(final boolean andSearchActive) {
		this.andSearchActive = andSearchActive;
	}

	public void setReadApiState(final boolean readApiState) {
		this.readApiState = readApiState;
	}

	public void setReadAllEntries(final boolean readAllEntries) {
		this.readAllEntries = readAllEntries;
	}

	public Map<String, Object> getParameters() {
		return this.parameters != null ? this.parameters : new HashMap<>();
	}

	public void setParameters(final Map<String, Object> parameters, final String parametersString) {
		this.parameters = parameters;
		this.parametersString = parametersString;
	}

	public void setMaxResults(final int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMaxResults() {
		return this.maxResults;
	}

	public String getSearchTerm() {
		return this.searchTerm != null ? this.searchTerm : "";
	}

	public void setSearchTerm(final String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public void setProjectProvider(final IAbapProjectProvider projectProvider) {
		this.projectProvider = projectProvider;
	}

	public IAbapProjectProvider getProjectProvider() {
		return this.projectProvider;
	}

	public String getParametersString() {
		return this.parametersString != null ? this.parametersString : "";
	}

}
