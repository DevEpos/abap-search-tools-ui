package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.elementinfo.ILazyLoadingElementInfo;
import com.devepos.adt.base.elementinfo.LazyLoadingElementInfo;
import com.devepos.adt.base.elementinfo.LazyLoadingRefreshMode;
import com.devepos.adt.base.elementinfo.SimpleElementInfo;
import com.devepos.adt.base.ui.elementinfo.ActionElementInfo;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.analytics.OpenWithAnalysisForOfficeExecutable;
import com.devepos.adt.saat.internal.analytics.OpenWithQueryMonitorExecutable;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.AdtObjectReferenceDeserializer;
import com.devepos.adt.saat.internal.search.ObjectSearchElementInfoProvider;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content Handler for deserializing the Element information of a CDS View
 *
 * @author stockbal
 */
public class CdsViewElementInfoContentHandler extends AdtObjectElementInfoContentHandlerBase {
  private final boolean createSecondaryElementsFolder;
  private final boolean hasAnalyticsService;
  private boolean isQuery;

  public CdsViewElementInfoContentHandler(final String destinationId,
      final boolean createSecondaryElementsFolder, final boolean hasAnalyticsService) {
    super(destinationId);
    this.createSecondaryElementsFolder = createSecondaryElementsFolder;
    this.hasAnalyticsService = hasAnalyticsService;
  }

  @Override
  public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
      final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

