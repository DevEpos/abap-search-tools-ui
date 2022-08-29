package com.devepos.adt.saat.internal.cdsanalysis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.AdtUtil;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;

/**
 * Service for analyzing CDS Views
 *
 * @author Ludwig Stockbauer-Muhr
 */
public class CdsAnalysisService implements ICdsAnalysisService {

  @Override
  public IAdtObjectReferenceElementInfo loadTopDownAnalysis(final String cdsView,
      final ICdsTopDownSettings settings, final String destinationId) {
    final Map<String, Object> parameters = new HashMap<>();
    if (settings != null && settings.isLoadAssociations()) {
      parameters.put("withAssociations", "X"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    if (cdsView == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = getProviderAndLogon(destinationId);
    if (projectProvider == null) {
      return null;
    }
    final URI resourceUri = new CdsAnalysisUriDiscovery(destinationId)
        .createTopDownAnalysisResourceUri(cdsView, parameters);
    return analyzeCdsView(resourceUri, new CdsTopDownAnalysisContentHandler(destinationId,
        settings), destinationId, projectProvider);
  }

  @Override
  public IAdtObjectReferenceElementInfo loadUsedEntitiesAnalysis(final String cdsView,
      final String destinationId) {
    if (cdsView == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = getProviderAndLogon(destinationId);
    if (projectProvider == null) {
      return null;
    }
    final URI resourceUri = new CdsAnalysisUriDiscovery(destinationId)
        .createUsedEntitiesAnalysisResourceUri(cdsView, null);
    return analyzeCdsView(resourceUri, new CdsUsedEntitiesAnalysisContentHandler(destinationId),
        destinationId, projectProvider);
  }

  @Override
  public IElementInfo loadTopDownFieldAnalysis(final String cdsView, final String field,
      final String destinationId) {
    if (cdsView == null || field == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = getProviderAndLogon(destinationId);
    if (projectProvider == null) {
      return null;
    }
    final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new FieldAnalysisContentHandler(
        destinationId, true);
    final FieldAnalysisUriDiscovery uriDiscovery = new FieldAnalysisUriDiscovery(projectProvider
        .getDestinationId());

    final URI resourceUri = uriDiscovery.createTopDownCdsAnalysisResourceUri(cdsView, field);

    return getFieldAnalysis(projectProvider, adtObjectHandler, resourceUri);
  }

  @Override
  public IElementInfo loadWhereUsedFieldAnalysis(final String objectName, final String field,
      final ICdsFieldAnalysisSettings settings, final String destinationId) {
    if (objectName == null || field == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = getProviderAndLogon(destinationId);
    if (projectProvider == null) {
      return null;
    }
    final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new FieldAnalysisContentHandler(
        destinationId, false, settings);
    final FieldAnalysisUriDiscovery uriDiscovery = new FieldAnalysisUriDiscovery(projectProvider
        .getDestinationId());

    boolean isSearchCalcFields = false;
    boolean isSearchDbViews = false;
    if (settings != null) {
      isSearchCalcFields = settings.isSearchInCalcFields();
      isSearchDbViews = settings.isSearchInDatabaseViews();
    }
    final URI resourceUri = uriDiscovery.createWhereUsedAnalysisResourceUri(objectName, field,
        isSearchCalcFields, isSearchDbViews);

    return getFieldAnalysis(projectProvider, adtObjectHandler, resourceUri);
  }

  private IAdtObjectReferenceElementInfo analyzeCdsView(final URI resourceUri,
      final IContentHandler<IAdtObjectReferenceElementInfo> contentHandler,
      final String destinationId, final IAbapProjectProvider projectProvider) {
    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(contentHandler);

    try {
      return restResource.get(null, AdtUtil.getHeaders(), IAdtObjectReferenceElementInfo.class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  /*
   * Retrieves the field analysis result with GET
   */
  private IAdtObjectReferenceElementInfo getFieldAnalysis(
      final IAbapProjectProvider projectProvider,
      final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler,
      final URI resourceUri) {

    if (resourceUri == null) {
      return null;
    }
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

  private IAbapProjectProvider getProviderAndLogon(final String destinationId) {
    if (destinationId == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    return projectProvider;
  }
}
