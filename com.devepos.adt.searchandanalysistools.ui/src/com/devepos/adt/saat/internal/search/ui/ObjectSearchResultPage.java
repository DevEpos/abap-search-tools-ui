package com.devepos.adt.saat.internal.search.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.Page;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.AdtBaseUIResources;
import com.devepos.adt.base.ui.ContextHelper;
import com.devepos.adt.base.ui.IAdtBaseImages;
import com.devepos.adt.base.ui.IGeneralCommandConstants;
import com.devepos.adt.base.ui.IGeneralContextConstants;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.StylerFactory;
import com.devepos.adt.base.ui.action.CollapseAllTreeNodesAction;
import com.devepos.adt.base.ui.action.CollapseTreeNodesAction;
import com.devepos.adt.base.ui.action.CommandFactory;
import com.devepos.adt.base.ui.action.CopyToClipboardAction;
import com.devepos.adt.base.ui.action.ExecuteAdtObjectAction;
import com.devepos.adt.base.ui.action.ExpandAllAction;
import com.devepos.adt.base.ui.action.OpenAdtObjectAction;
import com.devepos.adt.base.ui.search.ISearchResultPageExtension;
import com.devepos.adt.base.ui.tree.ActionTreeNode;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.ICollectionTreeNode;
import com.devepos.adt.base.ui.tree.IStyledTreeNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.base.ui.tree.LoadingTreeItemsNode;
import com.devepos.adt.base.ui.tree.PackageNode;
import com.devepos.adt.base.ui.tree.launchable.ILaunchableNode;
import com.devepos.adt.base.ui.util.AdtTypeUtil;
import com.devepos.adt.base.ui.util.WorkbenchUtil;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.menu.SaatMenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.IExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.util.FeatureTester;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * The result page for an executed Object Search
 *
 * @author stockbal
 */
