package com.devepos.adt.saat.internal.search;

/**
 * Query parameters for ADT Rest Request
 *
 * @author stockbal
 */
public enum QueryParameterName {
  /**
   * The name for an object to be searched. Can include patterns
   */
  OBJECT_NAME("", "objectName"),
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
   * Identifies the PARAM parameter which can be used to find CDS Views with a
   * given parameter
   */
  CDS_PARAMETER("PARAM", "param"),
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
  WITH_PACKAGE_HIERARCHY("WITHPACKAGEHIERARCHY", "withPackageHierarchy"),
  /**
   * If <code>true</code> this parameter controls whether the search will only
   * consider association usages if they are defined locally in the CDS view
   */
  LOCAL_DECLARED_ASSOC_ONLY("LOCALDECLAREDASSOCONLY", "localDeclaredAssocOnly"),
  /**
   * Search for classes with a given global friend
   */
  FRIEND("FRIEND", "friend"),
  /**
   * Search for classes with a certain category (e.g. "Exception")
   */
  CATEGORY("CAT", "category"),
  /**
   * Search for classes with a given ABAP Language (e.g. "Key User")
   */
  ABAP_LANGUAGE("LANG", "abapLanguage"),
  /**
   * Search for classes/interfaces that use a certain interface
   */
  INTERFACE("INTF", "interface"),
  /**
   * Search for Classes that have a certain super class
   */
  SUPER_TYPE("SUPER", "superType"),
  /**
   * Search for Classes that have a certain attribute
   */
  ATTRIBUTE("ATTR", "attribute"),
  /**
   * Search for Classes that have a certain method
   */
  METHOD("METH", "method"),
  /**
   * Search classes for a set of properties
   */
  FLAG("FLAG", "flag"),
  /**
   * Search tables by delivery class
   */
  DELIVERY_CLASS("DCLASS", "deliveryClass");

  private String stringForm;
  private String key;

  QueryParameterName(final String key, final String stringForm) {
    this.stringForm = stringForm;
    this.key = key;
  }

  /**
   * Retrieves the upper case key of the query parameter
   *
   * @return the upper case string identifier of the parameter
   */
  public String getKey() {
    return key;
  }

  /**
   * Retrieves the lower case key of the query parameter
   *
   * @return the lower case string identifier of the parameter
   */
  public String getLowerCaseKey() {
    return key.toLowerCase();
  }

  @Override
  public String toString() {
    return stringForm;
  }
}