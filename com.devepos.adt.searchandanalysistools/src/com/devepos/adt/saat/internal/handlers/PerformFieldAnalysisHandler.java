package com.devepos.adt.saat.internal.handlers;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.FieldAnalysis;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.util.IAdtObject;

/**
 * Handler for performing a field analy
 *
 * @author stockbal
 */
public class PerformFieldAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformFieldAnalysisHandler() {
		super(CdsAnalysisType.FIELD_ANALYSIS);
	}

	@Override
	protected boolean canExecute(final IAdtObject selectedObject) {
		/*
		 * At least the "Where-Used" field analysis can be executed for every ADT Object
		 * that was successfully adapted from the current selection
		 */
		return true;
	}

	@Override
	protected CdsAnalysis createTypedAnalysis(final IAdtObjectReferenceElementInfo objectRefInfo) {
		return new FieldAnalysis(objectRefInfo);
	}
}
