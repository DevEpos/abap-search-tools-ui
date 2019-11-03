package com.devepos.adt.saat.internal.elementinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of element information objects
 *
 * @author stockbal
 */
public class ElementInfoCollection extends ElementInfoBase implements IElementInfoCollection {
	private List<IElementInfo> children;

	public ElementInfoCollection() {
		super();
	}

	public ElementInfoCollection(final String name) {
		super(name);
	}

	public ElementInfoCollection(final String name, final String imageId) {
		super(name, imageId);
	}

	public ElementInfoCollection(final String name, final String displayName, final String imageId, final String description) {
		super(name, displayName, imageId, description);
	}

	@Override
	public List<IElementInfo> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}

	@Override
	public int size() {
		return this.children == null ? 0 : this.children.size();
	}

	@Override
	public IElementInfo getChild(final String name) {
		if (!hasChildren() || name == null) {
			return null;
		}
		return this.children.stream().filter(el -> name.equals(name)).findFirst().orElse(null);
	}

	@Override
	public boolean hasChild(final String name) {
		if (!hasChildren() || name == null) {
			return false;
		}
		return this.children.stream().anyMatch(el -> name.equals(el.getName()));
	}

}
