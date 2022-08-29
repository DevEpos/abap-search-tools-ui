package com.devepos.adt.saat.internal.cdsanalysis;

import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Mode for CDS Field Analysis
 * 
 * @author Ludwig Stockbauer-Muhr
 */
public enum FieldAnalysisType {
  TOP_DOWN(Messages.FieldAnalysisType_topDownName_xlbl, "topDown"), // $NON-NLS-2$
  BOTTOM_UP(Messages.FieldAnalysisType_bottomUpName_xlbl, "bottomUp"); // $NON-NLS-2$

  private String label;
  private String prefKey;

  FieldAnalysisType(final String label, String prefKey) {
    this.label = label;
    this.prefKey = prefKey;
  }

  public String getPrefKey() {
    return prefKey;
  }

  @Override
  public String toString() {
    return label;
  }

}
