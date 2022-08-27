package com.devepos.adt.saat.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisKey;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisManager;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsTopDownAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsUsedEntitiesAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.FieldAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.RunNewCdsAnalysisDialog;
import com.devepos.adt.saat.internal.cdsanalysis.ui.WhereUsedInCdsAnalysis;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Command handler to run a new CDS analysis via dialog
 *
 * @author Ludwig Stockbauer-Muhr
 *
 */
public class RunCdsAnalysisHandler extends AbstractHandler {

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    RunNewCdsAnalysisDialog dialog = new RunNewCdsAnalysisDialog(HandlerUtil.getActiveShell(event));
    if (dialog.open() == Window.OK) {
      IAdtObjectReference objectRef = dialog.getSelectedObject();
      IProject project = dialog.getProject();
      String destinationId = DestinationUtil.getDestinationId(project);
      CdsAnalysisManager analysisManager = CdsAnalysisManager.getInstance();
      CdsAnalysisKey analysisKey = new CdsAnalysisKey(dialog.getSelectedAnalysisType(), objectRef
          .getUri(), destinationId);
      CdsAnalysis existing = analysisManager.getExistingAnalysis(analysisKey);
      if (existing == null) {
        // determine ADT information about CDS view
        Job adtObjectRetrievalJob = Job.create(Messages.CdsAnalysis_LoadAdtObjectJobName_xmsg,
            (ICoreRunnable) monitor -> {
              // check if search is possible in selected project
              final IAdtObjectReferenceElementInfo adtObjectRefElemInfo = ElementInfoRetrievalServiceFactory
                  .createService()
                  .retrieveBasicElementInformation(destinationId, objectRef.getUri());
              if (adtObjectRefElemInfo != null) {
                final CdsAnalysis newAnalysis = createAnalysisForType(adtObjectRefElemInfo, dialog
                    .getSelectedAnalysisType());
                PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                  analysisManager.addAnalysis(newAnalysis);
                  analysisManager.registerAnalysis(analysisKey, newAnalysis);
                  analysisManager.showAnalysis(newAnalysis, dialog.isRunInNew());
                });
              }
            });
        adtObjectRetrievalJob.schedule();

      } else {
        analysisManager.showAnalysis(existing, dialog.isRunInNew());
      }
    }
    return null;
  }

  private CdsAnalysis createAnalysisForType(final IAdtObjectReferenceElementInfo objRefElemInfo,
      final CdsAnalysisType type) {
    switch (type) {
    case DEPENDENCY_TREE_USAGES:
      return new CdsUsedEntitiesAnalysis(objRefElemInfo);
    case FIELD_ANALYSIS:
      return new FieldAnalysis(objRefElemInfo);
    case TOP_DOWN:
      return new CdsTopDownAnalysis(objRefElemInfo);
    case WHERE_USED:
      return new WhereUsedInCdsAnalysis(objRefElemInfo);
    default:
      throw new UnsupportedOperationException(String.format("CDS Analysis type %s is not supported", //$NON-NLS-1$
          type));
    }
  }
}
