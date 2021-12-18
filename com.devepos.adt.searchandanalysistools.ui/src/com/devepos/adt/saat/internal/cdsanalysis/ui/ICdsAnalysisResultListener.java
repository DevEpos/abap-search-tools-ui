package com.devepos.adt.saat.internal.cdsanalysis.ui;

/**
 * A listener for changes in a result of a CDS analysis
 *
 * @author stockbal
 */
public interface ICdsAnalysisResultListener {

  /**
   * Triggers a label update in the corresponding analysis page/view
   */
  void updateLabel();
}
