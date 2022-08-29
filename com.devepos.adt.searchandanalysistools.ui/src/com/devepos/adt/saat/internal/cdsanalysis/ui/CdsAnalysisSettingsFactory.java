package com.devepos.adt.saat.internal.cdsanalysis.ui;

import com.devepos.adt.saat.internal.cdsanalysis.ICdsFieldAnalysisSettings;
import com.devepos.adt.saat.internal.cdsanalysis.IWhereUsedInCdsSettings;

public class CdsAnalysisSettingsFactory {

  /**
   * Creates new instance of settings for CDS Top-Down analysis
   */
  public static ICdsTopDownSettingsUi createCdsTopDownSettings() {
    return new CdsTopDownSettings();
  }

  /**
   * Creates new instance of settings for Where-Used in CDS Analysis
   */
  public static IWhereUsedInCdsSettings createWhereUsedInCdsSettings() {
    return new WhereUsedInCdsSettings();
  }

  /**
   * Creates new instance of settings for CDS Field Analysis
   */
  public static ICdsFieldAnalysisSettings createFieldAnalysisSettings() {
    return new CdsFieldAnalysisSettings();
  }

  private static class CdsTopDownSettings implements ICdsTopDownSettingsUi {
    private boolean loadAssociations;
    private boolean showDescriptions;
    private boolean showAliasNames;

    @Override
    public void setLoadAssociations(boolean loadAssociations) {
      this.loadAssociations = loadAssociations;
    }

    @Override
    public boolean isLoadAssociations() {
      return loadAssociations;
    }

    @Override
    public Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    public void setShowDescriptions(boolean showDescriptions) {
      this.showDescriptions = showDescriptions;
    }

    @Override
    public boolean isShowDescriptions() {
      return showDescriptions;
    }

    @Override
    public void setShowAliasNames(boolean showAliasNames) {
      this.showAliasNames = showAliasNames;
    }

    @Override
    public boolean isShowAliasNames() {
      return showAliasNames;
    }

  }

  private static class WhereUsedInCdsSettings implements IWhereUsedInCdsSettings {
    private boolean searchFrom;
    private boolean searchAssociations;
    private boolean localAssociationsOnly;
    private boolean releasedUsagesOnly;

    @Override
    public void setSearchFromPart(boolean searchFrom) {
      this.searchFrom = searchFrom;
    }

    @Override
    public boolean isSearchFromPart() {
      return searchFrom;
    }

    @Override
    public void setSearchAssociation(boolean searchAssociations) {
      this.searchAssociations = searchAssociations;
    }

    @Override
    public boolean isSearchAssociations() {
      return searchAssociations;
    }

    @Override
    public void setLocalAssociationsOnly(boolean localAssociationsOnly) {
      this.localAssociationsOnly = localAssociationsOnly;
    }

    @Override
    public boolean isLocalAssociationsOnly() {
      return localAssociationsOnly;
    }

    @Override
    public void setReleasedUsagesOnly(boolean releasedUsagesOnly) {
      this.releasedUsagesOnly = releasedUsagesOnly;
    }

    @Override
    public boolean isReleasedUsagesOnly() {
      return releasedUsagesOnly;
    }
  }

  private static class CdsFieldAnalysisSettings implements ICdsFieldAnalysisSettings {
    private boolean topDown;
    private boolean searchInDbViews;
    private boolean searchInCalcFields;

    @Override
    public void setTopDown(boolean topDown) {
      this.topDown = topDown;
    }

    @Override
    public boolean isTopDown() {
      return topDown;
    }

    @Override
    public void setSearchInCalcFields(boolean searchInCalcFields) {
      this.searchInCalcFields = searchInCalcFields;
    }

    @Override
    public boolean isSearchInCalcFields() {
      return searchInCalcFields;
    }

    @Override
    public void setSearchInDatabaseViews(boolean searchInDbViews) {
      this.searchInDbViews = searchInDbViews;
    }

    @Override
    public boolean isSearchInDatabaseViews() {
      return searchInDbViews;
    }

  }
}
