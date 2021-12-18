package com.devepos.adt.saat.internal.navtargets;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content handler for deserializing a list of Navigation targets for a given
 * ADT object
 *
 * @author stockbal
 */
public class NavigationTargetsContentHandler implements IContentHandler<INavigationTarget[]> {
  private final List<INavigationTarget> navigationTargets = new ArrayList<>();

  @Override
  public INavigationTarget[] deserialize(final IMessageBody body,
      final Class<? extends INavigationTarget[]> clazz) {
    final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

    try {
      deserializeNavigationTargets(utility.parseXML(body));
      if (navigationTargets != null) {
        return navigationTargets.toArray(new INavigationTarget[navigationTargets.size()]);
      }
    } catch (final Exception e) {
    }
    return null;
  }

  private void deserializeNavigationTargets(final IXmlElement rootElement) {
    if (!rootElement.getName().equals(IXmlTags.EL_NAV_TARGETS) || !rootElement.hasChildren()) {
      return;
    }
    for (final IXmlElement navTargetEl : rootElement.getChildren()) {
      final String name = navTargetEl.getAttributeValue("name");

      if (name == null || name.isEmpty()) {
        continue;
      }
      INavigationTarget target = null;
      switch (name) {
      case "EXCEL":
        target = new NavigationTarget(name,
            Messages.ElementInformation_AnalysisForOfficeTarget_xtit, IImages.EXCEL_APPLICATION);
        break;
      case "QUERY_MONITOR":
        target = new NavigationTarget(name, Messages.ElementInformation_QueryMonitorTarget_xtit,
            IImages.ANALYTICAL_QUERY);
        break;
      }
      if (target != null) {
        navigationTargets.add(target);
      }
    }
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
  }

  @Override
  public Class<INavigationTarget[]> getSupportedDataType() {
    return INavigationTarget[].class;
  }

  @Override
  public IMessageBody serialize(final INavigationTarget[] arg0, final Charset arg1) {
    return null;
  }

}