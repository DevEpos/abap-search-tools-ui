package com.devepos.adt.saat.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class Executable implements IExecutable {

	@Override
	public IAction createAction(final String name, final String imageId) {
		ImageDescriptor image = null;
		if (imageId != null) {
			image = SearchAndAnalysisPlugin.getDefault().getImageDescriptor(imageId);
		}
		final Action action = new Action(name, image) {
			@Override
			public void run() {
				execute();
			}
		};
		return action;
	}

}
