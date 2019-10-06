package com.devepos.adt.saat.internal.search;

/**
 * Query parameters for ADT Rest Request
 *
 * @author stockbal
 */
public enum QueryParameterName {
	/**
	 * Identifies the object to be searched for
	 */
	QUERY("QUERY", "query"),
	/**
	 * Identifies the OBJECT_TYPE parameter e.g. <em>C</em> for <em>CDS View</em>
	 */
	OBJECT_TYPE("", "objectType"),
	/**
	 * Identifies the OWNER parameter
	 */
	OWNER("OWNER", "userName"),
	/**
	 * Identifies the FROM parameter
	 */
	SELECT_SOURCE_IN("FROM", "selectSourceIn"),
	/**
	 * Identifies the ASSOC parameter
	 */
	ASSOCIATED_IN("ASSOC", "associatedIn"),
	/**
	 * Identifies the API parameter
	 */
	RELEASE_STATE("API", "releaseState"),
	/**
	 * Identifies the DESC parameter
	 */
	DESCRIPTION("DESC", "description"),
	/**
	 * Identifies the TYPE parameter
	 */
	TYPE("TYPE", "type"),
	/**
	 * Identifies the PACKAGE parameter
	 */
	PACKAGE_NAME("PACKAGE", "packageName"),
	/**
	 * Identifies the ANNO parameter
	 */
	ANNOTATION("ANNO", "annotation"),
	/**
	 * Identifies the FIELD parameter
	 */
	FIELD_NAME("FIELD", "fieldName"),
	/**
	 * Identifies the PARAMS parameter
	 */
	HAS_PARAMS("PARAMS", "hasParams"),
	/**
	 * Hidden parameter to toggle the logical AND for combination of filter options
	 */
	AND_FILTER("ANDOPTION", "useAndForFilters"),
	/**
	 * Identifies the MAXROWS parameter
	 */
	MAX_ROWS("MAXROWS", "maxRows"),
	/**
	 * Identifies the EXTBY parameter
	 */
	EXTENDED_BY("EXTBY", "extendedBy"),
	/**
	 * If this parameter has the value <code>X</code> the API state will also be
	 * determined
	 */
	WITH_API_STATE("WITHAPISTATE", "withApiState"),
	/**
	 * If this parameter has the value <code>X</code> all objects that match the
	 * query will be returned
	 */
	GET_ALL_RESULTS("GETALL", "getAllResults"),
	/**
	 * If <code>true</code> this parameter control whether the package hierarchy of
	 * the found ADT objects will get determined
	 */
	WITH_PACKAGE_HIERARCHY("WITHPACKAGEHIERARCHY", "withPackageHierarchy");

	private String stringForm;
	private String key;

	private QueryParameterName(final String key, final String stringForm) {
		this.stringForm = stringForm;
		this.key = key;
	}

	/**
	 * Retrieves the upper case key of the query parameter
	 *
	 * @return the upper case string identifier of the parameter
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Retrieves the lower case key of the query parameter
	 *
	 * @return the lower case string identifier of the parameter
	 */
	public String getLowerCaseKey() {
		return this.key.toLowerCase();
	}

	@Override
	public String toString() {
		return this.stringForm;
	}
}