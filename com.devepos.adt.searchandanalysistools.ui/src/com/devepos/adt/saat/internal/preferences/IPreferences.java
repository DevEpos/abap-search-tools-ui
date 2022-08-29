package com.devepos.adt.saat.internal.preferences;

import com.devepos.adt.saat.internal.search.SearchType;

/**
 * Constants for Plugin preferences
 *
 * @author stockbal
 */
public interface IPreferences {

  /**
   * Id to the main preference page of this plugin
   */
  String MAIN_PREF_PAGE_ID = "com.devepos.adt.saat.ui.internal.MainPreferencePage"; //$NON-NLS-1$
  /**
   * Id to the object search preference page
   */
  String OBJECT_SEARCH_PREF_PAGE_ID = "com.devepos.adt.saat.ui.prefs.ObjectSearchPreferences"; //$NON-NLS-1$
  /**
   * Id to the CDS analysis preference page
   */
  String CDS_ANALYSIS_PREF_PAGE_ID = "com.devepos.adt.saat.ui.prefs.CdsAnalysisPreferences"; //$NON-NLS-1$
  /**
   * Boolean Setting for Object Search.<br>
   * if <code>true</code> the cursor will point to the end of entered search
   * string instead of selecting the whole text
   */
  String CURSOR_AT_END_OF_SEARCH_INPUT = "com.devepos.adt.saat.objectsearch.cursorAtEndOfSearchInput"; //$NON-NLS-1$
  /**
   * Boolean Setting for Object Search.<br>
   * if <code>true</code> the query opened from the Search View will be
   * overwritten upon running search
   */
  String OVERWRITE_OPENED_SEARCH_QUERY = "com.devepos.adt.saat.objectsearch.overwriteOpenedQuery"; //$NON-NLS-1$
  /**
   * Integer Setting for Object Search.<br>
   * It will set the default value of the maximum number of results in the Object
   * Search Page
   */
  String MAX_SEARCH_RESULTS = "com.devepos.adt.saat.objectsearch.maxSearchResults"; //$NON-NLS-1$
  /**
   * Boolean Setting for the Detail Result of a CDS View in the Search View.<br>
   * If <code>true</code> the name of th association (e.g. _Language) will also be
   * schon for a given association
   */
  String SHOW_FULL_ASSOCIATION_NAME = "com.devepos.adt.saat.objectsearch.showAssocName"; //$NON-NLS-1$
  /**
   * The default {@link SearchType} for the object search
   */
  String DEFAULT_SEARCH_TYPE = "com.devepos.adt.saat.objectsearch.defaultSearchType"; //$NON-NLS-1$
  /**
   * Maximum number of history entries in the CDS Analyzer view
   */
  String MAX_CDS_ANALYZER_HISTORY = "com.devepos.adt.saat.cdsanalyzer.maxHistorySize"; //$NON-NLS-1$
  /**
   * Sets focus to search type field in object page, otherwise on the first input
   * field
   */
  String FOCUS_ON_SEARCH_TYPE = "com.devepos.adt.saat.objectsearch.focusOnSearchType"; //$NON-NLS-1$
  /**
   * Boolean preferences to use current text selection in object name field in
   * search dialog
   */
  String TAKE_TEXT_SELECTION_INTO_SEARCH = "com.devepos.adt.saat.objectsearch.considerTextSelection"; //$NON-NLS-1$

}
