package com.devepos.adt.saat.internal.handlers;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis.AnalysisMode;
import com.devepos.adt.saat.internal.util.IAdtObject;

public class PerformWhereUsedInCdsAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformWhereUsedInCdsAnalysisHandler() {
		super(AnalysisMode.WHERE_USED);
	}

	@Override
	protected boolean canExecute(final IAdtObject selectedObject) {
		return true;
	}

}
