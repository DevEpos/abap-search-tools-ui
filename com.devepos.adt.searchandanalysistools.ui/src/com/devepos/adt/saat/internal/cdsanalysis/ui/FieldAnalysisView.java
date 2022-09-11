package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.action.ActionFactory;
import com.devepos.adt.base.ui.action.IToggleViewLayoutActionSettings;
import com.devepos.adt.base.ui.action.ToggleViewLayoutAction;
import com.devepos.adt.base.ui.action.ViewLayoutActionFactory;
import com.devepos.adt.base.ui.action.ViewLayoutOrientation;
import com.devepos.adt.base.ui.controls.FilterableComposite;
import com.devepos.adt.base.ui.controls.FilterableComposite.IWordMatcher;
import com.devepos.adt.base.ui.tree.FilterableTree;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.base.ui.tree.SimpleInfoTreeNode;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.FieldAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.FieldAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisPreferences;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsFieldAnalysisSettings;
import com.devepos.adt.saat.internal.menu.SaatMenuItemFactory;
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

  private static final String VIEW_LAYOUT_PREF_KEY = "com.devepos.adt.saat.fieldanalysis.viewLayout"; //$NON-NLS-1$
  private SashForm fieldsHierarchySplitter;
  private FilterableComposite<TreeViewer, Tree> fieldsTree;
  private ViewForm fieldsViewerViewForm;
  private FieldHierarchyView hierarchyView;
  private String currentEntity;
  private IDestinationProvider destProvider;
  private Action searchDbViewUsages;
  FieldAnalysisUriDiscovery uriDiscovery;
  private ToggleViewLayoutAction viewLayoutToggleAction;

  public FieldAnalysisView(final CdsAnalysisView viewPart) {
    super(viewPart);
  }

  private class UiState extends TreeViewUiState {
    private Map<String, FieldHierarchyViewerInput> hierarchyInputCache;

    @Override
    public void applyToTreeViewer(final TreeViewer viewer) {
      super.applyToTreeViewer(viewer);
      if (hierarchyView != null) {
        hierarchyView.setInputCache(hierarchyInputCache);
      }
    }

    @Override
    public void setFromTreeViewer(final TreeViewer viewer) {
      super.setFromTreeViewer(viewer);
      if (hierarchyView != null) {
        hierarchyInputCache = hierarchyView.getInputCache();
      }
    }
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
  public void setActionBars(final IActionBars actionBars) {
    super.setActionBars(actionBars);
    final IMenuManager menu = actionBars.getMenuManager();
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, searchDbViewUsages);
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, new Separator());
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, viewLayoutToggleAction);
  }

  @Override
  protected void clearViewerInput() {
    super.clearViewerInput();
    hierarchyView.clearInputCache();
  }

  @Override
  protected void configureTreeViewer(final TreeViewer treeViewer) {
    treeViewer.setContentProvider(new LazyLoadingTreeContentProvider());
    treeViewer.setUseHashlookup(true);
    treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
        new TreeViewerLabelProvider()));
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

  @Override
  protected void createActions() {
    super.createActions();
    final IPreferenceStore prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
    searchDbViewUsages = ActionFactory.createAction(
        Messages.FieldAnalysisView_SearchDbViewsInWhereUsed_xmit, null, Action.AS_CHECK_BOX, () -> {
          analysisResult.getSettings().setSearchInDatabaseViews(searchDbViewUsages.isChecked());
        });
    IToggleViewLayoutActionSettings viewLayoutActionSettings = ViewLayoutActionFactory
        .getInstance()
        .createDefaultSettings();
    viewLayoutActionSettings.setLayoutPrefOptions(prefStore, VIEW_LAYOUT_PREF_KEY);
    viewLayoutToggleAction = ViewLayoutActionFactory.getInstance()
        .createToggleViewLayoutAction(fieldsHierarchySplitter, getControl(),
            viewLayoutActionSettings);
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
  protected void fillContextMenu(final IMenuManager mgr,
      final CommandPossibleChecker commandPossibleChecker) {
    super.fillContextMenu(mgr, commandPossibleChecker);
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.CDS_TOP_DOWN_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
    }
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
    }
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.USED_ENTITIES_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.USED_ENTITIES_ANALYSIS);
    }
  }

  @Override
  protected ViewUiState getUiState() {
    final UiState state = new UiState();
    state.setFromTreeViewer((TreeViewer) getViewer());
    return state;
  }

  @Override
  protected void loadInput(final ViewUiState uiState) {
    final IAdtObjectReferenceElementInfo adtObjectInfo = analysisResult.getAdtObjectInfo();
    final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
    final ObjectType type = ObjectType.getFromAdtType(adtObjectInfo.getAdtObjectReference()
        .getType());
    uriDiscovery = new FieldAnalysisUriDiscovery(destProvider.getDestinationId());
    currentEntity = adtObjectInfo.getDisplayName();
    this.destProvider = destProvider;
    hierarchyView.setEntityInformation(adtObjectInfo.getDisplayName(), destProvider, type);

    final TreeViewer viewer = (TreeViewer) getViewer();
    if (analysisResult.isResultLoaded()) {
      setActionStateFromSettings();
      hierarchyView.setSettings(analysisResult.getSettings());
      viewer.setInput(analysisResult.getResult());
      // update ui state
      if (uiState instanceof UiState) {
        ((TreeViewUiState) uiState).applyToTreeViewer(viewer);
      } else {
        viewer.expandAll();
      }
    } else {
      initActionState();
      hierarchyView.setSettings(analysisResult.getSettings());
      analysisResult.setResultLoaded(true);
      viewer.setInput(analysisResult.getResult());
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
    final IStructuredSelection selection = (IStructuredSelection) fieldsTree.getViewer()
        .getSelection();
    if (selection != null && !selection.isEmpty() && !(selection
        .getFirstElement() instanceof IAdtObjectReferenceNode)) {
      refreshFieldsTree = false;
    }
    if (refreshFieldsTree) {
      // refresh complete field tree
      fieldsTree.resetFilter();
      hierarchyView.clearInputCache();
      analysisResult.refreshAnalysis();
      getViewPart().updateLabel();
      getViewer().refresh();
    } else {
      hierarchyView.reloadFieldInput();
    }
  }

  /*
   * Creates the filtered tree for the display of the fields of a database entity
   */
  private FilterableTree createFilteredTree(final Composite parent) {
    final FilterableTree tree = new FilterableTree(parent, "type filter text", false) {
      @Override
      protected void filterStringChanged() {
        super.filterStringChanged();
        Display.getDefault().timerExec(500, (Runnable) () -> {
          String filterString = getFilterString();
          if (filterString == null || filterString.trim().length() == 0) {
            getViewer().expandAll();
          }
        });
      }
    };
    tree.setViewer(new TreeViewer(tree, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
        | SWT.FULL_SELECTION));
    tree.setElementMatcher(element -> {
      IWordMatcher wordMatcher = tree.getWordMatcher();
      if (element instanceof ITreeNode) {
        final ITreeNode node = (ITreeNode) element;
        return wordMatcher.matchesWord(node.getName()) || wordMatcher.matchesWord(node
            .getDisplayName()) || wordMatcher.matchesWord(node.getDescription());
      }
      final DelegatingStyledCellLabelProvider labelProvider = (DelegatingStyledCellLabelProvider) getViewer()
          .getLabelProvider();
      final String labelText = labelProvider.getStyledStringProvider()
          .getStyledText(element)
          .getString();
      return wordMatcher.matchesWord(labelText);
    });
    return tree;
  }

  private void initActionState() {
    IPreferenceStore prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
    boolean isSearchInDbViews = prefStore.getBoolean(
        ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_DB_VIEWS);
    searchDbViewUsages.setChecked(isSearchInDbViews);

    if (analysisResult != null) {
      ICdsFieldAnalysisSettings settings = analysisResult.getSettings();
      settings.setSearchInDatabaseViews(isSearchInDbViews);
      settings.setTopDown(FieldAnalysisType.TOP_DOWN.getPrefKey()
          .equals(prefStore.getString(ICdsAnalysisPreferences.FIELD_ANALYSIS_ANALYSIS_DIRECTION)));
      settings.setSearchInCalcFields(prefStore.getBoolean(
          ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_CALC_FIELDS));
    }
  }

  private void setActionStateFromSettings() {
    ICdsFieldAnalysisSettings analysisSettings = analysisResult.getSettings();
    searchDbViewUsages.setChecked(analysisSettings.isSearchInDatabaseViews());
  }
}
