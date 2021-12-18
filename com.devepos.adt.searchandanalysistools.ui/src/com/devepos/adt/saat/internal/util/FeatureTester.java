package com.devepos.adt.saat.internal.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.devepos.adt.base.ui.adtobject.IAdtObject;
import com.devepos.adt.base.util.IUriDiscovery;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.dbbrowserintegration.DbBrowserIntegrationUriDiscovery;
import com.devepos.adt.saat.internal.navtargets.NavigationTargetsUriDiscovery;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.sap.adt.tools.core.project.IAbapProject;

public final class FeatureTester {

  private FeatureTester() {
  }

  /**
   * Checks if the object search is available in the given project
   *
   * @param project ABAP Project
   * @return <code>true</code> if feature is available
   */
  public static boolean isObjectSearchAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

    final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.getObjectSearchUri() != null;
  }

  /**
   * Checks if navigation targets for an ADT object can be determined
   *
   * @param project ABAP Project
   * @return <code>true</code> if feature is available
   */
  public static boolean isNavigationTargetsFeatureAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
    final NavigationTargetsUriDiscovery uriDiscovery = new NavigationTargetsUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.getNavTargetsUri() != null;
  }

  /**
   * Checks if the CDS Analysis feature is available for the given project
   *
   * @param project ABAP Project
   * @return <code>true</code> if feature is available
   */
  public static boolean isCdsAnalysisAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

    final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery.getCdsAnalysisUri() != null;
  }

  /**
   * Checks if the CDS Top Down analysis is available in the given project
   *
   * @param project ABAP Project
   * @return <code>true</code> if feature is available
   */
  public static boolean isCdsTopDownAnalysisAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

    final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery
        .isTopDownAnalysisAvailable();
  }

  /**
   * Checks if the CDS Used Entities Analysis feature is available for the given
   * project
   *
   * @param project ABAP Project
   * @return <code>true</code> if feature is available
   */
  public static boolean isCdsUsedEntitiesAnalysisAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

    final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.isResourceDiscoverySuccessful() && uriDiscovery
        .isUsedEntitiesAnalysisAvailable();
  }

  /**
   * Returns <code>true</code> if the DB Browser Application is available in the
   * given project
   *
   * @param project the project where the availability of the DB Browser should be
   *                checked
   * @return <code>true</code> if the DB Browser Application is available in the
   *         given project
   */
  public static boolean isSapGuiDbBrowserAvailable(final IProject project) {
    final IAbapProject abapProject = project.getAdapter(IAbapProject.class);

    final IUriDiscovery uriDiscovery = new DbBrowserIntegrationUriDiscovery(abapProject
        .getDestinationId());
    return uriDiscovery.isResourceDiscoverySuccessful();
  }

  /**
   * Returns <code>true</code> if the DB Browser integration feature is availabe
   * in the projects of the given ADT Objects
   *
   * @param adtObjects a list of ADT Objects
   * @return <code>true</code> if the DB Browser integration feature is availabe
   *         in the projects of the given ADT Objects
   */
  public static boolean isSapGuiDbBrowserAvailable(final List<IAdtObject> adtObjects) {
    if (adtObjects == null || adtObjects.isEmpty()) {
      return false;
    }
    if (adtObjects.size() == 1) {
      return isSapGuiDbBrowserAvailable(adtObjects.get(0).getProject());
    }
    final Set<IProject> uniqueProjects = new HashSet<>();
    for (final IAdtObject adtObject : adtObjects) {
      uniqueProjects.add(adtObject.getProject());
    }

    for (final IProject project : uniqueProjects) {
      if (!isSapGuiDbBrowserAvailable(project)) {
        return false;
      }
    }
    return true;
  }

}
