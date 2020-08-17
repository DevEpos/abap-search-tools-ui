package com.devepos.adt.saat.internal.handlers;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.WhereUsedInCdsAnalysis;
import com.devepos.adt.tools.base.adtobject.IAdtObject;
import com.devepos.adt.tools.base.elementinfo.IAdtObjectReferenceElementInfo;

public class PerformWhereUsedInCdsAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformWhereUsedInCdsAnalysisHandler() {
		super(CdsAnalysisType.WHERE_USED);
	}

	@Override
	protected boolean canExecute(final IAdtObject selectedObject) {
		return true;
	}

	@Override
	protected CdsAnalysis createTypedAnalysis(final IAdtObjectReferenceElementInfo objectRefInfo) {
		return new WhereUsedInCdsAnalysis(objectRefInfo);
	}

}
