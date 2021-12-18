package com.devepos.adt.saat.internal.search;

import java.util.List;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;

/**
 * Wrapper for Object search result
 *
 * @author stockbal
 */
public class ObjectSearchResult {
  private List<IAdtObjectReferenceElementInfo> rawResult;
  private List<IAdtObjectReferenceElementInfo> packageResult;

  /**
   * @return the packageResult
   */
  public List<IAdtObjectReferenceElementInfo> getPackageResult() {
    return packageResult;
  }

  /**
   * @param packageResult the packageResult to set
   */
  public void setPackageResult(final List<IAdtObjectReferenceElementInfo> packageResult) {
    this.packageResult = packageResult;
  }

  /**
   * @return the rawResult
   */
  public List<IAdtObjectReferenceElementInfo> getRawResult() {
    return rawResult;
  }

  /**
   * @param rawResult the rawResult to set
   */
  public void setRawResult(final List<IAdtObjectReferenceElementInfo> rawResult) {
    this.rawResult = rawResult;
  }

}
