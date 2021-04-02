package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.IGeneralContextMenuConstants;
import com.devepos.adt.base.ui.StylerFactory;
import com.devepos.adt.base.ui.action.PreferenceToggleAction;
import com.devepos.adt.base.ui.tree.ICollectionTreeNode;
import com.devepos.adt.base.ui.tree.ILazyLoadingListener;
import com.devepos.adt.base.ui.tree.IStyledTreeNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.base.ui.tree.LoadingTreeItemsNode;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisPreferences;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.QueryParameterName;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.ui.TreeViewUiState;
import com.devepos.adt.saat.internal.ui.ViewUiState;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Where-Used page of CDS Analysis page
 *
 * @see {@link CdsAnalyzerPage}
 * @author stockbal
 */
public class WhereUsedInCdsAnalysisView extends CdsAnalysisPage<WhereUsedInCdsAnalysis> {
    private final ILazyLoadingListener lazyLoadingListener;
    private PreferenceToggleAction showFromUses;
    private PreferenceToggleAction showAssocUses;
    private PreferenceToggleAction releasedUsagesOnly;
    private PreferenceToggleAction localAssociationsOnly;
    private boolean isLocalAssocOnlyFeatureAvailable;
    private static final String USES_IN_SELECT_PREF_KEY = "com.devepos.adt.saat.whereusedincds.showReferencesInSelectPartOfCds"; //$NON-NLS-1$
    private static final String USES_IN_ASSOC_PREF_KEY = "com.devepos.adt.saat.whereusedincds.showReferencesInAssocPartOfCds"; //$NON-NLS-1$
    private static final String LOCAL_ASSOCIATIONS_ONLY_PREF_KEY = "com.devepos.adt.saat.whereusedincds.onlyLocalDefinedAssociation"; //$NON-NLS-1$
    private final IPropertyChangeListener propertyChangeListener;

