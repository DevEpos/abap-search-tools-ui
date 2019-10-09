package com.devepos.adt.saat.internal.preferences;

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

}
