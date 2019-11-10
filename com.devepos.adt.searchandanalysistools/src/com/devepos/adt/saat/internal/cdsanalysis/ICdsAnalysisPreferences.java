package com.devepos.adt.saat.internal.cdsanalysis;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisView;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsTopDownAnalysisView;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsUsageAnalysisView;

/**
 * Preferences constants for CDS Analysis
 *
 * @see    {@link CdsAnalysisView}
 * @see    {@link CdsTopDownAnalysisView}
 * @see    {@link CdsUsageAnalysisView}
 * @author stockbal
 */
public interface ICdsAnalysisPreferences {
	/**
	 * Preference for loading Associations in CDS Top down analysis
	 */
	String TOP_DOWN_LOAD_ASSOCIATIONS = "com.devepos.adt.saat.cdstopdownanalysis.loadAssociations"; //$NON-NLS-1$
	/**
	 * Preference for showing only usages in released CDS Views
	 */
	String WHERE_USED_ONLY_RELEASED_USAGES = "com.devepos.adt.saat.whereusedincds.onlyReleasedUsages"; //$NON-NLS-1$
}
