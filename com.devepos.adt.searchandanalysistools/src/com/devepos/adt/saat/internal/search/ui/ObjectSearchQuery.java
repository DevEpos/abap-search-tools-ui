package com.devepos.adt.saat.internal.search.ui;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.ObjectSearchContentHandler;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.model.QueryParameterName;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
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
	private final ObjectSearchRequest searchRequest;
	private final ObjectSearchResult searchResult;

	/**
	 * Creates a new Object Search Query from the given search request. The
	 * {@link ObjectSearchRequest} holds all the data needed to execute this query
	 *
	 * @param searchRequest the search request of this query
	 */
	public ObjectSearchQuery(final ObjectSearchRequest searchRequest) {
		this.searchRequest = searchRequest;
		this.searchResult = new ObjectSearchResult(this);
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) throws OperationCanceledException {
		this.searchResult.cleanup();

		// perform object search
		final IAbapProjectProvider projectProvider = this.searchRequest.getProjectProvider();
		final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(projectProvider.getDestinationId());

		final Map<String, Object> parameterMap = this.searchRequest.getParameters();
		// add hidden parameters to search query
		parameterMap.put(QueryParameterName.AND_FILTER.toString(), Boolean.toString(this.searchRequest.isAndSearchActive()));
		// set the search type manually, as it will not be included in the pattern
		parameterMap.put(QueryParameterName.OBJECT_TYPE.toString(), this.searchRequest.getSearchType().getId());
		if (this.searchRequest.shouldReadApiState()) {
			parameterMap.put(QueryParameterName.WITH_API_STATE.toString(), "X"); //$NON-NLS-1$
		}
		if (this.searchRequest.shouldReadAllEntries()) {
			parameterMap.put(QueryParameterName.GET_ALL_RESULTS.toString(), "X"); //$NON-NLS-1$
			parameterMap.remove(QueryParameterName.MAX_ROWS.toString());
		} else {
			parameterMap.put(QueryParameterName.MAX_ROWS.toString(), this.searchRequest.getMaxResults());
		}
		final String searchTerm = this.searchRequest.getSearchTerm();
		final URI objectSearchUri = uriDiscovery.createResourceUriFromTemplate(parameterMap, searchTerm);
		if (objectSearchUri == null) {
			return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
				NLS.bind(Messages.ObjectSearch_SearchNotSupportedInProject_xmsg, projectProvider.getProjectName()));
		}

		monitor.beginTask(Messages.ObjectSearch_SearchJobProgressText_xmsg, 1);

		final ISystemSession session = projectProvider.createStatelessSession();

		final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
			.createRestResource(objectSearchUri, session);
		restResource.addContentHandler(new ObjectSearchContentHandler(projectProvider.getDestinationId()));

		try {
			final IAdtObjectReferenceElementInfo[] searchResult = restResource.get(monitor, AdtUtil.getHeaders(),
				IAdtObjectReferenceElementInfo[].class);
			if (!this.searchRequest.shouldReadAllEntries() && searchResult.length > this.searchRequest.getMaxResults()) {
				this.searchResult.setHasMoreResults(true);
			}
			this.searchResult.addSearchResult(searchResult);
			monitor.worked(1);
			monitor.done();
			return Status.OK_STATUS;
		} catch (final ResourceException exc) {
			final String localizedMessage = exc.getLocalizedMessage();
			return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
				localizedMessage != null ? localizedMessage : Messages.ObjectSearch_GeneralError_xmsg);
		}
	}

	@Override
	public String getLabel() {
		return this.searchRequest != null ? this.searchRequest.toString() : "";
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
		return this.searchRequest != null ? this.searchRequest.getProjectProvider() : null;
	}

	@Override
	public ISearchResult getSearchResult() {
		return this.searchResult;
	}

	/**
	 * @return the searchRequest of the search query
	 */
	public ObjectSearchRequest getSearchRequest() {
		return this.searchRequest;
	}

}
