package com.devepos.adt.saat.internal.activation;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.devepos.adt.base.IAdtObjectTypeConstants;
import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.base.ui.util.AdtTypeUtil;
import com.devepos.adt.base.ui.util.IAdtObjectTypeProxy;
import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.communication.internal.content.plaintext.PlainTextContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.session.AdtSystemSessionFactory;
import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.tools.core.IAdtObjectReference;

/**
 * Activation success listener to handle post activation activities for CDS views
 *
 * @author Ludwig Stockbauer-Muhr
 *
 */
@SuppressWarnings("restriction")
public class CdsActivationSuccessListener implements IActivationSuccessListener {

  @Override
  public void onActivationSuccess(final List<IAdtObjectReference> activatedObjectRefs,
      final IProject project) {
    String destinationId = DestinationUtil.getDestinationId(project);
    CdsActivationUriDiscovery uriDisc = new CdsActivationUriDiscovery(destinationId);

    if (!uriDisc.isResourceDiscoverySuccessful() || !uriDisc.isCdsPostActivaionAvailable()) {
      return;
    }
    IAdtObjectTypeProxy ddlType = AdtTypeUtil.getInstance()
        .getType(IAdtObjectTypeConstants.DATA_DEFINITION);

    URI resourceUri = null;
    String payload = null;

    List<String> ddlNamesInActivationList = activatedObjectRefs.stream()
        .filter(objRef -> objRef.getUri().getPath().startsWith(ddlType.getAdtResourceUriPath()))
        .map(IAdtObjectReference::getName)
        .collect(Collectors.toList());

    if (ddlNamesInActivationList.size() == 1) {
      resourceUri = uriDisc.createCdsPostActivationUri(ddlNamesInActivationList.get(0));
    } else if (ddlNamesInActivationList.size() > 1) {
      payload = String.join(";", ddlNamesInActivationList);
      resourceUri = uriDisc.getCdsPostActivationUri();
    }

    if (resourceUri != null) {
      Job postActionationJob = createPostActivationJob(resourceUri, payload, destinationId);
      postActionationJob.schedule();
    }
  }

  private Job createPostActivationJob(final URI resourceUri, final String payload,
      final String destinationId) {
    return Job.createSystem("Run CDS Post Activation", monitor -> { //$NON-NLS-1$
      final ISystemSession session = AdtSystemSessionFactory.createSystemSessionFactory()
          .createStatelessSession(destinationId);

      final IRestResource cdsPostActivationResource = AdtRestResourceFactory
          .createRestResourceFactory()
          .createRestResource(resourceUri, session);
      if (payload != null) {
        cdsPostActivationResource.addContentHandler(new PlainTextContentHandler());
      }
      cdsPostActivationResource.post(new NullProgressMonitor(), String.class, payload);
      monitor.done();
    });
  }

}
