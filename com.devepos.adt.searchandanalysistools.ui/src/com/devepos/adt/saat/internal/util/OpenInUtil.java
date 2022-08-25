package com.devepos.adt.saat.internal.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.WorkbenchPart;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.AbapProjectProxy;
import com.devepos.adt.base.ui.util.AdtUIUtil;
import com.devepos.adt.base.util.AdtUtil;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.analytics.AnalysisForOfficeLauncherContentHandler;
import com.devepos.adt.saat.internal.analytics.AnalysisForOfficeUriDiscovery;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;

/**
 * Helper class to open given entity in DB Browser transaction in new SAP GUI
 * tab
 *
 * @author stockbal
 */
public class OpenInUtil {

  /**
   * Open the given entity in <em>DB Browser</em> transaction
   *
   * @param project             project where transaction should be opened in
   * @param entityId            the id of the entity
   * @param entityMode          the entity mode for the transaction
   * @param skipSelectionScreen if <code>true</code> the selection screen of the
   *                            DB Browser will be skipped
   */
  public static void openEntity(final IProject project, final String entityId,
      final String entityMode, final boolean skipSelectionScreen) {
    final WorkbenchPart part = (WorkbenchPart) AdtSapGuiEditorUtilityFactory
        .createSapGuiEditorUtility()
        .openEditorAndStartTransaction(project, "ZDBBR", true, //$NON-NLS-1$
            Stream.of(new String[][] { { "ADT", String.valueOf(true) }, { "ENTITY_ID", entityId }, //$NON-NLS-1$ //$NON-NLS-2$
                { "ENTITY_MODE", entityMode }, { "SKIP_SELSCREEN", String.valueOf( //$NON-NLS-1$ //$NON-NLS-2$
                    skipSelectionScreen) } })
                .collect(Collectors.toMap(data -> data[0], data -> data[1])));
    AdtUIUtil.overrideSapGuiPartTitle(part, project, entityId, String.format("DB Browser - %s", //$NON-NLS-1$
        entityId), SearchAndAnalysisPlugin.getDefault().getImage(IImages.DB_BROWSER_DATA_PREVIEW));
  }

  /**
   * Open the given query CDS view in the Query monitor
   *
   * @param project     the project to be used for the system connection
   * @param cdsViewName the name of the CDS view
   */
  public static void openCDSInQueryMonitor(final IProject project, final String cdsViewName) {
    final WorkbenchPart sapGuipart = (WorkbenchPart) AdtSapGuiEditorUtilityFactory
        .createSapGuiEditorUtility()
        .openEditorAndStartTransaction(project, "ZSAT_ADT_QRYMONOPEN", true, //$NON-NLS-1$
            Stream.of(new String[][] { { "ENTITY_ID", cdsViewName.toUpperCase() } }) //$NON-NLS-1$
                .collect(Collectors.toMap(data -> data[0], data -> data[1])));
    AdtUIUtil.overrideSapGuiPartTitle(sapGuipart, project, cdsViewName, String.format(
        "%s (Query Monitor)", //$NON-NLS-1$
        cdsViewName), SearchAndAnalysisPlugin.getDefault().getImage(IImages.ANALYTICAL_QUERY));
  }

  /**
   * Opens the given query CDS view in the Analysis for Office Plugin
   *
   * @param project     the project to be used for the system connection
   * @param cdsViewName the name of the CDS view
   */
  public static void openCDSInAnalysisForOffice(final IProject project, final String cdsViewName) {
    final Job openInAnalysisForOfficeJob = Job.create("Open In Analysis for Office", monitor -> { //$NON-NLS-1$
      final IAbapProjectProvider projectProvider = new AbapProjectProxy(project);

      final AnalysisForOfficeUriDiscovery aoxUriDiscovery = new AnalysisForOfficeUriDiscovery(
          projectProvider.getDestinationId());
      final URI launcherResourceURI = aoxUriDiscovery.createAnalysisForOfficeLauncherURI(cdsViewName
          .toUpperCase());
      if (launcherResourceURI == null) {
        throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
            Messages.OpenInUtil_AnalysisForOfficeNotActive_xmsg));
      }
      final IRestResource launcherResource = AdtRestResourceFactory.createRestResourceFactory()
          .createRestResource(launcherResourceURI, projectProvider.createStatelessSession());
      launcherResource.addContentHandler(new AnalysisForOfficeLauncherContentHandler());
      final String launcherContent = launcherResource.get(monitor, AdtUtil.getHeaders(),
          String.class);

      Display.getDefault().asyncExec(() -> {
        if (launcherContent != null) {
          try {
            final File launcherFile = File.createTempFile("sapaoxlauncher", ".sapaox"); //$NON-NLS-1$ //$NON-NLS-2$
            final FileWriter launcherFileWriter = new FileWriter(launcherFile);
            launcherFileWriter.write(launcherContent);
            launcherFileWriter.close();
            Program.launch(launcherFile.getAbsolutePath());
          } catch (final IOException e1) {
            e1.printStackTrace();
          }
        }
      });

    });
    if (openInAnalysisForOfficeJob != null) {
      openInAnalysisForOfficeJob.schedule();
    }

  }
}
