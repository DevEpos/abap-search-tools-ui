package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.resources.IProject;

import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis.AnalysisMode;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.tools.core.project.IAbapProject;

public class PerformCdsTopDownAnalysisHandler extends OpenInCdsAnalyzerHandler {

	public PerformCdsTopDownAnalysisHandler() {
		super(AnalysisMode.TOP_DOWN);
	}

	@Override
	protected boolean isFeatureAvailable(final IProject project) {
		final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
		return new CdsAnalysisUriDiscovery(abapProject.getDestinationId()).isTopDownAnalysisAvailable();
	}

	@Override
	protected String getFeatureUnavailableMessage() {
		return Messages.CdsAnalysis_TopDownAnalsysisFeatureNotAvailable;
	}
}
