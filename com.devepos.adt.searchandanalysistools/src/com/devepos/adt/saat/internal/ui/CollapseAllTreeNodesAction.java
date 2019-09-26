package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Action for collapsing all nodes in the given {@link TreeViewer}
 *
 * @author stockbal
 */
public class CollapseAllTreeNodesAction extends Action {
	private final TreeViewer viewer;

	public CollapseAllTreeNodesAction(final TreeViewer viewer) {
		super(Messages.Actions_CollapseAllNodes_xmit, SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.COLLAPSE_ALL));
		this.viewer = viewer;
	}

	@Override
	public void run() {
		this.viewer.collapseAll();
	}
}