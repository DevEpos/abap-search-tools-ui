package com.devepos.adt.saat.internal.elementinfo;

import java.nio.charset.Charset;

import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content handler for deserializing the information
 *
 * @author stockbal
 */
public class CdsSecondaryElementInfoContentHandler implements
    IContentHandler<IElementInfoCollection> {

  private IElementInfoCollection collection = null;
  private final String destinationId;

  public CdsSecondaryElementInfoContentHandler(final String destinationId) {
    this.destinationId = destinationId;
  }

  @Override
  public IElementInfoCollection deserialize(final IMessageBody body,
      final Class<? extends IElementInfoCollection> clazz) {

    try {
      processSecondaryElements(new AdtStaxContentHandlerUtility().parseXML(body));
      return collection;
    } catch (final Exception e) {
    }
    return null;
  }

  private void processSecondaryElements(final IXmlElement rootElement) {
    createCollection(rootElement);

    for (final IXmlElement child : rootElement.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_CDS_ACCESS_CONTROLS:
        addAdtObjectCollection(child, Messages.ElementInformation_AccessControls_xtit);
        break;
      case IXmlTags.EL_CDS_BUSINESS_OBJECT:
        addAdtObjectCollection(child, Messages.ElementInformation_BusinessObject_xtit);
        break;
      case IXmlTags.EL_CDS_EXTENSIONS:
        addAdtObjectCollection(child, Messages.ElementInformation_Extensions_xtit);
        break;
      case IXmlTags.EL_CDS_METADATA_EXTENSIONS:
        addAdtObjectCollection(child, Messages.ElementInformation_MetadataExtensions_xtit);
        break;
      case IXmlTags.EL_CDS_REFERENCED_CLASSES:
        addAdtObjectCollection(child, Messages.ElementInformation_ReferencedClasses_xtit);
        break;
      default:
        break;
      }
    }

  }

  private void addAdtObjectCollection(final IXmlElement element, final String displayName) {
    final IElementInfoCollection collection = new ElementInfoCollection(displayName,
        SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_GROUP));
    for (final IXmlElement child : element.getChildren()) {
      final IAdtObjectReferenceElementInfo elementInfo = ElementInfoXMLExtractor
          .deserializeAdtObjectInfo(destinationId, child);
      if (elementInfo != null) {
        elementInfo.setLazyLoadingSupport(false);
        collection.getChildren().add(elementInfo);
      }
    }
    this.collection.getChildren().add(collection);
  }

  private void createCollection(final IXmlElement rootElement) {
    final String displayName = rootElement.getAttributeValue(IXmlTags.AT_RAW_NAME);
    collection = new ElementInfoCollection(displayName, SearchAndAnalysisPlugin.getDefault()
        .getImage(IImages.VIRTUAL_FOLDER));
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
    // no implementation needed for serialization
    return null;
  }

}
