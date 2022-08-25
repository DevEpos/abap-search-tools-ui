package com.devepos.adt.saat.internal.elementinfo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.AdtUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.analytics.AnalysisForOfficeUriDiscovery;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Service implementation for element information retrieval
 *
 * @author stockbal
 */
class ElementInfoRetrievalService implements IElementInfoRetrievalService {

  @Override
  public IAdtObjectReferenceElementInfo retrieveElementInformation(final String destinationId,
      final IAdtObjectReference objectReference) {
    // TODO: throw proper exception
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }

    IAdtObjectReferenceElementInfo elementInfo = null;

    final String objectName = objectReference.getName();
    final Map<String, Object> paramsMap = new HashMap<>();
    final String type = objectReference.getType();
    ObjectType objectType = null;
    IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = null;
    final ElementInfoUriDiscovery uriDiscovery = new ElementInfoUriDiscovery(projectProvider
        .getDestinationId());
    final AnalysisForOfficeUriDiscovery anlyticsURIDiscovery = new AnalysisForOfficeUriDiscovery(
        projectProvider.getDestinationId());

    if (type.equals(ObjectType.DATA_DEFINITION.getAdtExecutionType())) {
      adtObjectHandler = new CdsViewElementInfoContentHandler(destinationId, uriDiscovery
          .getCDSSecondaryElementInfoTemplate() != null, anlyticsURIDiscovery
              .getLauncherTemplate() != null);
      if (SearchAndAnalysisPlugin.getDefault()
          .getPreferenceStore()
          .getBoolean(IPreferences.SHOW_FULL_ASSOCIATION_NAME)) {
        paramsMap.put("showAssocName", "X"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      objectType = ObjectType.DATA_DEFINITION;
    } else if (type.equals(ObjectType.TABLE.getAdtExecutionType())) {
      adtObjectHandler = new TableElementInfoContentHandler(destinationId);
      objectType = ObjectType.TABLE;
    } else if (type.equals(ObjectType.VIEW.getAdtExecutionType())) {
      adtObjectHandler = new ViewElementInfoContentHandler(destinationId);
      objectType = ObjectType.VIEW;
    }

    if (objectType != null || adtObjectHandler != null) {

      final URI resourceUri = uriDiscovery.createElementInfoResourceUri(objectName, objectType,
          paramsMap);

      final ISystemSession session = projectProvider.createStatelessSession();
      final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
          .createRestResource(resourceUri, session);
      restResource.addContentHandler(adtObjectHandler);

      try {
        elementInfo = restResource.get(null, AdtUtil.getHeaders(),
            IAdtObjectReferenceElementInfo.class);
      } catch (final ResourceException exc) {
        exc.printStackTrace();
      }
    }
    return elementInfo;
  }

  @Override
  public IElementInfoCollection retrieveCDSSecondaryElements(final String destinationId,
      final String cdsViewName) {
    // TODO: throw proper exception
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    IElementInfoCollection secondaryElementInfo = null;

    final IContentHandler<IElementInfoCollection> handler = new CdsSecondaryElementInfoContentHandler(
        destinationId);
    final URI resourceUri = new ElementInfoUriDiscovery(projectProvider.getDestinationId())
        .createCDSSecondaryElementInfoResourceURI(cdsViewName);

    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(handler);

    try {
      secondaryElementInfo = restResource.get(null, AdtUtil.getHeaders(),
          IElementInfoCollection.class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return secondaryElementInfo;
  }

  @Override
  public IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId,
      final String objectName, final ObjectType objectType) {
    if (destinationId == null || objectType == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new BasicElementInfoContentHandler(
        destinationId);
    final ElementInfoUriDiscovery uriDiscovery = new ElementInfoUriDiscovery(projectProvider
        .getDestinationId());
    if (!uriDiscovery.isResourceDiscoverySuccessful()) {
      return null;
    }

    final Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("basicInfoOnly", "X");
    final URI resourceUri = uriDiscovery.createElementInfoResourceUri(objectName, objectType,
        paramsMap);

    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(adtObjectHandler);

    IAdtObjectReferenceElementInfo elementInfo = null;
    try {
      elementInfo = restResource.get(null, AdtUtil.getHeaders(),
          IAdtObjectReferenceElementInfo.class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return elementInfo;
  }

  @Override
  public IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId,
      final String uri) {
    if (destinationId == null || uri == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new BasicElementInfoContentHandler(
        destinationId);
    final ElementInfoUriDiscovery uriDiscovery = new ElementInfoUriDiscovery(projectProvider
        .getDestinationId());

    final Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("basicInfoOnly", "X");
    final URI resourceUri = uriDiscovery.createElementInfoResourceUri(uri, paramsMap);

    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(adtObjectHandler);

    IAdtObjectReferenceElementInfo elementInfo = null;
    try {
      elementInfo = restResource.get(null, AdtUtil.getHeaders(),
          IAdtObjectReferenceElementInfo.class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return elementInfo;
  }

}
