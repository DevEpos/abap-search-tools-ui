package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content Handler for deserializing the Element information of a Database View
 *
 * @author stockbal
 */
public class ViewElementInfoContentHandler extends AdtObjectElementInfoContentHandlerBase {

  public ViewElementInfoContentHandler(final String destinationId) {
    super(destinationId);
  }

  @Override
  public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
      final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

    try {
      processViewInfo(utility.parseXML(messageBody));
      return elementInfo;
    } catch (final Exception e) {
    }
    return null;
  }

  private void processViewInfo(final IXmlElement rootElement) {
    createElementInfo(rootElement);

    for (final IXmlElement child : rootElement.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_PROPERTIES:
        addCollection(ElementInfoXMLExtractor.deserializeProperties(child));
        break;
      }
    }
  }

}
