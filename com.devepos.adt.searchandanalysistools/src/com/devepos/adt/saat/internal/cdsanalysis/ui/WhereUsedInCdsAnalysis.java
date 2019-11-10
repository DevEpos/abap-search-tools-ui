package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.text.DecimalFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.WhereUsedInCdsElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.ILazyLoadingListener;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.util.IImages;

public class WhereUsedInCdsAnalysis extends CdsAnalysis {
	private static final String INDETERMINATE_COUNT = "?"; //$NON-NLS-1$
	private String rootWhereUsedCount = INDETERMINATE_COUNT;
	private WhereUsedInCdsElementInfoProvider rootWhereUsedProvider;
	private ILazyLoadingNode cdsWhereUsedNode;
	private boolean showSelectFromUses = true;
	private boolean showAssocUses;
	private LazyLoadingAdtObjectReferenceNode node;

	public WhereUsedInCdsAnalysis(final IAdtObjectReferenceElementInfo adtObjectInfo) {
		super(adtObjectInfo);
	}

	@Override
	public Object getResult() {
		return new Object[] { this.node };
	}

	public void updateWhereUsedProvider(final boolean showSelectUses, final boolean showAssocUses) {
		this.showAssocUses = showAssocUses;
		this.showSelectFromUses = showSelectUses;
		this.rootWhereUsedProvider.updateSearchParameters(showSelectUses, showAssocUses);
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.WHERE_USED_IN);
	}

	@Override
	public String getLabel() {
		if (this.rootWhereUsedCount.equals(INDETERMINATE_COUNT)) {
			return super.getLabel();
		} else {
			return String.format("%s  -  %s", super.getLabel(), this.rootWhereUsedCount); //$NON-NLS-1$
		}
	}

	@Override
	protected String getLabelPrefix() {
		if (this.showAssocUses && this.showSelectFromUses) {
			return Messages.WhereUsedInCdsAnalysisView_ViewLabel_xfld;
		} else if (this.showAssocUses) {
			return Messages.WhereUsedInCdsAnalysisView_ViewLabelAssocSearch_xfld;
		} else {
			return Messages.WhereUsedInCdsAnalysisView_ViewLabelSelectFromSearch_xlfd;
		}
	}

	@Override
	public CdsAnalysisType getType() {
		return CdsAnalysisType.WHERE_USED;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.WHERE_USED_IN);
	}

	@Override
	public void refreshAnalysis() {
		if (this.cdsWhereUsedNode == null) {
			return;
		}
		this.cdsWhereUsedNode.resetLoadedState();
		this.rootWhereUsedCount = INDETERMINATE_COUNT;
	}

	/**
	 * Creates the result of the where used analysis
	 *
	 * @param l the listener to be notified after the result of the root node has
	 *          been determined
	 */
	public void createResult(final ILazyLoadingListener l) {
		final ILazyLoadingListener lazyLoadingListener = (count) -> {
			this.rootWhereUsedCount = NLS.bind(
				count == 1 ? Messages.WhereUsedInCdsAnalysisView_SingleReferenceLabelSuffix_xfld
					: Messages.WhereUsedInCdsAnalysisView_MultipleReferencesLabelSuffix_xfld,
				new DecimalFormat("###,###").format(count)); //$NON-NLS-1$
		};
		this.node = new LazyLoadingAdtObjectReferenceNode(this.adtObjectInfo.getName(), this.adtObjectInfo.getDisplayName(),
			this.adtObjectInfo.getDescription(), this.adtObjectInfo.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = this.adtObjectInfo.getAdapter(IDestinationProvider.class);
		this.rootWhereUsedProvider = new WhereUsedInCdsElementInfoProvider(
			destProvider != null ? destProvider.getDestinationId() : null, this.adtObjectInfo.getName(), this.showSelectFromUses,
			this.showAssocUses);
		this.cdsWhereUsedNode = this.node;
		this.node.setElementInfoProvider(this.rootWhereUsedProvider);
		this.node.addLazyLoadingListener(lazyLoadingListener);
		this.node.addLazyLoadingListener(l);
	}
}
