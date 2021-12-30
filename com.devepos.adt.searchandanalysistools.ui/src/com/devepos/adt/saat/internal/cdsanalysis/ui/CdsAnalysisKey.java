package com.devepos.adt.saat.internal.cdsanalysis.ui;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;

/**
 * Unique key that identifies a certain analysis
 *
 * @author stockbal
 */
public class CdsAnalysisKey {
  public String uri;
  public String destinationId;
  public CdsAnalysisType mode;

  public CdsAnalysisKey(final CdsAnalysisType mode, final String uri, final String destinationId) {
    this.uri = uri;
    this.destinationId = destinationId;
    this.mode = mode;
  }

  @Override
  public int hashCode() {
    int result = uri == null ? 0 : uri.hashCode();
    result = 31 * result + (mode == null ? 0 : mode.hashCode());
    result = 31 * result + (destinationId == null ? 0 : destinationId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof CdsAnalysisKey)) {
      return false;
    }
    final CdsAnalysisKey other = (CdsAnalysisKey) obj;
    return uri.equalsIgnoreCase(other.uri) && destinationId.equals(other.destinationId)
        && mode == other.mode;
  }
}
