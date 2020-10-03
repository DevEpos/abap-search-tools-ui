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
		if (!this.inputLoaded) {
			this.inputLoaded = true;
		}
		return this.node;
	}

	public ILazyLoadingNode getNodeValue() {
		return this.node;
	}

	public boolean isInputAlreadyLoaded() {
		return this.inputLoaded;
	}

	public boolean hasContent() {
		return this.node != null;
	}

	public void setExpandedState(final TreePath[] paths) {
		this.expandedTreePaths = paths;
	}

	public TreePath[] getExpandedState() {
		return this.expandedTreePaths == null ? new TreePath[0] : this.expandedTreePaths;
	}

	public void refreshInput() {
		if (this.node == null) {
			return;
		}
		this.inputLoaded = false;
		this.expandedTreePaths = null;
		this.node.resetLoadedState();
	}
}
