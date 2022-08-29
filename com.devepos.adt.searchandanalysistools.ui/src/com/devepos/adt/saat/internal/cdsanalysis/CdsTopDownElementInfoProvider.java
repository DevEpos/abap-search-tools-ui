package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;

public class CdsTopDownElementInfoProvider implements IElementInfoProvider {
  private final String cdsViewName;
  private final String destinationId;
  private ICdsTopDownSettings settings;

  public CdsTopDownElementInfoProvider(final String destinationId, final String cdsViewName,
      ICdsTopDownSettings settings) {
    this.cdsViewName = cdsViewName;
    this.destinationId = destinationId;
    this.settings = settings;
  }

  @Override
  public List<IElementInfo> getElements() {
    final IElementInfo cdsTopDownInfo = CdsAnalysisServiceFactory.createCdsAnalysisService()
        .loadTopDownAnalysis(cdsViewName, settings, destinationId);
    if (cdsTopDownInfo != null) {
      return ((IElementInfoCollection) cdsTopDownInfo).getChildren();
    }
    return null;
  }

  @Override
  public String getProviderDescription() {
    return NLS.bind(Messages.CdsAnalysis_TopDownAnalysisProviderDescription_xmsg, cdsViewName);
  }

}
