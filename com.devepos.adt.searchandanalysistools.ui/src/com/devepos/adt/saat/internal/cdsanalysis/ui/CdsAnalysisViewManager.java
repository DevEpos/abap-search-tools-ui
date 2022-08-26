package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.LinkedList;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.util.Logging;
import com.devepos.adt.saat.internal.ui.ViewPartLookup;

/**
 * This class manages all open Views of the CDS Analyzer.<br>
 *
 * @author Ludwig Stockbauer-Muhr
 */
public class CdsAnalysisViewManager {

  private static CdsAnalysisViewManager instance;
  private static int viewCount = 0;
  private final LinkedList<CdsAnalysisView> openViews;

  private CdsAnalysisViewManager() {
    openViews = new LinkedList<>();
  }

  public static CdsAnalysisViewManager getInstance() {
    if (instance == null) {
      instance = new CdsAnalysisViewManager();
    }
    return instance;
  }

  /**
   * Retrieves the CDS Analysis view from the workspace
   *
   * @return
   */
  public CdsAnalysisView activateCdsAnalysisView(final boolean openNew) {
    CdsAnalysisView view = null;
    final IWorkbenchPage activePage = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage();

    if (activePage == null) {
      return null;
    }

    try {
      if (!openNew) {
        view = findLruAnalysisView(activePage, true);
      }
      String secondaryId = null;
      if (view == null) {
        if (activePage.findViewReference(CdsAnalysisView.VIEW_ID) != null) {
          secondaryId = String.valueOf(++viewCount);
        }
      } else {
        secondaryId = view.getViewSite().getSecondaryId();
      }
      return (CdsAnalysisView) activePage.showView(CdsAnalysisView.VIEW_ID, secondaryId,
          IWorkbenchPage.VIEW_ACTIVATE);
    } catch (final PartInitException e) {
      Logging.getLogger(ViewPartLookup.class).error(e);
    }
    return view;
  }

  public void cdsAnalysisViewActivated(final CdsAnalysisView view) {
    openViews.remove(view);
    openViews.addFirst(view);
  }

  public void cdsAnalysisViewClosed(final CdsAnalysisView view) {
    openViews.remove(view);
  }

  /**
   * Checks if the given CDS analysis is shown in any of the open CDS Analyzer
   * views
   *
   * @param analysis the analysis to be checked
   * @return <code>true</code> if the given analysis is shown in any view
   */
  public boolean isAnalysisShown(final CdsAnalysis analysis) {
    synchronized (openViews) {
      for (final CdsAnalysisView view : openViews) {
        final CdsAnalysis shownAnalysis = view.getCurrentAnalysis();
        if (shownAnalysis == analysis) {
          return true;
        }
      }
      return false;
    }
  }

  private CdsAnalysisView findLruAnalysisView(final IWorkbenchPage page,
      final boolean avoidPinnedViews) {
    boolean viewFoundInPage = false;
    for (CdsAnalysisView view : openViews) {
      if (page.equals(view.getSite().getPage())) {
        if (!avoidPinnedViews || !view.isPinned()) {
          return view;
        }
        viewFoundInPage = true;
      }
    }

    if (!viewFoundInPage) {
      // find unresolved views
      IViewReference[] viewReferences = page.getViewReferences();
      for (IViewReference curr : viewReferences) {
        if (CdsAnalysisView.VIEW_ID.equals(curr.getId()) && page.equals(curr.getPage())) {
          CdsAnalysisView view = (CdsAnalysisView) curr.getView(true);
          if (view != null && (!avoidPinnedViews || !view.isPinned())) {
            return view;
          }

        }
      }
    }
    return null;
  }
}
