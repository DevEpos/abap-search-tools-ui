package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.SimpleElementInfo;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Extracts element information from {@link IXmlElement}
 *
 * @author stockbal
 */
public class ElementInfoXMLExtractor {
  /**
   * Deserialize the properties collection from the given element
   *
   * @param propertiesEl the XML element to used for deserialization
   * @return
   */
  public static IElementInfoCollection deserializeProperties(final IXmlElement propertiesEl) {

    final IElementInfoCollection properties = new ElementInfoCollection(
        Messages.ElementInformation_PropertiesCollection_xtit, SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.PROPERTIES));

    final String owner = propertiesEl.getAttributeValue(IXmlTags.AT_OWNER);
    if (owner != null && !owner.isEmpty()) {

      properties.getChildren()
          .add(new SimpleElementInfo(Messages.ElementInformation_OwnerProp_xtit,
              Messages.ElementInformation_OwnerProp_xtit, SearchAndAnalysisPlugin.getDefault()
                  .getImage(IImages.USER), owner));
    }
    final String packageName = propertiesEl.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
    if (packageName != null && !packageName.isEmpty()) {
      properties.getChildren()
          .add(new SimpleElementInfo(Messages.ElementInformation_PackageProp_xtit,
              Messages.ElementInformation_PackageProp_xtit, SearchAndAnalysisPlugin.getDefault()
                  .getImage(IImages.PACKAGE_PARAM), packageName));
    }
    final String createdDate = propertiesEl.getAttributeValue(IXmlTags.AT_CREATED_DATE);
    if (createdDate != null && !createdDate.isEmpty()) {
      properties.getChildren()
          .add(new SimpleElementInfo(Messages.ElementInformation_CreatedDateProp_xtit,
              Messages.ElementInformation_CreatedDateProp_xtit, SearchAndAnalysisPlugin.getDefault()
                  .getImage(IImages.DATE), createdDate));
    }
    final String changedDate = propertiesEl.getAttributeValue(IXmlTags.AT_CHANGED_DATE);
    if (changedDate != null && !changedDate.isEmpty()) {
      properties.getChildren()
          .add(new SimpleElementInfo(Messages.ElementInformation_ChangedDateProp_xtit,
              Messages.ElementInformation_ChangedDateProp_xtit, SearchAndAnalysisPlugin.getDefault()
                  .getImage(IImages.DATE), changedDate));
    }

    return properties;
  }

  /**
   * Deserialize the element information for an ADT Object References
   *
   * @param adtObjectInfoEl an XML Element
   * @return
   */
  public static IAdtObjectReferenceElementInfo deserializeAdtObjectInfo(final String destinationId,
      final IXmlElement adtObjectInfoEl) {
    IAdtObjectReferenceElementInfo adtObjRefElInfo;

    final String name = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_NAME);
    final String rawName = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_RAW_NAME);
    final String description = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_DESCRIPTION);
    final String uri = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_URI);
    final String adtType = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_ADT_TYPE);
    final String packageName = adtObjectInfoEl.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);

    final IAdtObjectReference objectReference = AdtObjectReferenceModelFactory.createReference(
        destinationId, name, adtType, uri);
    objectReference.setPackageName(packageName);

    adtObjRefElInfo = new AdtObjectReferenceElementInfo(name, rawName, description);
    adtObjRefElInfo.setAdtObjectReference(objectReference);

    return adtObjRefElInfo;
  }
}
