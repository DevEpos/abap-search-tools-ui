package com.devepos.adt.saat.internal.search.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.IContextMenuConstants;
import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.SearchEngine;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.search.contentassist.SearchPatternProvider;
import com.devepos.adt.saat.internal.search.model.InternalSearchEngine;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQuery;
import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.internal.tree.ActionTreeNode;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ICollectionTreeNode;
import com.devepos.adt.saat.internal.tree.IStyledTreeNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider.LoadingElement;
import com.devepos.adt.saat.internal.ui.CollapseAllTreeNodesAction;
import com.devepos.adt.saat.internal.ui.CollapseTreeNodesAction;
import com.devepos.adt.saat.internal.ui.CopyToClipboardAction;
import com.devepos.adt.saat.internal.ui.IStatusUpdater;
import com.devepos.adt.saat.internal.ui.MenuItemFactory;
import com.devepos.adt.saat.internal.ui.OpenAdtDataPreviewAction;
import com.devepos.adt.saat.internal.ui.OpenAdtObjectAction;
import com.devepos.adt.saat.internal.ui.StylerFactory;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.AbapProjectProxy;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.TextControlUtil;
import com.devepos.adt.saat.search.model.IExtendedAdtObjectInfo;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.devepos.adt.saat.search.model.IObjectSearchQueryListener;
import com.devepos.adt.saat.search.model.IObjectSearchQueryResult;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;

/**
 * Object explorer for searching/analyzing ABAP Repository objects
 * <p>
 * Currently only two types of objects are supported
 * <ul>
 * <li>CDS Views</li>
 * <li>Database Tables/Views</li>
 * </ul>
 * </p>
 *
 * @author stockbal
 */
public class ObjectExplorerView extends ViewPart implements IStatusUpdater, IObjectSearchQueryListener {
	public static final String VIEW_ID = "com.devepos.adt.saat.views.dbobjectexplorer"; //$NON-NLS-1$
	public static final String LAST_PROJECT_DESTINATION_PREF = "com.devepos.adt.saat.dbobjectexplorer.lastSelectedProject";
	private static final int LABEL_X_HINT = 45;
	@Inject
	IWorkbench workbench;
	private ComboViewer projectViewer;
	private ComboViewer searchTypeViewer;
	private Text searchInput;
	private TreeViewer searchResultTree;
	private Label searchStatusImageLabel;
	private SearchPatternProvider searchPatternProvider;
	final AbapProjectProxy projectProvider;
	private Label searchStatusTextLabel;
	private IResourceChangeListener projectChangeListener;
	private CopyToClipboardAction copyToClipBoardAction = null;

	private Composite statusArea;
	private Action refreshSearchAction;
	private IAction andFilterToggleAction;
	private boolean andFilterOption;
	private Composite searchComposite;
	private IAction historyAction;
	private boolean controlsEnabled = true;
	private SearchFavoritesAction favoritesAction;
	private MenuManager menuMgr;
	private IAction collapseNodesAction;
	private boolean updateLastProjectPreference;
	private final IPreferenceStore prefStore;
	private final IPropertyChangeListener preferenceListener;

	public ObjectExplorerView() {
		super();
		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.prefStore.setDefault(LAST_PROJECT_DESTINATION_PREF, ""); //$NON-NLS-1$
		this.updateLastProjectPreference = this.prefStore.getBoolean(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER);
		this.preferenceListener = (event) -> {
			if (event.getProperty().equals(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER)) {
				this.updateLastProjectPreference = (boolean) event.getNewValue();
			}
		};
		this.prefStore.addPropertyChangeListener(this.preferenceListener);
		this.projectProvider = new AbapProjectProxy(null);
	}

	@Override
	public void createPartControl(final Composite parent) {
		GridLayoutFactory.swtDefaults().margins(5, 5).numColumns(3).applyTo(parent);

		createProjectChooserInput(parent);
		createSearchComposite(parent);
		createStatusArea(parent);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, SearchAndAnalysisPlugin.PLUGIN_ID + ".main"); //$NON-NLS-1$

		this.searchPatternProvider = new SearchPatternProvider(this.projectProvider,
			(SearchType) this.searchTypeViewer.getStructuredSelection().getFirstElement());
		this.searchPatternProvider.addContentAssist(this.searchInput);

