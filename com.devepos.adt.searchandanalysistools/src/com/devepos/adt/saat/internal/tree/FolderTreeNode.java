package com.devepos.adt.saat.internal.tree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple node in a tree that has child nodes
 *
 * @author stockbal
 */
public class FolderTreeNode extends TreeNodeBase implements ICollectionTreeNode {
	protected List<ITreeNode> children;
	protected String imageId;

	public FolderTreeNode(final String name, final ITreeNode parent, final String imageId, final List<ITreeNode> children) {
		this(name, name, parent, imageId, children);
	}

	public FolderTreeNode(final String name, final String displayName, final ITreeNode parent, final String imageId,
		final List<ITreeNode> children) {
		this(name, displayName, null, parent, imageId, children);
	}

	public FolderTreeNode(final String name, final String displayName, final String description, final ITreeNode parent,
		final String imageId, final List<ITreeNode> children) {
		super(name, displayName, description, parent);
		this.imageId = imageId;
		this.children = children;
	}

	@Override
	public String getImageId() {
		return this.imageId;
	}

	@Override
	public List<ITreeNode> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		return this.children;
	}

	@Override
	public void setChildren(final List<ITreeNode> children) {
		this.children = children;
	}

	@Override
	public void addChild(final ITreeNode child) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(child);
	}

	@Override
	public String getSizeAsString() {
		return new DecimalFormat("###,###").format(getChildren().size());
	}

	@Override
	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}

	@Override
	public int size() {
		return getChildren().size();
	}

}
