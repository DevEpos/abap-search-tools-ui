package com.devepos.adt.saat.internal.cdsanalysis.ui;

import com.devepos.adt.saat.internal.cdsanalysis.ICdsTopDownSettings;

/**
 * UI settings enhancement for CDS Top down analysis
 *
 * @author Ludwig Stockbauer-Muhr
 *
 */
public interface ICdsTopDownSettingsUi extends ICdsTopDownSettings {

  /**
   * Sets whether descriptions for the entities shall be shown or not
   *
   * @param showDescriptions if {@code true} descriptions are shown
   */
  void setShowDescriptions(boolean showDescriptions);

  /**
   * @return {@code true} if descriptions are shown
   */
  boolean isShowDescriptions();

  /**
   * Sets whether alias names shall be shown
   *
   * @param showAliasNames if {@code true} alias names are shown
   */
  void setShowAliasNames(boolean showAliasNames);

  /**
   * @return {@code true} if alias names are shown
   */
  boolean isShowAliasNames();
}
