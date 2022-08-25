package com.devepos.adt.saat.internal.search.ui;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.util.AdtUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ObjectSearchContentHandler;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;

/**
 * Represents an Object Search Query which can be executed from the eclipse
 * search dialog
 *
 * @author stockbal
 */
public class ObjectSearchQuery implements ISearchQuery {
  private static final String TRUE = "X"; //$NON-NLS-1$
  private ObjectSearchRequest searchRequest;
  private final ObjectSearchResult searchResult;

  /**
   * Creates a new Object Search Query from the given search request. The
   * {@link ObjectSearchRequest} holds all the data needed to execute this query
   *
   * @param searchRequest the search request of this query
   */
  public ObjectSearchQuery(final ObjectSearchRequest searchRequest) {
    this.searchRequest = searchRequest;
    searchResult = new ObjectSearchResult(this);
  }

  /**
   * Sets the search request for this query
   *
   * @param searchRequest the new search request
   */
  public void setSearchRequest(final ObjectSearchRequest searchRequest) {
    this.searchRequest = searchRequest;
  }

  @Override
  public IStatus run(final IProgressMonitor monitor) throws OperationCanceledException {
    searchResult.cleanup();

    // perform object search
    final String destinationId = searchRequest.getDestinationId();
    IAbapProjectProvider projectProvider = searchRequest.getProjectProvider();
    if (projectProvider == null) {
      projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
      searchRequest.setProjectProvider(projectProvider);
    }
    if (projectProvider == null) {
      return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          "Destination Id ''{0}'' is not valid", destinationId));
    }
    if (!projectProvider.ensureLoggedOn()) {
      return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.ObjectSearch_ProjectLogonFailed_xmsg, projectProvider.getProjectName()));
    }
    final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(destinationId);

    final Map<String, Object> parameterMap = searchRequest.getParameters();
    // add hidden parameters to search query
    if (searchRequest.isAndSearchActive()) {
      parameterMap.put(QueryParameterName.AND_FILTER.toString(), TRUE);
    }
    if (searchRequest.isReadPackageHierarchy()) {
      parameterMap.put(QueryParameterName.WITH_PACKAGE_HIERARCHY.toString(), TRUE);
    }
    if (searchRequest.shouldReadApiState()) {
      parameterMap.put(QueryParameterName.WITH_API_STATE.toString(), TRUE);
    }
    if (searchRequest.shouldReadAllEntries()) {
      parameterMap.put(QueryParameterName.GET_ALL_RESULTS.toString(), TRUE);
      parameterMap.remove(QueryParameterName.MAX_ROWS.toString());
    } else {
      parameterMap.put(QueryParameterName.MAX_ROWS.toString(), searchRequest.getMaxResults());
    }
    final String searchTerm = searchRequest.getSearchTerm();
    parameterMap.put(QueryParameterName.OBJECT_NAME.toString(), searchTerm != null ? searchTerm
        : "");
    final URI objectSearchUri = uriDiscovery.createResourceUriFromTemplate(searchRequest
        .getSearchType(), parameterMap);
    if (objectSearchUri == null) {
      return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
          Messages.ObjectSearch_SearchNotSupportedInProject_xmsg, projectProvider
              .getProjectName()));
    }

    monitor.beginTask(Messages.ObjectSearch_SearchJobProgressText_xmsg, 1);

    final ISystemSession session = projectProvider.createStatelessSession();

    final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
        .createRestResource(objectSearchUri, session);
    restResource.addContentHandler(new ObjectSearchContentHandler(projectProvider
        .getDestinationId()));

    try {
      final com.devepos.adt.saat.internal.search.ObjectSearchResult searchResult = restResource.get(
          monitor, AdtUtil.getHeaders(),
          com.devepos.adt.saat.internal.search.ObjectSearchResult.class);

      final List<IAdtObjectReferenceElementInfo> rawResult = searchResult.getRawResult();
      if (rawResult != null && !searchRequest.shouldReadAllEntries() && rawResult
          .size() > searchRequest.getMaxResults()) {
        this.searchResult.setHasMoreResults(true);
      }
      this.searchResult.addSearchResult(rawResult, searchResult.getPackageResult());
      monitor.worked(1);
      monitor.done();
      return Status.OK_STATUS;
    } catch (final ResourceException exc) {
      final String localizedMessage = exc.getLocalizedMessage();
      return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, localizedMessage != null
          ? localizedMessage
          : Messages.ObjectSearch_GeneralError_xmsg);
    }
  }

  @Override
  public String getLabel() {
    return searchRequest != null ? Messages.ObjectSearch_SearchQueryLabel_xmsg : "";
  }

  @Override
  public boolean canRerun() {
    return true;
  }

  @Override
  public boolean canRunInBackground() {
    return true;
  }

  public IAbapProjectProvider getProjectProvider() {
    return searchRequest != null ? searchRequest.getProjectProvider() : null;
  }

  @Override
  public ISearchResult getSearchResult() {
    return searchResult;
  }

  /**
   * @return the searchRequest of the search query
   */
  public ObjectSearchRequest getSearchRequest() {
    return searchRequest;
  }

}
