package com.devepos.adt.saat.internal.cdsanalysis;

import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Type of a CDS analysis
 *
 * @author Ludwig Stockbauer-Muhr
 */
public enum CdsAnalysisType {
  /**
   * Top-Down analysis which displays the direct usages in the SELECT clause of
   * CDS view
   */
  TOP_DOWN(Messages.CdsAnalysisType_topDownAnalysis_xlbl),
  /**
   * Where-Used analysis for a database object (table or (CDS-)view)
   */
  WHERE_USED(Messages.CdsAnalysisType_whereUsedInCdsAnalysis_xlbl),
  /**
   * Aggregated analysis of the dependency tree of a CDS view
   */
  DEPENDENCY_TREE_USAGES(Messages.CdsAnalysisType_usedEntitiesAnalysis_xlbl),
  /**
   * Field analysis of database object (table or (CDS-)view)
   */
  FIELD_ANALYSIS(Messages.CdsAnalysisType_fieldAnalysis_xlbl);

  private String label;

  CdsAnalysisType(final String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

}