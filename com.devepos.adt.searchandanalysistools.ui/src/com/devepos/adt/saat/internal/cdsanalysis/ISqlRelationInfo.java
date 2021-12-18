package com.devepos.adt.saat.internal.cdsanalysis;

/**
 * Relational SQL information
 *
 * @author stockbal
 */
public interface ISqlRelationInfo {
  /**
   * Returns the defined SQL relation or <code>null</code>
   *
   * @return
   */
  String getRelation();

  /**
   * Returns the type of SQL relation
   *
   * @return
   */
  String getType();

  /**
   * Returns the alias name of node in the given SQL relation
   *
   * @return
   */
  String getAliasName();

}
