package com.devepos.adt.saat.internal.search.favorites;

import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.search.favorites.IObjectSearchFavorite;

/**
 * Represents the
 *
 * @author stockbal
 */
public class ObjectSearchFavorite implements IObjectSearchFavorite {

	private String query;
	private String description;
	private SearchType searchType;
	private String destinationId;
	private boolean useAndSearch;
	private boolean projectIndependent;

	public ObjectSearchFavorite() {
	}

	public ObjectSearchFavorite(final String query, final String description, final SearchType searchType,
		final String destinationId, final boolean useAndSearch) {
		this.query = query;
		this.description = description;
		this.searchType = searchType;
		this.destinationId = destinationId;
		this.useAndSearch = useAndSearch;
		this.projectIndependent = destinationId == null;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public SearchType getSearchType() {
		return this.searchType;
	}

	@Override
	public String getDestinationId() {
		return this.destinationId;
	}

	@Override
	public boolean isProjectIndependent() {
		return this.projectIndependent;
	}

	@Override
	public boolean isAndSearchActive() {
		return this.useAndSearch;
	}

	@Override
	public void setQuery(final String query) {
		this.query = query;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
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
	public void setProjectIndependent(final boolean isProjectIndependent) {
		this.projectIndependent = isProjectIndependent;
	}

	@Override
	public void setAndSearchActive(final boolean isAndSearchActive) {
		this.useAndSearch = isAndSearchActive;
	}

	@Override
	public String toString() {
		String destinationId = null;
		if (isProjectIndependent()) {
			destinationId = "[?]";
		} else {
			final String[] destinationData = getDestinationId().split("_");
			destinationId = String.format("[%s-%s]", destinationData[0], destinationData[1]);
		}
		return String.format("%s %s - '%s'", destinationId, getDescription(), getQuery());
	}

}
