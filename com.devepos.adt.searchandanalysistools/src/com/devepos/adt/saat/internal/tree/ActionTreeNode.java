package com.devepos.adt.saat.internal.tree;

import com.devepos.adt.saat.internal.IExecutable;

/**
 * Represents a node in a {@link org.eclipse.jface.viewers.TreeViewer}
 *
 * @author stockbal
 */
public class ActionTreeNode extends SimpleInfoTreeNode {
	private final IExecutable action;

	public ActionTreeNode(final String name, final String imageId, final ITreeNode parent, final IExecutable action) {
		super(name, imageId, parent);
		this.action = action;
	}

	public IExecutable getAction() {
		return this.action;
	}

}
