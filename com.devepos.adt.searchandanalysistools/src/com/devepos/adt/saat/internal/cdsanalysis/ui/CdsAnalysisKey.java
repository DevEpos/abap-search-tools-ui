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
		int result = this.uri == null ? 0 : this.uri.hashCode();
		result = 31 * result + (this.mode == null ? 0 : this.mode.hashCode());
		result = 31 * result + (this.destinationId == null ? 0 : this.destinationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CdsAnalysisKey)) {
			return false;
		}
		final CdsAnalysisKey other = (CdsAnalysisKey) obj;
		return this.uri.equalsIgnoreCase(other.uri) && this.destinationId.equals(other.destinationId) && this.mode == other.mode;
	}
}
