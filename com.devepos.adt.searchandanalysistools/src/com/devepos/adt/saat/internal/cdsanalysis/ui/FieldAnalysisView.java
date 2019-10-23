package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.IContextMenuConstants;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoRetrievalService;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.tree.SimpleInfoTreeNode;
import com.devepos.adt.saat.internal.ui.PreferenceToggleAction;
import com.devepos.adt.saat.internal.ui.PrefixedAsteriskFilteredTree;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Analysis page for analyzing the fields of a database entity like (Table, View
 * or CDS View)
 *
 * @author stockbal
 */
public class FieldAnalysisView extends CdsAnalysisPage {

	static final String SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY = "com.devepos.adt.saat.fieldanalysis.searchDbViewUsages"; //$NON-NLS-1$
	private SashForm fieldsHierarchySplitter;
	private FilteredTree fieldsTree;
	private ViewForm fieldsViewerViewForm;
	private FieldHierarchyView hierarchyView;
	private String currentEntity;
	private IDestinationProvider destProvider;
	private PreferenceToggleAction searchDbViewUsages;

	public FieldAnalysisView(final CdsAnalysis viewPart) {
		super(viewPart);
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
	public void createControl(final Composite parent) {
		super.createControl(parent);

		registerViewerToClipboardAction(this.hierarchyView.getViewer());
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
	protected void fillPullDownMenu(final IMenuManager menu) {
		super.fillPullDownMenu(menu);
		menu.add(this.searchDbViewUsages);
	}

	@Override
	protected void createActions() {
		super.createActions();
		this.searchDbViewUsages = new PreferenceToggleAction(Messages.FieldAnalysisView_SearchDbViewsInWhereUsed_xmit, null,
			SEARCH_DB_VIEWS_WHERE_USED_PREF_KEY, false);
	}

	@Override
	protected TreeViewer createTreeViewer(final Composite parent) {
		// just the returns the viewer of the filtered tree
		return this.fieldsTree.getViewer();
	}

	@Override
	protected Composite createTreeViewerComposite(final Composite parent) {

		this.fieldsHierarchySplitter = new SashForm(parent, SWT.VERTICAL);

		this.fieldsViewerViewForm = new ViewForm(this.fieldsHierarchySplitter, SWT.NONE);
		this.fieldsTree = createFilteredTree(this.fieldsViewerViewForm);
		this.fieldsViewerViewForm.setContent(this.fieldsTree);

		this.hierarchyView = new FieldHierarchyView(this, this.fieldsHierarchySplitter);
		// Register hierarchy viewer
		getSelectionAdapter().addViewer(this.hierarchyView.getViewer());

		/*
		 * initially the detail part is not visible at startup as the fields be loaded
		 * dynamically
		 */
		this.hierarchyView.setVisible(false);

		return this.fieldsHierarchySplitter;
	}

	@Override
	protected void setTreeInput(final IAdtObjectReferenceElementInfo adtObjectInfo, final TreeViewer treeViewer) {
		final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(adtObjectInfo.getName(),
			adtObjectInfo.getDisplayName(), adtObjectInfo.getDescription(), adtObjectInfo.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
		final ObjectType type = ObjectType.getFromAdtType(adtObjectInfo.getAdtObjectReference().getType());
		if (type == null) {
			return;
		}
		this.currentEntity = adtObjectInfo.getDisplayName();
		this.destProvider = destProvider;
		this.hierarchyView.setEntityInformation(adtObjectInfo.getDisplayName(), destProvider, type);
		node.setElementInfoProvider(new IElementInfoProvider() {
			@Override
			public String getProviderDescription() {
				return NLS.bind(Messages.FieldAnalysisView_FieldLoadingProviderDesc_xmsg, adtObjectInfo.getDisplayName());
			}

			@Override
			public List<IElementInfo> getElements() {
				final IElementInfoRetrievalService elementInfoService = ElementInfoRetrievalServiceFactory.createService();
				return elementInfoService.getElementColumnInformation(destProvider.getDestinationId(), adtObjectInfo.getName(),
					ObjectType.getFromAdtType(adtObjectInfo.getAdtObjectReference().getType()));
			}
		});
		treeViewer.setInput(new Object[] { node });
		treeViewer.expandAll();
	}

	@Override
	protected void refreshAnalysis() {
		final Object[] nodes = (Object[]) this.fieldsTree.getViewer().getInput();
		if (nodes == null) {
			return;
		}
		boolean refreshFieldsTree = true;
		final IStructuredSelection selection = (IStructuredSelection) this.fieldsTree.getViewer().getSelection();
		if (selection != null && !selection.isEmpty()) {
			if (!(selection.getFirstElement() instanceof IAdtObjectReferenceNode)) {
				refreshFieldsTree = false;
			}
		}
		if (refreshFieldsTree) {
			// refresh complete field tree
			this.fieldsTree.getFilterControl().setText(""); //$NON-NLS-1$
			final ILazyLoadingNode node = (ILazyLoadingNode) nodes[0];
			this.hierarchyView.clearInputCache();

			node.resetLoadedState();
			getViewPart().updateLabel();
			getViewer().refresh();
		} else {
			this.hierarchyView.reloadFieldInput();
		}
	}

	@Override
	protected void configureTreeViewer(final TreeViewer treeViewer) {
		treeViewer.setContentProvider(new LazyLoadingTreeContentProvider());
		treeViewer.setUseHashlookup(true);
		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeViewerLabelProvider()));
		treeViewer.addDoubleClickListener((event) -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (selection.isEmpty()) {
				return;
			}
			final Object selectedObj = selection.getFirstElement();
			if (selectedObj instanceof SimpleInfoTreeNode) {
				AdtUtil.navigateToEntityColumn(this.currentEntity, ((SimpleInfoTreeNode) selectedObj).getDisplayName(),
					this.destProvider.getDestinationId());
			}
		});
		treeViewer.addSelectionChangedListener(event -> {
			boolean hierarchyVisible = false;
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (!selection.isEmpty()) {
				final Object selected = selection.getFirstElement();
				if (selected instanceof SimpleInfoTreeNode) {
					final SimpleInfoTreeNode fieldNode = (SimpleInfoTreeNode) selected;
					this.hierarchyView.setFieldHierarchyInput(fieldNode);
					hierarchyVisible = true;
				}
			}
			if (hierarchyVisible != this.hierarchyView.isVisible()) {
				this.hierarchyView.setVisible(hierarchyVisible);
				this.fieldsHierarchySplitter.layout();
			}
		});

	}

	@Override
	protected String getLabelPrefix() {
		return Messages.FieldAnalysisView_ViewLabel_xfld;
	}

	/*
	 * Creates the filtered tree for the display of the fields of a database entity
	 */
	private FilteredTree createFilteredTree(final Composite parent) {
		final FilteredTree tree = new PrefixedAsteriskFilteredTree(parent,
			SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION, createPatternFilter()) {
			@Override
			protected void textChanged() {
				super.textChanged();
				Display.getDefault().timerExec(500, (Runnable) () -> {
					if (this.filterText != null && this.filterText.getText().length() == 0) {
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
					return wordMatches(node.getName()) || wordMatches(node.getDisplayName())
						|| wordMatches(node.getDescription());
				}
				final DelegatingStyledCellLabelProvider labelProvider = (DelegatingStyledCellLabelProvider) getViewer()
					.getLabelProvider();
				final String labelText = labelProvider.getStyledStringProvider().getStyledText(element).getString();
				return wordMatches(labelText);
			}
		};
	}

}
