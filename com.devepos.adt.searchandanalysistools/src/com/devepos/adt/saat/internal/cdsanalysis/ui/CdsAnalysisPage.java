package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.Page;

import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.IDataSourceType;
import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.ActionTreeNode;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ICollectionTreeNode;
import com.devepos.adt.saat.internal.tree.IStyledTreeNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider.LoadingElement;
import com.devepos.adt.saat.internal.ui.CollapseAllTreeNodesAction;
import com.devepos.adt.saat.internal.ui.CopyToClipboardAction;
import com.devepos.adt.saat.internal.ui.OpenAdtDataPreviewAction;
import com.devepos.adt.saat.internal.ui.OpenAdtObjectAction;
import com.devepos.adt.saat.internal.ui.SelectionProviderAdapter;
import com.devepos.adt.saat.internal.ui.SelectionProviderProxy;
import com.devepos.adt.saat.internal.ui.StylerFactory;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.model.IExtendedAdtObjectInfo;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Page part of the {@link CdsAnalyzerPage}
 *
 * @author stockbal
 */
public abstract class CdsAnalysisPage extends Page {

	protected Composite composite;
	protected IAdtObjectReferenceElementInfo adtObjectInfo;
	protected IAbapProjectProvider projectProvider;
	private StructuredViewer viewer;
	private final CdsAnalysis parentView;
	private MenuManager menuMgr;
	private SelectionProviderProxy selectionProvider;
	private CopyToClipboardAction copyToClipBoardAction;

	public CdsAnalysisPage(final CdsAnalysis viewPart) {
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
		getSite().registerContextMenu(this.parentView.getViewSite().getId(), this.menuMgr, this.selectionProvider);

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
		CdsAnalysis.createToolBarGroups(tbm);
		fillToolbar(tbm);
		tbm.update(false);
	}

	protected ISelectionProvider createSelectionProvider() {
		return new SelectionProviderAdapter();
	}

	/**
	 * Returns an image descriptor to proper the purpose of this analysis page
	 * <p>
	 * The default implementation returns <code>null</code>. <br>
	 * Subclasses should override this method
	 * </p>
	 *
	 * @return
	 */
	public Image getImage() {
		return null;
	}

	/**
	 * Returns an image to proper describe the purpose of this analysis page
	 * <p>
	 * The default implementation returns <code>null</code>. <br>
	 * Subclasses should override this method
	 * </p>
	 *
	 * @return
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
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
	public CdsAnalysis getViewPart() {
		return this.parentView;
	}

	/**
	 * Returns the label for this CDS Analysis page
	 *
	 * @return
	 */
	public String getLabel() {
		if (this.adtObjectInfo == null) {
			return null;
		}
		String systemId = null;
		final IDestinationProvider destProvider = this.adtObjectInfo.getAdapter(IDestinationProvider.class);
		if (destProvider != null) {
			systemId = destProvider.getSystemId();
		}

		if (systemId == null) {
			return String.format("%s: '%s'", getLabelPrefix(), this.adtObjectInfo.getDisplayName()); //$NON-NLS-1$
		} else {
			return String.format("%s: '%s' [%s]", getLabelPrefix(), this.adtObjectInfo.getDisplayName(), systemId); //$NON-NLS-1$
		}

	}

