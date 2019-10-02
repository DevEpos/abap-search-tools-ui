package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.Page;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.tree.ActionTreeNode;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ICollectionTreeNode;
import com.devepos.adt.saat.internal.tree.IStyledTreeNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider.LoadingElement;
import com.devepos.adt.saat.internal.ui.StylerFactory;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.model.IExtendedAdtObjectInfo;

public class ObjectSearchResultPage extends Page implements ISearchResultPage, ISearchResultListener {

	private String id;
	private Object state;
	private ObjectSearchResult result;
	private ISearchResultViewPart searchViewPart;
	private TreeViewer searchResultTree;
	private Composite mainComposite;
	private ObjectSearchQuery searchQuery;

	@Override
	public void createControl(final Composite parent) {
		this.mainComposite = createTreeViewerComposite(parent);
		createResultTree(this.mainComposite);
		getSite().setSelectionProvider(this.searchResultTree);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public Control getControl() {
		return this.mainComposite;
	}

	@Override
	public void setActionBars(final IActionBars actionBars) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
	}

	@Override
	public Object getUIState() {
		if (this.searchResultTree != null && !this.searchResultTree.getControl().isDisposed()) {
			return this.searchResultTree.getExpandedTreePaths();
		}
		return null;
	}

	@Override
	public void setInput(final ISearchResult search, final Object uiState) {
		if (this.result != null) {
			// clean up old search
			this.result.removeListener(this);
		}

		this.result = (ObjectSearchResult) search;
		if (this.result != null) {
			this.result.addListener(this);
			this.searchResultTree.setInput(this.result);
			this.state = uiState;
			this.searchQuery = (ObjectSearchQuery) this.result.getQuery();
			if (!NewSearchUI.isQueryRunning(this.searchQuery)) {
				updateUiState();
			}
		} else {
			this.searchViewPart.updateLabel();
		}
	}

	@Override
	public void setViewPart(final ISearchResultViewPart part) {
		this.searchViewPart = part;
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
		return this.id;
	}

	@Override
	public String getLabel() {
		if (this.result != null) {
			return this.result.getLabel();
		}
		return "";
	}

	@Override
	public void searchResultChanged(final SearchResultEvent e) {
		Display.getDefault().asyncExec(() -> {
			this.searchViewPart.updateLabel();
			this.searchResultTree.setInput(e.getSearchResult());
			this.searchResultTree.refresh();
			updateUiState();
		});

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

	/*
	 * Creates the result tree of the object search
	 */
	private void createResultTree(final Composite parent) {
		this.searchResultTree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		this.searchResultTree.setContentProvider(new TreeContentProvider());
		this.searchResultTree.setLabelProvider(new DelegatingStyledCellLabelProvider(new ViewLabelProvider()));
		this.searchResultTree.addDoubleClickListener(event -> {
			final ITreeSelection sel = (ITreeSelection) event.getSelection();
			final Object selectedObject = sel.getFirstElement();
			if (selectedObject instanceof IAdtObjectReferenceNode) {
				final IAdtObjectReferenceNode selectedAdtObject = (IAdtObjectReferenceNode) selectedObject;

				if (selectedAdtObject != null) {
					this.searchQuery.getProjectProvider().openObjectReference(selectedAdtObject.getObjectReference());
				}
			} else if (selectedObject instanceof ICollectionTreeNode) {
				final boolean isExpanded = this.searchResultTree.getExpandedState(selectedObject);
				if (isExpanded) {
					this.searchResultTree.collapseToLevel(selectedObject, 1);
				} else {
					this.searchResultTree.expandToLevel(selectedObject, 1);
				}
			} else if (selectedObject instanceof ActionTreeNode) {
				((ActionTreeNode) selectedObject).getAction().execute();
			}
		});

	}

	private void updateUiState() {
		Display.getDefault().asyncExec(() -> {
			if (this.searchResultTree == null || this.searchResultTree.getControl().isDisposed()) {
				return;
			}
			if (this.state == null || !(this.state instanceof TreePath[])) {
				return;
			}
			this.searchResultTree.setExpandedTreePaths((TreePath[]) this.state);
			this.searchResultTree.refresh();
		});
	}

	/**
	 * Custom view label provider for the Result Tree
	 *
	 * @author stockbal
	 */
	class ViewLabelProvider extends LabelProvider implements ILabelProvider, IStyledLabelProvider {

		@Override
		public String getText(final Object element) {
			final ITreeNode searchResult = (ITreeNode) element;

			return searchResult.getName();
		}

		@Override
		public Image getImage(final Object element) {
			Image image = null;
			final ITreeNode searchResult = (ITreeNode) element;
			final String imageId = searchResult.getImageId();
			if (imageId != null) {
				image = SearchAndAnalysisPlugin.getDefault().getImage(imageId);

				if (element instanceof IAdtObjectReferenceNode) {
					final IExtendedAdtObjectInfo extendedResult = ((IAdtObjectReferenceNode) element)
						.getAdapter(IExtendedAdtObjectInfo.class);
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
			}
			return image;
		}

		@Override
		public StyledString getStyledText(final Object element) {
			StyledString text = new StyledString();
			final ITreeNode searchResult = (ITreeNode) element;

			if (element instanceof IStyledTreeNode) {
				text = ((IStyledTreeNode) element).getStyledText();
				if (text == null) {
					text = new StyledString();
				}
			} else {
				if (element instanceof LoadingElement) {
					text.append(searchResult.getDisplayName(), StylerFactory.ITALIC_STYLER);
					return text;
				} else {
					text.append(searchResult.getDisplayName());
				}

				if (element instanceof ICollectionTreeNode) {
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
						StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR, null));
				}
			}

			return text;
		}
	}

	class TreeContentProvider extends LazyLoadingTreeContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {
			if (ObjectSearchResultPage.this.result != null) {
				return ObjectSearchResultPage.this.result.getResultForTree();
			}
			return new Object[0];
		}
	}

}
