package com.devepos.adt.saat.internal.search.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.IExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.tools.base.IAdtObjectTypeConstants;
import com.devepos.adt.tools.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.tools.base.ui.tree.AdtObjectReferenceNode;
import com.devepos.adt.tools.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.tools.base.ui.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.tools.base.ui.tree.PackageNode;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public class ObjectSearchResult implements ISearchResult {

	private static final String TEMP_PACKAGE_NAME = "$TMP"; //$NON-NLS-1$
	private static final String KEY_PATTERN = "%s:%s"; //$NON-NLS-1$
	private final ObjectSearchQuery searchQuery;
	private final HashSet<ISearchResultListener> searchResultListener;
	private IAdtObjectReferenceNode[] treeResult;
	private IAdtObjectReferenceNode[] packages;
	private boolean isGroupedResult;
	private int resultCount;
	private boolean hasMoreResults;
	private IAdtObjectReference tempPackageReference;
	private static final IAdtObjectReferenceNode[] EMPTY_RESULT = new IAdtObjectReferenceNode[0];
	private List<IAdtObjectReferenceElementInfo> searchResult;
	private List<IAdtObjectReferenceElementInfo> packageResult;

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
		final String label = NLS.bind(Messages.ObjectSearch_SearchResultLabel_xmsg, this.searchQuery.getSearchRequest(),
			resultsLabel);
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

	public void addSearchResult(final List<IAdtObjectReferenceElementInfo> searchResult,
		final List<IAdtObjectReferenceElementInfo> packageResult) {
		final ObjectSearchResultEvent resultEvent = new ObjectSearchResultEvent(this);
		if (searchResult != null && searchResult.size() > 0) {
			this.searchResult = searchResult;
			this.packageResult = packageResult;
			this.resultCount = this.searchResult.size();
		} else {
			this.searchResult = null;
			this.packageResult = null;
			this.packages = null;
			this.treeResult = null;
			this.resultCount = 0;
		}
		informListener(resultEvent);
	}

	/**
	 * Returns an Array of Tree Nodes, where the root nodes are either CDS Views,
	 * Database Tables or Views
	 *
	 * @param  groupByPackage if <code>true</code> the search result should be
	 *                        grouped by their packages
	 * @return                an Array of Tree Nodes, where the root nodes are
	 *                        either CDS Views, Database Tables or Views
	 */
	public IAdtObjectReferenceNode[] getResultForTree(final boolean groupByPackage) {
		if (this.resultCount == 0) {
			return EMPTY_RESULT;
		}
		if (this.treeResult == null || this.treeResult == EMPTY_RESULT || groupByPackage != this.isGroupedResult) {
			this.isGroupedResult = groupByPackage;
			if (groupByPackage) {
				createGroupedResult();
			} else {
				createResult();
			}
		}
		return this.treeResult;
	}

	public List<IAdtObjectReferenceElementInfo> getResult() {
		return this.searchResult != null ? this.searchResult : new ArrayList<>();
	}

	public void cleanup() {
		this.searchResult = null;
		this.packages = null;
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

	private void createResult() {
		final List<IAdtObjectReferenceNode> nodes = new ArrayList<>(this.resultCount);

		for (final IAdtObjectReferenceElementInfo adtObjRefInfo : this.searchResult) {
			if (IAdtObjectTypeConstants.PACKAGE.equals(adtObjRefInfo.getAdtType())) {
				continue;
			}
			if (adtObjRefInfo.hasLazyLoadingSupport()) {
				final LazyLoadingAdtObjectReferenceNode lazyLoadingNode = new LazyLoadingAdtObjectReferenceNode(
					adtObjRefInfo.getName(), adtObjRefInfo.getDisplayName(), adtObjRefInfo.getDescription(),
					adtObjRefInfo.getAdtObjectReference(), null);
				lazyLoadingNode.setElementInfoProvider(adtObjRefInfo.getElementInfoProvider());
				lazyLoadingNode.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
				nodes.add(lazyLoadingNode);
			} else {
				final AdtObjectReferenceNode nonLazyLoadingNode = new AdtObjectReferenceNode(adtObjRefInfo.getName(),
					adtObjRefInfo.getDisplayName(), adtObjRefInfo.getDescription(), adtObjRefInfo.getAdtObjectReference(), null);
				nonLazyLoadingNode.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
				nodes.add(nonLazyLoadingNode);
			}
		}
		this.treeResult = nodes.toArray(new IAdtObjectReferenceNode[nodes.size()]);
	}

	private void createGroupedResult() {
		final List<IAdtObjectReferenceNode> nodes = new LinkedList<>();
		final List<IAdtObjectReferenceNode> packageNodes = new LinkedList<>();
		final Map<String, IAdtObjectReferenceNode> nodeMap = new TreeMap<>();

		for (final IAdtObjectReferenceElementInfo adtObjRefInfo : this.searchResult) {
			IAdtObjectReferenceNode node = null;
			if (adtObjRefInfo.hasLazyLoadingSupport()) {
				final LazyLoadingAdtObjectReferenceNode lazyLoadingNode = new LazyLoadingAdtObjectReferenceNode(
					adtObjRefInfo.getName(), adtObjRefInfo.getDisplayName(), adtObjRefInfo.getDescription(),
					adtObjRefInfo.getAdtObjectReference(), null);
				lazyLoadingNode.setElementInfoProvider(adtObjRefInfo.getElementInfoProvider());
				lazyLoadingNode.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
				node = lazyLoadingNode;
			} else {
				final AdtObjectReferenceNode nonLazyLoadingNode = new AdtObjectReferenceNode(adtObjRefInfo.getName(),
					adtObjRefInfo.getDisplayName(), adtObjRefInfo.getDescription(), adtObjRefInfo.getAdtObjectReference(), null);
				nonLazyLoadingNode.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
				node = nonLazyLoadingNode;
			}

			node.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());

			nodeMap.put(String.format(KEY_PATTERN, adtObjRefInfo.getName(), adtObjRefInfo.getAdtType()), node);
		}
		for (final IAdtObjectReferenceElementInfo adtObjRefInfo : this.packageResult) {
			IAdtObjectReferenceNode node = null;
			if (TEMP_PACKAGE_NAME.equals(adtObjRefInfo.getName())) {
				this.tempPackageReference = adtObjRefInfo.getAdtObjectReference();
				continue;
			}
			node = new PackageNode(adtObjRefInfo.getName(), adtObjRefInfo.getDescription(),
				adtObjRefInfo.getAdtObjectReference());
			node.setAdditionalInfo(adtObjRefInfo.getAdditionalInfo());
			packageNodes.add(node);
			nodeMap.put(String.format(KEY_PATTERN, adtObjRefInfo.getName(), adtObjRefInfo.getAdtType()), node);
		}

		final Map<String, IAdtObjectReferenceNode> tmpPackageNodeMap = new HashMap<>();
		final List<IAdtObjectReferenceNode> tmpPackageNodes = new LinkedList<>();

		// build the tree nodes
		for (final Entry<String, IAdtObjectReferenceNode> entry : nodeMap.entrySet()) {
			final IAdtObjectReferenceNode node = entry.getValue();
			final String packageName = node.getObjectReference().getPackageName();
			final String[] keyComponents = entry.getKey().split(":"); //$NON-NLS-1$
			final boolean isPackage = IAdtObjectTypeConstants.PACKAGE.equals(keyComponents[1]);
			final boolean hasParent = packageName != null && !packageName.isEmpty();

			if (isPackage) {
				if (!hasParent) {
					nodes.add(node);
				}
			}
			if (hasParent) {
				// find parent in node map --> parent has to be a package
				if (packageName.equals(TEMP_PACKAGE_NAME)) {
					handleTmpPackageObject(node, tmpPackageNodeMap, tmpPackageNodes);
				} else {
					// find parent in node map --> parent has to be a package
					final String parentKey = String.format(KEY_PATTERN, packageName, IAdtObjectTypeConstants.PACKAGE);
					final IAdtObjectReferenceNode parentNode = nodeMap.get(parentKey);
					if (parentNode != null) {
						parentNode.addChild(node);
					}
				}
			}
		}
		packageNodes.addAll(tmpPackageNodes);
		this.packages = packageNodes.toArray(new IAdtObjectReferenceNode[packageNodes.size()]);

		tmpPackageNodes.addAll(nodes);
		this.treeResult = tmpPackageNodes.toArray(new IAdtObjectReferenceNode[tmpPackageNodes.size()]);
	}

	private void handleTmpPackageObject(final IAdtObjectReferenceNode node,
		final Map<String, IAdtObjectReferenceNode> tmpPackageMap, final List<IAdtObjectReferenceNode> tmpPackageNodes) {

		final IExtendedAdtObjectInfo extendedInfo = node.getAdapter(IExtendedAdtObjectInfo.class);
		final String owner = extendedInfo != null ? extendedInfo.getOwner() : "";

		final String personalizedTmpPackageName = TEMP_PACKAGE_NAME + " - " + owner; //$NON-NLS-1$

		final String parentKey = String.format(KEY_PATTERN, personalizedTmpPackageName, IAdtObjectTypeConstants.PACKAGE);

		IAdtObjectReferenceNode tempPackageNode = tmpPackageMap.get(parentKey);
		if (tempPackageNode == null) {
			tempPackageNode = new PackageNode(personalizedTmpPackageName, null, this.tempPackageReference);
			tmpPackageMap.put(parentKey, tempPackageNode);
			tmpPackageNodes.add(tempPackageNode);
		}
		tempPackageNode.addChild(node);
	}

	/**
	 * Returns the package ADT objects in the search result
	 *
	 * @return
	 */
	public IAdtObjectReferenceNode[] getPackages() {
		return this.packages;
	}
}
