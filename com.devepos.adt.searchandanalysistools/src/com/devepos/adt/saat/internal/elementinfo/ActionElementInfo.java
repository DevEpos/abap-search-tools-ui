package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.IExecutable;

/**
 * Represents the action which can be executed on an element information
 *
 * @author stockbal
 */
public class ActionElementInfo extends ElementInfoBase implements IExecutableElementInfo {

	private final IExecutable action;

	public ActionElementInfo(final String name, final String imageId, final IExecutable action) {
		super(name, imageId);
		this.action = action;
	}

	@Override
	public IExecutable getExecutable() {
		return this.action;
	}

}
