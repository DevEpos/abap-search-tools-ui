package com.devepos.adt.saat.internal.cdsanalysis;

/**
 * Type of a CDS analysis
 *
 * @author stockbal
 */
public enum CdsAnalysisType {
  /**
   * Top-Down analysis which displays the direct usages in the SELECT clause of
   * CDS view
   */
  TOP_DOWN,
  /**
   * Where-Used analysis for a database object (table or (CDS-)view)
   */
  WHERE_USED,
  /**
   * Aggregated analysis of the dependency tree of a CDS view
   */
  DEPENDENCY_TREE_USAGES,
  /**
   * Field analysis of database object (table or (CDS-)view)
   */
  FIELD_ANALYSIS
}