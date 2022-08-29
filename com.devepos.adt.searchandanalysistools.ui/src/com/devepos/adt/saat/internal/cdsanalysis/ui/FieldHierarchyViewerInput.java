package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.viewers.TreeViewer;

import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.ui.tree.ILazyLoadingNode;
import com.devepos.adt.base.ui.tree.LazyLoadingFolderNode;
import com.devepos.adt.saat.internal.cdsanalysis.FieldWhereUsedInCdsElementInfoProvider;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsFieldAnalysisSettings;

/**
 * Input for the Field Hierarchy Tree Viewer
 *
 * @author stockbal
 */
public class FieldHierarchyViewerInput {
  private final TreeViewer viewer;
  private FieldHierarchyViewerNode currentNode;
  private final FieldHierarchyViewerNode topDownNode;
  private FieldHierarchyViewerNode whereUsedNode;
  private final IDestinationProvider destinationProvider;
  private final String baseEntityName;
  private final String baseFieldName;
  private FieldWhereUsedInCdsElementInfoProvider whereUsedProvider;

  public FieldHierarchyViewerInput(final TreeViewer viewer, final ILazyLoadingNode topDownNode,
      final String baseEntityName, final String baseFieldName,
      final IDestinationProvider destinationProvider) {
    this.viewer = viewer;
    this.topDownNode = new FieldHierarchyViewerNode(topDownNode);
    this.baseEntityName = baseEntityName;
    this.baseFieldName = baseFieldName;
    this.destinationProvider = destinationProvider;
  }

  public void setViewerInput(final boolean topDown) {
    FieldHierarchyViewerNode node = null;
    if (topDown) {
      node = topDownNode;
    } else {
      node = whereUsedNode;
    }
    if (node == null) {
      return;
    }
    if (currentNode != node) {
      cacheCurrentNodeState();
    }
    currentNode = node;
    if (node.isInputAlreadyLoaded()) {
      // restore tree state
      viewer.setInput(node.getInput());
      viewer.setExpandedTreePaths(node.getExpandedState());
    } else {
      viewer.setInput(node.getInput());
      viewer.expandAll();
    }

  }

  public void cacheCurrentNodeState() {
    if (currentNode == null) {
      return;
    }
    currentNode.setExpandedState(viewer.getExpandedTreePaths());
  }

  public FieldHierarchyViewerNode getTopDownNode() {
    return topDownNode;
  }

  public FieldHierarchyViewerNode getWhereUsedNode() {
    return whereUsedNode;
  }

  public FieldHierarchyViewerNode getCurrentInput() {
    return currentNode;
  }

  /**
   * Creates the Where-Used node for the Where-Used analysis
   */
  public void createWhereUsedNode(final ICdsFieldAnalysisSettings settings) {
    whereUsedProvider = new FieldWhereUsedInCdsElementInfoProvider(destinationProvider
        .getDestinationId(), baseEntityName, baseFieldName, settings);
    whereUsedNode = new FieldHierarchyViewerNode(new LazyLoadingFolderNode(baseFieldName,
        whereUsedProvider, null, null));
  }

  /**
   * Refreshes the input by reloading the asynchronous nodes
   *
   * @param topDown if <code>true</code> the top-down analysis should be
   *                refreshed, otherwise the where-used analysis will be refreshed
   */
  public void refresh(final boolean topDown) {
    if (topDownNode != null) {
      topDownNode.refreshInput();
    }
    if (whereUsedNode != null) {
      whereUsedNode.refreshInput();
    }
    currentNode = null;
    setViewerInput(topDown);
  }

}
