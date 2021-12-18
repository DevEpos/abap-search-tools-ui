package com.devepos.adt.saat.internal.cdsanalysis;

/**
 * Holds information about the used entity in the dependency tree of a CDS view
 *
 * @author stockbal
 */
public interface ICdsEntityUsageInfo {
  /**
   * Returns the number of how often an entity occurs in the dependency tree
   *
   * @return
   */
  int getOccurrence();

  /**
   * Returns the number of the used entities for a given entity in the dependency
   * tree
   *
   * @return
   */
  int getUsedEntitiesCount();

  /**
   * Returns the number of the used joins for a given entity in the dependency
   * tree
   *
   * @return
   */
  int getUsedJoinCount();

  /**
   * Returns the number of the used Unions for a given entity in the dependency
   * tree
   *
   * @return
   */
  int getUsedUnionCount();
}
