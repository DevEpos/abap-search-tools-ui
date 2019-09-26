package com.devepos.adt.saat.internal.handlers;

import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis.AnalysisMode;

public class PerformCdsUsedEntitiesAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformCdsUsedEntitiesAnalysisHandler() {
		super(AnalysisMode.DEPENDENCY_TREE_USAGES);
	}
}
