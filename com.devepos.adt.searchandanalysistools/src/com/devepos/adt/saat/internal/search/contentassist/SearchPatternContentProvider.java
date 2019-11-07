package com.devepos.adt.saat.internal.search.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Provides content for object search pattern content assist queries
 *
 * @author stockbal
 *
 */
public class SearchPatternContentProvider {
	boolean pendingResultsExist;
	private String pendingQuery;
	private final List<IContentProposal> queryResults;
	private Job queryJob;
	private final Object queryJobSemaphor;
	private final long jobDelay = 400L;
	private final ListenerList<IContentChangeListener> proposalListeners;
	private final SearchPatternAnalyzer patternAnalyzer;

	public SearchPatternContentProvider(final SearchPatternAnalyzer patternAnalyzer) {
		this.patternAnalyzer = patternAnalyzer;
		this.queryJobSemaphor = new Object();
		this.proposalListeners = new ListenerList<>();
		this.queryResults = new ArrayList<>();
	}

	/**
	 * Adds the given listener to the list of proposal listeners list
	 *
	 * @param listener
	 *          the content change listener to be registered
	 */
	public void addContentChangeListener(final IContentChangeListener listener) {
		this.proposalListeners.add(listener);
	}

	/**
	 * Removes the given listener from the proposal listeners list
	 *
	 * @param listener
	 *          the content change listener to be removed
	 */
	public void removeContentChangeListener(final IContentChangeListener listener) {
		this.proposalListeners.remove(listener);
	}

	/**
	 * Computes search pattern proposals for the given query string
	 *
	 * @param query
	 *          the search query String
	 * @return determined proposals for the given query or <code>null</code> if
	 *         none could be found
	 */
	public List<IContentProposal> computeProposals(final String query) {
		final List<IContentProposal> result = new ArrayList<>();
		result.add(new ContentProposal("", Messages.SearchPatternProvider_loading_xmsg, null));
		if (this.pendingQuery != null) {
			if (this.pendingResultsExist && this.pendingQuery.equalsIgnoreCase(query)) {
				return this.queryResults;
			}
			if (this.isQueryJobRunning()) {
				if (this.pendingQuery.equalsIgnoreCase(query)) {
					return result;
				}
				this.cancelQueryJob();
			}
		}
		this.createAndStartQueryJob(query);
		return result;
	}

	private void notfiyListenersOfProposalsLoaded() {
		for (final IContentChangeListener listener : this.proposalListeners) {
			listener.notifyContentChange(this.queryResults);
		}
	}

	/**
	 * Creates a new Query proposal job with the given query string
	 *
	 * @param query
	 *          the String the job shall be created for
	 */
	private void createAndStartQueryJob(final String query) {
		synchronized (this.queryJobSemaphor) {
			this.pendingQuery = query;
			this.pendingResultsExist = false;
			(this.queryJob = new QueryJob(this, query)).addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(final IJobChangeEvent event) {
					SearchPatternContentProvider.this.queryJob = null;
					final IStatus result = event.getResult();
					if (result != null && result.getSeverity() != 4) {
						result.getSeverity();
					}
					if (SearchPatternContentProvider.this.pendingQuery != null) {
						SearchPatternContentProvider.this.pendingQuery = null;
						Display.getDefault().asyncExec(() -> {
							// notify proposal listeners of calculated result
							if (SearchPatternContentProvider.this.pendingResultsExist) {
								SearchPatternContentProvider.this.notfiyListenersOfProposalsLoaded();
							}
						});
					}
				}

			});
			this.queryJob.schedule(this.jobDelay);
		}
	}

	/**
	 * Cancels the currently running query proposal job
	 */
	private void cancelQueryJob() {
		synchronized (this.queryJobSemaphor) {
			if (this.isQueryJobRunning()) {
				this.queryJob.cancel();
			}
		}
	}

	/**
	 * Checks if there currently is a query job running or not
	 *
	 * @return
	 */
	private boolean isQueryJobRunning() {
		return this.queryJob != null;
	}

	/**
	 * Job to query for proposals
	 *
	 * @author stockbal
	 *
	 */
	private static class QueryJob extends Job {

		private final SearchPatternContentProvider provider;
		private final String query;

		public QueryJob(final SearchPatternContentProvider contentProvider, final String query) {
			super(NLS.bind(Messages.SearchPatternContentProvider_loadingJob_xmsg, query));
			this.provider = contentProvider;
			this.query = query;
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			List<IContentProposal> proposals;
			try {
				proposals = this.provider.patternAnalyzer.getProposals(this.query);
				if (monitor.isCanceled()) {
					this.provider.pendingResultsExist = false;
					return Status.CANCEL_STATUS;
				}
				this.provider.pendingResultsExist = true;
			} catch (final OperationCanceledException e) {
				throw e;
			} catch (final Exception e2) {
				this.provider.pendingResultsExist = false;
				return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, e2.getLocalizedMessage(), e2);
			}
			synchronized (this.provider.queryResults) {
				this.provider.queryResults.clear();
				if (proposals != null && proposals.size() > 0) {
					this.provider.queryResults.addAll(proposals);
				}
			}
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}
}
