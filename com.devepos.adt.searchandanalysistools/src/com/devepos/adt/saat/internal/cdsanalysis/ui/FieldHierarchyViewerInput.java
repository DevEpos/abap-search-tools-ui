package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.viewers.TreeViewer;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.FieldWhereUsedInCdsElementInfoProvider;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingFolderNode;

/**
 * Input for the Field Hierarchy Tree Viewer
 *
 * @author stockbal
 */
public class FieldHierarchyViewerInput {
	private final TreeViewer viewer;
	private boolean searchCalcFields;
	private FieldHierarchyViewerNode currentNode;
	private final FieldHierarchyViewerNode topDownNode;
	private FieldHierarchyViewerNode whereUsedNode;
	private final IDestinationProvider destinationProvider;
	private final String baseEntityName;
	private final String baseFieldName;
	private FieldWhereUsedInCdsElementInfoProvider whereUsedProvider;

	public FieldHierarchyViewerInput(final TreeViewer viewer, final ILazyLoadingNode topDownNode, final String baseEntityName,
		final String baseFieldName, final IDestinationProvider destinationProvider) {
		this.viewer = viewer;
		this.topDownNode = new FieldHierarchyViewerNode(topDownNode);
		this.searchCalcFields = false;
		this.baseEntityName = baseEntityName;
		this.baseFieldName = baseFieldName;
		this.destinationProvider = destinationProvider;
		SearchAndAnalysisPlugin.getDefault().getPreferenceStore().addPropertyChangeListener((event) -> {
			if (FieldAnalysisView.SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY.equals(event.getProperty())) {
				if (this.whereUsedProvider != null) {
					this.whereUsedProvider.setSearchDbViews((boolean) event.getNewValue());
				}
			}
		});
	}

	public void setSearchCalcFields(final boolean searchCalcFields) {
		this.searchCalcFields = searchCalcFields;
		this.whereUsedProvider.setSearchCalcFields(searchCalcFields);
	}

	public boolean isSearchCalcFieldsActive() {
		return this.searchCalcFields;
	}

	public void setViewerInput(final boolean topDown) {
		FieldHierarchyViewerNode node = null;
		if (topDown) {
			node = this.topDownNode;
		} else {
			node = this.whereUsedNode;
		}
		if (node == null) {
			return;
		}
		if (this.currentNode != node) {
			cacheCurrentNodeState();
		}
		this.currentNode = node;
		if (node.isInputAlreadyLoaded()) {
			// restore tree state
			this.viewer.setInput(node.getInput());
			this.viewer.setExpandedTreePaths(node.getExpandedState());
		} else {
			this.viewer.setInput(node.getInput());
			this.viewer.expandAll();
		}

	}

	public void cacheCurrentNodeState() {
		if (this.currentNode == null) {
			return;
		}
		this.currentNode.setExpandedState(this.viewer.getExpandedTreePaths());
	}

	public FieldHierarchyViewerNode getTopDownNode() {
		return this.topDownNode;
	}

	public FieldHierarchyViewerNode getWhereUsedNode() {
		return this.whereUsedNode;
	}

	public FieldHierarchyViewerNode getCurrentInput() {
		return this.currentNode;
	}

	/**
	 * Creates the Where-Used node for the Where-Used analysis
	 */
	public void createWhereUsedNode() {
		this.whereUsedProvider = new FieldWhereUsedInCdsElementInfoProvider(this.destinationProvider.getDestinationId(),
			this.baseEntityName, this.baseFieldName, false,
			SearchAndAnalysisPlugin.getDefault().getPreferenceStore().getBoolean(FieldAnalysisView.SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY));
		this.whereUsedNode = new FieldHierarchyViewerNode(
			new LazyLoadingFolderNode(this.baseFieldName, this.whereUsedProvider, null, null));
	}

	/**
	 * Refreshes the input by reloading the asynchronous nodes
	 *
	 * @param topDown if <code>true</code> the top-down analysis should be
	 *                refreshed, otherwise the where-used analysis will be refreshed
	 */
	public void refresh(final boolean topDown) {
		if (this.topDownNode != null) {
			this.topDownNode.refreshInput();
		}
		if (this.whereUsedNode != null) {
			this.whereUsedNode.refreshInput();
		}
		this.currentNode = null;
		setViewerInput(topDown);
	}

}
