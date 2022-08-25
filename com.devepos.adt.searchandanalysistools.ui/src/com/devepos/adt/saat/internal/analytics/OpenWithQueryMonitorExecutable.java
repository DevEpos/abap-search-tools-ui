package com.devepos.adt.saat.internal.analytics;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.action.Executable;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.OpenInUtil;

/**
 * An Action which opens a CDS view with Query Monitor SAP GUI Transaction
 * (RSRT)
 *
 * @author stockbal
 */
public class OpenWithQueryMonitorExecutable extends Executable {
  private final String cdsViewName;
  private final String destinationId;

  public OpenWithQueryMonitorExecutable(final String destinationId, final String cdsViewName) {
    this.destinationId = destinationId;
    this.cdsViewName = cdsViewName;
  }

  @Override
  public void execute() {
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider != null) {
      OpenInUtil.openCDSInQueryMonitor(projectProvider.getProject(), cdsViewName);
    }
  }

}
