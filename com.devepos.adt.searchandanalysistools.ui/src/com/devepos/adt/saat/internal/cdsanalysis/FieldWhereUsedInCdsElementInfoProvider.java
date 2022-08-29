package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Element info provider which reads a Where-Used list for a given database
 * entity field
 *
 * @author stockbal
 */
public class FieldWhereUsedInCdsElementInfoProvider implements IElementInfoProvider {

  private final String objectName;
  private final String field;
  private final String destinationId;
  private ICdsFieldAnalysisSettings settings;

  public FieldWhereUsedInCdsElementInfoProvider(final String destinationId, final String objectName,
      final String field) {
    this(destinationId, objectName, field, null);
  }

  public FieldWhereUsedInCdsElementInfoProvider(final String destinationId, final String objectName,
      final String field, ICdsFieldAnalysisSettings settings) {
    this.objectName = objectName;
    this.settings = settings;
    this.field = field;
    this.destinationId = destinationId;
  }

  @Override
  public List<IElementInfo> getElements() {
    final IElementInfo whereUsedInCdsInfo = CdsAnalysisServiceFactory.createCdsAnalysisService()
        .loadWhereUsedFieldAnalysis(objectName, field, settings, destinationId);
    if (whereUsedInCdsInfo instanceof IElementInfoCollection) {
      return ((IElementInfoCollection) whereUsedInCdsInfo).getChildren();
    }
    return null;
  }

  @Override
  public String getProviderDescription() {
    return NLS.bind(Messages.FieldWhereUsedInCdsElementInfoProvider_ProviderDescription_xmsg,
        objectName, field);
  }
}
