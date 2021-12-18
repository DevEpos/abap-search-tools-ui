package com.devepos.adt.saat.internal.ui;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.util.Logging;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisView;

/**
 * Looks up View parts
 *
 * @author stockbal
 */
public class ViewPartLookup {

  /**
   * Retrieves the CDS Analysis view from the workspace
   *
   * @return
   */
  public static CdsAnalysisView getCdsAnalysisView() {
    CdsAnalysisView view = null;
    final IWorkbenchPage page = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage();
    try {
      final IViewPart viewPart = page.showView(CdsAnalysisView.VIEW_ID);
      if (viewPart instanceof CdsAnalysisView) {
        view = (CdsAnalysisView) viewPart;
      }
    } catch (final PartInitException e) {
      Logging.getLogger(ViewPartLookup.class).error(e);
    }
    return view;
  }
}
