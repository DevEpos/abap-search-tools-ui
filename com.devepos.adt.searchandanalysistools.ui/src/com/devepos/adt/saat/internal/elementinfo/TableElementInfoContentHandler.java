package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content Handler for deserializing the Element information of a Database Table
 *
 * @author stockbal
 */
public class TableElementInfoContentHandler extends AdtObjectElementInfoContentHandlerBase {

  public TableElementInfoContentHandler(final String destinationId) {
    super(destinationId);
  }

  private static final String EL_TECH_SETTINGS_URI = "technicalSettings"; //$NON-NLS-1$

  @Override
  public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
      final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

    try {
      processTableInfo(utility.parseXML(messageBody));
      return elementInfo;
    } catch (final Exception e) {
    }
    return null;
  }

  private void processTableInfo(final IXmlElement rootElement) {
    createElementInfo(rootElement);

    for (final IXmlElement child : rootElement.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_PROPERTIES:
        addCollection(ElementInfoXMLExtractor.deserializeProperties(child));
        break;
      case EL_TECH_SETTINGS_URI:
        deserializeTechSettings(child);
        break;
      }
    }
  }

  private void deserializeTechSettings(final IXmlElement element) {
    final IElementInfoCollection techSettingsColl = new ElementInfoCollection(
        Messages.ElementInformation_TechSettings_xtit, SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.FOLDER));
    final IAdtObjectReferenceElementInfo techSettings = ElementInfoXMLExtractor
        .deserializeAdtObjectInfo(destinationId, element.getFirstChild());
    techSettings.setLazyLoadingSupport(false);
    techSettingsColl.getChildren().add(techSettings);
    addCollection(techSettingsColl);
  }

}
