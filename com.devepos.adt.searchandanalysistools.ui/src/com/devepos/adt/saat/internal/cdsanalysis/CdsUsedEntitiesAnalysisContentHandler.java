package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.elementinfo.AdtObjectElementInfoContentHandlerBase;
import com.devepos.adt.saat.internal.elementinfo.ExtendedAdtObjectInfo;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content Handler for CDS Used Entities analysis
 *
 * @author stockbal
 */
public class CdsUsedEntitiesAnalysisContentHandler extends AdtObjectElementInfoContentHandlerBase {

  public CdsUsedEntitiesAnalysisContentHandler(final String destinationId) {
    super(destinationId);
  }

  @Override
  public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
      final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

    try {
      deserializeNodeInfo(utility.parseXML(messageBody, IXmlTags.EL_PROPERTY), null, true);
      return elementInfo;
    } catch (final Exception e) {
    }
    return null;
  }

  protected void deserializeNodeInfo(final IXmlElement element, IElementInfo parent,
      final boolean isRoot) {
    IElementInfo elementInfo = null;
    final String name = element.getAttributeValue(IXmlTags.AT_NAME);
    final String rawName = element.getAttributeValue(IXmlTags.AT_RAW_NAME);
    final String description = element.getAttributeValue(IXmlTags.AT_DESCRIPTION);
    final String packageName = element.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
    final String uri = element.getAttributeValue(IXmlTags.AT_URI);
    final String type = element.getAttributeValue(IXmlTags.AT_TYPE);

    if (uri != null && !uri.isEmpty() && type != null && !type.isEmpty()) {
      final IAdtObjectReferenceElementInfo adtObjRefInfo = new AdtObjectReferenceElementInfo(name,
          rawName, description);
      final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(
          destinationId, name, type, uri);
      adtObjectRef.setPackageName(packageName);
      adtObjRefInfo.setAdtObjectReference(adtObjectRef);
      adtObjRefInfo.setLazyLoadingSupport(false);
      elementInfo = adtObjRefInfo;
    }

    if (elementInfo == null) {
      return;
    }

    if (isRoot) {
      this.elementInfo = (IAdtObjectReferenceElementInfo) elementInfo;
      parent = elementInfo;
    } else if (parent instanceof IElementInfoCollection) {
      setAdditionalInfo(element, elementInfo);
    }

    if (!isRoot && parent != null && elementInfo != null) {
      ((IElementInfoCollection) parent).getChildren().add(elementInfo);
    }

    parent = elementInfo;

    for (final IXmlElement child : element.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_ELEMENT_INFO:
        deserializeNodeInfo(child, parent, false);
        break;
      }
    }

  }

  private void setAdditionalInfo(final IXmlElement element, final IElementInfo elementInfo) {
    if (!element.hasChild(IXmlTags.EL_PROPERTIES)) {
      return;
    }
    for (final IXmlElement propertiesEl : element.getChildren()) {
      if (IXmlTags.EL_PROPERTIES.equals(propertiesEl.getName())) {
        setDatasourceUsageInfo(propertiesEl.getChildren(), elementInfo);
      }
    }
  }

  private void setDatasourceUsageInfo(final List<IXmlElement> children,
      final IElementInfo elementInfo) {
    if (children == null) {
      return;
    }
    final CdsEntityUsageInfo usageInfo = new CdsEntityUsageInfo();

    for (final IXmlElement propertyEl : children) {
      final String attribute = propertyEl.getAttributeValue(IXmlTags.AT_KEY);

      try {
        switch (attribute) {
        case "OCCURRENCE": //$NON-NLS-1$
          usageInfo.occurrence = Integer.parseInt(propertyEl.getText().trim());
          break;
        case "USED_ENTITIES_COUNT": //$NON-NLS-1$
          usageInfo.usedEntitiesCount = Integer.parseInt(propertyEl.getText().trim());
          break;
        case "USED_JOIN_COUNT": //$NON-NLS-1$
          usageInfo.usedJoinCount = Integer.parseInt(propertyEl.getText().trim());
          break;
        case "USED_UNION_COUNT": //$NON-NLS-1$
          usageInfo.usedUnionCount = Integer.parseInt(propertyEl.getText().trim());
          break;
        case "API_STATE": //$NON-NLS-1$
          usageInfo.setApiState(propertyEl.getText());
          break;
        case "SOURCE_TYPE": //$NON-NLS-1$
          usageInfo.setSourceType(CdsSourceType.getFromId(propertyEl.getText()));
          break;
        }
      } catch (final NumberFormatException exc) {
      }
    }
    elementInfo.setAdditionalInfo(usageInfo);
  }

  private static final class CdsEntityUsageInfo extends ExtendedAdtObjectInfo implements
      ICdsEntityUsageInfo {
    public int occurrence;
    public int usedEntitiesCount;
    public int usedJoinCount;
    public int usedUnionCount;

    @Override
    public int getOccurrence() {
      return occurrence;
    }

    @Override
    public int getUsedEntitiesCount() {
      return usedEntitiesCount;
    }

    @Override
    public int getUsedJoinCount() {
      return usedJoinCount;
    }

    @Override
    public int getUsedUnionCount() {
      return usedUnionCount;
    }

  }
}
