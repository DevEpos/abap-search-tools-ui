package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.LinkedList;

/**
 * This class manages all open Views of the CDS Analyzer.<br>
 * <strong>Note:</strong> Currently only one active view is supported
 *
 * @author stockbal
 */
public class CdsAnalysisViewManager {

	private final LinkedList<CdsAnalysisView> openViews;
	private static CdsAnalysisViewManager instance;

	public static CdsAnalysisViewManager getInstance() {
		if (instance == null) {
			instance = new CdsAnalysisViewManager();
		}
		return instance;
	}

	private CdsAnalysisViewManager() {
		this.openViews = new LinkedList<>();
	}

	/**
	 * Checks if the given CDS analysis is shown in any of the open CDS Analyzer
	 * views
	 *
	 * @param  analysis the analysis to be checked
	 * @return          <code>true</code> if the given analysis is shown in any view
	 */
	public boolean isAnalysisShown(final CdsAnalysis analysis) {
		synchronized (this.openViews) {
			for (final CdsAnalysisView view : this.openViews) {
				final CdsAnalysis shownAnalysis = view.getCurrentAnalysis();
				if (shownAnalysis == analysis) {
					return true;
				}
			}
			return false;
		}
	}

	public void cdsAnalysisViewActivated(final CdsAnalysisView view) {
		this.openViews.remove(view);
		this.openViews.addFirst(view);
	}

	public void cdsAnalysisViewClosed(final CdsAnalysisView view) {
		this.openViews.remove(view);
	}

}
