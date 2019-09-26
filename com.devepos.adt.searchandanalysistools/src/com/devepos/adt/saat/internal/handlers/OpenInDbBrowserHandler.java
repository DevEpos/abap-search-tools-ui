package com.devepos.adt.saat.internal.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAdtObject;
import com.devepos.adt.saat.internal.util.OpenInUtil;

public class OpenInDbBrowserHandler extends AbstractHandler {
	public static final String PARAM_SKIP_SELSCREEN = "com.devepos.adt.saat.openindbbrowser.skipSelscreenParam";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final List<IAdtObject> selectedAdtObjects = AdtUtil.getAdtObjectsFromSelection(true);
		if (selectedAdtObjects == null || selectedAdtObjects.isEmpty()) {
			return null;
		}

		final String skipSelscreenParam = event.getParameter(PARAM_SKIP_SELSCREEN);
		if (skipSelscreenParam != null) {
			final boolean skipSelscreen = Boolean.parseBoolean(skipSelscreenParam);
			for (final IAdtObject adtObject : selectedAdtObjects) {
				OpenInUtil.openEntity(adtObject.getProject(), adtObject.getName(), adtObject.getObjectType().getId(),
					skipSelscreen);
			}
		}

		return null;
	}

}
