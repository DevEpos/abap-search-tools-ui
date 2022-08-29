package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.part.PageSwitcher;
import org.eclipse.ui.part.ShowInContext;

import com.devepos.adt.base.ui.AdtBaseUIResources;
import com.devepos.adt.base.ui.DummyPart;
import com.devepos.adt.base.ui.IAdtBaseImages;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.IPinnableView;
import com.devepos.adt.base.ui.action.CommandFactory;
import com.devepos.adt.base.ui.action.OpenPreferencesAction;
import com.devepos.adt.base.ui.action.PinViewAction;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.help.HelpContexts;
import com.devepos.adt.saat.internal.help.HelpUtil;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.ui.ViewUiState;
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
public class CdsAnalysisView extends PageBookView implements ICdsAnalysisListener,
    ICdsAnalysisResultListener, IPinnableView {

  public static final String VIEW_ID = "com.devepos.adt.saat.views.cdsanalyzer"; //$NON-NLS-1$

  private Composite pageContent;
  private Composite descriptionComposite;
  private final Map<DummyPart, CdsAnalysisPage<?>> partsToPages;
  final Map<CdsAnalysisPage<?>, DummyPart> pagesToParts;
  private final Map<CdsAnalysis, ViewUiState> viewStates;
  private CdsAnalysis currentAnalysis;
  private boolean isPinned;

  private Label description;
  private RefreshCurrentAnalysisAction refreshAnalysisAction;
  private CdsAnalysisHistoryDropDownAction analysesHistoryAction;
  private PinViewAction pinViewAction;
  private Action openPreferencesAction;

  private DummyPart defaultPart;

  private final CdsAnalysisConfigRegistry configRegistry;

  public CdsAnalysisView() {
    super();
    partsToPages = new HashMap<>();
    pagesToParts = new HashMap<>();
    viewStates = new HashMap<>();
    configRegistry = new CdsAnalysisConfigRegistry(this);
    SearchAndAnalysisPlugin.getDefault()
        .getPreferenceStore()
        .setDefault(IPreferences.MAX_CDS_ANALYZER_HISTORY, 10);
    isPinned = false;
  }

  /**
   * Action to run the currently entered search again
   *
   * @author stockbal
   */
  private class RefreshCurrentAnalysisAction extends Action {
    public RefreshCurrentAnalysisAction() {
      super(Messages.CdsAnalysis_RefreshAction_xtol, AdtBaseUIResources.getImageDescriptor(
          IAdtBaseImages.REFRESH));
    }

    @Override
    public void run() {
      getActivePage().refreshAnalysis();
    }

  }

  private static class WelcomePage extends CdsAnalysisPage<CdsAnalysis> {
    private CdsAnalysisWelcomeText infoText;

    public WelcomePage() {
      super(null);
    }

    @Override
    public void createControl(final Composite parent) {
      infoText = new CdsAnalysisWelcomeText(parent);
    }

    @Override
    public Control getControl() {
      return infoText;
    }

    @Override
    public void setFocus() {
      infoText.setFocus();
    }

    @Override
    protected void configureTreeViewer(final TreeViewer treeViewer) {
    }

    @Override
    protected ViewUiState getUiState() {
      return null;
    }

    @Override
    protected void loadInput(final ViewUiState uiState) {
    }

    @Override
    protected void refreshAnalysis() {
    }
  }

  public static void createContextMenuGroups(final IMenuManager mgr) {
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_NEW));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_OPEN));
    mgr.add(new Separator(IContextMenuConstants.GROUP_DB_BROWSER));
    mgr.add(new Separator(IContextMenuConstants.GROUP_CDS_ANALYSIS));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_NODE_ACTIONS));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_ADDITIONS));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_EDIT));
  }

  /**
   * Creates the groups and separators for the search view's tool bar
   *
   * @param toolbar the toolbar
   */
  public static void createToolBarGroups(final IToolBarManager toolbar) {
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_NEW));
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_EDIT));
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_NODE_ACTIONS));
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_SEARCH));
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_GOTO));
    toolbar.add(new Separator(IGeneralMenuConstants.GROUP_ADDITIONS));
  }

  public static void createViewMenuGroups(final IMenuManager mgr) {
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_PROPERTIES));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_FILTERING));
    mgr.add(new Separator(IGeneralMenuConstants.GROUP_ADDITIONS));
  }

  @Override
  public void analysisRemoved(final CdsAnalysis analysis) {
    if (analysis.equals(currentAnalysis)) {
      showCdsAnalysis(null);
      partActivated(defaultPart);
    }
    viewStates.remove(analysis);

    analysesHistoryAction.disposeMenu();
    updateViewActions();
  }

  @Override
  public void createPartControl(final Composite parent) {
    createActions();

    pageContent = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    pageContent.setLayout(layout);

    descriptionComposite = null;

    super.createPartControl(pageContent);
    getPageBook().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    CdsAnalysisManager.getInstance().addCdsAnalysisListener(this);

    HelpUtil.setHelp(pageContent, HelpContexts.CDS_ANALYZER);

    initializeToolBar();
    initializePageSwitcher();

    showLatestAnalysis();
  }

  @Override
  public void dispose() {
    CdsAnalysisManager.getInstance().removeCdsAnalysisListener(this);
    CdsAnalysisViewManager.getInstance().cdsAnalysisViewClosed(this);
    super.dispose();
  }

  /**
   * Returns the active {@link CdsAnalysisPage}
   *
   * @return the active {@link CdsAnalysisPage}
   */
  public CdsAnalysisPage<?> getActivePage() {
    final IPage page = getCurrentPage();
    return page instanceof CdsAnalysisPage ? (CdsAnalysisPage<?>) page : null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(final Class<T> adapter) {
    final Object superAdapter = super.getAdapter(adapter);
    if (superAdapter != null) {
      return (T) superAdapter;
    }
    if (adapter == IShowInSource.class) {
      return (T) (IShowInSource) () -> new ShowInContext(null, getSelectionProvider()
          .getSelection());
    }
    return null;
  }

  /**
   * Returns the active analysis of the view
   *
   * @return
   */
  public CdsAnalysis getCurrentAnalysis() {
    return currentAnalysis;
  }

  @Override
  public void init(final IViewSite site) throws PartInitException {
    super.init(site);

    final IMenuManager menuManager = site.getActionBars().getMenuManager();
    createViewMenuGroups(menuManager);
  }

  @Override
  public boolean isPinned() {
    return isPinned;
  }

  @Override
  public void partActivated(final IWorkbenchPart part) {
    super.partActivated(part);
    if (part == this) {
      CdsAnalysisViewManager.getInstance().cdsAnalysisViewActivated(this);
    }
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

  @Override
  public void setPinned(final boolean pinned) {
    isPinned = pinned;
  }

  public void showCdsAnalysis(final CdsAnalysis analysis) {
    CdsAnalysisPage<?> newPage = null;
    if (analysis != null) {
      newPage = configRegistry.findPageForType(analysis.getType());
      if (newPage == null) {
        // TODO: log error message that page could not be created
        return;
      }
    }
    internalShowCdsAnalysisPage(newPage, analysis);
  }

  /**
   * Updates the label of the current page
   */
  @Override
  public void updateLabel() {
    final CdsAnalysis analysis = getCurrentAnalysis();
    String label = ""; //$NON-NLS-1$
    if (analysis != null) {
      label = LegacyActionTools.escapeMnemonics(analysis.getLabel());
    }
    if (pageContent.isDisposed()) {
      return;
    }
    if (label.length() == 0) {
      if (descriptionComposite != null) {
        descriptionComposite.dispose();
        descriptionComposite = null;
        pageContent.layout();
      }
    } else {
      if (descriptionComposite == null) {
        descriptionComposite = new Composite(pageContent, SWT.NONE);
        descriptionComposite.moveAbove(null);

        GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(descriptionComposite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(descriptionComposite);

        description = new Label(descriptionComposite, SWT.LEAD | SWT.TOP | SWT.WRAP);
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER)
            .grab(true, false)
            .indent(5, SWT.DEFAULT)
            .applyTo(description);
        description.setText(label);

        final Label separator = new Label(descriptionComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
        pageContent.layout();
      } else {
        description.setText(label);
        pageContent.layout();
      }
    }
  }

  @Override
  protected IPage createDefaultPage(final PageBook book) {
    final CdsAnalysisPage<?> page = new WelcomePage();
    initPage(page);
    page.createControl(book);
    final DummyPart part = new DummyPart(getSite());
    partsToPages.put(part, page);
    pagesToParts.put(page, part);
    defaultPart = part;

    return page;
  }

  @Override
  protected PageRec doCreatePage(final IWorkbenchPart part) {
    final IPageBookViewPage page = partsToPages.get(part);
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
    partsToPages.remove(part);
  }

  @Override
  protected IWorkbenchPart getBootstrapPart() {
    return null;
  }

  @Override
  protected void initPage(final IPageBookViewPage page) {
    super.initPage(page);
    final IActionBars actionBars = page.getSite().getActionBars();
    actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAnalysisAction);
    updateViewActions();
    actionBars.updateActionBars();

  }

  @Override
  protected boolean isImportant(final IWorkbenchPart part) {
    return part instanceof DummyPart;
  }

  void updateViewActions() {
    final boolean historyHasAnalyses = CdsAnalysisManager.getInstance().hasAnalyses();
    analysesHistoryAction.setEnabled(historyHasAnalyses);
    refreshAnalysisAction.setEnabled(historyHasAnalyses);
  }

  private void createActions() {
    // create search functions like refresh, history dropdown, etc.
    refreshAnalysisAction = new RefreshCurrentAnalysisAction();
    refreshAnalysisAction.setEnabled(false);
    refreshAnalysisAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);

    analysesHistoryAction = new CdsAnalysisHistoryDropDownAction(this);
    refreshAnalysisAction.setEnabled(false);
    pinViewAction = new PinViewAction(this);
    openPreferencesAction = new OpenPreferencesAction(IPreferences.CDS_ANALYSIS_PREF_PAGE_ID);
  }

  private void initializePageSwitcher() {
    new PageSwitcher(this) {
      @Override
      public void activatePage(final Object page) {
        final CdsAnalysis analysis = (CdsAnalysis) page;
        CdsAnalysisManager.getInstance().activated(analysis);
        showCdsAnalysis(analysis);
      }

      @Override
      public ImageDescriptor getImageDescriptor(final Object page) {
        final CdsAnalysis analysis = (CdsAnalysis) page;
        return analysis.getImageDescriptor();
      }

      @Override
      public String getName(final Object page) {
        final CdsAnalysis analysis = (CdsAnalysis) page;
        return analysis.getLabel();
      }

      @Override
      public Object[] getPages() {
        return CdsAnalysisManager.getInstance().getAnalyses();
      }
    };

  }

  /*
   * initializes the Part toolbar
   */
  private void initializeToolBar() {
    final IActionBars actionBars = getViewSite().getActionBars();
    final IToolBarManager tbm = actionBars.getToolBarManager();
    createToolBarGroups(tbm);
    tbm.appendToGroup(IGeneralMenuConstants.GROUP_NEW, CommandFactory.createContribItem(
        ICommandConstants.RUN_CDS_ANALYSIS, SearchAndAnalysisPlugin.getDefault()
            .getImageDescriptor(IImages.RUN_NEW_ANALYSIS), null, null));
    tbm.appendToGroup(IGeneralMenuConstants.GROUP_SEARCH, refreshAnalysisAction);
    tbm.appendToGroup(IGeneralMenuConstants.GROUP_GOTO, analysesHistoryAction);
    tbm.appendToGroup(IGeneralMenuConstants.GROUP_ADDITIONS, pinViewAction);

    IMenuManager viewMenuMgr = actionBars.getMenuManager();
    createViewMenuGroups(viewMenuMgr);
    viewMenuMgr.add(new Separator());
    viewMenuMgr.add(openPreferencesAction);
  }

  private void internalShowCdsAnalysisPage(final CdsAnalysisPage<?> page,
      final CdsAnalysis analysisInput) {
    // detach the previous page.
    final CdsAnalysisPage<?> currentPage = getActivePage();
    if (currentAnalysis != null && currentPage != null) {
      viewStates.put(currentAnalysis, currentPage.getUiState());
      currentPage.setInput(null, null);
    }

    currentAnalysis = analysisInput;

    if (page != null) {
      if (page != currentPage) {
        DummyPart part = pagesToParts.get(page);
        if (part == null) {
          part = new DummyPart(getSite());
          pagesToParts.put(page, part);
          partsToPages.put(part, page);
        }
        // part.setLastActivation(++fActivationCount);
        partActivated(part);
        page.setFocus();
      }

      // connect to the new pages
      final ViewUiState uiState = analysisInput != null ? viewStates.get(analysisInput) : null;
      page.setInput(analysisInput, uiState);
    }
    updateViewActions();
    updateLabel();
  }

  private void showLatestAnalysis() {
    if (!CdsAnalysisManager.getInstance().hasAnalyses()) {
      return;
    }
    final CdsAnalysis[] analyses = CdsAnalysisManager.getInstance().getAnalyses();
    showCdsAnalysis(analyses[0]);
  }

}
