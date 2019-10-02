package com.devepos.adt.saat.internal.search.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.util.IImages;

public class ObjectSearchResult implements ISearchResult {

	private final ObjectSearchQuery searchQuery;
	private final HashSet<ISearchResultListener> searchResultListener;
	private ITreeNode[] treeResult;
	private int resultCount;
	private boolean hasMoreResults;
	private IAdtObjectReferenceElementInfo[] searchResult;

	public ObjectSearchResult(final ObjectSearchQuery searchQuery) {
		this.searchQuery = searchQuery;
		this.searchResultListener = new HashSet<>();
	}

	@Override
	public void addListener(final ISearchResultListener l) {
		this.searchResultListener.add(l);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.OBJECT_SEARCH);
	}

	@Override
	public String getLabel() {
		String resultsLabel = null;
		if (this.resultCount == 1) {
			resultsLabel = Messages.ObjectSearch_OneResult_xmsg;
		} else if (this.resultCount > 1) {
			if (this.hasMoreResults) {
				resultsLabel = NLS.bind(Messages.ObjectSearch_MoreThanMaxRowsResults_xmsg,
					this.searchQuery.getSearchRequest().getMaxResults());
			} else {
				resultsLabel = NLS.bind(Messages.ObjectSearch_Results_xmsg, this.resultCount);

			}
		} else {
			resultsLabel = Messages.ObjectSearch_NoResults_xmsg;
		}
		final String label = String.format("ABAP DB Object Search: %s - %s", this.searchQuery.getSearchRequest(), resultsLabel);
		return label;
	}

	@Override
	public ISearchQuery getQuery() {
		return this.searchQuery;
	}

	@Override
	public String getTooltip() {
		return getLabel();
	}

	@Override
	public void removeListener(final ISearchResultListener l) {
		this.searchResultListener.remove(l);
	}

	/**
	 * Sets the variable <code>hasMoreResults</code> to indicate that the query has
	 * found more results than specified by the maximum number results restriction
	 *
	 * @param hasMoreResults
	 */
	public void setHasMoreResults(final boolean hasMoreResults) {
		this.hasMoreResults = hasMoreResults;
	}

	public void addSearchResult(final IAdtObjectReferenceElementInfo[] searchResult) {
		final ObjectSearchResultEvent resultEvent = new ObjectSearchResultEvent(this);
		if (searchResult != null && searchResult.length > 0) {
			this.searchResult = searchResult;
			this.treeResult = new IAdtObjectReferenceNode[searchResult.length];
			this.resultCount = searchResult.length;
			for (int i = 0; i < searchResult.length; i++) {
				final IAdtObjectReferenceElementInfo adtObjRefInfo = searchResult[i];
				final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(adtObjRefInfo.getName(),
					adtObjRefInfo.getDisplayName(), adtObjRefInfo.getDescription(), adtObjRefInfo.getAdtObjectReference(), null);
				node.setElementInfoProvider(adtObjRefInfo.getElementInfoProvider());
				node.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
				this.treeResult[i] = node;
			}
		} else {
			this.searchResult = null;
			this.treeResult = null;
			this.resultCount = 0;
		}
		informListener(resultEvent);

	}

	public ITreeNode[] getResultForTree() {
		return this.treeResult != null ? this.treeResult : new ITreeNode[0];
	}

	public List<IAdtObjectReferenceElementInfo> getResult() {
		return this.searchResult != null ? Arrays.asList(this.searchResult) : new ArrayList<>();
	}

	public void cleanup() {
		this.searchResult = null;
		this.resultCount = 0;
		this.hasMoreResults = false;
		this.treeResult = null;
		final ObjectSearchResultEvent resultEvent = new ObjectSearchResultEvent(this);
		resultEvent.setCleanup(true);
		informListener(resultEvent);
	}

	protected void informListener(final ObjectSearchResultEvent resultEvent) {
		for (final ISearchResultListener listener : this.searchResultListener) {
			listener.searchResultChanged(resultEvent);
		}
	}

}
