package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.action.PreferenceToggleAction;
import com.devepos.adt.base.ui.action.ToggleViewLayoutAction;
import com.devepos.adt.base.ui.action.ViewLayoutOrientation;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.base.ui.tree.PrefixedAsteriskFilteredTree;
import com.devepos.adt.base.ui.tree.SimpleInfoTreeNode;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.FieldAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.TreeViewUiState;
import com.devepos.adt.saat.internal.ui.ViewUiState;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.NavigationUtil;

/**
 * Analysis page for analyzing the fields of a database entity like (Table, View
 * or CDS View)
 *
 * @author stockbal
 */
public class FieldAnalysisView extends CdsAnalysisPage<FieldAnalysis> {

    static final String SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY = "com.devepos.adt.saat.fieldanalysis.searchDbViewUsages"; //$NON-NLS-1$
    private static final String VIEW_LAYOUT_PREF_KEY = "com.devepos.adt.saat.fieldanalysis.viewLayout"; //$NON-NLS-1$
    private SashForm fieldsHierarchySplitter;
    private FilteredTree fieldsTree;
    private ViewForm fieldsViewerViewForm;
    private FieldHierarchyView hierarchyView;
    private String currentEntity;
    private IDestinationProvider destProvider;
    private PreferenceToggleAction searchDbViewUsages;
    FieldAnalysisUriDiscovery uriDiscovery;
    private ToggleViewLayoutAction viewLayoutToggleAction;

    public FieldAnalysisView(final CdsAnalysisView viewPart) {
        super(viewPart);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        SearchAndAnalysisPlugin.getDefault()
            .getPreferenceStore()
            .setDefault(VIEW_LAYOUT_PREF_KEY, ViewLayoutOrientation.AUTOMATIC.name());
        registerViewerToClipboardAction(hierarchyView.getViewer());
        /*
         * Workaround for activated DevStyle theme so that the field tree is snapped to
         * the top. As it may not only be the DevStyle theme which causes this problem a
         * precondition check on the current workbench theme is not done
         */
        Display.getDefault().asyncExec(() -> {
            Control control = getControl();
            if (control != null) {
                control.pack(true);
                control.requestLayout();
            }
        });
    }

