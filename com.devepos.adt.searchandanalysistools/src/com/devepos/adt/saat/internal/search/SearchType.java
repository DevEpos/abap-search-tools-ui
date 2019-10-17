package com.devepos.adt.saat.internal.search;

public enum SearchType {
	// TODO: not yet supported by any backend system
//	ALL("All", "all"),
	CDS_VIEW("CDS View", "cds"),
	DB_TABLE_VIEW("Database Table/View", "dbtabview");

	private final String name;
	private final String uriTerm;

	private SearchType(final String description, final String uriTerm) {
		this.name = description;
		this.uriTerm = uriTerm;
	}

	/**
	 * Creates key/value pair array from enum name and description
	 *
	 * @return key/value pair array from enum name and description
	 */
	public static String[][] toNamesAndKeys() {
		final SearchType[] types = SearchType.values();
		final String[][] keyValue = new String[types.length][2];
		for (int i = 0; i < types.length; i++) {
			keyValue[i][0] = types[i].toString();
			keyValue[i][1] = types[i].name();
		}
		return keyValue;
	}

	public String getUriTerm() {
		return this.uriTerm;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
