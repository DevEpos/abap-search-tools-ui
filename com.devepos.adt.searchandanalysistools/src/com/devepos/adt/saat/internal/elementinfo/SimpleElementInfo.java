package com.devepos.adt.saat.internal.elementinfo;

/**
 * Simple representation of an elements' information
 *
 * @author stockbal
 */
public class SimpleElementInfo extends ElementInfoBase {
	public SimpleElementInfo() {
		super("", "", null, null);
	}

	public SimpleElementInfo(final String name) {
		super(name, name, null, null);
	}

	public SimpleElementInfo(final String name, final String imageId) {
		super(name, name, imageId, null);
	}

	public SimpleElementInfo(final String name, final String displayName, final String imageId,
		final String description) {
		super(name, displayName, imageId, description);
	}
}
