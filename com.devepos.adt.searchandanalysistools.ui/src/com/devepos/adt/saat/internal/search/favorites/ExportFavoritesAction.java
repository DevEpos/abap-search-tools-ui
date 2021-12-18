package com.devepos.adt.saat.internal.search.favorites;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.ui.AdtBaseUIResources;
import com.devepos.adt.base.ui.IAdtBaseImages;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Exports the current search favorites to the file system
 *
 * @author stockbal
 */
public class ExportFavoritesAction extends Action {
  public ExportFavoritesAction() {
    super(Messages.ExportFavoritesAction_ActionTitle_xmit, AdtBaseUIResources.getImageDescriptor(
        IAdtBaseImages.EXPORT));
  }

  @Override
  public void run() {
    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterNames(new String[] { "XML (*.xml)", //$NON-NLS-1$
        Messages.ImportFavoritesAction_AllFilesFileType_xmit });
    dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
    dialog.setFileName("favorites.xml"); //$NON-NLS-1$

    final String exportFileName = dialog.open();
    if (!exportFileName.equals("")) { //$NON-NLS-1$
      final IObjectSearchFavorites favorites = SearchAndAnalysisPlugin.getDefault()
          .getFavoriteManager();
      ObjectSearchFavoriteStorage.serialize(favorites, exportFileName);
    }
  }
}
