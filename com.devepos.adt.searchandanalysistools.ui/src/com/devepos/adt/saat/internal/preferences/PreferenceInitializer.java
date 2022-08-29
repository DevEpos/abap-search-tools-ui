package com.devepos.adt.saat.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.FieldAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisPreferences;
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
    store.setDefault(IPreferences.FOCUS_ON_SEARCH_TYPE, false);
    store.setDefault(IPreferences.OVERWRITE_OPENED_SEARCH_QUERY, false);
    store.setDefault(IPreferences.DEFAULT_SEARCH_TYPE, SearchType.CDS_VIEW.name());
    store.setDefault(IPreferences.MAX_SEARCH_RESULTS, 50);
    store.setDefault(IPreferences.SHOW_FULL_ASSOCIATION_NAME, true);

    store.setDefault(ICdsAnalysisPreferences.TOP_DOWN_LOAD_ASSOCIATIONS, false);
    store.setDefault(ICdsAnalysisPreferences.TOP_DOWN_SHOW_ALIAS_NAMES, true);
    store.setDefault(ICdsAnalysisPreferences.TOP_DOWN_SHOW_DESCRIPTIONS, true);

    store.setDefault(ICdsAnalysisPreferences.WHERE_USED_USES_IN_SELECT, true);
    store.setDefault(ICdsAnalysisPreferences.WHERE_USED_USES_IN_ASSOC, false);
    store.setDefault(ICdsAnalysisPreferences.WHERE_USED_LOCAL_ASSOCIATIONS_ONLY, false);
    store.setDefault(ICdsAnalysisPreferences.WHERE_USED_ONLY_RELEASED_USAGES, false);

    store.setDefault(ICdsAnalysisPreferences.FIELD_ANALYSIS_ANALYSIS_DIRECTION,
        FieldAnalysisType.TOP_DOWN.getPrefKey());
    store.setDefault(ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_DB_VIEWS, false);
    store.setDefault(ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_CALC_FIELDS, false);
  }

}
