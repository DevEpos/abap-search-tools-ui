package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.resources.IProject;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsUsedEntitiesAnalysis;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.tools.core.project.IAbapProject;

public class PerformCdsUsedEntitiesAnalysisHandler extends OpenInCdsAnalyzerHandler {

  public PerformCdsUsedEntitiesAnalysisHandler() {
    super(CdsAnalysisType.DEPENDENCY_TREE_USAGES);
  }

  @Override
  protected boolean isFeatureAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
    return new CdsAnalysisUriDiscovery(abapProject.getDestinationId())
        .isUsedEntitiesAnalysisAvailable();
  }

  @Override
  protected String getFeatureUnavailableMessage() {
    return Messages.CdsAnalysis_UsedEntitiesFeatureNotAvailable;
  }

  @Override
  protected CdsAnalysis createTypedAnalysis(final IAdtObjectReferenceElementInfo objectRefInfo) {
    return new CdsUsedEntitiesAnalysis(objectRefInfo);
  }
}
