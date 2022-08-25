package com.devepos.adt.saat.internal.ddicaccess;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.AdtUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoCollectionContentHandler;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Implementation for DDIC Repository Access
 *
 * @author stockbal
 */
class DdicRepositoryAccess implements IDdicRepositoryAccess {
  private static final String KEY_PROP = "ddicIsKey"; //$NON-NLS-1$

  @Override
  public List<IElementInfo> getElementColumnInformation(final String destinationId,
      final String objectUri) {

    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    final IElementInfoCollection ddicInfo = accessDdicInformation(destinationId, "getColumns", //$NON-NLS-1$
        objectUri, null, Arrays.asList("noClientCols:X"), projectProvider); //$NON-NLS-1$

    return filterColumns(ddicInfo);
  }

  private List<IElementInfo> filterColumns(final IElementInfoCollection ddicInfo) {
    final List<IElementInfo> columns = ddicInfo != null ? ddicInfo.getChildren() : null;
    if (columns == null || columns.isEmpty()) {
      return null;
    }
    final Iterator<IElementInfo> columnIter = columns.iterator();
    while (columnIter.hasNext()) {
      final IElementInfo columnInfo = columnIter.next();
      final String isKeyProp = columnInfo.getPropertyValue(KEY_PROP);
      if ("X".equalsIgnoreCase(isKeyProp)) { //$NON-NLS-1$
        columnInfo.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.KEY_COLUMN));
      } else {
        columnInfo.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.COLUMN));
      }
    }
    return columns;
  }

  @Override
  public IAdtObjectReference getColumnUri(final String destinationId, final String objectName,
      final String column) {
    final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
        .getProviderForDestination(destinationId);
    if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
      return null;
    }
    final IElementInfoCollection ddicInfo = accessDdicInformation(destinationId, "getUriForPaths", //$NON-NLS-1$
        null, Arrays.asList(String.format("%s.%s", objectName, column)), null, projectProvider); //$NON-NLS-1$
    if (ddicInfo != null && ddicInfo.hasChildren()) {
      final IElementInfo firstInfo = ddicInfo.getChildren().get(0);
      return firstInfo instanceof IAdtObjectReferenceElementInfo
          ? ((IAdtObjectReferenceElementInfo) firstInfo).getAdtObjectReference()
          : null;
    }
    return null;
  }

  /*
   * Retrieves DDIC information
   */
  private IElementInfoCollection accessDdicInformation(final String destinationId,
      final String accessMode, final String objectUri, final List<String> paths,
      final List<String> filters, final IAbapProjectProvider projectProvider) {
    IElementInfoCollection ddicInfo = null;

    final IContentHandler<IElementInfoCollection> handler = new ElementInfoCollectionContentHandler(
        destinationId);
    final URI resourceUri = new DdicRepositoryAccessUriDiscovery(projectProvider.getDestinationId())
        .createDdicAccessResource(accessMode, objectUri, paths, filters);

    final ISystemSession session = projectProvider.createStatelessSession();
    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(resourceUri, session);
    restResource.addContentHandler(handler);

    try {
      ddicInfo = restResource.get(null, AdtUtil.getHeaders(), IElementInfoCollection.class);
    } catch (final ResourceException exc) {
      exc.printStackTrace();
    }
    return ddicInfo;
  }

}
