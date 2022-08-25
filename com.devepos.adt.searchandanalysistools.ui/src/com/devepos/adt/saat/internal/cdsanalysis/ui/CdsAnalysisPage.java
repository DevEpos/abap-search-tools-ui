package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.Page;

import com.devepos.adt.base.IAdtObjectTypeConstants;
import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.StylerFactory;
import com.devepos.adt.base.ui.action.CollapseAllTreeNodesAction;
import com.devepos.adt.base.ui.action.CopyToClipboardAction;
import com.devepos.adt.base.ui.action.ExecuteAdtObjectAction;
import com.devepos.adt.base.ui.action.OpenAdtObjectAction;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.ui.tree.ActionTreeNode;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.ICollectionTreeNode;
import com.devepos.adt.base.ui.tree.IStyledTreeNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LoadingTreeItemsNode;
import com.devepos.adt.base.ui.util.AdtTypeUtil;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.IDataSourceType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.menu.SaatMenuItemFactory;
import com.devepos.adt.saat.internal.search.IExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.ui.SelectionProviderAdapter;
import com.devepos.adt.saat.internal.ui.SelectionProviderProxy;
import com.devepos.adt.saat.internal.ui.ViewUiState;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Page part of the {@link CdsAnalyzerPage}
 *
 * @author stockbal
 */
public abstract class CdsAnalysisPage<T extends CdsAnalysis> extends Page {

  protected Composite composite;
  protected IAbapProjectProvider projectProvider;
  protected T analysisResult;
  private StructuredViewer viewer;
  private final CdsAnalysisView parentView;
  private MenuManager menuMgr;
  private SelectionProviderProxy selectionProvider;
  private CopyToClipboardAction copyToClipBoardAction;

  public CdsAnalysisPage(final CdsAnalysisView viewPart) {
    this.parentView = viewPart;
  }

  @Override
  public void createControl(final Composite parent) {
    this.menuMgr = new MenuManager("#PopUp"); //$NON-NLS-1$
    this.menuMgr.setRemoveAllWhenShown(true);
    this.menuMgr.setParent(getSite().getActionBars().getMenuManager());
    this.menuMgr.addMenuListener(mgr -> {
      fillContextMenu(mgr);
    });

    this.selectionProvider = new SelectionProviderProxy();
    getSite().setSelectionProvider(this.selectionProvider);
    // Register menu
    getSite().registerContextMenu(this.parentView.getViewSite().getId(), this.menuMgr,
        this.selectionProvider);

    this.composite = createTreeViewerComposite(parent);
    this.viewer = createTreeViewer(this.composite);
    if (this.viewer != null) {
      configureTreeViewer((TreeViewer) this.viewer);
      registerTreeListeners();
      createActions();
      this.selectionProvider.addViewer(this.viewer, true);

      final Menu menu = this.menuMgr.createContextMenu(this.viewer.getControl());
      this.viewer.getControl().setMenu(menu);
    }

    final IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
    tbm.removeAll();
    CdsAnalysisView.createToolBarGroups(tbm);
    fillToolbar(tbm);
    tbm.update(false);
  }

  protected ISelectionProvider createSelectionProvider() {
    return new SelectionProviderAdapter();
  }

  @Override
  public Control getControl() {
    return this.composite;
  }

  /**
   * Returns the owning view of this page
   *
   * @return
   */
  public CdsAnalysisView getViewPart() {
    return this.parentView;
  }

