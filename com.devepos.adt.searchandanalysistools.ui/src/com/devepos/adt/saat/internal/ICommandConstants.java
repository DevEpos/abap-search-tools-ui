package com.devepos.adt.saat.internal;

/**
 * Constants for the commands in the DB Browser tools Plugin
 *
 * @author stockbal
 */
public interface ICommandConstants {
  /**
   * Command id to open an ADT object in the SAP GUI Transaction "DB Browser"
   */
  String OPEN_IN_DB_BROWSER = "com.devepos.adt.saat.dbbrowser.command.openInDbBrowser";
  /**
   * Command id to open the SAP GUI Transaction "DB Browser"
   */
  String OPEN_DB_BROWSER = "com.devepos.adt.saat.dbbrowser.command.openDbBrowser";
  /**
   * Command id to execute the CDS Top-Down Analysis
   */
  String CDS_TOP_DOWN_ANALYSIS = "com.devepos.adt.saat.cdsanalysis.command.performCdsTopDown";
  /**
   * Command id to execute the Where-Used in CDS Analysis
   */
  String WHERE_USED_IN_CDS_ANALYSIS = "com.devepos.adt.saat.cdsanalysis.command.performWhereUsedInCds";
  /**
   * Command id to execute the Used entities Analysis
   */
  String USED_ENTITIES_ANALYSIS = "com.devepos.adt.saat.cdsanalysis.command.performCdsUsedEntities";
  /**
   * Command id to execute the Field Analysis
   */
  String FIELD_ANALYSIS = "com.devepos.adt.saat.cdsanalysis.command.performFieldAnalysis";
  /**
   * Command id to run a new CDS analysis via dialog selection
   */
  String RUN_CDS_ANALYSIS = "com.devepos.adt.saat.cdsanalysis.command.runCdsAnalysis";
}
