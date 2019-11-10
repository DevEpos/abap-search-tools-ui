package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisServiceFactory;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoCollection;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Aggregated analysis of Dependency tree result of a CDS view
 *
 * @author stockbal
 */
public class CdsUsedEntitiesAnalysis extends CdsAnalysis {

	private LazyLoadingAdtObjectReferenceNode node;

	public CdsUsedEntitiesAnalysis(final IAdtObjectReferenceElementInfo cdsViewAdtObj) {
		super(cdsViewAdtObj);
		this.node = new LazyLoadingAdtObjectReferenceNode(cdsViewAdtObj.getName(), cdsViewAdtObj.getDisplayName(),
			cdsViewAdtObj.getDescription(), cdsViewAdtObj.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = cdsViewAdtObj.getAdapter(IDestinationProvider.class);
		this.node.setElementInfoProvider(new IElementInfoProvider() {
			@Override
			public String getProviderDescription() {
				return NLS.bind(Messages.CdsAnalysis_UsageAnalysisProviderDescription_xmsg, cdsViewAdtObj.getDisplayName());
			}

			@Override
			public List<IElementInfo> getElements() {
				if (destProvider == null) {
					return null;
				}
				final IElementInfo cdsTopDownInfo = CdsAnalysisServiceFactory.createCdsAnalysisService()
					.loadUsedEntitiesAnalysis(cdsViewAdtObj.getName(), destProvider.getDestinationId());
				if (cdsTopDownInfo != null) {
					return ((IElementInfoCollection) cdsTopDownInfo).getChildren();
				}
				return null;
			}
		});
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.USAGE_ANALYZER);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.USAGE_ANALYZER);
	}

	@Override
	public String getLabelPrefix() {
		return Messages.CdsUsageAnalysisView_ViewLabel_xfld;
	}

	@Override
	public CdsAnalysisType getType() {
		return CdsAnalysisType.DEPENDENCY_TREE_USAGES;
	}

	@Override
	public Object getResult() {
		return this.node;
	}

	@Override
	public void refreshAnalysis() {
		// TODO Auto-generated method stub

	}
}