    @Override
    protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandPossibleChecker) {
        super.fillContextMenu(mgr, commandPossibleChecker);
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.CDS_TOP_DOWN_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
        }
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
        }
        if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.USED_ENTITIES_ANALYSIS)) {
            MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
                ICommandConstants.USED_ENTITIES_ANALYSIS);
        }
    }

    @Override
    public void setActionBars(final IActionBars actionBars) {
        super.setActionBars(actionBars);
        final IMenuManager menu = actionBars.getMenuManager();
        menu.add(searchDbViewUsages);
        menu.add(new Separator());
        menu.add(viewLayoutToggleAction);
    }

    @Override
    protected void createActions() {
        super.createActions();
        final IPreferenceStore prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
        searchDbViewUsages = new PreferenceToggleAction(Messages.FieldAnalysisView_SearchDbViewsInWhereUsed_xmit, null,
            SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY, false, prefStore);
        viewLayoutToggleAction = new ToggleViewLayoutAction(fieldsHierarchySplitter, getControl(), prefStore,
            VIEW_LAYOUT_PREF_KEY, true, true, true);
    }

    @Override
    protected TreeViewer createTreeViewer(final Composite parent) {
        // just returns the viewer of the filtered tree
        return fieldsTree.getViewer();
    }

    @Override
    protected Composite createTreeViewerComposite(final Composite parent) {

        fieldsHierarchySplitter = new SashForm(parent, SWT.VERTICAL);

        fieldsViewerViewForm = new ViewForm(fieldsHierarchySplitter, SWT.NONE);
        fieldsTree = createFilteredTree(fieldsViewerViewForm);
        fieldsViewerViewForm.setContent(fieldsTree);

        hierarchyView = new FieldHierarchyView(this, fieldsHierarchySplitter);
        // Register hierarchy viewer
        getSelectionAdapter().addViewer(hierarchyView.getViewer());

        /*
         * initially the detail part is not visible at startup as the fields be loaded
         * dynamically
         */
        hierarchyView.setVisible(false);
        return fieldsHierarchySplitter;
    }

    @Override
    protected ViewUiState getUiState() {
        final UiState state = new UiState();
        state.setFromTreeViewer((TreeViewer) getViewer());
        return state;
    }

    @Override
    protected void clearViewerInput() {
        super.clearViewerInput();
        hierarchyView.clearInputCache();
    }

    @Override
    protected void loadInput(final ViewUiState uiState) {
        final IAdtObjectReferenceElementInfo adtObjectInfo = analysisResult.getAdtObjectInfo();
        final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
        final ObjectType type = ObjectType.getFromAdtType(adtObjectInfo.getAdtObjectReference().getType());
        uriDiscovery = new FieldAnalysisUriDiscovery(destProvider.getDestinationId());
        currentEntity = adtObjectInfo.getDisplayName();
        this.destProvider = destProvider;
        hierarchyView.setEntityInformation(adtObjectInfo.getDisplayName(), destProvider, type);

        final TreeViewer viewer = (TreeViewer) getViewer();
        viewer.setInput(analysisResult.getResult());
        if (analysisResult.isResultLoaded()) {
            // update ui state
            if (uiState instanceof UiState) {
                ((TreeViewUiState) uiState).applyToTreeViewer(viewer);
            } else {
                viewer.expandAll();
            }
        } else {
            analysisResult.setResultLoaded(true);
            viewer.expandAll();
        }
    }

    @Override
    protected void refreshAnalysis() {
        final Object[] nodes = (Object[]) fieldsTree.getViewer().getInput();
        if (nodes == null) {
            return;
        }
        boolean refreshFieldsTree = true;
        final IStructuredSelection selection = (IStructuredSelection) fieldsTree.getViewer().getSelection();
        if (selection != null && !selection.isEmpty() && !(selection
            .getFirstElement() instanceof IAdtObjectReferenceNode)) {
            refreshFieldsTree = false;
        }
        if (refreshFieldsTree) {
            // refresh complete field tree
            fieldsTree.getFilterControl().setText(""); //$NON-NLS-1$
            hierarchyView.clearInputCache();
            analysisResult.refreshAnalysis();
            getViewPart().updateLabel();
            getViewer().refresh();
        } else {
            hierarchyView.reloadFieldInput();
        }
    }

    @Override
    protected void configureTreeViewer(final TreeViewer treeViewer) {
        treeViewer.setContentProvider(new LazyLoadingTreeContentProvider());
        treeViewer.setUseHashlookup(true);
        treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeViewerLabelProvider()));
        treeViewer.addDoubleClickListener(event -> {
            final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            if (selection.isEmpty()) {
                return;
            }
            final Object selectedObj = selection.getFirstElement();
            if (selectedObj instanceof SimpleInfoTreeNode) {
                NavigationUtil.navigateToEntityColumn(currentEntity, ((SimpleInfoTreeNode) selectedObj)
                    .getDisplayName(), destProvider.getDestinationId());
            }
        });
        treeViewer.addSelectionChangedListener(event -> {
            boolean hierarchyVisible = false;
            final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            if (!selection.isEmpty()) {
                final Object selected = selection.getFirstElement();
                if (selected instanceof SimpleInfoTreeNode) {
                    final SimpleInfoTreeNode fieldNode = (SimpleInfoTreeNode) selected;
                    hierarchyView.setFieldHierarchyInput(fieldNode);
                    hierarchyVisible = true;
                }
            }
            if (hierarchyVisible != hierarchyView.isVisible()) {
                hierarchyView.setVisible(hierarchyVisible);
                fieldsHierarchySplitter.layout();
            }
        });

    }

    /*
     * Creates the filtered tree for the display of the fields of a database entity
     */
    private FilteredTree createFilteredTree(final Composite parent) {
        final FilteredTree tree = new PrefixedAsteriskFilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION, createPatternFilter()) {
            @Override
            protected void textChanged() {
                super.textChanged();
                Display.getDefault().timerExec(500, (Runnable) () -> {
                    if (filterText != null && filterText.getText().length() == 0) {
                        getViewer().expandAll();
                    }
                });
            }
        };
        return tree;
    }

    /*
     * Creates the pattern filter which will be used for filtering the field tree
     */
    private PatternFilter createPatternFilter() {
        return new PatternFilter() {
            @Override
            protected boolean isLeafMatch(final Viewer viewer, final Object element) {
                if (element instanceof ITreeNode) {
                    final ITreeNode node = (ITreeNode) element;
                    return wordMatches(node.getName()) || wordMatches(node.getDisplayName()) || wordMatches(node
                        .getDescription());
                }
                final DelegatingStyledCellLabelProvider labelProvider = (DelegatingStyledCellLabelProvider) getViewer()
                    .getLabelProvider();
                final String labelText = labelProvider.getStyledStringProvider().getStyledText(element).getString();
                return wordMatches(labelText);
            }
        };
    }

    private class UiState extends TreeViewUiState {
        private Map<String, FieldHierarchyViewerInput> hierarchyInputCache;

        @Override
        public void setFromTreeViewer(final TreeViewer viewer) {
            super.setFromTreeViewer(viewer);
            if (hierarchyView != null) {
                hierarchyInputCache = hierarchyView.getInputCache();
            }
        }

        @Override
        public void applyToTreeViewer(final TreeViewer viewer) {
            super.applyToTreeViewer(viewer);
            if (hierarchyView != null) {
                hierarchyView.setInputCache(hierarchyInputCache);
            }
        }
    }
}