		makeActions();
		registerResourceListener();
		prepareMenuManager();

		if (loadProjectList(false)) {
			// load last project if available
			if (!loadLastUsedProject()) {
				// no last project was found so use the current "selected" project
				final IProject currentABAPProject = AdtUtil.getCurrentAbapProject();
				if (currentABAPProject != null) {
					this.projectViewer.setSelection(new StructuredSelection(currentABAPProject));
				} else {
					selectFirstProject();
				}
			}
		} else {
			setControlInputEnabledState(false);
		}
		initializeListeners();
		SearchAndAnalysisPlugin.getDefault().getHistory().setActiveEntry(null);
		getSite().setSelectionProvider(this.searchResultTree);
	}

	/**
	 * Sets the given project as the currently selected project in the project
	 * chooser dropdown
	 *
	 * @param project the project to set as the active one in the project chooser
	 */
	public void setProject(final IProject project) {
		if (this.projectViewer == null || project == null) {
			return;
		}
		if (this.projectViewer.testFindItem(project) == null) {
			loadProjectList(false);
		}
		this.projectViewer.setSelection(new StructuredSelection(project), true);
	}

	/**
	 * Search for the given object and shows it the result tree
	 *
	 * @param name
	 * @param objectType
	 */
	public void showObject(final String name, final ObjectType objectType) {
		SearchType searchType = null;
		switch (objectType) {
		case CDS_VIEW:
			searchType = SearchType.CDS_VIEW;
			break;
		case TABLE:
			searchType = SearchType.DB_TABLE_VIEW;
			break;
		default:
			return;
		}
		this.searchInput.setText(name + "<"); //$NON-NLS-1$
		this.searchTypeViewer.setSelection(new StructuredSelection(searchType));
		refreshSearch();
	}

	@Override
	public void dispose() {
		if (this.projectChangeListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.projectChangeListener);
		}
		if (this.preferenceListener != null) {
			this.prefStore.removePropertyChangeListener(this.preferenceListener);
		}
		if (this.searchPatternProvider != null) {
			this.searchPatternProvider.dispose();
			this.searchPatternProvider = null;
		}
		InternalSearchEngine.getInstance().getQueryManager().removeQueryListener(this);
		InternalSearchEngine.getInstance().getQueryManager().removeStatusUpdate(this);

		if (this.menuMgr != null) {
			this.menuMgr.dispose();
			this.menuMgr = null;
		}
		super.dispose();
	}

	/**
	 * Shows the DB Object Explorer
	 */
	public void showExplorer() {
		if (this.searchInput != null) {
			final boolean setCursorToEnd = this.prefStore.getBoolean(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT);
			if (!setCursorToEnd) {
				this.searchInput.selectAll();
			} else {
				final String currentInput = this.searchInput.getText();
				final int length = currentInput != null ? currentInput.length() : 0;
				this.searchInput.setSelection(length);
			}
			this.searchInput.setFocus();
		}
	}

	@Override
	public void setFocus() {
		if (this.searchResultTree != null && this.searchResultTree.getInput() != null) {
			this.searchResultTree.getControl().setFocus();
		} else if (this.searchInput != null && !this.searchInput.isDisposed()) {
			this.searchInput.setFocus();
		}
	}

	@Override
	public void updateStatus(final int statusCode, final String text) {
		if (this.searchStatusImageLabel == null || this.searchStatusImageLabel.isDisposed()) {
			return;
		}
		this.searchStatusImageLabel.setImage(getImageForStatus(statusCode));
		this.searchStatusTextLabel.setText(text);
		this.searchStatusTextLabel.setToolTipText(text);
		this.searchStatusTextLabel.pack(true);
		this.statusArea.layout();
	}

	@Override
	public void queryFinished(final IObjectSearchQueryResult queryResult) {
		final IObjectSearchQuery query = queryResult.getQuery();
		final IAdtObjectReferenceElementInfo[] searchResult = queryResult.getResults();
		if (queryResult.createHistoryEntry()) {
			SearchAndAnalysisPlugin.getDefault().getHistory().addEntry(queryResult.getQuery(), searchResult.length);
		}
		Display.getDefault().asyncExec(() -> {
			if (query.shouldUpdateView()) {
				updateViewFromQuery(query);
			}
			final IAdtObjectReferenceNode[] nodes = new LazyLoadingAdtObjectReferenceNode[searchResult.length];
			for (int i = 0; i < searchResult.length; i++) {
				final IAdtObjectReferenceElementInfo elemInfo = searchResult[i];
				final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(elemInfo.getName(),
					elemInfo.getDisplayName(), elemInfo.getDescription(), elemInfo.getAdtObjectReference(), null);
				node.setElementInfoProvider(elemInfo.getElementInfoProvider());
				node.setAdditionalInfo(elemInfo.getAdditionalInfo());
				nodes[i] = node;
			}
			this.searchResultTree.setInput(nodes);
			this.searchResultTree.refresh();
			if (searchResult != null && searchResult.length > 0) {
				// warning message if result count > maxResult parameter
				String resultCountString = null;
				int statusCode = IStatus.INFO;
				if (searchResult.length == 1) {
					ObjectExplorerView.this.searchResultTree.expandAll();
					ObjectExplorerView.this.searchResultTree.getControl().setFocus();
					resultCountString = Messages.ObjectSearch_OneResult_xmsg;
				} else if (searchResult.length > queryResult.getMaxResultCount()) {
					resultCountString = NLS.bind(Messages.ObjectSearch_MoreThanMaxRowsResults_xmsg,
						new DecimalFormat("###,###").format(queryResult.getMaxResultCount())); //$NON-NLS-1$
					statusCode = IStatus.WARNING;
				} else {
					resultCountString = NLS.bind(Messages.ObjectSearch_Results_xmsg,
						new DecimalFormat("###,###").format(searchResult.length)); //$NON-NLS-1$
				}
				updateStatus(statusCode, resultCountString);
			} else {
				updateStatus(IStatus.INFO, Messages.ObjectSearch_NoResults_xmsg);
			}
		});
	}

	/**
	 * Prepares the menu manager for the search view
	 */
	private void prepareMenuManager() {
		this.menuMgr = new MenuManager();
		this.menuMgr.setRemoveAllWhenShown(true);
		this.menuMgr.addMenuListener(manager -> {
			fillContextMenu(manager);
		});
		final Menu menu = this.menuMgr.createContextMenu(this.searchResultTree.getControl());
		this.searchResultTree.getControl().setMenu(menu);
		this.menuMgr.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		final IWorkbenchPartSite site = getSite();
		site.registerContextMenu(this.menuMgr, site.getSelectionProvider());
	}

	/**
	 * Creates actions Note: Example on how to implement tool bar buttons in coding
	 */
	private void makeActions() {
		this.copyToClipBoardAction = new CopyToClipboardAction();
		this.copyToClipBoardAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
		this.copyToClipBoardAction.registerViewer(this.searchResultTree);
		this.copyToClipBoardAction.registerTextControl(this.searchInput);
		this.collapseNodesAction = new CollapseTreeNodesAction(this.searchResultTree);
		final IActionBars actionBars = getViewSite().getActionBars();
		if (actionBars == null) {
			return;
		}
		fillToolbar(actionBars);

		final IMenuManager menuManager = actionBars.getMenuManager();
		// add actions to pull down menu
		menuManager.add(new DbBrowserSettingsAction());
	}

	private void fillToolbar(final IActionBars actionBars) {
		final IToolBarManager toolBarMgr = actionBars.getToolBarManager();
		toolBarMgr.add(new Separator(IContextMenuConstants.GROUP_NODE_ACTIONS));
		toolBarMgr.appendToGroup(IContextMenuConstants.GROUP_NODE_ACTIONS, new CollapseAllTreeNodesAction(this.searchResultTree));

		this.refreshSearchAction = new RefreshCurrentSearchAction();
		this.refreshSearchAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), this.refreshSearchAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), this.copyToClipBoardAction);
		this.andFilterToggleAction = new Action(Messages.ObjectSearch_UseAndFilter_xtol, SWT.TOGGLE) {
			@Override
			public void run() {
				ObjectExplorerView.this.andFilterOption = isChecked();
			}
		};
		this.andFilterToggleAction
			.setImageDescriptor(SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.INNER_JOIN));

		toolBarMgr.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		toolBarMgr.appendToGroup(IContextMenuConstants.GROUP_ADDITIONS, this.andFilterToggleAction);
		toolBarMgr.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
		this.favoritesAction = new SearchFavoritesAction(this.projectProvider);
		toolBarMgr.appendToGroup(IContextMenuConstants.GROUP_SEARCH, this.favoritesAction);
		this.historyAction = new HistoryManagerAction();
		toolBarMgr.appendToGroup(IContextMenuConstants.GROUP_SEARCH, this.historyAction);
		toolBarMgr.appendToGroup(IContextMenuConstants.GROUP_SEARCH, this.refreshSearchAction);

		actionBars.updateActionBars();
	}

	/**
	 * Fill context menu
	 *
	 * @param menuManager
	 */
	private void fillContextMenu(final IMenuManager menuManager) {
		final IStructuredSelection selection = this.searchResultTree.getStructuredSelection();
		if (selection == null || selection.isEmpty()) {
			return;
		}
		boolean selectionHasExpandedNodes = false;
		final List<IAdtObjectReference> adtObjRefs = new ArrayList<>();
		final List<IAdtObjectReference> previewAdtObjRefs = new ArrayList<>();
		final int selectionSize = selection.size();
		boolean singleDataPreviewObjectSelected = false;
		boolean singleCdsViewSelected = false;

		for (final Object selectedObject : selection.toList()) {
			if (selectedObject instanceof IAdtObjectReferenceNode) {
				final IAdtObjectReferenceNode objRefNode = (IAdtObjectReferenceNode) selectedObject;
				final IAdtObjectReference adtObjectRef = objRefNode.getObjectReference();
				if (objRefNode.supportsDataPreview()) {
					previewAdtObjRefs.add(adtObjectRef);
				}
				adtObjRefs.add(adtObjectRef);

				if (selectionSize == 1) {
					singleDataPreviewObjectSelected = true;
					singleCdsViewSelected = objRefNode.getObjectType() == ObjectType.CDS_VIEW;
				}
			}

			if (!selectionHasExpandedNodes && selectedObject instanceof ICollectionTreeNode
				&& this.searchResultTree.getExpandedState(selectedObject)) {
				selectionHasExpandedNodes = true;
			}
		}

		if (!adtObjRefs.isEmpty()) {
			menuManager.add(new OpenAdtObjectAction(this.projectProvider, adtObjRefs));
		}
		if (!previewAdtObjRefs.isEmpty()) {
			menuManager.add(new OpenAdtDataPreviewAction(this.projectProvider.getProject(), previewAdtObjRefs));
			menuManager.add(new Separator(IContextMenuConstants.GROUP_DB_BROWSER));
			MenuItemFactory.addOpenInDbBrowserCommand(menuManager, false);
			MenuItemFactory.addOpenInDbBrowserCommand(menuManager, true);
		}

		if (singleDataPreviewObjectSelected) {
			// check if action is supported in the current project
			if (new CdsAnalysisUriDiscovery(this.projectProvider.getDestinationId()).getCdsAnalysisUri() != null) {
				menuManager.add(new Separator(IContextMenuConstants.GROUP_CDS_ANALYSIS));
				if (singleCdsViewSelected) {
					MenuItemFactory.addCdsAnalyzerCommandItem(menuManager, IContextMenuConstants.GROUP_CDS_ANALYSIS,
						ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
				}
				if (!previewAdtObjRefs.isEmpty()) {
					MenuItemFactory.addCdsAnalyzerCommandItem(menuManager, IContextMenuConstants.GROUP_CDS_ANALYSIS,
						ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
				}
				if (singleCdsViewSelected) {
					MenuItemFactory.addCdsAnalyzerCommandItem(menuManager, IContextMenuConstants.GROUP_CDS_ANALYSIS,
						ICommandConstants.USED_ENTITIES_ANALYSIS);
				}
				if (!previewAdtObjRefs.isEmpty()) {
					MenuItemFactory.addCdsAnalyzerCommandItem(menuManager, IContextMenuConstants.GROUP_CDS_ANALYSIS,
						ICommandConstants.FIELD_ANALYSIS);
				}

			}
		}
		if (selectionHasExpandedNodes) {
			menuManager.add(new Separator(IContextMenuConstants.GROUP_NODE_ACTIONS));
			menuManager.add(this.collapseNodesAction); // , collapsableNodes));
		}

		menuManager.add(new Separator(IContextMenuConstants.GROUP_EDIT));
		menuManager.appendToGroup(IContextMenuConstants.GROUP_EDIT, this.copyToClipBoardAction);
	}

	/*
	 * Loads the last used project at start up of View
	 */
	private boolean loadLastUsedProject() {
		final boolean useLastProject = this.prefStore.getBoolean(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER);
		if (!useLastProject) {
			return false;
		}
		final String destinationId = this.prefStore.getString(ObjectExplorerView.LAST_PROJECT_DESTINATION_PREF);
		if (destinationId == null || destinationId.isEmpty()) {
			return false;
		}
		// find project with this id
		final IProject[] projects = (IProject[]) this.projectViewer.getInput();
		for (final IProject project : projects) {
			final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
			if (abapProject != null && destinationId.equals(abapProject.getDestinationId())) {
				this.projectViewer.setSelection(new StructuredSelection(project));
				return true;
			}
		}
		// project obviously could not be found so set the stored preference to ""
		this.prefStore.setToDefault(LAST_PROJECT_DESTINATION_PREF);
		return false;
	}

	/*
	 * Initialized the query listener
	 */
	private void initializeListeners() {
		InternalSearchEngine.getInstance().getQueryManager().addQueryListener(this);
		InternalSearchEngine.getInstance().getQueryManager().addStatusUpdater(this);
	}

	/**
	 * Creates control for choosing the current project to be used
	 *
	 * @param parent the composite control
	 */
	private void createProjectChooserInput(final Composite parent) {
		final Label projectComboLabel = new Label(parent, SWT.NONE);
		GridDataFactory.swtDefaults().hint(LABEL_X_HINT, SWT.DEFAULT).applyTo(projectComboLabel);
		projectComboLabel.setText(Messages.ObjectSearch_ProjectInput_xfld);

		this.projectViewer = new ComboViewer(parent, SWT.READ_ONLY);
		final GridData projectViewerGridData = new GridData(150, SWT.DEFAULT);
		projectViewerGridData.horizontalSpan = 2;
		this.projectViewer.getControl().setLayoutData(projectViewerGridData);

		this.projectViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.projectViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof IProject) {
					final IProject project = (IProject) element;
					return project.getName();
				}
				return super.getText(element);
			}
		});
		this.projectViewer.addSelectionChangedListener((evt) -> {
			final IStructuredSelection selection = evt.getStructuredSelection();
			if (selection != null) {
				final IProject newProjectSelection = (IProject) selection.getFirstElement();
				this.projectProvider.setProject(newProjectSelection);
				if (this.updateLastProjectPreference) {
					this.prefStore.setValue(ObjectExplorerView.LAST_PROJECT_DESTINATION_PREF,
						this.projectProvider.getAbapProject().getDestinationId());
				}
			}
		});
	}

	/**
	 * Handle the <strong>enabled</strong> state of the input controls
	 *
	 * @param enable
	 */
	private void setControlInputEnabledState(final boolean enable) {
		if (this.controlsEnabled == enable) {
			return;
		}
		this.controlsEnabled = enable;

		if (this.searchInput != null && !this.searchInput.isDisposed()) {
			this.searchInput.setEnabled(enable);
		}
		if (this.searchResultTree != null && !this.searchResultTree.getControl().isDisposed()) {
			this.searchResultTree.getControl().setEnabled(enable);
			if (!enable) {
				this.searchResultTree.setInput(null);
			}
		}
		if (this.projectViewer != null && !this.projectViewer.getControl().isDisposed()) {
			this.projectViewer.getControl().setEnabled(enable);
		}
		if (this.searchTypeViewer != null && !this.searchTypeViewer.getControl().isDisposed()) {
			this.searchTypeViewer.getControl().setEnabled(enable);
		}
		if (this.refreshSearchAction != null) {
			this.refreshSearchAction.setEnabled(enable);
		}
		if (this.historyAction != null) {
			this.historyAction.setEnabled(enable);
		}
		if (this.favoritesAction != null) {
			this.favoritesAction.setEnabled(enable);
		}

		if (!enable) {
			updateStatus(IStatus.ERROR, Messages.ObjectSearch_NoProjectAvailable_xmsg);
		} else {
			updateStatus(-1, ""); //$NON-NLS-1$
		}
	}

	private void updateViewFromQuery(final IObjectSearchQuery query) {
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
			.getProviderForDestination(query.getDestinationId());
		this.projectViewer.setSelection(new StructuredSelection(projectProvider.getProject()));

		this.searchTypeViewer.setSelection(new StructuredSelection(query.getSearchType()));
		this.searchInput.setText(query.getQuery());
		this.andFilterToggleAction.setChecked(query.isAndSearchActive());
		this.andFilterOption = query.isAndSearchActive();
	}

	/**
	 * Creates new composite to hold the controls for performing a search
	 *
	 * @param parent the parent composite
	 */
	private void createSearchComposite(final Composite parent) {
		this.searchComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(this.searchComposite);
		GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(this.searchComposite);

		createSearchTypeInput(this.searchComposite);
		createSearchField(this.searchComposite);
		createSearchResultTree(this.searchComposite);
	}

	/**
	 * Create control for selecting the current search type
	 *
	 * @param parent the composite control for the control
	 */
	private void createSearchTypeInput(final Composite parent) {
		final Label typeComboLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().hint(LABEL_X_HINT, SWT.DEFAULT).applyTo(typeComboLabel);
		typeComboLabel.setText(Messages.ObjectSearch_SearchTypeInput_xfld);

		this.searchTypeViewer = new ComboViewer(parent, SWT.READ_ONLY);
		this.searchTypeViewer.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
		this.searchTypeViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.searchTypeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof SearchType) {
					return ((SearchType) element).toString();
				}
				return super.getText(element);
			}
		});
		this.searchTypeViewer.setInput(SearchType.values());
		this.searchTypeViewer.setSelection(new StructuredSelection(SearchType.CDS_VIEW));
		this.searchTypeViewer.addSelectionChangedListener(event -> {
			final SearchType selectedSearchType = (SearchType) event.getStructuredSelection().getFirstElement();
			this.searchPatternProvider.setSearchType(selectedSearchType);
		});
	}

	/**
	 * Creates search input field
	 *
	 * @param parent the composite control for the field
	 */
	private void createSearchField(final Composite parent) {
		final Label searchInputLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().span(3, 1).indent(0, 10).applyTo(searchInputLabel);
		searchInputLabel.setText(Messages.ObjectSearch_SearchInputPrompt_xfld);

		this.searchInput = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.searchInput);
		TextControlUtil.addWordSupport(this.searchInput);
		this.searchInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					refreshSearch();
				}
			}
		});
	}

	/**
	 * Create search result tree
	 *
	 * @param parent the composite control for the tree
	 */
	private void createSearchResultTree(final Composite parent) {
		final Label searchResultLabel = new Label(parent, SWT.NONE);
		searchResultLabel.setText(Messages.ObjectSearch_Results_xfld);

		this.searchResultTree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.span(3, 1)
			.grab(true, true)
			.applyTo(this.searchResultTree.getControl());

		this.searchResultTree.setContentProvider(new LazyLoadingTreeContentProvider());
		this.searchResultTree.setLabelProvider(new DelegatingStyledCellLabelProvider(new ViewLabelProvider()));
		this.searchResultTree.addDoubleClickListener(event -> {
			final ITreeSelection sel = (ITreeSelection) event.getSelection();
			final Object selectedObject = sel.getFirstElement();
			if (selectedObject instanceof IAdtObjectReferenceNode) {
				final IAdtObjectReferenceNode selectedAdtObject = (IAdtObjectReferenceNode) selectedObject;

				if (selectedAdtObject != null) {
					this.projectProvider.openObjectReference(selectedAdtObject.getObjectReference());
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

	private void createStatusArea(final Composite parent) {
		this.statusArea = new Composite(parent, SWT.NONE);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.statusArea);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this.statusArea);

		this.searchStatusImageLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(this.searchStatusImageLabel);

		this.searchStatusTextLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this.searchStatusTextLabel);
	}

	private void refreshSearch() {

		if (!this.projectProvider.hasProject()) {
			return;
		}

		// Trigger logon dialog if necessary
		if (!this.projectProvider.ensureLoggedOn()) {
			updateStatus(IStatus.ERROR,
				NLS.bind(Messages.ObjectSearch_ProjectLogonFailed_xmsg, this.projectProvider.getProjectName()));
			return;
		}

		final IObjectSearchQuery searchQuery = new ObjectSearchQuery(this.searchInput.getText(),
			(SearchType) this.searchTypeViewer.getStructuredSelection().getFirstElement(),
			this.projectProvider.getDestinationId());
		searchQuery.setAndSearchActice(this.andFilterOption);
		searchQuery.setCreateHistory(true);
		searchQuery.setReadApiState(true);
		SearchEngine.runObjectSearch(searchQuery);
	}

	/**
	 * Retrieves an image for the given status code
	 *
	 * @param  status
	 * @return        {@link Image}
	 */
	private Image getImageForStatus(final int status) {
		switch (status) {
		case IStatus.INFO:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		case IStatus.OK:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		default:
			return null;
		}
	}

	/**
	 * Loads project list into project combo
	 *
	 * @param  keepSelection pass <code>true</code> if the currently selected
	 *                       project should still be selected after refresh of the
	 *                       list
	 * @return               <code>true</code> if at least one project could be
	 *                       loaded
	 */
	private boolean loadProjectList(final boolean keepSelection) {
		if (this.projectViewer.getControl().isDisposed()) {
			return false;
		}
		IProject oldSelection = null;
		if (keepSelection) {
			oldSelection = (IProject) this.projectViewer.getStructuredSelection().getFirstElement();
		}
		final IProject[] abapProjects = AdtUtil.getAbapProjects();

		this.projectViewer.setInput(abapProjects);

		boolean selectFirst = false;
		if (keepSelection) {
			if (oldSelection == null && abapProjects.length > 0) {
				selectFirst = true;
			} else if (abapProjects != null && abapProjects.length > 0) {
				if (Arrays.asList(abapProjects).contains(oldSelection)) {
					this.projectViewer.setSelection(new StructuredSelection(oldSelection), true);
				} else {
					selectFirst = true;
				}
			}
		}

		if (selectFirst) {
			this.projectViewer.setSelection(new StructuredSelection(abapProjects[0]), true);
		}

		return abapProjects != null && abapProjects.length > 0;
	}

	/**
	 * Selects the first project in the project chooser dropdown
	 */
	private void selectFirstProject() {
		final IProject[] abapProjects = (IProject[]) this.projectViewer.getInput();
		if (abapProjects != null && abapProjects.length > 0) {
			final IProject project = abapProjects[0];
			this.projectViewer.setSelection(new StructuredSelection(project));
		}
	}

	/**
	 * Registers resource listener to automatically reload the project drop down or
	 * lock UI controls if all ABAP projects should be deleted
	 */
	private void registerResourceListener() {
		this.projectChangeListener = (IResourceChangeListener) event -> {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				final IResourceDelta delta = event.getDelta();
				final IResource resource = delta.getResource();
				if (resource instanceof IProject
					|| Stream.of(delta.getAffectedChildren()).anyMatch((child) -> child.getResource() instanceof IProject)) {
					Display.getDefault().asyncExec((Runnable) () -> {
						ObjectExplorerView.this.setControlInputEnabledState(ObjectExplorerView.this.loadProjectList(true));
					});
				}
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.projectChangeListener);
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

	/**
	 * Action for opening the DB Browser tools preference page from this view
	 *
	 * @author stockbal
	 */
	private class DbBrowserSettingsAction extends Action {
		public DbBrowserSettingsAction() {
			super(Messages.ObjectSearchOpenPreferences_xmit);
		}

		@Override
		public void run() {
			PreferencesUtil
				.createPreferenceDialogOn(null, IPreferences.MAIN_PREF_PAGE_ID, new String[] { IPreferences.MAIN_PREF_PAGE_ID },
					(Object) null)
				.open();
		}
	}

	/**
	 * Action to run the currently entered search again
	 *
	 * @author stockbal
	 */
	private class RefreshCurrentSearchAction extends Action {
		public RefreshCurrentSearchAction() {
			super(Messages.ObjectSearch_RefreshSearch_xtol,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.REFRESH));
		}

		@Override
		public void run() {
			refreshSearch();
		}

	}

}