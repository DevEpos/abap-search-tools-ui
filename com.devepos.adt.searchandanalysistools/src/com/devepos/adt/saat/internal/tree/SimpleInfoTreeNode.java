package com.devepos.adt.saat.internal.tree;

public class SimpleInfoTreeNode extends TreeNodeBase {
	private final String imageId;

	public SimpleInfoTreeNode(final String name, final String imageId, final ITreeNode parent) {
		this(name, name, imageId, null, parent);
	}

	public SimpleInfoTreeNode(final String name, final String displayName, final String imageId, final String description,
		final ITreeNode parent) {
		super(name, displayName, description, parent);
		this.imageId = imageId;
	}

	@Override
	public String getImageId() {
		return this.imageId;
	}

}
