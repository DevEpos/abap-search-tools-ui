package com.devepos.adt.saat.internal.handlers;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis.AnalysisMode;
import com.devepos.adt.saat.internal.util.IAdtObject;

/**
 * Handler for performing a field analy
 *
 * @author stockbal
 */
public class PerformFieldAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformFieldAnalysisHandler() {
		super(AnalysisMode.FIELD_ANALYSIS);
	}

	@Override
	protected boolean canExecute(final IAdtObject selectedObject) {
		/*
		 * At least the "Where-Used" field analysis can be executed for every ADT Object
		 * that was successfully adapted from the current selection
		 */
		return true;
	}
}
