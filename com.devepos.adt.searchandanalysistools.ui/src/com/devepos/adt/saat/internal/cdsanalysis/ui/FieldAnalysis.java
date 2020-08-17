package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.ddicaccess.DdicRepositoryAccessFactory;
import com.devepos.adt.saat.internal.ddicaccess.IDdicRepositoryAccess;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.tools.base.destinations.IDestinationProvider;
import com.devepos.adt.tools.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.tools.base.elementinfo.IElementInfo;
import com.devepos.adt.tools.base.elementinfo.IElementInfoProvider;
import com.devepos.adt.tools.base.ui.tree.LazyLoadingAdtObjectReferenceNode;

/**
 * Result for CDS Field Analysis
 *
 * @author stockbal
 */
public class FieldAnalysis extends CdsAnalysis {

	private LazyLoadingAdtObjectReferenceNode node;

	public FieldAnalysis(final IAdtObjectReferenceElementInfo adtObjectInfo) {
		super(adtObjectInfo);
		this.node = new LazyLoadingAdtObjectReferenceNode(adtObjectInfo.getName(), adtObjectInfo.getDisplayName(),
			adtObjectInfo.getDescription(), adtObjectInfo.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
		this.node.setElementInfoProvider(new IElementInfoProvider() {
			@Override
			public String getProviderDescription() {
				return NLS.bind(Messages.FieldAnalysisView_FieldLoadingProviderDesc_xmsg, adtObjectInfo.getDisplayName());
			}

			@Override
			public List<IElementInfo> getElements() {
				final IDdicRepositoryAccess ddicRepoAccess = DdicRepositoryAccessFactory.createDdicAccess();
				return ddicRepoAccess.getElementColumnInformation(destProvider.getDestinationId(), adtObjectInfo.getUri());
			}
		});
	}

	@Override
	protected String getLabelPrefix() {
		return Messages.FieldAnalysisView_ViewLabel_xfld;
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.FIELD_ANALYSIS);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.FIELD_ANALYSIS);
	}

	@Override
	public CdsAnalysisType getType() {
		return CdsAnalysisType.FIELD_ANALYSIS;
	}

	@Override
	public Object getResult() {
		return new Object[] { this.node };
	}

	@Override
	public void refreshAnalysis() {
		this.node.resetLoadedState();
	}

}
