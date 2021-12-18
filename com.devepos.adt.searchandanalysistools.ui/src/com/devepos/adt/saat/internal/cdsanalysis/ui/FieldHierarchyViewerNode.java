package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;

import com.devepos.adt.base.ui.tree.ILazyLoadingNode;

/**
 * Root node for the Field Hierarchy {@link TreeViewer}
 *
 * @author stockbal
 */
public class FieldHierarchyViewerNode {
  private final ILazyLoadingNode node;
  private boolean inputLoaded;
  private TreePath[] expandedTreePaths;

  public FieldHierarchyViewerNode(final ILazyLoadingNode node) {
    this.node = node;
  }

  public Object getInput() {
    if (!inputLoaded) {
      inputLoaded = true;
    }
    return node;
  }

  public ILazyLoadingNode getNodeValue() {
    return node;
  }

  public boolean isInputAlreadyLoaded() {
    return inputLoaded;
  }

  public boolean hasContent() {
    return node != null;
  }

  public void setExpandedState(final TreePath[] paths) {
    expandedTreePaths = paths;
  }

  public TreePath[] getExpandedState() {
    return expandedTreePaths == null ? new TreePath[0] : expandedTreePaths;
  }

  public void refreshInput() {
    if (node == null) {
      return;
    }
    inputLoaded = false;
    expandedTreePaths = null;
    node.resetLoadedState();
  }
}
