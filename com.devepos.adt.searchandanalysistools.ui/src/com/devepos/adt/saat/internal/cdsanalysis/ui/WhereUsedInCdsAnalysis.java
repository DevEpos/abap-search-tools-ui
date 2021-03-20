package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.text.DecimalFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.tree.ILazyLoadingListener;
import com.devepos.adt.base.ui.tree.ILazyLoadingNode;
import com.devepos.adt.base.ui.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.WhereUsedInCdsElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

public class WhereUsedInCdsAnalysis extends CdsAnalysis {
    private static final String INDETERMINATE_COUNT = "?"; //$NON-NLS-1$
    private String rootWhereUsedCount = INDETERMINATE_COUNT;
    private WhereUsedInCdsElementInfoProvider rootWhereUsedProvider;
    private ILazyLoadingNode cdsWhereUsedNode;
    private boolean showSelectFromUses = true;
    private boolean showAssocUses;
    private LazyLoadingAdtObjectReferenceNode node;
    private boolean localAssociationsOnly;

    public WhereUsedInCdsAnalysis(final IAdtObjectReferenceElementInfo adtObjectInfo) {
        super(adtObjectInfo);
    }

    @Override
    public Object getResult() {
        return new Object[] { node };
    }

    public void updateWhereUsedProvider(final boolean showSelectUses, final boolean showAssocUses) {
        this.showAssocUses = showAssocUses;
        showSelectFromUses = showSelectUses;
        rootWhereUsedProvider.updateSearchParameters(showSelectUses, showAssocUses);
    }

    public void setLocalAssociationsOnly(final boolean localAssociationsOnly) {
        this.localAssociationsOnly = localAssociationsOnly;
        rootWhereUsedProvider.setLocalAssociationsOnly(localAssociationsOnly);
    }

    @Override
    public Image getImage() {
        return SearchAndAnalysisPlugin.getDefault().getImage(IImages.WHERE_USED_IN);
    }

    @Override
    public String getLabel() {
        if (rootWhereUsedCount.equals(INDETERMINATE_COUNT)) {
            return super.getLabel();
        }
        return String.format("%s  -  %s", super.getLabel(), rootWhereUsedCount); //$NON-NLS-1$
    }

    @Override
    protected String getLabelPrefix() {
        if (showAssocUses && showSelectFromUses) {
            return Messages.WhereUsedInCdsAnalysisView_ViewLabel_xfld;
        }
        if (showAssocUses) {
            return Messages.WhereUsedInCdsAnalysisView_ViewLabelAssocSearch_xfld;
        }
        return Messages.WhereUsedInCdsAnalysisView_ViewLabelSelectFromSearch_xlfd;
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
        if (cdsWhereUsedNode == null) {
            return;
        }
        cdsWhereUsedNode.resetLoadedState();
        rootWhereUsedCount = INDETERMINATE_COUNT;
    }

    /**
     * Creates the result of the where used analysis
     *
     * @param l the listener to be notified after the result of the root node has
     *          been determined
     */
    public void createResult(final ILazyLoadingListener l) {
        final ILazyLoadingListener lazyLoadingListener = count -> {
            rootWhereUsedCount = NLS.bind(count == 1
                    ? Messages.WhereUsedInCdsAnalysisView_SingleReferenceLabelSuffix_xfld
                    : Messages.WhereUsedInCdsAnalysisView_MultipleReferencesLabelSuffix_xfld, new DecimalFormat(
                            "###,###").format(count)); //$NON-NLS-1$
        };
        node = new LazyLoadingAdtObjectReferenceNode(adtObjectInfo.getName(), adtObjectInfo.getDisplayName(),
                adtObjectInfo.getDescription(), adtObjectInfo.getAdtObjectReference(), null);
        final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
        rootWhereUsedProvider = new WhereUsedInCdsElementInfoProvider(destProvider != null ? destProvider
                .getDestinationId() : null, adtObjectInfo.getName(), showSelectFromUses, showAssocUses);
        cdsWhereUsedNode = node;
        rootWhereUsedProvider.setLocalAssociationsOnly(localAssociationsOnly);
        node.setElementInfoProvider(rootWhereUsedProvider);
        node.addLazyLoadingListener(lazyLoadingListener);
        node.addLazyLoadingListener(l);
    }
}
