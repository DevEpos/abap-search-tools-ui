package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;

/**
 * Registry which is used to find a specific page for the CDS Analyzer
 *
 * @author stockbal
 */
public class CdsAnalysisConfigRegistry {

  private final Map<CdsAnalysisType, CdsAnalysisPage<?>> pageMap;
  private final CdsAnalysisView viewPart;

  public CdsAnalysisConfigRegistry(final CdsAnalysisView viewPart) {
    pageMap = new HashMap<>();
    this.viewPart = viewPart;
  }

  /**
   * Returns the analysis page for the given analysis type
   *
   * @param type the type for which the analysis page should be returned
   * @return the found page
   */
  public CdsAnalysisPage<?> findPageForType(final CdsAnalysisType type) {
    CdsAnalysisPage<?> page = pageMap.get(type);
    if (page == null) {
      page = createPage(type);
      Assert.isNotNull(page);
      pageMap.put(type, page);
    }
    return page;
  }

  private CdsAnalysisPage<?> createPage(final CdsAnalysisType type) {
    switch (type) {
    case DEPENDENCY_TREE_USAGES:
      return new CdsUsageAnalysisView(viewPart);
    case FIELD_ANALYSIS:
      return new FieldAnalysisView(viewPart);
    case TOP_DOWN:
      return new CdsTopDownAnalysisView(viewPart);
    case WHERE_USED:
      return new WhereUsedInCdsAnalysisView(viewPart);
    default:
      return null;
    }
  }
}
