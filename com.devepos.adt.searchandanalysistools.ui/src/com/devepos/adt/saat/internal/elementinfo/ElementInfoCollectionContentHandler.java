package com.devepos.adt.saat.internal.elementinfo;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.SimpleElementInfo;
import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.search.ObjectSearchElementInfoProvider;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content handler for generic list ofr element information objects
 *
 * @author stockbal
 */
public class ElementInfoCollectionContentHandler implements
    IContentHandler<IElementInfoCollection> {
  private final String destinationId;
  protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

  public ElementInfoCollectionContentHandler(final String destinationId) {
    this.destinationId = destinationId;
  }

  @Override
  public IElementInfoCollection deserialize(final IMessageBody body,
      final Class<? extends IElementInfoCollection> dataType) {
    final IElementInfoCollection collection = new ElementInfoCollection();

    final IXmlElement rootElement = utility.parseXML(body, IXmlTags.EL_PROPERTY);

    if (rootElement != null) {
      if (rootElement.getName().equals(IXmlTags.EL_ELEMENT_INFOS)) {
        deserializeElementInfoList(rootElement, collection.getChildren());
      } else {
        deserializeElementInfo(rootElement, collection.getChildren());
      }
    }

    return collection;
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
  }

  @Override
  public Class<IElementInfoCollection> getSupportedDataType() {
    return IElementInfoCollection.class;
  }

  @Override
  public IMessageBody serialize(final IElementInfoCollection arg0, final Charset arg1) {
    return null;
  }

  private void deserializeElementInfoList(final IXmlElement xmlElem,
      final List<IElementInfo> elemInfoList) {
    for (final IXmlElement elementInfoElement : xmlElem.getChildren()) {
      deserializeElementInfo(elementInfoElement, elemInfoList);
    }
  }

  private void deserializeElementInfo(final IXmlElement xmlElemList,
      final List<IElementInfo> elemInfoList) {
    final String name = xmlElemList.getAttributeValue(IXmlTags.AT_NAME);
    final String rawName = xmlElemList.getAttributeValue(IXmlTags.AT_RAW_NAME);
    final String description = xmlElemList.getAttributeValue(IXmlTags.AT_DESCRIPTION);

    final String packageName = xmlElemList.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
    final String owner = xmlElemList.getAttributeValue(IXmlTags.AT_OWNER);
    final String uri = xmlElemList.getAttributeValue(IXmlTags.AT_URI);
    final String type = xmlElemList.getAttributeValue(IXmlTags.AT_TYPE);

    IElementInfo elementInfo = null;
    if (name != null && !name.isEmpty() && uri != null && !uri.isEmpty() && type != null && !type
        .isEmpty()) {
      final IAdtObjectReferenceElementInfo adtObjRefElemInfo = new AdtObjectReferenceElementInfo(
          name, rawName, description);
      final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(
          destinationId, name, type, uri);
      adtObjectRef.setPackageName(packageName);
      adtObjRefElemInfo.setAdtObjectReference(adtObjectRef);
      adtObjRefElemInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(destinationId,
          adtObjectRef));
      elementInfo = adtObjRefElemInfo;
    } else if (xmlElemList.hasChild(IXmlTags.EL_ELEMENT_INFO)) {
      final IElementInfoCollection collection = new ElementInfoCollection(name, rawName, null,
          description);
      deserializeElementInfoList(xmlElemList, collection.getChildren());
      elementInfo = collection;
    } else {
      elementInfo = new SimpleElementInfo(name, rawName, null, description);
    }

    if (xmlElemList.hasChild(IXmlTags.EL_PROPERTIES)) {
      final Map<String, String> properties = ContentHandlerUtil.deserializeProperties(xmlElemList);
      addAdditionalResultInformation(properties, elementInfo);
      elementInfo.getProperties().putAll(properties);
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
    }
    elemInfoList.add(elementInfo);
  }

  private void addAdditionalResultInformation(final Map<String, String> properties,
      final IElementInfo elementInfo) {
    if (properties.isEmpty()) {
      return;
    }
    final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
    final Iterator<String> keyIter = properties.keySet().iterator();
    while (keyIter.hasNext()) {
      final String key = keyIter.next();
      final String value = properties.get(key);
      switch (key) {
      case "API_STATE":
        extendedInfo.setApiState(value);
        break;
      case "SOURCE_TYPE":
        if (value != null && !value.isEmpty()) {
          extendedInfo.setSourceType(CdsSourceType.getFromId(value));
        }
        break;
      default:
        continue;
      }
      keyIter.remove();
//			properties.remove(key);
    }
    elementInfo.setAdditionalInfo(extendedInfo);
  }
}
