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

}
