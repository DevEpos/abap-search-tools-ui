package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * History drop down action for all CDS analyses in the history buffer
 *
 * @author stockbal
 */
class CdsAnalysisHistoryDropDownAction extends Action implements IMenuCreator {

  private Menu menu;
  private final CdsAnalysisView analysisView;

  public CdsAnalysisHistoryDropDownAction(final CdsAnalysisView analysisView) {
    super(Messages.CdsAnalysis_SwitchAnalysisPages_xtol, SearchAndAnalysisPlugin.getDefault()
        .getImageDescriptor(IImages.HISTORY_LIST));
    setMenuCreator(this);
    this.analysisView = analysisView;
  }

  @Override
  public void dispose() {
    disposeMenu();
  }

  @Override
  public void setEnabled(final boolean enabled) {
    super.setEnabled(enabled);
  }

  @Override
  public Menu getMenu(final Control parent) {
    disposeMenu();
    menu = new Menu(parent);

    final CdsAnalysis currentAnalysis = analysisView.getCurrentAnalysis();

    for (final CdsAnalysis analysis : CdsAnalysisManager.getInstance().getAnalyses()) {
      final IAction showAnalysisPageAction = new ShowAnalysisAction(analysis);
      showAnalysisPageAction.setChecked(analysis == currentAnalysis);
      addActionToMenu(menu, showAnalysisPageAction);
    }
    final Separator separator = new Separator();
    separator.fill(menu, -1);
    addActionToMenu(menu, new Action(Messages.CdsAnalysis_ManageHistory_xmit) {
      @Override
      public void run() {
        openManagePageDialog();
      }
    });
    addActionToMenu(menu, new Action(Messages.CdsAnalysis_ClearHistoryAction_xmit) {
      @Override
      public void run() {
        CdsAnalysisManager.getInstance().removeAll();
      }
    });
    return menu;
  }

  public void disposeMenu() {
    if (menu != null && !menu.isDisposed()) {
      menu.dispose();
    }
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
    final CdsAnalysisManager analysisManager = CdsAnalysisManager.getInstance();
    final List<CdsAnalysis> analyses = new ArrayList<>();
    for (final CdsAnalysis analysis : analysisManager.getAnalyses()) {
      analyses.add(analysis);
    }

    final ManageCdsAnalysesDialog dialog = new ManageCdsAnalysesDialog(analyses, PlatformUI
        .getWorkbench()
        .getActiveWorkbenchWindow()
        .getShell());
    if (dialog.open() == Window.OK) {
      final CdsAnalysis selectedAnalysis = dialog.getSelectedAnalysis();
      if (selectedAnalysis != null) {
        analysisManager.showAnalysis(selectedAnalysis, dialog.isOpenInNew());
      }
      final List<CdsAnalysis> removedAnalyses = dialog.getDeletedAnalyses();
      if (removedAnalyses != null && !removedAnalyses.isEmpty()) {
        for (final CdsAnalysis analysisToRemove : removedAnalyses) {
          analysisManager.removeAnalysis(analysisToRemove);
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

  private class ShowAnalysisAction extends Action {
    private final CdsAnalysis analysis;

    public ShowAnalysisAction(final CdsAnalysis analysis) {
      super(analysis.getLabel(), AS_RADIO_BUTTON);
      this.analysis = analysis;
      setImageDescriptor(analysis.getImageDescriptor());
    }

    @Override
    public void runWithEvent(final Event event) {
      runIfChecked(event.stateMask == SWT.CTRL);
    }

    @Override
    public void run() {
      runIfChecked(false);
    }

    private void runIfChecked(boolean openNewAnalysisView) {
      if (isChecked())
        CdsAnalysisManager.getInstance().showAnalysis(analysis, openNewAnalysisView);
    }
  }
}