public class ObjectSearchResultPage extends Page implements ISearchResultPage,
    ISearchResultListener, ISearchResultPageExtension<ObjectSearchQuery> {
  public static final String GROUPED_BY_PACKAGE_PREF = "com.devepos.adt.saat.objectsearch.groupByPackage"; //$NON-NLS-1$
  private String id;
  private UIState state;
  private ObjectSearchResult result;
  private ISearchResultViewPart searchViewPart;
  private TreeViewer searchResultTree;
  private Composite mainComposite;
  private ObjectSearchQuery searchQuery;

  private CollapseAllTreeNodesAction collapseAllNodesAction;
  private ExpandAllAction expandAllAction;
  private CollapseTreeNodesAction collapseNodesAction;
  private ExpandSelectedPackageNodesAction expandPackageNodesAction;
  private CopyToClipboardAction copyToClipBoardAction;
  private OpenObjectSearchPreferences openPreferencesAction;
  private GroupByPackageAction groupByPackageAction;
  private SearchFavoritesAction favoritesAction;

  private IAbapProjectProvider projectProvider;
  private boolean isDbBrowserIntegrationAvailable;
  private boolean isCdsTopDownAnalysisAvailable;
  private boolean isCdsUsedEntitiesAnalysisAvailable;
  private boolean isCdsAnalysisAvailable;
  private final IPreferenceStore prefStore;
  private ContextHelper contextHelper;

  public ObjectSearchResultPage() {
    prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
    prefStore.setDefault(GROUPED_BY_PACKAGE_PREF, false);
  }

  /**
   * @return the {@link ObjectSearchQuery} of this the result page
   */
  @Override
  public ObjectSearchQuery getSearchQuery() {
    return searchQuery != null ? searchQuery : null;
  }

  @Override
  public String getSearchPageId() {
    return ObjectSearchPage.PAGE_ID;
  }

  @Override
  public void createControl(final Composite parent) {
    mainComposite = createTreeViewerComposite(parent);

    createResultTree(mainComposite);
    initializeActions();
    hookContextMenu();
    getSite().setSelectionProvider(searchResultTree);

    contextHelper = ContextHelper.createForServiceLocator(getSite());
    contextHelper.activateAbapContext();
    contextHelper.activateContext(IGeneralContextConstants.SEARCH_PAGE_VIEWS);
  }

  @Override
  public void setActionBars(final IActionBars actionBars) {
    final IToolBarManager tbm = actionBars.getToolBarManager();
    tbm.appendToGroup(IContextMenuConstants.GROUP_NEW, CommandFactory.createContribItemById(
        IGeneralCommandConstants.OPEN_QUERY_IN_SEARCH_DIALOG, false, null));
    tbm.appendToGroup(IContextMenuConstants.GROUP_NEW, favoritesAction);
    tbm.appendToGroup(IContextMenuConstants.GROUP_EDIT, collapseAllNodesAction);
    tbm.appendToGroup(IContextMenuConstants.GROUP_EDIT, expandAllAction);
    tbm.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, groupByPackageAction);
    copyToClipBoardAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
    actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyToClipBoardAction);
    actionBars.updateActionBars();

    actionBars.getMenuManager().add(openPreferencesAction);
  }

  @Override
  public void dispose() {
    if (contextHelper != null) {
      contextHelper.deactivateAllContexts();
    }
    super.dispose();
  }

  @Override
  public Control getControl() {
    return mainComposite;
  }

  @Override
  public void setFocus() {
    if (searchResultTree != null && !searchResultTree.getControl().isDisposed()) {
      searchResultTree.getControl().setFocus();
    }
  }

  @Override
  public Object getUIState() {
    if (searchResultTree != null && !searchResultTree.getControl().isDisposed()) {
      final UIState uiState = new UIState();
      uiState.setExpandedPaths(searchResultTree.getExpandedTreePaths());
      uiState.setSelection(searchResultTree.getSelection());
      return uiState;
    }
    return null;
  }

  @Override
  public void setInput(final ISearchResult search, final Object uiState) {
    if (result != null) {
      // clean up old search
      result.removeListener(this);
      searchResultTree.setInput(null);
    }
    result = (ObjectSearchResult) search;
    if (result != null) {
      result.addListener(this);
      searchResultTree.setInput(result);
      state = uiState instanceof UIState ? (UIState) uiState : null;
      searchQuery = (ObjectSearchQuery) result.getQuery();
      projectProvider = searchQuery.getProjectProvider();
      checkFeatureAvailibility();
      if (!NewSearchUI.isQueryRunning(searchQuery)) {
        updateUiState();
      }
    } else {
      searchViewPart.updateLabel();
    }
  }

  @Override
  public void setViewPart(final ISearchResultViewPart part) {
    searchViewPart = part;
  }

  @Override
  public void restoreState(final IMemento memento) {

  }

  @Override
  public void saveState(final IMemento memento) {

  }

  @Override
  public void setID(final String id) {
    this.id = id;
  }

  @Override
  public String getID() {
    return id;
  }

  @Override
  public String getLabel() {
    if (result != null) {
      return result.getLabel();
    }
    return ""; //$NON-NLS-1$
  }

  @Override
  public void searchResultChanged(final SearchResultEvent e) {
    if (e instanceof ObjectSearchResultEvent && ((ObjectSearchResultEvent) e).isCleanup()) {
      return;
    }
    state = null;
    Display.getDefault().asyncExec(() -> {
      WorkbenchUtil.bringPartToFront(searchViewPart);
      searchViewPart.updateLabel();
      final IAbapProjectProvider projectProvider = searchQuery.getProjectProvider();
      if (projectProvider != this.projectProvider) {
        this.projectProvider = projectProvider;
        checkFeatureAvailibility();
      }
      searchResultTree.setInput(e.getSearchResult());
      if (groupByPackageAction.isChecked()) {
        expandAllPackages();
      }
      updateUiState();
    });

  }

  /**
   * Creates the composite which will hold the tree viewer of the page
   * <p>
   * Subclasses may override to create a more complex layout <br>
   * </p>
   *
   * @param parent the parent composite
   * @return
   */
  protected Composite createTreeViewerComposite(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setSize(100, 100);
    composite.setLayout(new FillLayout());
    return composite;
  }

  private void checkFeatureAvailibility() {
    isDbBrowserIntegrationAvailable = false;
    isCdsTopDownAnalysisAvailable = false;
    isCdsUsedEntitiesAnalysisAvailable = false;
    isCdsAnalysisAvailable = false;
    if (projectProvider != null && projectProvider.ensureLoggedOn()) {
      isDbBrowserIntegrationAvailable = FeatureTester.isSapGuiDbBrowserAvailable(projectProvider
          .getProject());
      isCdsTopDownAnalysisAvailable = FeatureTester.isCdsTopDownAnalysisAvailable(projectProvider
          .getProject());
      isCdsUsedEntitiesAnalysisAvailable = FeatureTester.isCdsUsedEntitiesAnalysisAvailable(
          projectProvider.getProject());
      isCdsAnalysisAvailable = new CdsAnalysisUriDiscovery(projectProvider.getDestinationId())
          .getCdsAnalysisUri() != null;
    }

  }

  private void initializeActions() {
    favoritesAction = new SearchFavoritesAction();
    collapseAllNodesAction = new CollapseAllTreeNodesAction(searchResultTree);
    collapseNodesAction = new CollapseTreeNodesAction(searchResultTree);
    copyToClipBoardAction = new CopyToClipboardAction();
    copyToClipBoardAction.registerViewer(searchResultTree);
    groupByPackageAction = new GroupByPackageAction();
    groupByPackageAction.setChecked(prefStore.getBoolean(GROUPED_BY_PACKAGE_PREF));
    expandAllAction = new ExpandAllPackageNodesAction();
    expandAllAction.setTreeViewer(searchResultTree);
    expandAllAction.setEnabled(groupByPackageAction.isChecked());
    expandPackageNodesAction = new ExpandSelectedPackageNodesAction(searchResultTree);
    openPreferencesAction = new OpenObjectSearchPreferences();
  }

  /*
   * Creates the result tree of the object search
   */
  private void createResultTree(final Composite parent) {

    searchResultTree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    searchResultTree.setContentProvider(new TreeContentProvider());
    searchResultTree.setLabelProvider(new DelegatingStyledCellLabelProvider(
        new ViewLabelProvider()));
    searchResultTree.addOpenListener(event -> {
      final ITreeSelection sel = (ITreeSelection) event.getSelection();
      final Iterator<?> selIter = sel.iterator();
      while (selIter.hasNext()) {
        handleOpenOnTreeNode(selIter.next());
      }
    });
  }

  private void handleOpenOnTreeNode(final Object node) {
    if (node == null) {
      return;
    }
    if (node instanceof IAdtObjectReferenceNode) {
      final IAdtObjectReferenceNode selectedAdtObject = (IAdtObjectReferenceNode) node;

      if (selectedAdtObject != null) {
        searchQuery.getProjectProvider()
            .openObjectReference(selectedAdtObject.getObjectReference());
      }
    } else if (node instanceof ICollectionTreeNode) {
      final boolean isExpanded = searchResultTree.getExpandedState(node);
      if (isExpanded) {
        searchResultTree.collapseToLevel(node, 1);
      } else {
        searchResultTree.expandToLevel(node, 1);
      }
    } else if (node instanceof ActionTreeNode) {
      ((ActionTreeNode) node).getAction().execute();
    }
  }

  private void hookContextMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);

    menuMgr.addMenuListener(menu -> {
      fillContextMenu(menu);
    });
    final Control viewerControl = searchResultTree.getControl();
    final Menu menu = menuMgr.createContextMenu(viewerControl);
    viewerControl.setMenu(menu);
    getSite().registerContextMenu(searchViewPart.getViewSite().getId(), menuMgr, searchResultTree);
  }

  private void fillContextMenu(final IMenuManager menu) {
    menu.add(new Separator(IContextMenuConstants.GROUP_NEW));
    menu.add(new Separator(IContextMenuConstants.GROUP_EDIT));
    menu.add(new GroupMarker(IContextMenuConstants.GROUP_OPEN));
    menu.add(new GroupMarker(com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_DB_BROWSER));
    menu.add(new GroupMarker(
        com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS));
    menu.add(new GroupMarker(IGeneralMenuConstants.GROUP_NODE_ACTIONS));
    menu.add(new GroupMarker(IContextMenuConstants.GROUP_SEARCH));

    var additionalItems = new ArrayList<IContributionItem>();

    final IStructuredSelection selection = searchResultTree.getStructuredSelection();
    if (selection == null || selection.isEmpty()) {
      return;
    }
    boolean selectionHasExpandedNodes = false;
    final List<IAdtObjectReference> adtObjRefs = new ArrayList<>();
    final List<IAdtObjectReference> previewAdtObjRefs = new ArrayList<>();
    final int selectionSize = selection.size();
    int launchableNodeCount = 0;
    boolean singleDataPreviewObjectSelected = false;
    boolean singleCdsViewSelected = false;
    boolean hasCollapsedPackages = false;

    // determine overall action availability depending on the selection
    for (final Object selectedObject : selection.toList()) {
      if (selectedObject instanceof ILaunchableNode) {
        launchableNodeCount++;
      }
      if (selectedObject instanceof IAdtObjectReferenceNode) {
        final IAdtObjectReferenceNode objRefNode = (IAdtObjectReferenceNode) selectedObject;
        final IAdtObjectReference adtObjectRef = objRefNode.getObjectReference();
        if (objRefNode.supportsDataPreview()) {
          previewAdtObjRefs.add(adtObjectRef);
        }
        adtObjRefs.add(adtObjectRef);

        if (selectionSize == 1) {
          singleDataPreviewObjectSelected = true;
          singleCdsViewSelected = objRefNode.getObjectType() == ObjectType.DATA_DEFINITION;
        }
      }

      if (!selectionHasExpandedNodes && selectedObject instanceof ICollectionTreeNode
          && searchResultTree.getExpandedState(selectedObject)) {
        selectionHasExpandedNodes = true;
      }
      if (!hasCollapsedPackages && selectedObject instanceof PackageNode && !searchResultTree
          .getExpandedState(selectedObject)) {
        hasCollapsedPackages = true;
      }
    }

    // fill Open object action
    if (!adtObjRefs.isEmpty()) {
      menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, new Separator());
      menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, new OpenAdtObjectAction(projectProvider
          .getProject(), adtObjRefs));
    }
    // Fill Data Preview actions
    if (!previewAdtObjRefs.isEmpty()) {
      menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, new ExecuteAdtObjectAction(
          projectProvider.getProject(), previewAdtObjRefs, true));
      if (isDbBrowserIntegrationAvailable) {
        menu.appendToGroup(com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_DB_BROWSER,
            new Separator());
        SaatMenuItemFactory.addOpenInDbBrowserCommand(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_DB_BROWSER, false);
        SaatMenuItemFactory.addOpenInDbBrowserCommand(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_DB_BROWSER, true);
      }

      // is a separator at the end of the group needed?
      if (!singleDataPreviewObjectSelected) {
        if (isDbBrowserIntegrationAvailable) {
          menu.appendToGroup(com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_DB_BROWSER,
              new Separator());
        } else {
          menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, new Separator());
        }
      }
    }

    if (!adtObjRefs.isEmpty() && selectionSize == 1) {
      additionalItems.add(CommandFactory.createContribItemById(
          IGeneralCommandConstants.WHERE_USED_IN, true, null));
    }

    // fill CDS analysis actions
    if (singleDataPreviewObjectSelected && isCdsAnalysisAvailable) {
      menu.appendToGroup(com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
          new Separator());
      if (singleCdsViewSelected && isCdsTopDownAnalysisAvailable) {
        SaatMenuItemFactory.addCdsAnalyzerCommandItem(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
            ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
      }
      if (!previewAdtObjRefs.isEmpty()) {
        SaatMenuItemFactory.addCdsAnalyzerCommandItem(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
            ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
      }
      if (singleCdsViewSelected && isCdsUsedEntitiesAnalysisAvailable) {
        SaatMenuItemFactory.addCdsAnalyzerCommandItem(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
            ICommandConstants.USED_ENTITIES_ANALYSIS);
      }
      if (!previewAdtObjRefs.isEmpty()) {
        SaatMenuItemFactory.addCdsAnalyzerCommandItem(menu,
            com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
            ICommandConstants.FIELD_ANALYSIS);
      }
      // is a separator at the end of the group needed?
      if (!additionalItems.isEmpty() && !selectionHasExpandedNodes && !hasCollapsedPackages) {
        menu.appendToGroup(com.devepos.adt.saat.internal.IContextMenuConstants.GROUP_CDS_ANALYSIS,
            new Separator());
      }
    }

    // fill folder actions like expand/collapse
    if (selectionHasExpandedNodes || hasCollapsedPackages) {
      menu.appendToGroup(IGeneralMenuConstants.GROUP_NODE_ACTIONS, new Separator());
      if (hasCollapsedPackages) {
        menu.appendToGroup(IGeneralMenuConstants.GROUP_NODE_ACTIONS, expandPackageNodesAction);
      }
      if (selectionHasExpandedNodes) {
        menu.appendToGroup(IGeneralMenuConstants.GROUP_NODE_ACTIONS, collapseNodesAction);
      }
      // is a separator at the end of the group needed?
      if (!additionalItems.isEmpty()) {
        menu.appendToGroup(IGeneralMenuConstants.GROUP_NODE_ACTIONS, new Separator());
      }
    }

    menu.appendToGroup(IContextMenuConstants.GROUP_EDIT, copyToClipBoardAction);

    if (!additionalItems.isEmpty()) {
      // if 'additions' node is created as separator if it is not needed than the context menu will
      // end with a separator which is not pretty
      menu.add(launchableNodeCount == selectionSize ? new Separator(
          IWorkbenchActionConstants.MB_ADDITIONS)
          : new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
      for (var item : additionalItems) {
        menu.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, item);
      }
    }
  }

  private void updateUiState() {
    Display.getDefault().asyncExec(() -> {
      if (searchResultTree == null || searchResultTree.getControl().isDisposed()) {
        return;
      }
      if (state != null) {
        searchResultTree.getControl().setRedraw(false);
        try {
          searchResultTree.setExpandedTreePaths(state.getExpandedPaths());
        } finally {
          searchResultTree.getControl().setRedraw(true);
        }
      }
      searchResultTree.getControl().setFocus();
      final IAdtObjectReferenceNode[] result = this.result.getResultForTree(groupByPackageAction
          .isChecked());
      if (result != null && result.length > 0) {
        if (state != null && state.hasSelection()) {
          searchResultTree.setSelection(state.getSelection());
        } else {
          searchResultTree.setSelection(new StructuredSelection(result[0]));
        }
      }
      searchResultTree.refresh();
    });
  }

  private void updateGrouping() {
    BusyIndicator.showWhile(getSite().getShell().getDisplay(), () -> {
      searchResultTree.refresh();
    });
    expandAllAction.setEnabled(groupByPackageAction.isChecked());
    prefStore.putValue(GROUPED_BY_PACKAGE_PREF, Boolean.toString(groupByPackageAction.isChecked()));
  }

  /*
   * Expands all package nodes
   */
  private void expandAllPackages() {
    final Object[] packages = result.getPackages();
    if (packages != null) {
      BusyIndicator.showWhile(getSite().getShell().getDisplay(), () -> {
        try {
          searchResultTree.getControl().setRedraw(false);
          searchResultTree.setExpandedElements(packages);
        } finally {
          searchResultTree.getControl().setRedraw(true);
        }
      });
    }
  }

  /**
   * Custom view label provider for the Result Tree
   *
   * @author stockbal
   */
  static class ViewLabelProvider extends LabelProvider implements ILabelProvider,
      IStyledLabelProvider {

    @Override
    public String getText(final Object element) {
      final ITreeNode searchResult = (ITreeNode) element;

      return searchResult.getName();
    }

    @Override
    public Image getImage(final Object element) {
      Image image;
      final ITreeNode searchResult = (ITreeNode) element;
      image = searchResult.getImage();
      if (image == null && element instanceof IAdtObjectReferenceNode) {
        final IAdtObjectReferenceNode adtObjRefNode = (IAdtObjectReferenceNode) element;
        if (adtObjRefNode.getObjectType() == ObjectType.DATA_DEFINITION) {
          image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.CDS_VIEW);
        } else {
          image = AdtTypeUtil.getInstance().getTypeImage(adtObjRefNode.getAdtObjectType());
        }
        final IExtendedAdtObjectInfo extendedResult = adtObjRefNode.getAdapter(
            IExtendedAdtObjectInfo.class);
        if (extendedResult != null) {
          final String[] overlayImages = new String[4];
          if (extendedResult.getSourceType() != null) {
            overlayImages[IDecoration.BOTTOM_RIGHT] = extendedResult.getSourceType().getImageId();
          }
          if (extendedResult.isReleased()) {
            overlayImages[IDecoration.TOP_RIGHT] = IImages.RELEASED_API_OVR;
          }
          image = SearchAndAnalysisPlugin.getDefault().overlayImage(image, overlayImages);
        }

      }
      return image;
    }

    @Override
    public StyledString getStyledText(final Object element) {
      StyledString text = new StyledString();
      final ITreeNode searchResult = (ITreeNode) element;

      boolean isAdtObjRefNode = false;
      if (element instanceof IAdtObjectReferenceNode) {
        isAdtObjRefNode = true;
      }

      if (element instanceof IStyledTreeNode) {
        text = ((IStyledTreeNode) element).getStyledText();
        if (text == null) {
          text = new StyledString();
        }
      } else {
        if (element instanceof LoadingTreeItemsNode) {
          text.append(searchResult.getDisplayName(), StylerFactory.ITALIC_STYLER);
          return text;
        }
        text.append(searchResult.getDisplayName());

        if (element instanceof ICollectionTreeNode && !isAdtObjRefNode) {
          final ICollectionTreeNode collectionNode = (ICollectionTreeNode) element;
          if (collectionNode.hasChildren()) {
            final String size = ((ICollectionTreeNode) element).getSizeAsString();
            if (size != null) {
              text.append(" (" + size + ")", StyledString.COUNTER_STYLER); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        }

        final String description = searchResult.getDescription();
        if (description != null && !description.isEmpty()) {
          text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
              StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR,
                  null));
        }
      }

      return text;
    }
  }

  private class TreeContentProvider extends LazyLoadingTreeContentProvider {
    @Override
    public Object[] getElements(final Object inputElement) {
      if (result != null) {
        return result.getResultForTree(groupByPackageAction.isChecked());
      }
      return new Object[0];
    }
  }

  private class GroupByPackageAction extends Action {
    public GroupByPackageAction() {
      super(Messages.ObjectSearch_GroupByPackageAction_xtol, AS_CHECK_BOX);
      setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.PACKAGE));
    }

    @Override
    public void run() {
      updateGrouping();
    }
  }

  private class ExpandAllPackageNodesAction extends ExpandAllAction {
    @Override
    public void run() {
      expandAllPackages();
    }
  }

  private class ExpandSelectedPackageNodesAction extends Action {
    private final TreeViewer viewer;

    public ExpandSelectedPackageNodesAction(final TreeViewer viewer) {
      super(Messages.ObjectSearch_ExpandNodeAction_xmsg, AdtBaseUIResources.getImageDescriptor(
          IAdtBaseImages.EXPAND_ALL));
      this.viewer = viewer;
    }

    @Override
    public void run() {
      final IStructuredSelection selection = viewer.getStructuredSelection();
      if (selection == null) {
        return;
      }
      BusyIndicator.showWhile(getSite().getShell().getDisplay(), () -> {
        viewer.getControl().setRedraw(false);
        try {
          for (final Object selectedObject : selection.toList()) {
            final PackageNode node = (PackageNode) selectedObject;
            viewer.setExpandedState(node, true);
            for (final PackageNode subNode : node.getSubPackages()) {
              viewer.setExpandedState(subNode, true);
            }
          }
        } finally {
          viewer.getControl().setRedraw(true);
        }
      });
    }
  }

  /*
   * Represents the current state of the object
   */
  private static class UIState {
    private ISelection selection;

    /**
     * @return the stored selection
     */
    public ISelection getSelection() {
      return selection;
    }

    /**
     * @return <code>true</code> if the stored state has a selection
     */
    public boolean hasSelection() {
      return selection != null && !selection.isEmpty();
    }

    /**
     * @param selection the selectedObject to set
     */
    public void setSelection(final ISelection selection) {
      this.selection = selection;
    }

    private TreePath[] expandedPaths;

    /**
     * @return the expandedPaths
     */
    public TreePath[] getExpandedPaths() {
      return expandedPaths;
    }

    /**
     * @param expandedPaths the expandedPaths to set
     */
    public void setExpandedPaths(final TreePath[] expandedPaths) {
      this.expandedPaths = expandedPaths;
    }

  }

}
