package com.devepos.adt.saat.internal.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.adtobject.IAdtObject;
import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.ui.project.AbapProjectProxy;
import com.devepos.adt.base.ui.util.AdtUIUtil;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysis;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisKey;
import com.devepos.adt.saat.internal.cdsanalysis.ui.CdsAnalysisManager;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.FeatureTester;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;

/**
 * Handler for the command to open a CDS view in the CDS Analyzer View
 *
 * @author stockbal
 */
public abstract class OpenInCdsAnalyzerHandler extends AbstractHandler {
  private final CdsAnalysisType mode;

  protected OpenInCdsAnalyzerHandler(final CdsAnalysisType mode) {
    this.mode = mode;
  }

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final List<IAdtObject> selectedObjects = AdtUIUtil.getAdtObjectsFromSelection(true);
    if (selectedObjects == null || selectedObjects.isEmpty() || selectedObjects.size() > 1) {
      return null;
    }
    final IAdtObject selectedObject = selectedObjects.get(0);
    final IProject project = selectedObject.getProject();
    if (!canExecute(selectedObject)) {
      return null;
    }
    if (!isFeatureAvailable(project)) {
      MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
          Messages.Dialog_InfoTitle_xmsg, NLS.bind(getFeatureUnavailableMessage(), DestinationUtil
              .getDestinationId(project)));
      return null;
    }
    final IAbapProject abapProject = selectedObject.getProject().getAdapter(IAbapProject.class);
    // register the abapProject
    AbapProjectProviderAccessor.registerProjectProvider(new AbapProjectProxy(selectedObject
        .getProject()));
    final IAdtObjectReference objectRef = selectedObject.getReference();
    if (objectRef != null && objectRef.getUri() != null) {
      analyzeObject(objectRef, abapProject.getDestinationId());
    }
    return null;

  }

  protected boolean canExecute(final IAdtObject selectedObject) {
    return selectedObject.getObjectType() == ObjectType.DATA_DEFINITION;
  }

  /**
   * Creates CDS analysis object which can be shown in the CDS Analyzer view
   *
   * @param objectRefInfo information about ADT object reference
   * @return the created CDS analysis instance
   */
  protected abstract CdsAnalysis createTypedAnalysis(IAdtObjectReferenceElementInfo objectRefInfo);

  /**
   * @return the message text to be used if the given feature is not available
   */
  protected String getFeatureUnavailableMessage() {
    return Messages.CdsAnalysis_FeatureIsNotSupported_xmsg;
  }

  protected boolean isFeatureAvailable(final IProject project) {
    if (project == null) {
      return false;
    }
    return FeatureTester.isCdsAnalysisAvailable(project);
  }

  private void analyzeObject(final IAdtObjectReference objectRef, final String destinationId) {
    final CdsAnalysisManager analysisManager = CdsAnalysisManager.getInstance();
    final CdsAnalysisKey analysisKey = new CdsAnalysisKey(mode, objectRef.getUri(), destinationId);
    final CdsAnalysis existing = analysisManager.getExistingAnalysis(analysisKey);
    if (existing == null) {
      // determine ADT information about CDS view
      final Job adtObjectRetrievalJob = Job.create(Messages.CdsAnalysis_LoadAdtObjectJobName_xmsg,
          (ICoreRunnable) monitor -> {
            // check if search is possible in selected project
            final IAdtObjectReferenceElementInfo adtObjectRefElemInfo = ElementInfoRetrievalServiceFactory
                .createService()
                .retrieveBasicElementInformation(destinationId, objectRef.getUri());
            if (adtObjectRefElemInfo != null) {
              final CdsAnalysis newAnalysis = createTypedAnalysis(adtObjectRefElemInfo);
              PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                analysisManager.addAnalysis(newAnalysis);
                analysisManager.registerAnalysis(analysisKey, newAnalysis);
                analysisManager.showAnalysis(newAnalysis, false);
              });
            }
          });
      adtObjectRetrievalJob.schedule();

    } else {
      analysisManager.showAnalysis(existing, false);
    }

  }

}
