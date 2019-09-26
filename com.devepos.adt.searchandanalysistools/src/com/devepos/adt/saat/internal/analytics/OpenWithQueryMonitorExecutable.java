package com.devepos.adt.saat.internal.analytics;

import com.devepos.adt.saat.internal.Executable;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
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
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(this.destinationId);
		if (projectProvider != null) {
			OpenInUtil.openCDSInQueryMonitor(projectProvider.getProject(), this.cdsViewName);
		}
	}

}
