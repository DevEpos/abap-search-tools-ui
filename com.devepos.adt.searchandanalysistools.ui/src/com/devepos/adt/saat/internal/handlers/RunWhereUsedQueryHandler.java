package com.devepos.adt.saat.internal.handlers;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.devepos.adt.base.IAdtObjectTypeConstants;
import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.search.AdtRisSearchUtil;
import com.devepos.adt.base.ui.search.IAdtRisSearchResultProxy;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.ris.search.ui.usagereferences.AdtRisUsageReferencesSearchQuery;
import com.sap.adt.ris.search.ui.usagereferences.AdtRisUsageReferencesSearchQueryParameters;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Command handler for executing a Where Used Query for an ABAP Development
 * Object
 *
 * @author stockbal
 */
@SuppressWarnings("restriction")
public class RunWhereUsedQueryHandler extends AbstractHandler {
  private String ddlsUri;

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // open search dialog to choose ABAP Development object
    final IAdtRisSearchResultProxy result = AdtRisSearchUtil.searchAdtObjectViaDialog(
        Messages.RunWhereUsedQueryHandler_openObjectDialog_xtit, null, false, null);

    if (result == null) {
      return null;
    }

    final IAdtObjectReference selectedAdtObjRef = result.getFirstResult();
    final IProject project = result.getSelectedProject();

    final String adtObjectUri = selectedAdtObjRef.getUri();
    final String adtObjectType = selectedAdtObjRef.getType();

    if (IAdtObjectTypeConstants.DATA_DEFINITION.equals(adtObjectType)
        || IAdtObjectTypeConstants.CDS_VIEW.equals(adtObjectType)) {
      runWhereUsedForDdls(event, project, selectedAdtObjRef);
    } else {
      runWhereUsed(project, adtObjectUri);
    }

    return null;
  }

  /*
   * Runs where used query for a CDS View
   */
  private void runWhereUsedForDdls(final ExecutionEvent event, final IProject project,
      final IAdtObjectReference adtObjectRef) {
    final Display display = HandlerUtil.getActiveShell(event).getDisplay();

    final Job readDdlsUriJob = Job.create(
        Messages.ElementInfoProvider_RetrievingElementInfoDescription_xmsg, monitor -> {
          final IAdtObjectReferenceElementInfo ddlsObjectInfo = ElementInfoRetrievalServiceFactory
              .createService()
              .retrieveBasicElementInformation(DestinationUtil.getDestinationId(project),
                  adtObjectRef.getName(), ObjectType.DATA_DEFINITION);
          if (ddlsObjectInfo != null) {
            ddlsUri = ddlsObjectInfo.getUri();
          }
          monitor.done();
        });
    readDdlsUriJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(final IJobChangeEvent event) {
        display.asyncExec(() -> {
          runWhereUsed(project, ddlsUri != null ? ddlsUri : adtObjectRef.getUri());
        });
      }
    });
    readDdlsUriJob.schedule();
  }

  /*
   * Runs Where-used query for the given URI
   */
  private void runWhereUsed(final IProject project, final String uri) {
    final AdtRisUsageReferencesSearchQueryParameters usageSearchParameters = new AdtRisUsageReferencesSearchQueryParameters(
        project, URI.create(uri));
    final AdtRisUsageReferencesSearchQuery searchQuery = new AdtRisUsageReferencesSearchQuery(
        usageSearchParameters);
    NewSearchUI.runQueryInBackground(searchQuery);

    /*
     * If there is no active page in the workbench window the search view will not
     * be brought to the front so it has to be done manually
     */
    final IWorkbenchPage activeSearchPage = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage();
    final IWorkbenchPart activeSearchView = activeSearchPage.getActivePart();
    if (activeSearchPage != null && activeSearchView != null && activeSearchPage.isPartVisible(
        activeSearchView)) {
      activeSearchPage.bringToTop(activeSearchView);
    }

  }

}
