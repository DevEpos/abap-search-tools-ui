package com.devepos.adt.saat.internal.navtargets;

import java.net.URI;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.AdtUtil;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;

/**
 * Service which handles the retrieval of available navigation targets for ADT
 * objects
 *
 * @author stockbal
 */
public class NavigationTargetService implements INavigationTargetService {

  private final String destinationId;

  public NavigationTargetService(final String destinationId) {
    this.destinationId = destinationId;
  }

  @Override
  public INavigationTarget[] getTargets(final String objectName, final ObjectType objectType) {
    if (objectName == null || objectType == null || objectType.getId() == null) {
      return null;
    }
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (!projectProvider.ensureLoggedOn()) {
      return null;
    }
    final IContentHandler<INavigationTarget[]> adtObjectHandler = new NavigationTargetsContentHandler();
    final NavigationTargetsUriDiscovery uriDiscovery = new NavigationTargetsUriDiscovery(
        projectProvider.getDestinationId());

    final URI resourceUri = uriDiscovery.createNavTargetsResourceUri(objectName, objectType);
    if (resourceUri == null) {
      return null;
    }

    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(adtObjectHandler);

    INavigationTarget[] targets = null;
    try {
      targets = restResource.get(null, AdtUtil.getHeaders(), INavigationTarget[].class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return targets;
  }

}
