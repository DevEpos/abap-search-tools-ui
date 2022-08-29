package com.devepos.adt.saat.internal.cdsanalysis;

public class CdsAnalysisServiceFactory {
  /**
   * Creates new instance of a CDS analysis service
   */
  public static ICdsAnalysisService createCdsAnalysisService() {
    return new CdsAnalysisService();
  }
}
