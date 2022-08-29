package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.Map;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.SimpleElementInfo;
import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.AdtObjectElementInfoContentHandlerBase;
import com.devepos.adt.saat.internal.elementinfo.ContentHandlerUtil;
import com.devepos.adt.saat.internal.elementinfo.ExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content handler for deserializing the Top-down or dependency usages
 * information for a CDS view
 *
 * @author stockbal
 */
public class FieldAnalysisContentHandler extends AdtObjectElementInfoContentHandlerBase {

  protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();
  private final boolean topDown;
  private ICdsFieldAnalysisSettings settings;

  public FieldAnalysisContentHandler(final String destinationId, final boolean topDown) {
    this(destinationId, topDown, null);
  }

  public FieldAnalysisContentHandler(final String destinationId, final boolean topDown,
      ICdsFieldAnalysisSettings settings) {
    super(destinationId);
    this.topDown = topDown;
    this.settings = settings;
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
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

  private void deserializeNodeInfo(final IXmlElement xmlElement, IElementInfo parent,
      final boolean isRoot) {
    final IElementInfo elementInfo = extractElementInfo(xmlElement, parent, isRoot);

    if (isRoot) {
      this.elementInfo = (IAdtObjectReferenceElementInfo) elementInfo;
      parent = elementInfo;
    }

    if (!isRoot && parent != null && elementInfo != null) {
      ((IElementInfoCollection) parent).getChildren().add(elementInfo);
    }

    if (elementInfo instanceof IElementInfoCollection) {
      parent = elementInfo;
    }

    for (final IXmlElement child : xmlElement.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_ELEMENT_INFO:
        deserializeNodeInfo(child, parent, false);
        break;
      }
    }
  }

  private IElementInfo extractElementInfo(final IXmlElement xmlElement, final IElementInfo parent,
      final boolean isRoot) {
    IElementInfo elementInfo = null;
    final String name = xmlElement.getAttributeValue(IXmlTags.AT_NAME);
    final String displayName = xmlElement.getAttributeValue(IXmlTags.AT_RAW_NAME);
    final String type = xmlElement.getAttributeValue(IXmlTags.AT_TYPE);
    final String uri = xmlElement.getAttributeValue(IXmlTags.AT_URI);

    final Map<String, String> properties = ContentHandlerUtil.deserializeProperties(xmlElement);
    final String fieldName = properties.get(ICdsAnalysisConstants.FIELD_PROP);

    if (xmlElement.hasChild(IXmlTags.EL_ELEMENT_INFO)) {
      final IAdtObjectReferenceElementInfo adtObjInfo = createAdtObjectRefInfo(name, displayName,
          type, uri, fieldName, false, properties);
      elementInfo = adtObjInfo;
    } else {
      if (topDown) {
        final IAdtObjectReferenceElementInfo adtObjRefInfo = createAdtObjectRefInfo(name,
            displayName, type, uri, fieldName, false, properties);
        if (properties.get(ICdsAnalysisConstants.IS_CALCULATED_PROP) != null && isRoot) {
          final IElementInfo calculatedFieldInfo = new SimpleElementInfo(
              Messages.FieldAnalysisContentHandler_CalculatedField_xmsg, SearchAndAnalysisPlugin
                  .getDefault()
                  .getImage(IImages.FUNCTION));
          adtObjRefInfo.getChildren().add(calculatedFieldInfo);
        }

        elementInfo = adtObjRefInfo;
      } else {
        final IAdtObjectReferenceElementInfo adtObjRefInfo = createAdtObjectRefInfo(name,
            displayName, type, uri, fieldName, true, properties);
        adtObjRefInfo.setElementInfoProvider(new FieldWhereUsedInCdsElementInfoProvider(
            destinationId, displayName, fieldName, settings));
        elementInfo = adtObjRefInfo;
      }
    }

    return elementInfo;
  }

  private IAdtObjectReferenceElementInfo createAdtObjectRefInfo(final String name,
      final String displayName, final String type, final String uri, final String fieldName,
      final boolean lazyLoadingSupport, final Map<String, String> properties) {
    final IAdtObjectReferenceElementInfo adtObjInfo = new AdtObjectReferenceElementInfo(name,
        displayName, null);
    adtObjInfo.setAdtObjectReference(AdtObjectReferenceModelFactory.createReference(destinationId,
        name, type, uri));
    adtObjInfo.setLazyLoadingSupport(lazyLoadingSupport);
    adtObjInfo.getProperties().putAll(properties);

    // set additional information
    final String apiState = properties.get("API_STATE"); //$NON-NLS-1$
    final String sourceType = properties.get("SOURCE_TYPE"); //$NON-NLS-1$
    if (apiState != null || sourceType != null) {
      final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
      extendedInfo.setApiState(apiState);
      extendedInfo.setSourceType(CdsSourceType.getFromId(sourceType));
      adtObjInfo.setAdditionalInfo(extendedInfo);
    }
    return adtObjInfo;
  }

}