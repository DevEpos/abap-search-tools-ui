package com.devepos.adt.saat.internal.util;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.ui.project.AbapProjectProviderAccessor;
import com.devepos.adt.base.ui.util.AdtUIUtil;
import com.devepos.adt.saat.internal.ddicaccess.DdicRepositoryAccessFactory;
import com.devepos.adt.saat.internal.ddicaccess.IDdicRepositoryAccess;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public class NavigationUtil {
  /**
   * Performs an ADT Link navigation to the given column in the given entity
   *
   * @param entityName    the name of an Database entity
   * @param fieldName     the name of the field which should be selected after the
   *                      editor opened
   * @param destinationId the destination id of the ABAP project
   */
  public static void navigateToEntityColumn(final String entityName, final String fieldName,
      final String destinationId) {
    final Job loadFieldUriJob = Job.create(NLS.bind("Load Field URI for ''{0}.{1}''", entityName,
        fieldName), monitor -> {
          final IDdicRepositoryAccess ddicRepoAccess = DdicRepositoryAccessFactory
              .createDdicAccess();
          final IAdtObjectReference adtObjectRef = ddicRepoAccess.getColumnUri(destinationId,
              entityName, fieldName);
          if (adtObjectRef != null) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
              AdtUIUtil.navigateWithObjectReference(adtObjectRef, AbapProjectProviderAccessor
                  .getProviderForDestination(destinationId)
                  .getProject());
            });
          }
          monitor.done();
        });
    loadFieldUriJob.schedule();
  }
}