  /**
   * Analyzes the given CDS View (or Database table/view for Where-Used) and shows
   * the result in the structured viewer of the page
   *
   * @param analysis the analysis input for the page
   */
  @SuppressWarnings("unchecked")
  public final void setInput(final CdsAnalysis analysis, final ViewUiState uiState) {
    if (this.analysisResult != null) {
      clearViewerInput();
    }
    this.analysisResult = (T) analysis;
    if (analysis != null) {
      final IAdtObjectReferenceElementInfo adtObjectInfo = analysis.getAdtObjectInfo();
      final IDestinationProvider destProvider = ((IAdaptable) adtObjectInfo).getAdapter(
          IDestinationProvider.class);
      if (destProvider != null) {
        this.projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destProvider
            .getDestinationId());
      }
      loadInput(uiState);
    }
  }

  @Override
  public void setFocus() {
    if (this.viewer != null) {
      this.viewer.getControl().setFocus();
    } else if (this.composite != null) {
      this.composite.setFocus();
    }
  }

  /**
   * Returns the current result displayed in this page
   *
   * @return
   */
  public T getAnalysisResult() {
    return this.analysisResult;
  }

  /**
   * Clears viewer input before new content is set
   */
  protected void clearViewerInput() {
    // perform some clean up
    if (this.viewer != null && !this.viewer.getControl().isDisposed()) {
      this.viewer.setInput(null);
    }
  }

  protected SelectionProviderProxy getSelectionAdapter() {
    return this.selectionProvider;
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

  /**
   * Loads the current input into the analysis page
   *
   * @param uiState the UI state from the last activation or <code>null</code>
   */
  protected abstract void loadInput(ViewUiState uiState);

  /**
   * Retrieves the current UI state of the page
   *
   * @return the current UI state of the page
   */
  protected abstract ViewUiState getUiState();

  /**
   * Refreshes the current analysis content
   */
  protected abstract void refreshAnalysis();

  /**
   * Configures the tree viewer of the view
   *
   * @param treeViewer the tree viewer to be configures
   */
  protected abstract void configureTreeViewer(TreeViewer treeViewer);

  /**
   * Returns the viewer of this page if {@link #FLAG_LAYOUT_FLAT} is set then a
   * {@link TableViewer} instance will be returned, otherwise a {@link TreeViewer}
   *
   * @return
   */
  protected StructuredViewer getViewer() {
    return this.viewer;
  }

  /**
   * Create actions for the View toolbar or the context menu
   */
  protected void createActions() {
    this.copyToClipBoardAction = new CopyToClipboardAction();
    this.copyToClipBoardAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
    this.copyToClipBoardAction.registerViewer(this.viewer);
    getSite().getActionBars()
        .setGlobalActionHandler(ActionFactory.COPY.getId(), this.copyToClipBoardAction);
  }

  /**
   * Registers the given viewer to the Copy-To-Clipboard action which is
   * accessible with the global shortcut <code>Strg+C</code>
   *
   * @param viewer the viewer to be registered
   */
  protected final void registerViewerToClipboardAction(final StructuredViewer viewer) {
    if (this.copyToClipBoardAction == null || viewer == null) {
      return;
    }
    this.copyToClipBoardAction.registerViewer(viewer);
  }

  /**
   * Register listeners for the {@link TreeViewer} <br>
   * Subclasses may override
   */
  protected void registerTreeListeners() {
    this.viewer.addOpenListener(event -> {
      final ITreeSelection sel = (ITreeSelection) event.getSelection();
      final Iterator<?> selectionIter = sel.iterator();
      while (selectionIter.hasNext()) {
        handleOpenOnNode(selectionIter.next());
      }
    });
  }

  /**
   * Handles the open event on one or several tree nodes in the main tree viewer
   * of the CDS Analysis page
   *
   * @param treeNode the tree node to be handled
   */
  protected void handleOpenOnNode(final Object treeNode) {
    if (treeNode == null) {
      return;
    }
    if (treeNode instanceof IAdtObjectReferenceNode) {
      final IAdtObjectReferenceNode selectedAdtObject = (IAdtObjectReferenceNode) treeNode;

      if (selectedAdtObject != null) {
        final IDestinationProvider destProvider = selectedAdtObject.getAdapter(
            IDestinationProvider.class);
        if (destProvider != null) {
          final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
              .getProviderForDestination(destProvider.getDestinationId());
          projectProvider.openObjectReference(selectedAdtObject.getObjectReference());
        }
      }
    } else if (treeNode instanceof ICollectionTreeNode) {
      final boolean isExpanded = ((TreeViewer) this.viewer).getExpandedState(treeNode);
      if (isExpanded) {
        ((TreeViewer) this.viewer).collapseToLevel(treeNode, 1);
      } else {
        ((TreeViewer) this.viewer).expandToLevel(treeNode, 1);
      }
    } else if (treeNode instanceof ActionTreeNode) {
      ((ActionTreeNode) treeNode).getAction().execute();
    }
  }

  /**
   * Retrieves the an {@link Image} or <code>null</code> for the given element in
   * a
   *
   * @param element
   * @return
   */
  protected Image getTreeNodeImage(final Object element) {
    Image image;
    final ITreeNode node = (ITreeNode) element;
    image = node.getImage();
    if (image == null) {

      if (element instanceof IAdtObjectReferenceNode) {
        final IAdtObjectReferenceNode adtObjRefNode = (IAdtObjectReferenceNode) element;
        if (adtObjRefNode.getObjectType() == ObjectType.DATA_DEFINITION) {
          image = AdtTypeUtil.getInstance().getTypeImage(IAdtObjectTypeConstants.CDS_VIEW);
        } else {
          image = AdtTypeUtil.getInstance().getTypeImage(adtObjRefNode.getAdtObjectType());
        }
      }
      if (element instanceof IAdaptable) {
        final IExtendedAdtObjectInfo extendedSearchResultInfo = ((IAdaptable) element).getAdapter(
            IExtendedAdtObjectInfo.class);
        if (extendedSearchResultInfo != null) {
          final String[] overlayIds = new String[4];
          if (extendedSearchResultInfo.isReleased()) {
            overlayIds[IDecoration.TOP_RIGHT] = IImages.RELEASED_API_OVR;
          }
          final IDataSourceType sourceType = extendedSearchResultInfo.getSourceType();
          if (sourceType != null && sourceType.getImageId() != null) {
            overlayIds[IDecoration.BOTTOM_RIGHT] = sourceType.getImageId();
          }
          image = SearchAndAnalysisPlugin.getDefault().overlayImage(image, overlayIds);
        }
      }
    }
    return image;
  }

  /**
   * Returns a {@link StyledString} for the given tree node
   * <code>element</element>
   *
   * @param element the tree node for which a styled text should be determined
   * @return
   */
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
//				text.append(" "); // for broader image due to overlay
        text.append(node.getDisplayName());
      }

      final String description = node.getDescription();
      if (description != null && !description.isEmpty()) {
        text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
            StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR, null));
      }
    }
    return text;
  }

  /**
   * Creates a tree viewer instance in the given {@link Composite}
   *
   * @param parent the parent composite for the {@link TreeViewer} instance
   * @return the created tree viewer
   */
  protected TreeViewer createTreeViewer(final Composite parent) {
    return new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
  }

  /**
   * Fills the pages' toolbar
   * <p>
   * Subclasses may override to add additional entries
   * </p>
   *
   * @param tbm the toolbar manager of the pages' site
   */
  protected void fillToolbar(final IToolBarManager tbm) {
    if (this.viewer instanceof TreeViewer) {
      tbm.appendToGroup(IGeneralMenuConstants.GROUP_NODE_ACTIONS, new CollapseAllTreeNodesAction(
          (TreeViewer) this.viewer));
    }
  }

  /**
   * Fills the context menu of the current Analysis page
   *
   * @param mgr            the menu manager instance
   * @param commandChecker instance to verify if a command should be enabled or
   *                       not
   */
  protected void fillContextMenu(final IMenuManager mgr,
      final CommandPossibleChecker commandChecker) {
    if (!commandChecker.hasSelection()) {
      mgr.appendToGroup(IGeneralMenuConstants.GROUP_EDIT, this.copyToClipBoardAction);
      return;
    }
    if (commandChecker.canCommandBeEnabled(ICommandConstants.OPEN_IN_DB_BROWSER)) {
      SaatMenuItemFactory.addOpenInDbBrowserCommand(mgr, IContextMenuConstants.GROUP_DB_BROWSER,
          false);
      SaatMenuItemFactory.addOpenInDbBrowserCommand(mgr, IContextMenuConstants.GROUP_DB_BROWSER,
          true);
    }

    if (this.projectProvider != null) {
      final List<IAdtObjectReference> selectedObjRefs = commandChecker.getSelectedAdtObjectRefs();

      mgr.appendToGroup(IGeneralMenuConstants.GROUP_OPEN, new OpenAdtObjectAction(
          this.projectProvider.getProject(), selectedObjRefs));
      if (commandChecker.hasSelection(true)) {
        mgr.appendToGroup(IGeneralMenuConstants.GROUP_OPEN, new ExecuteAdtObjectAction(
            this.projectProvider.getProject(), selectedObjRefs, true));
      }
    }

    mgr.appendToGroup(IGeneralMenuConstants.GROUP_EDIT, this.copyToClipBoardAction);
  }

  /**
   * Fills the context menu of the current Analysis page
   *
   * @param mgr the menu manager instance
   */
  protected final void fillContextMenu(final IMenuManager mgr) {
    CdsAnalysisView.createContextMenuGroups(mgr);
    fillContextMenu(mgr, new CommandPossibleChecker(false));
  }

  class TreeViewerLabelProvider extends LabelProvider implements ILabelProvider,
      IStyledLabelProvider {

    @Override
    public String getText(final Object element) {
      final ITreeNode node = (ITreeNode) element;
      return node.getName();
    }

    @Override
    public Image getImage(final Object element) {
      return getTreeNodeImage(element);
    }

    @Override
    public StyledString getStyledText(final Object element) {
      return getTreeNodeLabel(element);
    }
  }

  static final class SortListener implements Listener {
    private final TreeViewer viewer;
    private final int sortDirForNewColumn;

    public SortListener(final TreeViewer viewer) {
      this(viewer, SWT.UP);
    }

    public SortListener(final TreeViewer viewer, final int sortDirForNewColumn) {
      this.viewer = viewer;
      this.sortDirForNewColumn = sortDirForNewColumn;
    }

    @Override
    public void handleEvent(final Event event) {
      final TreeColumn newCol = (TreeColumn) event.widget;
      final Tree tree = newCol.getParent();

      final TreeColumn oldCol = tree.getSortColumn();

      if (oldCol != newCol) {
        tree.setSortColumn(newCol);
        tree.setSortDirection(sortDirForNewColumn);
      } else {
        final int oldDir = tree.getSortDirection();

        final int newDir = oldDir == SWT.DOWN ? SWT.UP : SWT.DOWN;
        tree.setSortDirection(newDir);
      }
      viewer.refresh(false);
    }
  }
}
