package com.devepos.adt.saat.internal.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis.AnalysisMode;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.ViewPartLookup;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.AbapProjectProxy;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAdtObject;
import com.sap.adt.tools.core.project.IAbapProject;

/**
 * Handler for the command to open a CDS view in the CDS Analyzer View
 *
 * @author stockbal
 */
public abstract class OpenInCdsAnalyzerHandler extends AbstractHandler {
	private final AnalysisMode mode;

	protected OpenInCdsAnalyzerHandler(final AnalysisMode mode) {
		this.mode = mode;
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final List<IAdtObject> selectedObjects = AdtUtil.getAdtObjectsFromSelection(true);
		if (selectedObjects == null || selectedObjects.size() > 1) {
			return null;
		}
		final IAdtObject selectedObject = selectedObjects.get(0);
		final IProject project = selectedObject.getProject();
		if (!isFeatureAvailable(project)) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.Dialog_InfoTitle_xmsg,
				NLS.bind(Messages.CdsAnalysis_FeatureIsNotSupported_xmsg, AdtUtil.getDestinationId(project)));
			return null;
		}
		if (!canExecute(selectedObject)) {
			return null;
		}
		final CdsAnalysis cdsAnalyzerView = ViewPartLookup.getCdsAnalysisView();
		if (cdsAnalyzerView != null) {
			cdsAnalyzerView.setFocus();
			final IAbapProject abapProject = selectedObject.getProject().getAdapter(IAbapProject.class);
			// register the abapProject
			AbapProjectProviderAccessor.registerProjectProvider(new AbapProjectProxy(selectedObject.getProject()));
			// set the selected CDS view in the Analysis View
			cdsAnalyzerView.analyzeAdtObject(this.mode, selectedObject.getName(), selectedObject.getObjectType(),
				abapProject.getDestinationId());
		}
		return null;
	}

	protected boolean isFeatureAvailable(final IProject project) {
		if (project == null) {
			return false;
		}
		return AdtUtil.isCdsAnalysisAvailable(project);
	}

	protected boolean canExecute(final IAdtObject selectedObject) {
		return selectedObject.getObjectType() == ObjectType.CDS_VIEW;
	}
}
