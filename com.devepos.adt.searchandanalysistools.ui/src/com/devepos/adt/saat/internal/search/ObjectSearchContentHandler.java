package com.devepos.adt.saat.internal.search;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.base.IAdtObjectTypeConstants;
import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.elementinfo.ExtendedAdtObjectInfo;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content handler for the object search
 *
 * @author stockbal
 */
public class ObjectSearchContentHandler implements IContentHandler<ObjectSearchResult> {
  private final String destinationId;
  protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

  public ObjectSearchContentHandler(final String destinationId) {
    this.destinationId = destinationId;
  }

  @Override
  public ObjectSearchResult deserialize(final IMessageBody body,
      final Class<? extends ObjectSearchResult> dataType) {
    final ObjectSearchResult result = new ObjectSearchResult();
    final List<IAdtObjectReferenceElementInfo> rawResult = new ArrayList<>();
    final List<IAdtObjectReferenceElementInfo> packageResult = new ArrayList<>();

    final IXmlElement rootElement = utility.parseXML(body, IXmlTags.EL_PROPERTY);

    if (rootElement == null || !IXmlTags.EL_ELEMENT_INFOS.equals(rootElement.getName())) {
      return result;
    }

    for (final IXmlElement childElement : rootElement.getChildren()) {
      final String type = childElement.getAttributeValue(IXmlTags.AT_TYPE);
      if ("rawResult".equals(type)) { //$NON-NLS-1$
        deserializeResult(childElement, rawResult);
        result.setRawResult(rawResult);
      } else if ("packages".equals(type)) { //$NON-NLS-1$
        deserializeResult(childElement, packageResult);
        result.setPackageResult(packageResult);
      }
    }
    return result;
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
  }

  @Override
  public Class<ObjectSearchResult> getSupportedDataType() {
    return ObjectSearchResult.class;
  }

  @Override
  public IMessageBody serialize(final ObjectSearchResult arg0, final Charset arg1) {
    return null;
  }

  private void deserializeResult(final IXmlElement rootElement,
      final List<IAdtObjectReferenceElementInfo> collection) {
    for (final IXmlElement elementInfoElement : rootElement.getChildren()) {
      final String type = elementInfoElement.getAttributeValue(IXmlTags.AT_TYPE);
      final String name = elementInfoElement.getAttributeValue(IXmlTags.AT_NAME);
      final String rawName = elementInfoElement.getAttributeValue(IXmlTags.AT_RAW_NAME);
      final String description = elementInfoElement.getAttributeValue(IXmlTags.AT_DESCRIPTION);
      final String packageName = elementInfoElement.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
      final String uri = elementInfoElement.getAttributeValue(IXmlTags.AT_URI);
      final String owner = elementInfoElement.getAttributeValue(IXmlTags.AT_OWNER);

      final IAdtObjectReferenceElementInfo elementInfo = new AdtObjectReferenceElementInfo(name,
          rawName, description);

      if (name != null && !name.isEmpty() && uri != null && !uri.isEmpty() && type != null && !type
          .isEmpty()) {
        final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(
            destinationId, name, type, uri);
        adtObjectRef.setPackageName(packageName);
        elementInfo.setAdtObjectReference(adtObjectRef);
        if (IAdtObjectTypeConstants.CLASS.equals(type) || IAdtObjectTypeConstants.INTERFACE.equals(
            type)) {
          elementInfo.setLazyLoadingSupport(false);
        } else {
          elementInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(destinationId,
              adtObjectRef));
        }
      }
      addAdditionalResultInformation(elementInfoElement, elementInfo);

      // handle the owner information
      if (owner != null) {
        if (elementInfo.hasAdditionalInfo()) {
          ((ExtendedAdtObjectInfo) elementInfo.getAdditionalInfo()).setOwner(owner);
        } else {
          final ExtendedAdtObjectInfo extendedElementInfo = new ExtendedAdtObjectInfo();
          extendedElementInfo.setOwner(owner);
          elementInfo.setAdditionalInfo(extendedElementInfo);
        }
      }
      collection.add(elementInfo);
    }
  }

  private void addAdditionalResultInformation(final IXmlElement elementInfoElement,
      final IAdtObjectReferenceElementInfo elementInfo) {
    if (!elementInfoElement.hasChildren()) {
      return;
    }
    final IXmlElement propertiesEl = elementInfoElement.getChildren()
        .stream()
        .filter(child -> child.getName().equals(IXmlTags.EL_PROPERTIES))
        .findFirst()
        .orElse(null);
    if (propertiesEl == null || propertiesEl.getChildren() == null) {
      return;
    }
    final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
    for (final IXmlElement propertyEl : propertiesEl.getChildren()) {
      final String key = propertyEl.getAttributeValue(IXmlTags.AT_KEY);
      final String value = propertyEl.getText();
      switch (key) {
      case "API_STATE":
        extendedInfo.setApiState(value);

      case "SOURCE_TYPE":
        if (value != null && !value.isEmpty()) {
          extendedInfo.setSourceType(CdsSourceType.getFromId(value));
        }
      }
    }
    elementInfo.setAdditionalInfo(extendedInfo);
  }

}