    public WhereUsedInCdsAnalysisView(final CdsAnalysisView parentView) {
        super(parentView);
        lazyLoadingListener = count -> {
            PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                parentView.updateLabel();
            });
        };

        propertyChangeListener = event -> {
            final String propertyName = event.getProperty();
            final boolean showFromUsesChanged = USES_IN_SELECT_PREF_KEY.equals(propertyName);
            final boolean showAssocUsesChanged = USES_IN_ASSOC_PREF_KEY.equals(propertyName);
            final boolean localAssocsOnlyChanged = LOCAL_ASSOCIATIONS_ONLY_PREF_KEY.equals(propertyName);
            final boolean releasedUsagesOnlyChanged = ICdsAnalysisPreferences.WHERE_USED_ONLY_RELEASED_USAGES.equals(
                propertyName);

            if (!showFromUsesChanged && !showAssocUsesChanged && !releasedUsagesOnlyChanged
                && !localAssocsOnlyChanged) {
                return;
            }
            // trigger refresh of where used analysis
            analysisResult.updateWhereUsedProvider(showFromUses.isChecked(), showAssocUses.isChecked());
            analysisResult.setLocalAssociationsOnly(localAssociationsOnly.isChecked());
            if (localAssocsOnlyChanged) {
                if (showAssocUses.isChecked()) {
                    refreshAnalysis();
                }
            } else {
                refreshAnalysis();
            }
        };
        SearchAndAnalysisPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void dispose() {
        super.dispose();
        SearchAndAnalysisPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
    }

    @Override
    protected void createActions() {
        super.createActions();
        final IPreferenceStore prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
        showFromUses = new PreferenceToggleAction(Messages.WhereUsedInCdsAnalysisView_ShowUsesInSelectPartAction_xmit,
            SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.DATA_SOURCE), USES_IN_SELECT_PREF_KEY, true,
            prefStore);
        showAssocUses = new PreferenceToggleAction(
            Messages.WhereUsedInCdsAnalysisView_ShowUsesInAssociationsAction_xmit, SearchAndAnalysisPlugin.getDefault()
                .getImageDescriptor(IImages.ASSOCIATION), USES_IN_ASSOC_PREF_KEY, false, prefStore);
        localAssociationsOnly = new PreferenceToggleAction(
            Messages.WhereUsedInCdsAnalysisView_OnlyLocallyDefinedAssocUsages_xmit, null,
            LOCAL_ASSOCIATIONS_ONLY_PREF_KEY, false, prefStore);
        releasedUsagesOnly = new PreferenceToggleAction(
            Messages.WhereUsedInCdsAnalysisView_OnlyUsagesInReleasedEntities_xmit, null,
            ICdsAnalysisPreferences.WHERE_USED_ONLY_RELEASED_USAGES, false, prefStore);
        showAssocUses.addPropertyChangeListener(event -> {
            localAssociationsOnly.setEnabled(showAssocUses.isChecked() && isLocalAssocOnlyFeatureAvailable);
            if (!showAssocUses.isChecked()) {
                showFromUses.setChecked(true);
            }
        });
        showFromUses.addPropertyChangeListener(event -> {
            if (!showFromUses.isChecked()) {
                showAssocUses.setChecked(true);
            }
        });
    }

    @Override
    protected void configureTreeViewer(final TreeViewer treeViewer) {
        treeViewer.setContentProvider(new LazyLoadingTreeContentProvider());
        treeViewer.setUseHashlookup(true);
        treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeViewerLabelProvider()));
    }

    @Override
    protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandPossibleChecker) {
        super.fillContextMenu(mgr, commandPossibleChecker);
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.CDS_TOP_DOWN_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
        }
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.USED_ENTITIES_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.USED_ENTITIES_ANALYSIS);
        }
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.FIELD_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.FIELD_ANALYSIS);
        }
    }

    @Override
    protected ViewUiState getUiState() {
        final TreeViewUiState uiState = new TreeViewUiState();
        uiState.setFromTreeViewer((TreeViewer) getViewer());
        return uiState;
    }

    @Override
    protected void loadInput(final ViewUiState uiState) {
        checkFeatureState();
        final TreeViewer viewer = (TreeViewer) getViewer();
        if (analysisResult.isResultLoaded()) {
            viewer.setInput(analysisResult.getResult());
            analysisResult.updateWhereUsedProvider(showFromUses.isChecked(), showAssocUses.isChecked());
            analysisResult.setLocalAssociationsOnly(localAssociationsOnly.isChecked());
            if (uiState instanceof TreeViewUiState) {
                ((TreeViewUiState) uiState).applyToTreeViewer(viewer);
            } else {
                final Object[] input = (Object[]) viewer.getInput();
                if (input != null && input.length >= 1) {
                    viewer.expandToLevel(input[0], 1);
                    viewer.setSelection(new StructuredSelection(input[0]));
                }
            }
        } else {
            analysisResult.createResult(lazyLoadingListener);
            analysisResult.updateWhereUsedProvider(showFromUses.isChecked(), showAssocUses.isChecked());
            analysisResult.setLocalAssociationsOnly(localAssociationsOnly.isChecked());
            viewer.setInput(analysisResult.getResult());
            analysisResult.setResultLoaded(true);
            viewer.expandAll();
        }
    }

    @Override
    public void setActionBars(final IActionBars actionBars) {
        super.setActionBars(actionBars);
        final IMenuManager menu = actionBars.getMenuManager();
        menu.appendToGroup(IGeneralContextMenuConstants.GROUP_FILTERING, showFromUses);
        menu.appendToGroup(IGeneralContextMenuConstants.GROUP_FILTERING, showAssocUses);
        menu.appendToGroup(IGeneralContextMenuConstants.GROUP_ADDITIONS, releasedUsagesOnly);
        menu.appendToGroup(IGeneralContextMenuConstants.GROUP_ADDITIONS, localAssociationsOnly);
    }

    @Override
    protected void refreshAnalysis() {
        analysisResult.refreshAnalysis();
        getViewPart().updateLabel();
        getViewer().refresh();
    }

    @Override
    protected StyledString getTreeNodeLabel(final Object element) {
        StyledString text = null;
        final ITreeNode node = (ITreeNode) element;

        if (element instanceof IStyledTreeNode) {
            text = ((IStyledTreeNode) element).getStyledText();
        } else {
            text = new StyledString();
            if (element instanceof LoadingTreeItemsNode) {
                text.append(node.getDisplayName(), StylerFactory.ITALIC_STYLER);
            } else {
                text.append(" "); // for broader image due to overlay //$NON-NLS-1$
                text.append(node.getDisplayName());
            }

            if (element instanceof ICollectionTreeNode) {
                final String size = ((ICollectionTreeNode) element).getSizeAsString();
                if (size != null) {
                    text.append(" (" + size + ")", StyledString.COUNTER_STYLER); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            final String description = node.getDescription();
            if (description != null && !description.isEmpty()) {
                text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
                    StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR, null));
            }
        }
        return text;
    }

    private void checkFeatureState() {
        final IAdtObjectReferenceElementInfo adtObjElemInfo = analysisResult.getAdtObjectInfo();
        final IDestinationProvider destProvider = adtObjElemInfo.getAdapter(IDestinationProvider.class);
        final ObjectSearchUriDiscovery uriDiscovery = new ObjectSearchUriDiscovery(destProvider.getDestinationId());
        isLocalAssocOnlyFeatureAvailable = uriDiscovery.isParameterSupported(
            QueryParameterName.LOCAL_DECLARED_ASSOC_ONLY, SearchType.CDS_VIEW);
        localAssociationsOnly.setEnabled(isLocalAssocOnlyFeatureAvailable && showAssocUses.isChecked());
        releasedUsagesOnly.setEnabled(uriDiscovery.isParameterSupported(QueryParameterName.RELEASE_STATE,
            SearchType.CDS_VIEW));
    }
}
