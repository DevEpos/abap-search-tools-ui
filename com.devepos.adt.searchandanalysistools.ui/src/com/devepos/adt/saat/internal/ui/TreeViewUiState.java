package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * UI state for a view that holds a tree viewer as its main widget
 *
 * @author stockbal
 */
public class TreeViewUiState extends ViewUiState {
  protected TreePath[] expandedTreePaths;

  /**
   * Creates new UI state for the given Tree viewer
   *
   * @param viewer the Tree Viewer to be used for the ui state
   * @return the create UI state or <code>null</code>
   */
  public void setFromTreeViewer(final TreeViewer viewer) {
    if (viewer == null || viewer.getControl().isDisposed()) {
      return;
    }
    expandedTreePaths = viewer.getExpandedTreePaths();
    selection = viewer.getSelection();
  }

  /**
   * Applies the properties of this UI state to the given tree viewer
   *
   * @param viewer a Tree Viewer
   */
  public void applyToTreeViewer(final TreeViewer viewer) {
    if (viewer == null || viewer.getControl().isDisposed()) {
      return;
    }
    if (expandedTreePaths != null) {
      viewer.setExpandedTreePaths(expandedTreePaths);
    }
    if (selection != null) {
      viewer.setSelection(selection);
    }
  }

  /**
   * @return the expandedTreePaths
   */
  public TreePath[] getExpandedTreePaths() {
    return expandedTreePaths;
  }

  /**
   * @param expandedTreePaths the expandedTreePaths to set
   */
  public void setExpandedTreePaths(final TreePath[] expandedTreePaths) {
    this.expandedTreePaths = expandedTreePaths;
  }
}
