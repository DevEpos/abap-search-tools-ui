package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.CdsTopDownElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.tools.base.destinations.IDestinationProvider;
import com.devepos.adt.tools.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.tools.base.ui.tree.LazyLoadingAdtObjectReferenceNode;

/**
 * Top Down analysis for a CDS view
 *
 * @author stockbal
 */
public class CdsTopDownAnalysis extends CdsAnalysis {

	private final LazyLoadingAdtObjectReferenceNode cdsNode;

	public CdsTopDownAnalysis(final IAdtObjectReferenceElementInfo cdsObjInfo) {
		super(cdsObjInfo);
		final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(cdsObjInfo.getName(),
			cdsObjInfo.getDisplayName(), cdsObjInfo.getDescription(), cdsObjInfo.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = cdsObjInfo.getAdapter(IDestinationProvider.class);
		node.setElementInfoProvider(new CdsTopDownElementInfoProvider(
			destProvider != null ? destProvider.getDestinationId() : null, cdsObjInfo.getName()));
		node.setAdditionalInfo(cdsObjInfo.getAdditionalInfo());
		this.cdsNode = node;
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.TOP_DOWN);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TOP_DOWN);
	}

	@Override
	public String getLabelPrefix() {
		return Messages.CdsTopDownAnalysisView_ViewLabel_xfld;
	}

	@Override
	public CdsAnalysisType getType() {
		return CdsAnalysisType.TOP_DOWN;
	}

	@Override
	public Object getResult() {
		return new Object[] { this.cdsNode };
	}

	@Override
	public void refreshAnalysis() {
		if (this.cdsNode == null) {
			return;
		}
		this.cdsNode.resetLoadedState();
	}

}
