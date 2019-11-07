package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.part.PageSwitcher;

import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.help.HelpContexts;
import com.devepos.adt.saat.internal.help.HelpUtil;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.DummyPart;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * A view which is to be used to analyse a selected CDS view (either from the
 * project explorer view, or from the DB Object explorer view or even from an
 * open CDS view editor.<br>
 * <br>
 * The view provides the ability to navigate either from bottom to top, i.e.
 * analyze where the selected CDS view is used as a data selection source or
 * even as an Association source.<br>
 * The view also can be used to navigate from top to bottom, to see which
 * entities are used in the selection of the data.
 *
 * @author stockbal
 */
public class CdsAnalysis extends PageBookView {

	public enum AnalysisMode {
		TOP_DOWN,
		WHERE_USED,
		DEPENDENCY_TREE_USAGES,
		FIELD_ANALYSIS
	}

	public static final String VIEW_ID = "com.devepos.adt.saat.views.cdsanalyzer"; //$NON-NLS-1$

	private Composite pageContent;
	private Composite descriptionComposite;
	private final Map<DummyPart, CdsAnalysisPage> partsToPages;
	private final Map<CdsAnalysisPage, DummyPart> pagesToParts;
	private final Map<AnalysisObjectKey, CdsAnalysisPage> cdsViewToPage;

	private Label description;
	private RefreshCurrentAnalysisAction refreshAnalysisAction;
	private AnalysisPageSwitcherAction analysisPageSwitcher;

	public CdsAnalysis() {
		super();
		this.partsToPages = new HashMap<>();
		this.pagesToParts = new HashMap<>();
		this.cdsViewToPage = new LinkedHashMap<>();
	}

	@Override
	public void createPartControl(final Composite parent) {
		createActions();

		this.pageContent = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		this.pageContent.setLayout(layout);

		this.descriptionComposite = null;

		super.createPartControl(this.pageContent);
		getPageBook().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		HelpUtil.setHelp(this.pageContent, HelpContexts.CDS_ANALYZER);

		initializeToolBar();
		initializePageSwitcher();
	}

	/**
	 * Creates the groups and separators for the search view's tool bar
	 *
	 * @param toolbar the toolbar
	 */
	public static void createToolBarGroups(final IToolBarManager toolbar) {
		toolbar.add(new Separator(IContextMenuConstants.GROUP_EDIT));
		toolbar.add(new Separator(IContextMenuConstants.GROUP_NODE_ACTIONS));
		toolbar.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
		toolbar.add(new Separator(IContextMenuConstants.GROUP_GOTO));
		toolbar.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
	}

	public static void createContextMenuGroups(final IMenuManager mgr) {
		mgr.add(new Separator(IContextMenuConstants.GROUP_OPEN));
		mgr.add(new Separator(IContextMenuConstants.GROUP_DB_BROWSER));
		mgr.add(new Separator(IContextMenuConstants.GROUP_CDS_ANALYSIS));
		mgr.add(new Separator(IContextMenuConstants.GROUP_NODE_ACTIONS));
		mgr.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		mgr.add(new Separator(IContextMenuConstants.GROUP_EDIT));
	}

	public static void createViewMenuGroups(final IMenuManager mgr) {
		mgr.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));
		mgr.add(new Separator(IContextMenuConstants.GROUP_FILTERING));
		mgr.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
	}

	@Override
	public void setFocus() {
		final IPage page = getCurrentPage();
		if (page != null) {
			page.setFocus();
		} else {
			super.setFocus();
		}
	}

	/**
	 * Returns the active {@link CdsAnalysisPage}
	 *
	 * @return the active {@link CdsAnalysisPage}
	 */
	public CdsAnalysisPage getActivePage() {
		final IPage page = getCurrentPage();
		return page instanceof CdsAnalysisPage ? (CdsAnalysisPage) page : null;
	}

	/**
	 * Close the given page by removing the corresponding part of this page
	 *
	 * @param pageToClose the {@link CdsAnalysisPage} that should be closed
	 */
	void closePage(final CdsAnalysisPage pageToClose) {
		closePage(pageToClose, true);
	}

	/**
	 * Close the given page by removing the corresponding part of this page
	 *
	 * @param pageToClose   the {@link CdsAnalysisPage} that should be closed
	 * @param updateActions if <code>true</code> no actions/labels will be updated
	 */
	void closePage(final CdsAnalysisPage pageToClose, final boolean updateActions) {
		final DummyPart part = this.pagesToParts.get(pageToClose);
		CdsAnalysisPage pageToActivate = null;
		if (part == null) {
			return;
		}
		this.pagesToParts.remove(pageToClose);
		this.partsToPages.remove(part);

		CdsAnalysisPage previousPage = null;
		final Iterator<CdsAnalysisPage> iter = this.cdsViewToPage.values().iterator();
		while (iter.hasNext()) {
			final CdsAnalysisPage nextPage = iter.next();
			if (nextPage == pageToClose) {
				if (previousPage != null) {
					pageToActivate = previousPage;
				} else if (iter.hasNext()) {
					pageToActivate = iter.next();
				}
				break;
			}
			previousPage = nextPage;
		}
		this.cdsViewToPage.values().remove(pageToClose);
		partClosed(part);

		if (updateActions) {
			updateLabel();
			updateViewActions();
		}

		// activate the next open page if there still is any
		if (!updateActions) {
			return;
		}

		if (pageToActivate != null) {
			final DummyPart partToActivate = this.pagesToParts.get(pageToActivate);
			if (partToActivate != null) {
				partActivated(partToActivate);
				updateLabel();
			}
		}

	}

	/**
	 * Closes all pages of this Page View
	 */
	void closeAllPages() {
		final DummyPart[] parts = this.partsToPages.keySet().toArray(new DummyPart[this.partsToPages.keySet().size()]);
		for (final DummyPart part : parts) {
			partClosed(part);
		}
		this.pagesToParts.clear();
		this.partsToPages.clear();
		this.cdsViewToPage.clear();
		updateLabel();
		updateViewActions();
	}

	/**
	 * Updates the label of the current page
	 */
	public void updateLabel() {
		final CdsAnalysisPage page = getActivePage();
		String label = ""; //$NON-NLS-1$
		if (page != null) {
			label = LegacyActionTools.escapeMnemonics(page.getLabel());
		}
		if (this.pageContent.isDisposed()) {
			return;
		}
		if (label.length() == 0) {
			if (this.descriptionComposite != null) {
				this.descriptionComposite.dispose();
				this.descriptionComposite = null;
				this.pageContent.layout();
			}
		} else {
			if (this.descriptionComposite == null) {
				this.descriptionComposite = new Composite(this.pageContent, SWT.NONE);
				this.descriptionComposite.moveAbove(null);

				GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(this.descriptionComposite);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(this.descriptionComposite);

				this.description = new Label(this.descriptionComposite, SWT.LEAD | SWT.TOP | SWT.WRAP);
				GridDataFactory.fillDefaults()
					.align(SWT.FILL, SWT.CENTER)
					.grab(true, false)
					.indent(5, SWT.DEFAULT)
					.applyTo(this.description);
				this.description.setText(label);

				final Label separator = new Label(this.descriptionComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
				this.pageContent.layout();
			} else {
				this.description.setText(label);
				this.pageContent.layout();
			}
		}
	}

	/**
	 * Sets new ADT object for analysis
	 *
	 * @param mode          the mode for the CDS analysis
	 * @param objectUri     the URI of an ADT object
	 * @param destinationId the id of the destination of the ABAP project
	 */
	public void analyzeAdtObject(final AnalysisMode mode, final String objectUri, final String destinationId) {
		final AnalysisObjectKey newObject = new AnalysisObjectKey(mode, objectUri, destinationId);
		final CdsAnalysisPage pageForCds = this.cdsViewToPage.get(newObject);
		if (pageForCds != null) {
			// get the part
			final IWorkbenchPart part = this.pagesToParts.get(pageForCds);
			partActivated(part);
			updateLabel();
		} else {
			// determine ADT information about CDS view
			final Job adtObjectRetrievalJob = Job.create(Messages.CdsAnalysis_LoadAdtObjectJobName_xmsg,
				(ICoreRunnable) monitor -> {
					// check if search is possible in selected project
					final IAdtObjectReferenceElementInfo adtObjectRefElemInfo = ElementInfoRetrievalServiceFactory.createService()
						.retrieveBasicElementInformation(destinationId, objectUri);
					if (adtObjectRefElemInfo != null) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
							final CdsAnalysisPage page = createAnalyzerPage(newObject);
							page.setInput(adtObjectRefElemInfo);
							updateLabel();
						});
					}
				});
			adtObjectRetrievalJob.schedule();
		}
	}

	@Override
	protected IPage createDefaultPage(final PageBook book) {
		final MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(Messages.CdsAnalysis_NoOpenCdsAnalysis_xmsg);
		return page;
	}

	@Override
	protected PageRec doCreatePage(final IWorkbenchPart part) {
		final IPageBookViewPage page = this.partsToPages.get(part);
		initPage(page);
		page.createControl(getPageBook());
		final PageRec rec = new PageRec(part, page);
		return rec;
	}

	@Override
	protected void doDestroyPage(final IWorkbenchPart part, final PageRec pageRecord) {
		final IPage page = pageRecord.page;
		page.dispose();
		pageRecord.dispose();
		// empty cross-reference cache
		this.partsToPages.remove(part);
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return null;
	}

	@Override
	protected boolean isImportant(final IWorkbenchPart part) {
		return part instanceof DummyPart;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);

		final IMenuManager menuManager = site.getActionBars().getMenuManager();
		createViewMenuGroups(menuManager);
	}

	@Override
	protected void initPage(final IPageBookViewPage page) {
		super.initPage(page);
		final IActionBars actionBars = page.getSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), this.refreshAnalysisAction);
		updateViewActions();
		actionBars.updateActionBars();

	}

	@Override
	public void partActivated(final IWorkbenchPart part) {
		super.partActivated(part);
	}

	private void updateViewActions() {
		this.analysisPageSwitcher.setEnabled(this.cdsViewToPage.keySet().size() > 1);
		this.refreshAnalysisAction.setEnabled(!this.cdsViewToPage.isEmpty());
	}

	/*
	 * initializes the Part toolbar
	 */
	private void initializeToolBar() {
		final IActionBars actionBars = getViewSite().getActionBars();
		final IToolBarManager tbm = actionBars.getToolBarManager();
		createToolBarGroups(tbm);
		tbm.appendToGroup(IContextMenuConstants.GROUP_SEARCH, this.refreshAnalysisAction);
		tbm.appendToGroup(IContextMenuConstants.GROUP_GOTO, this.analysisPageSwitcher);
	}

	private void createActions() {
		// create search functions like refresh, history dropdown, etc.
		this.refreshAnalysisAction = new RefreshCurrentAnalysisAction();
		this.refreshAnalysisAction.setEnabled(false);
		this.refreshAnalysisAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);

		this.analysisPageSwitcher = new AnalysisPageSwitcherAction();
		this.refreshAnalysisAction.setEnabled(false);
	}

	private void initializePageSwitcher() {
		new PageSwitcher(this) {
			@Override
			public void activatePage(final Object page) {
				final DummyPart part = CdsAnalysis.this.pagesToParts.get(page);
				if (part != null) {
					partActivated(part);
					updateLabel();
				}
			}

			@Override
			public ImageDescriptor getImageDescriptor(final Object page) {
				if (page instanceof CdsAnalysisPage) {
					return ((CdsAnalysisPage) page).getImageDescriptor();
				}
				return null;
			}

			@Override
			public String getName(final Object page) {
				return ((CdsAnalysisPage) page).getLabel();
			}

			@Override
			public Object[] getPages() {
				return CdsAnalysis.this.cdsViewToPage.values().toArray();
			}

			@Override
			public int getCurrentPageIndex() {
				int i = 0;
				final Collection<CdsAnalysisPage> pages = CdsAnalysis.this.cdsViewToPage.values();
				final CdsAnalysisPage activePage = getActivePage();
				if (activePage != null) {
					for (final CdsAnalysisPage page : pages) {
						if (page == activePage) {
							break;
						}
						i++;
					}
				}
				return i;
			}

		};

	}

	private CdsAnalysisPage createAnalyzerPage(final AnalysisObjectKey cdsView) {
		final DummyPart part = new DummyPart(getSite());
		// create new CDS Analyzer page with Top-Down Analysis as starting view
		CdsAnalysisPage page = null;
		switch (cdsView.mode) {
		case DEPENDENCY_TREE_USAGES:
			page = new CdsUsageAnalysisView(this);
			break;
		case TOP_DOWN:
			page = new CdsTopDownAnalysisView(this);
			break;
		case WHERE_USED:
			page = new WhereUsedInCdsAnalysisView(this);
			break;
		case FIELD_ANALYSIS:
			page = new FieldAnalysisView(this);
		}
		if (page == null) {
			return null;
		}
		// set the input of page to the CDS view which should be analyzed
		this.partsToPages.put(part, page);
		this.pagesToParts.put(page, part);
		this.cdsViewToPage.put(cdsView, page);
		partActivated(part);
		return page;
	}

	private class AnalysisObjectKey {
		public String name;
		public String destinationId;
		public AnalysisMode mode;

		public AnalysisObjectKey(final AnalysisMode mode, final String name, final String destinationId) {
			this.name = name;
			this.destinationId = destinationId;
			this.mode = mode;
		}

		@Override
		public int hashCode() {
			int result = this.name == null ? 0 : this.name.hashCode();
			result = 31 * result + (this.mode == null ? 0 : this.mode.hashCode());
			result = 31 * result + (this.destinationId == null ? 0 : this.destinationId.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof AnalysisObjectKey)) {
				return false;
			}
			return this.name.equals(((AnalysisObjectKey) obj).name)
				&& this.destinationId.equals(((AnalysisObjectKey) obj).destinationId)
				&& this.mode == ((AnalysisObjectKey) obj).mode;
		}
	}

	/**
	 * Action to run the currently entered search again
	 *
	 * @author stockbal
	 */
	private class RefreshCurrentAnalysisAction extends Action {
		public RefreshCurrentAnalysisAction() {
			super(Messages.CdsAnalysis_RefreshAction_xtol,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.REFRESH));
		}

		@Override
		public void run() {
			getActivePage().refreshAnalysis();
		}

	}

	private class AnalysisPageSwitcherAction extends Action implements IMenuCreator {
		private Menu menu;

		public AnalysisPageSwitcherAction() {
			super(Messages.CdsAnalysis_SwitchAnalysisPages_xtol,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.HISTORY_LIST));
			setMenuCreator(this);
		}

		@Override
		public void dispose() {
			if (this.menu != null) {
				this.menu.dispose();
			}
		}

		@Override
		public void setEnabled(final boolean enabled) {
			super.setEnabled(enabled);
		}

		@Override
		public Menu getMenu(final Control parent) {
			if (this.menu != null) {
				this.menu.dispose();
			}
			this.menu = new Menu(parent);

			final CdsAnalysisPage activePage = getActivePage();

			for (final CdsAnalysisPage page : CdsAnalysis.this.cdsViewToPage.values()) {
				final IWorkbenchPart part = CdsAnalysis.this.pagesToParts.get(page);
				final IAction showAnalysisPageAction = new Action(page.getLabel(), IAction.AS_RADIO_BUTTON) {
					@Override
					public void run() {
						partActivated(part);
						updateLabel();
						updateViewActions();
					}
				};

				showAnalysisPageAction.setChecked(page == activePage);
				showAnalysisPageAction.setImageDescriptor(page.getImageDescriptor());
				addActionToMenu(this.menu, showAnalysisPageAction);
			}
			final Separator separator = new Separator();
			separator.fill(this.menu, -1);
			addActionToMenu(this.menu, new Action(Messages.CdsAnalysis_ManagePagesAction_xmit) {
				@Override
				public void run() {
					openManagePageDialog();
				}
			});
			addActionToMenu(this.menu, new Action(Messages.CdsAnalysis_CloseAllPagesAction_xmit) {
				@Override
				public void run() {
					closeAllPages();
				}
			});
			return this.menu;
		}

		@Override
		public Menu getMenu(final Menu parent) {
			return null;
		}

		@Override
		public void run() {
			openManagePageDialog();
		}

		private void openManagePageDialog() {
			final Set<CdsAnalysisPage> pagesKeySet = CdsAnalysis.this.pagesToParts.keySet();
			final List<CdsAnalysisPage> pages = pagesKeySet.stream().collect(Collectors.toList());
			final ManageCdsAnalysisPagesDialog dialog = new ManageCdsAnalysisPagesDialog(pages,
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			if (dialog.open() == Window.OK) {
				final Object[] dialogResult = dialog.getResult();
				if (dialogResult != null && dialogResult.length == 1) {
					final CdsAnalysisPage chosenPage = (CdsAnalysisPage) dialogResult[0];
					final DummyPart part = CdsAnalysis.this.pagesToParts.get(chosenPage);
					if (part != null) {
						partActivated(part);
						updateLabel();
					}
				}
				final List<CdsAnalysisPage> removedPages = dialog.getDeletedPages();
				if (removedPages != null && !removedPages.isEmpty()) {
					for (final CdsAnalysisPage pageToClose : removedPages) {
						closePage(pageToClose, false);
					}
				}
			}
		}

		/*
		 * Adds the given action to the given menu
		 */
		private void addActionToMenu(final Menu parent, final IAction action) {
			final ActionContributionItem item = new ActionContributionItem(action);
			item.fill(parent, -1);
		}
	}

}
