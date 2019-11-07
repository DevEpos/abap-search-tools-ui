package com.devepos.adt.saat.internal.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.AdtObjectExecutor;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Action for opening the ADT data preview for a list of ADT object references
 *
 * @author stockbal
 */
public class OpenAdtDataPreviewAction extends Action {
	/**
	 *
	 */
	private final List<IAdtObjectReference> adtObjectReferences;
	private final IProject project;

	public OpenAdtDataPreviewAction(final IProject project, final List<IAdtObjectReference> adtObjectReferences) {
		super(Messages.ObjectSearch_OpenWithADTDataPreview_xmit,
			SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.ADT_DATA_DATA_PREVIEW));
		this.adtObjectReferences = adtObjectReferences;
		this.project = project;
	}

	@Override
	public void run() {
		for (final IAdtObjectReference objRef : this.adtObjectReferences) {
			AdtObjectExecutor.executeObject(this.project, objRef);
		}
	}

}