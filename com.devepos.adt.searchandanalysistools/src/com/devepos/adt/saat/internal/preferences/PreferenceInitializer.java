package com.devepos.adt.saat.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.search.SearchType;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		store.setDefault(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT, true);
		store.setDefault(IPreferences.OVERWRITE_OPENED_SEARCH_QUERY, false);
		store.setDefault(IPreferences.DEFAULT_SEARCH_TYPE, SearchType.CDS_VIEW.name());
		store.setDefault(IPreferences.MAX_SEARCH_RESULTS, 50);
		store.setDefault(IPreferences.SHOW_FULL_ASSOCIATION_NAME, true);
	}

}
