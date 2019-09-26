package com.devepos.adt.saat.internal.search;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.ObjectSearchContentHandler;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.contentassist.SearchPatternProvider;
import com.devepos.adt.saat.internal.search.model.InternalSearchEngine;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQueryResult;
import com.devepos.adt.saat.internal.search.model.QueryParameterName;
import com.devepos.adt.saat.internal.ui.IStatusUpdater;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryListener;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;

/**
 * Provider implementation
 *
 * @author stockbal
 */
public class ObjectSearchProvider implements IObjectSearchProvider {

	protected Job searchJob;
	protected SearchPatternProvider searchPatternProvider;

	public ObjectSearchProvider() {
		this.searchPatternProvider = new SearchPatternProvider();
	}

	@Override
	public void runSearch(final IObjectSearchQuery query, final IAbapProjectProvider projectProvider) {
		if (this.searchJob != null) {
			if (this.searchJob.getState() == Job.RUNNING || this.searchJob.getState() == Job.WAITING) {
				this.searchJob.cancel();
			}
			this.searchJob = null;
		}
		this.searchPatternProvider.updateProjectProvider(projectProvider);
		this.searchPatternProvider.setSearchType(query.getSearchType());
		this.searchJob = createSearchJob(query, projectProvider, true, null, null);
		if (this.searchJob != null) {
			this.searchJob.schedule();
		}
	}

	@Override
	public void runSearch(final IObjectSearchQuery query, final IAbapProjectProvider projectProvider,
		final IObjectSearchQueryListener searchListener, final IStatusUpdater statusUpdater, final boolean useJob) {

		this.searchPatternProvider.updateProjectProvider(projectProvider);
		this.searchPatternProvider.setSearchType(query.getSearchType());

		if (useJob) {
			if (this.searchJob != null) {
				if (this.searchJob.getState() == Job.RUNNING || this.searchJob.getState() == Job.WAITING) {
					this.searchJob.cancel();
				}
				this.searchJob = null;
			}
			this.searchJob = createSearchJob(query, projectProvider, searchListener == null, searchListener, statusUpdater);
			if (this.searchJob != null) {
				this.searchJob.schedule();
			}
		} else {
			runInternalSearch(query, projectProvider, searchListener == null, searchListener, statusUpdater, null);
		}
	}

	private Job createSearchJob(final IObjectSearchQuery query, final IAbapProjectProvider projectProvider,
		final boolean notifyQueryManager, final IObjectSearchQueryListener searchListener, final IStatusUpdater statusUpdater) {
		this.searchJob = Job.create(Messages.ObjectSearch_SearchJobName_xmsg, (ICoreRunnable) monitor -> {
			runInternalSearch(query, projectProvider, notifyQueryManager, searchListener, statusUpdater, monitor);
		});

		return this.searchJob;
	}

	private void runInternalSearch(final IObjectSearchQuery query, final IAbapProjectProvider projectProvider,
		final boolean notifyQueryManager, final IObjectSearchQueryListener searchListener, final IStatusUpdater statusUpdater,
		final IProgressMonitor monitor) {
		// check if search is possible in selected project
		final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(projectProvider.getDestinationId());

		try {
			this.searchPatternProvider.checkSearchParametersComplete(query.getQuery());
		} catch (final CoreException e) {
			updateSearchStatus(e.getStatus().getSeverity(), e.getMessage(), statusUpdater, notifyQueryManager);
			return;
		}

		final Map<String, Object> parameterMap = this.searchPatternProvider.getSearchParameters(query.getQuery());
		// add hidden parameters to search query
		parameterMap.put(QueryParameterName.AND_FILTER.toString(), Boolean.toString(query.isAndSearchActive()));
		// set the search type manually, as it will not be included in the pattern
		parameterMap.put(QueryParameterName.OBJECT_TYPE.toString(), query.getSearchType().getId());
		if (query.shouldReadApiState()) {
			parameterMap.put(QueryParameterName.WITH_API_STATE.toString(), "X");
		}
		if (query.shouldReadAllEntries()) {
			parameterMap.put(QueryParameterName.GET_ALL_RESULTS.toString(), "X");
		}
		final String searchTerm = this.searchPatternProvider.getSearchTerm(query.getQuery());
		final URI objectSearchUri = uriDiscovery.createResourceUriFromTemplate(parameterMap, searchTerm);
		if (objectSearchUri == null) {
			updateSearchStatus(IStatus.ERROR,
				NLS.bind(Messages.ObjectSearch_SearchNotSupportedInProject_xmsg, projectProvider.getProjectName()), statusUpdater,
				notifyQueryManager);
			if (monitor != null) {
				monitor.setCanceled(true);
			}
			return;
		}

		updateSearchStatus(IStatus.INFO, Messages.ObjectSearch_SearchJobProgressText_xmsg, statusUpdater, notifyQueryManager);

		final ISystemSession session = projectProvider.createStatelessSession();

		final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
			.createRestResource(objectSearchUri, session);
		restResource.addContentHandler(new ObjectSearchContentHandler(projectProvider.getDestinationId()));

		try {
			final IAdtObjectReferenceElementInfo[] searchResult = restResource.get(monitor, AdtUtil.getHeaders(),
				IAdtObjectReferenceElementInfo[].class);
			final int maxResults = (int) parameterMap.get(QueryParameterName.MAX_ROWS.toString());
			if (notifyQueryManager) {
				InternalSearchEngine.getInstance()
					.getQueryManager()
					.queryFinished(new ObjectSearchQueryResult(query, maxResults, searchResult));
			} else {
				searchListener.queryFinished(new ObjectSearchQueryResult(query, maxResults, searchResult));
			}
		} catch (final ResourceException exc) {
			final String localizedMessage = exc.getLocalizedMessage();
			updateSearchStatus(IStatus.ERROR,
				localizedMessage != null ? localizedMessage : Messages.ObjectSearch_GeneralError_xmsg, statusUpdater,
				notifyQueryManager);
		}
		if (monitor != null) {
			monitor.done();
		}
	}

	private void updateSearchStatus(final int statusCode, final String text, final IStatusUpdater statusUpdater,
		final boolean notifyQueryManager) {
		if (!notifyQueryManager && statusUpdater == null) {
			return;
		}

		if (notifyQueryManager) {
			InternalSearchEngine.getInstance().getQueryManager().statusUpdated(statusCode, text, true);
		} else {
			Display.getDefault().asyncExec(() -> {
				statusUpdater.updateStatus(statusCode, text);
			});
		}
	}

}
