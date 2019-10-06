package com.devepos.adt.saat.internal.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * Base Tree node which has a view basic attributes like a name, a separate
 * display name and an optional description
 *
 * @author stockbal
 */
public abstract class TreeNodeBase implements ITreeNode {

	protected String name;
	protected String displayName;
	protected String description;
	protected ITreeNode parent;
	protected Map<String, String> properties;
	private Object additionalInfo;

	public TreeNodeBase(final String name, final ITreeNode parent) {
		this(name, name, "", parent);
	}

	public TreeNodeBase(final String name, final String displayName, final String description, final ITreeNode parent) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.parent = parent;
		this.properties = new HashMap<>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public ITreeNode getParent() {
		return this.parent;
	}

	@Override
	public void setParent(final ITreeNode parent) {
		this.parent = parent;
	}

	@Override
	public ITreeNode getRoot() {
		if (this.parent == null) {
			return this;
		}
		ITreeNode parent = this.parent;
		while (parent.getParent() != null) {
			parent = parent.getParent();
		}
		return parent;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		try {
			return adapter.cast(this.additionalInfo);
		} catch (final ClassCastException exc) {
			return null;
		}
	}

	@Override
	public void setAdditionalInfo(final Object info) {
		this.additionalInfo = info;
	}

	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}

	@Override
	public String getPropertyValue(final String key) {
		return this.properties.get(key);
	}
}
