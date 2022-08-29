package com.devepos.adt.saat.internal.cdsanalysis;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisView;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsTopDownAnalysisView;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsUsageAnalysisView;

/**
 * Preferences constants for CDS Analysis
 *
 * @see {@link CdsAnalysisView}
 * @see {@link CdsTopDownAnalysisView}
 * @see {@link CdsUsageAnalysisView}
 * @author Ludwig Stockbauer-Muhr
 */
public interface ICdsAnalysisPreferences {
  /**
   * Preference for showing descriptions of results of top down analysis
   */
  String TOP_DOWN_SHOW_DESCRIPTIONS = "com.devepos.adt.saat.cdstopdownanalysis.showDescriptions"; //$NON-NLS-1$
  /**
   * Preference for showing alias names of results of top down analysis
   */
  String TOP_DOWN_SHOW_ALIAS_NAMES = "com.devepos.adt.saat.cdstopdownanalysis.showAliasNames"; //$NON-NLS-1$
  /**
   * Preference for loading Associations in CDS Top down analysis
   */
  String TOP_DOWN_LOAD_ASSOCIATIONS = "com.devepos.adt.saat.cdstopdownanalysis.loadAssociations"; //$NON-NLS-1$
  /**
   * Preference for showing only usages in released CDS Views
   */
  String WHERE_USED_ONLY_RELEASED_USAGES = "com.devepos.adt.saat.whereusedincds.onlyReleasedUsages"; //$NON-NLS-1$
  /**
   * Preference for searching references in select parts
   */
  String WHERE_USED_USES_IN_SELECT = "com.devepos.adt.saat.whereusedincds.showReferencesInSelectPartOfCds"; //$NON-NLS-1$
  /**
   * Preference for searching references in associations
   */
  String WHERE_USED_USES_IN_ASSOC = "com.devepos.adt.saat.whereusedincds.showReferencesInAssocPartOfCds"; //$NON-NLS-1$
  /**
   * Preference for searching references only in local associations
   */
  String WHERE_USED_LOCAL_ASSOCIATIONS_ONLY = "com.devepos.adt.saat.whereusedincds.onlyLocalDefinedAssociation"; //$NON-NLS-1$
  /**
   * Preference for searching database views during field analysis bottom up
   */
  String FIELD_ANALYSIS_SEARCH_IN_DB_VIEWS = "com.devepos.adt.saat.fieldanalysis.searchDbViewUsages"; //$NON-NLS-1$
  /**
   * Preference for field analysis mode (top-down or bottom-up)
   */
  String FIELD_ANALYSIS_ANALYSIS_DIRECTION = "com.devepos.adt.saat.fieldanalysis.analysisDirection"; //$NON-NLS-1$
  /**
   * Preference for searching in calculated fields during bottom up direction
   */
  String FIELD_ANALYSIS_SEARCH_IN_CALC_FIELDS = "com.devepos.adt.saat.fieldanalysis.bottomUpSearchInCalcFields"; //$NON-NLS-1$
}
