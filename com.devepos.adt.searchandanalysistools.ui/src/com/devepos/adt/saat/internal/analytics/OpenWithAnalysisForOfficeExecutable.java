package com.devepos.adt.saat.internal.analytics;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.action.Executable;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.OpenInUtil;

/**
 * An action that opens a CDS View with the Analysis for Office plugin
 *
 * @author stockbal
 */
public class OpenWithAnalysisForOfficeExecutable extends Executable {
  private final String cdsViewname;
  private final String destinationId;

  public OpenWithAnalysisForOfficeExecutable(final String destinationId, final String cdsViewName) {
    cdsViewname = cdsViewName;
    this.destinationId = destinationId;
  }

  @Override
  public void execute() {
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider != null) {
      OpenInUtil.openCDSInAnalysisForOffice(projectProvider.getProject(), cdsViewname);
    }
  }

}
