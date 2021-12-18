package com.devepos.adt.saat.internal.search;

/**
 * Possible types for {@link INamedItemService}
 *
 * @author stockbal
 */
public enum NamedItemType {
  ANNOTATION("annotation"),
  ANNOTATION_VALUE("annotationvalue"),
  DB_ENTITY("dbentity"),
  CDS_FIELD("cdsfield"),
  TABLE_FIELD("tablefield"),
  TABLE_TYPE("tabletype"),
  API_STATE("releasestate"),
  CDS_TYPE("cdstype"),
  CDS_EXTENSION("cdsextension"),
  CLASS_TYPE("classtype"),
  ABAP_CLASS_LANGUAGE("abaplanguage"),
  CLASS_CATEGORY("classcategory"),
  CLASS_FLAG("classflag"),
  TABLE_DELIVERY_CLASS("tabledeliveryclass");

  private String discoveryTerm;

  NamedItemType(final String discoveryTerm) {
    this.discoveryTerm = discoveryTerm;
  }

  /**
   * Returns the discovery term of this named item type
   *
   * @return the discovery term of this named item type
   */
  public String getDiscoveryTerm() {
    return discoveryTerm;
  }
}