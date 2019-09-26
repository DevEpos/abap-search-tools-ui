package com.devepos.adt.saat.internal.ui;

import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Simple loading element to be used in Viewer controls
 *
 * @author stockbal
 */
public class LoadingElement {
	public static LoadingElement INSTANCE;

	static {
		INSTANCE = new LoadingElement();
	}

	public String getDisplayName() {
		return Messages.TreeContentProvider_LoadingText_xtit;
	}

	public String getImageId() {
		return IImages.WAITING;
	}

}
