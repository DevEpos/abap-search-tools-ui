package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Action to collapse the tree for a specific node
 *
 * @author stockbal
 */
public class CollapseTreeNodesAction extends Action {
	private final TreeViewer viewer;

	public CollapseTreeNodesAction(final TreeViewer viewer) { // , final List<Object> nodes) {
		super(Messages.Actions_CollapseNode_xtol, SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.COLLAPSE_ALL));
		this.viewer = viewer;
	}

	@Override
	public void run() {
		final IStructuredSelection selection = this.viewer.getStructuredSelection();
		if (selection == null) {
			return;
		}
		for (final Object sel : selection.toList()) {
			this.viewer.collapseToLevel(sel, TreeViewer.ALL_LEVELS);
		}
	}
}