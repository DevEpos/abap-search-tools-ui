package com.devepos.adt.saat;

/**
 * Constants for the commands in the DB Browser tools Plugin
 *
 * @author stockbal
 */
public interface ICommandConstants {
	/**
	 * Command id to open an ADT object in the SAP GUI Transaction "DB Browser"
	 */
	String OPEN_IN_DB_BROWSER = "com.devepos.adt.saat.openindbbrowser";
	/**
	 * Command id to open the SAP GUI Transaction "DB Browser"
	 */
	String OPEN_DB_BROWSER = "com.devepos.adt.saat.opendbbrowser";
	/**
	 * Command id to show the view "DB Object Explorer"
	 */
	String SWOW_DB_OBJECT_EXPLORER = "com.devepos.adt.saat.showDBObjectSearch";
	/**
	 * Command id to show an object in the "DB Object Explorer"
	 */
	String SHOW_IN_DB_OBJECT_EXPLORER = "com.devepos.adt.saat.showInObjectSearch";
	/**
	 * Command id to execute the CDS Top-Down Analysis
	 */
	String CDS_TOP_DOWN_ANALYSIS = "com.devepos.adt.saat.performCdsTopDown";
	/**
	 * Command id to execute the Where-Used in CDS Analysis
	 */
	String WHERE_USED_IN_CDS_ANALYSIS = "com.devepos.adt.saat.performWhereUsedInCds";
	/**
	 * Command id to execute the Used entities Analysis
	 */
	String USED_ENTITIES_ANALYSIS = "com.devepos.adt.saat.performCdsUsedEntities";
	/**
	 * Command id to execute the Field Analysis
	 */
	String FIELD_ANALYSIS = "com.devepos.adt.saat.performFieldAnalysis";
}
