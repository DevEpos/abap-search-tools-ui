package com.devepos.adt.saat.internal.search;

public enum SearchType {
	CDS_VIEW("CDS View", "C"),
	DB_TABLE_VIEW("Database Table/View", "D");

	private final String name;
	private final String id;

	private SearchType(final String description, final String id) {
		this.name = description;
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