    try {
      processCdsViewInfo(utility.parseXML(messageBody));
      return elementInfo;
    } catch (final Exception e) {
    }
    return null;
  }

  private void processCdsViewInfo(final IXmlElement rootElement) {
    createElementInfo(rootElement);

    for (final IXmlElement child : rootElement.getChildren()) {
      switch (child.getName()) {
      case IXmlTags.EL_PROPERTIES:
        addCollection(deserializeProperties(child));
        break;
      case IXmlTags.EL_CDS_AMDP_REFERENCE:
        deserializeAMDPReference(child);
        break;
      case IXmlTags.EL_CDS_ASSOCIATIONS:
        deserializeAssociations(child);
        break;
      case IXmlTags.EL_CDS_SELECT_FROM:
        deserializeSelectFromEntities(child);
        break;
      }
    }

    /*
     * add action/navigation information if this CDS view is an analytical query
     * view
     */
    if (isQuery && hasAnalyticsService) {
      final ElementInfoCollection openInTargets = new ElementInfoCollection(
          Messages.ElementInformation_CustomNavigationTarget_xtit, SearchAndAnalysisPlugin
              .getDefault()
              .getImage(IImages.EXTERNAL_TOOLS));
      openInTargets.getChildren()
          .add(new ActionElementInfo(Messages.ElementInformation_AnalysisForOfficeTarget_xtit,
              SearchAndAnalysisPlugin.getDefault().getImage(IImages.EXCEL_APPLICATION),
              new OpenWithAnalysisForOfficeExecutable(destinationId, elementInfo.getName())));
      openInTargets.getChildren()
          .add(new ActionElementInfo(Messages.ElementInformation_QueryMonitorTarget_xtit,
              SearchAndAnalysisPlugin.getDefault().getImage(IImages.ANALYTICAL_QUERY),
              new OpenWithQueryMonitorExecutable(destinationId, elementInfo.getName())));
      elementInfo.getChildren().add(openInTargets);
    }

    // add additional collection to lazy load additional information
    if (createSecondaryElementsFolder) {
      final ILazyLoadingElementInfo secondaryElementsFolder = new LazyLoadingElementInfo(
          Messages.ElementInformation_SecondaryObjInfo_xtit, SearchAndAnalysisPlugin.getDefault()
              .getImage(IImages.VIRTUAL_FOLDER), new CdsSecondaryObjectsProvider(destinationId,
                  elementInfo.getName()));
      secondaryElementsFolder.setContentRefreshMode(
          LazyLoadingRefreshMode.ROOT_AND_NON_LAZY_CHILDREN);
      elementInfo.getChildren().add(secondaryElementsFolder);
    }

  }

  @Override
  protected void createElementInfo(final IXmlElement rootElement) {
    super.createElementInfo(rootElement);
    // check if this CDS view has the type query
    final String isQueryProp = rootElement.getAttributeValue(IXmlTags.AT_CDS_IS_QUERY);
    if (isQueryProp != null && !isQueryProp.isEmpty()) {
      isQuery = Boolean.parseBoolean(isQueryProp);
    }
  }

  private IElementInfoCollection deserializeProperties(final IXmlElement propertiesEl) {
    final IElementInfoCollection propertiesCollection = ElementInfoXMLExtractor
        .deserializeProperties(propertiesEl);
    final String apiState = propertiesEl.getAttributeValue(IXmlTags.AT_CDS_API_STATE);
    if (apiState != null && !apiState.isEmpty()) {

      propertiesCollection.getChildren()
          .add(new SimpleElementInfo(Messages.ElementInformation_APIState_xtit,
              Messages.ElementInformation_APIState_xtit, SearchAndAnalysisPlugin.getDefault()
                  .getImage(IImages.API_PARAM), apiState));
    }
    return propertiesCollection;
  }

  private void deserializeAMDPReference(final IXmlElement child) {
    final IAdtObjectReference objectRef = AdtObjectReferenceDeserializer.deserializeFromElement(
        child.getFirstChild());
    if (objectRef != null) {
      if (objectRef instanceof IDestinationProvider) {
        ((IDestinationProvider) objectRef).setDestinationId(destinationId);
      }
      final IElementInfoCollection classesColl = new ElementInfoCollection(
          Messages.ElementInformation_ClassesCollection_xtit, SearchAndAnalysisPlugin.getDefault()
              .getImage(IImages.TYPE_GROUP));
      final IAdtObjectReferenceElementInfo objectRefInfo = new AdtObjectReferenceElementInfo(
          objectRef.getName(), objectRef.getName(), objectRef.getDescription());
      objectRefInfo.setAdtObjectReference(objectRef);
      classesColl.getChildren().add(objectRefInfo);

      addCollection(classesColl);
    }
  }

  private void deserializeSelectFromEntities(final IXmlElement selectFromEl) {
    final IElementInfoCollection selectFromCollection = new ElementInfoCollection(
        Messages.ElementInformation_SelectFromCollection_xtit, SearchAndAnalysisPlugin.getDefault()
            .getImage(IImages.SELECT_FROM_PARAM));

    for (final IXmlElement child : selectFromEl.getChildren()) {
      final IAdtObjectReferenceElementInfo selectFrom = ElementInfoXMLExtractor
          .deserializeAdtObjectInfo(destinationId, child);

      if (selectFrom != null) {
        addExtendedInfo(child, selectFrom);
        selectFrom.setElementInfoProvider(new ObjectSearchElementInfoProvider(destinationId,
            selectFrom.getAdtObjectReference()));
        selectFromCollection.getChildren().add(selectFrom);
      }
    }
    addCollection(selectFromCollection);
  }

  private void deserializeAssociations(final IXmlElement associationsEl) {
    final IElementInfoCollection associationColl = new ElementInfoCollection(
        Messages.ElementInformation_AssociationsCollection_xtit, SearchAndAnalysisPlugin
            .getDefault()
            .getImage(IImages.ASSOCIATION));
    for (final IXmlElement child : associationsEl.getChildren()) {
      final IAdtObjectReferenceElementInfo association = deserializeAssociation(child);
      if (association != null) {
        associationColl.getChildren().add(association);
      }
    }
    addCollection(associationColl);

  }

  private IAdtObjectReferenceElementInfo deserializeAssociation(final IXmlElement associationEl) {
    IAdtObjectReferenceElementInfo adtObjRefElInfo = null;

    final String assocName = associationEl.getAttributeValue(IXmlTags.AT_NAME);
    final String assocRawName = associationEl.getAttributeValue(IXmlTags.AT_RAW_NAME);

    final IXmlElement adtObjectInfo = associationEl.getFirstChild();
    if (adtObjectInfo != null) {
      final String description = adtObjectInfo.getAttributeValue(IXmlTags.AT_DESCRIPTION);
      final String name = adtObjectInfo.getAttributeValue(IXmlTags.AT_NAME);
      final String rawName = adtObjectInfo.getAttributeValue(IXmlTags.AT_RAW_NAME);
      final String uri = adtObjectInfo.getAttributeValue(IXmlTags.AT_URI);
      final String adtType = adtObjectInfo.getAttributeValue(IXmlTags.AT_ADT_TYPE);
      final String packageName = adtObjectInfo.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);

      if (assocName != null && !assocName.isEmpty()) {
        final String elementRawName = String.format("%s (%s)", assocRawName, rawName); //$NON-NLS-1$
        adtObjRefElInfo = new AdtObjectReferenceElementInfo(assocName, elementRawName, description);
      } else {
        adtObjRefElInfo = new AdtObjectReferenceElementInfo(name, rawName, description);
      }
      addExtendedInfo(adtObjectInfo, adtObjRefElInfo);
      final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(
          destinationId, name, adtType, uri);
      adtObjectRef.setPackageName(packageName);
      adtObjRefElInfo.setAdtObjectReference(adtObjectRef);
      adtObjRefElInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(destinationId,
          adtObjectRef));
    }
    return adtObjRefElInfo;
  }

  private void addExtendedInfo(final IXmlElement element,
      final IAdtObjectReferenceElementInfo objRefInfo) {
    final String sourceType = element.getAttributeValue(IXmlTags.AT_SOURCE_TYPE);
    final String apiState = element.getAttributeValue(IXmlTags.AT_CDS_API_STATE);
    final boolean sourceTypeFound = sourceType != null && !sourceType.isEmpty();
    if (sourceTypeFound || apiState != null) {
      final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
      if (sourceTypeFound) {
        extendedInfo.setSourceType(CdsSourceType.getFromId(sourceType));
      }
      extendedInfo.setApiState(apiState);
      objRefInfo.setAdditionalInfo(extendedInfo);
    }
  }

}