	/**
	 * Analyzes the given CDS View (or Database table/view for Where-Used) and shows
	 * the result in the structured viewer of the page
	 *
	 * @param adtObjectInfo ADT object information
	 */
	public final void setInput(final IAdtObjectReferenceElementInfo adtObjectInfo) {
		this.adtObjectInfo = adtObjectInfo;
		if (this.viewer == null && this.viewer.getControl().isDisposed()) {
			return;
		}
		final IDestinationProvider destProvider = ((IAdaptable) adtObjectInfo).getAdapter(IDestinationProvider.class);
		if (destProvider != null) {
			this.projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destProvider.getDestinationId());
		}
		setTreeInput(this.adtObjectInfo, (TreeViewer) this.viewer);
		fillPullDownMenu(getSite().getActionBars().getMenuManager());
	}

	@Override
	public void setFocus() {
		if (this.viewer != null) {
			this.viewer.getControl().setFocus();
		} else if (this.composite != null) {
			this.composite.setFocus();
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
	 * @param  parent the parent composite
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
	 * Sets the input for the the current tree viewer
	 *
	 * @param adtObjectInfo the ADT object reference information
	 * @param treeViewer    the tree viewer instance where the input should be set
	 */
	protected abstract void setTreeInput(IAdtObjectReferenceElementInfo adtObjectInfo, TreeViewer treeViewer);

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
	 * Returns the prefix for the label of this page
	 *
	 * @return
	 */
	protected abstract String getLabelPrefix();

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
		getSite().getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), this.copyToClipBoardAction);
	}

	/**
	 * Registers the given viewer to the Copy-To-Clipboard action which is
	 * accessible with the global shortcut <code>Strg+C</code>
	 *
	 * @param viewer the viewer to be registered
	 */
	protected final void registerViewerToClipboardAction(final StructuredViewer viewer) {
		if (this.copyToClipBoardAction == null) {
			return;
		}
		if (viewer == null) {
			return;
		}
		this.copyToClipBoardAction.registerViewer(viewer);
	}

	/**
	 * Register listeners for the {@link TreeViewer} <br>
	 * Subclasses may override
	 */
	protected void registerTreeListeners() {
		this.viewer.addDoubleClickListener(event -> {
			final ITreeSelection sel = (ITreeSelection) event.getSelection();
			final Object selectedObject = sel.getFirstElement();
			if (selectedObject instanceof IAdtObjectReferenceNode) {
				final IAdtObjectReferenceNode selectedAdtObject = (IAdtObjectReferenceNode) selectedObject;

				if (selectedAdtObject != null) {
					final IDestinationProvider destProvider = selectedAdtObject.getAdapter(IDestinationProvider.class);
					if (destProvider != null) {
						final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
							.getProviderForDestination(destProvider.getDestinationId());
						projectProvider.openObjectReference(selectedAdtObject.getObjectReference());
					}
				}
			} else if (selectedObject instanceof ICollectionTreeNode) {
				final boolean isExpanded = ((TreeViewer) this.viewer).getExpandedState(selectedObject);
				if (isExpanded) {
					((TreeViewer) this.viewer).collapseToLevel(selectedObject, 1);
				} else {
					((TreeViewer) this.viewer).expandToLevel(selectedObject, 1);
				}
			} else if (selectedObject instanceof ActionTreeNode) {
				((ActionTreeNode) selectedObject).getAction().execute();
			}
		});
	}

	/**
	 * Retrieves the an {@link Image} or <code>null</code> for the given element in
	 * a
	 *
	 * @param  element
	 * @return
	 */
	protected Image getTreeNodeImage(final Object element) {
		Image image = null;
		final ITreeNode node = (ITreeNode) element;
		final String imageId = node.getImageId();
		if (imageId != null) {
			image = SearchAndAnalysisPlugin.getDefault().getImage(imageId);

			if (imageId != IImages.WAITING && element instanceof IAdaptable) {
				final IExtendedAdtObjectInfo extendedSearchResultInfo = ((IAdaptable) element)
					.getAdapter(IExtendedAdtObjectInfo.class);
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
	 * @param  element the tree node for which a styled text should be determined
	 * @return
	 */
	protected StyledString getTreeNodeLabel(final Object element) {
		StyledString text = null;
		final ITreeNode node = (ITreeNode) element;

		if (element instanceof IStyledTreeNode) {
			text = ((IStyledTreeNode) element).getStyledText();
		} else {
			text = new StyledString();
			if (element instanceof LoadingElement) {
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
	 * @param  parent the parent composite for the {@link TreeViewer} instance
	 * @return        the created tree viewer
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
		tbm.appendToGroup(IContextMenuConstants.GROUP_EDIT, new ClosePageAction(false));
		tbm.appendToGroup(IContextMenuConstants.GROUP_EDIT, new ClosePageAction(true));
		if (this.viewer instanceof TreeViewer) {
			tbm.appendToGroup(IContextMenuConstants.GROUP_NODE_ACTIONS, new CollapseAllTreeNodesAction((TreeViewer) this.viewer));
		}
	}

	/**
	 * Fill the pull down menu of the View
	 * <p>
	 * Default implementation does nothing. Subclasses may override
	 * </p>
	 *
	 * @param menu the menu
	 */
	protected void fillPullDownMenu(final IMenuManager menu) {
	}

	/**
	 * Fills the context menu of the current Analysis page
	 *
	 * @param mgr            the menu manager instance
	 * @param commandChecker instance to verify if a command should be enabled or
	 *                       not
	 */
	protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandChecker) {
		if (!commandChecker.hasSelection()) {
			mgr.appendToGroup(IContextMenuConstants.GROUP_EDIT, this.copyToClipBoardAction);
			return;
		}
		if (commandChecker.canCommandBeEnabled(ICommandConstants.OPEN_IN_DB_BROWSER)) {
			MenuItemFactory.addOpenInDbBrowserCommand(mgr, IContextMenuConstants.GROUP_DB_BROWSER, false);
			MenuItemFactory.addOpenInDbBrowserCommand(mgr, IContextMenuConstants.GROUP_DB_BROWSER, true);
		}

		if (this.projectProvider != null) {
			final List<IAdtObjectReference> selectedObjRefs = commandChecker.getSelectedAdtObjectRefs();

			mgr.appendToGroup(IContextMenuConstants.GROUP_OPEN, new OpenAdtObjectAction(this.projectProvider, selectedObjRefs));
			if (commandChecker.hasSelection(true)) {
				mgr.appendToGroup(IContextMenuConstants.GROUP_OPEN,
					new OpenAdtDataPreviewAction(this.projectProvider.getProject(), selectedObjRefs));
			}
		}

		mgr.appendToGroup(IContextMenuConstants.GROUP_EDIT, this.copyToClipBoardAction);
	}

	/**
	 * Fills the context menu of the current Analysis page
	 *
	 * @param mgr the menu manager instance
	 */
	protected final void fillContextMenu(final IMenuManager mgr) {
		CdsAnalysis.createContextMenuGroups(mgr);
		fillContextMenu(mgr, new CommandPossibleChecker(false));
	}

	/**
	 * Action to close this or all open Analysis pages
	 *
	 * @author stockbal
	 */
	private class ClosePageAction extends Action {
		private final boolean closeOthersToo;

		public ClosePageAction(final boolean closeOthersToo) {
			super(closeOthersToo ? Messages.CdsAnalysis_CloseAllPages_xtol : Messages.CdsAnalysis_CloseCurrentPage_xtol);
			if (closeOthersToo) {
				setImageDescriptor(
					PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
			} else {
				setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
			}
			this.closeOthersToo = closeOthersToo;
		}

		@Override
		public void run() {
			if (this.closeOthersToo) {
				CdsAnalysisPage.this.parentView.closeAllPages();
			} else {
				CdsAnalysisPage.this.parentView.closePage(CdsAnalysisPage.this);
			}
		}

	}

	class TreeViewerLabelProvider extends LabelProvider implements ILabelProvider, IStyledLabelProvider {

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
				tree.setSortDirection(this.sortDirForNewColumn);
			} else {
				final int oldDir = tree.getSortDirection();

				final int newDir = oldDir == SWT.DOWN ? SWT.UP : SWT.DOWN;
				tree.setSortDirection(newDir);
			}
			this.viewer.refresh(false);
		}
	}

}
