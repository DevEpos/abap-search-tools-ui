package com.devepos.adt.saat.internal.search.favorites;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.base.ui.AdtBaseUIResources;
import com.devepos.adt.base.ui.IAdtBaseImages;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;

/**
 * Imports search favorites from a file
 *
 * @author stockbal
 */
public class ImportFavoritesAction extends Action {

  public ImportFavoritesAction() {
    super(Messages.ImportFavoritesAction_ImportFavoritesAction_xmit, AdtBaseUIResources
        .getImageDescriptor(IAdtBaseImages.IMPORT));
  }

  @Override
  public void run() {
    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterNames(new String[] { "XML (*.xml)", //$NON-NLS-1$
        Messages.ImportFavoritesAction_AllFilesFileType_xmit });
    dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
    dialog.setFileName("favorites.xml"); //$NON-NLS-1$

    final String importFileName = dialog.open();
    if (!"".equals(importFileName)) { //$NON-NLS-1$
      final IObjectSearchFavorites existingFavorites = SearchAndAnalysisPlugin.getDefault()
          .getFavoriteManager();
      final IObjectSearchFavorites importedFavorites = new ObjectSearchFavorites();
      ObjectSearchFavoriteStorage.deserialize(importedFavorites, importFileName);
      int importedFavoritesCount = 0;
      int favoritesInImportFile = 0;
      if (importedFavorites.hasEntries()) {
        favoritesInImportFile = importedFavorites.getFavorites().size();
        for (final IObjectSearchFavorite imported : importedFavorites.getFavorites()) {
          if (!existingFavorites.contains(imported.getDestinationId(), imported.getSearchType(),
              imported.getDescription())) {
            existingFavorites.addFavorite(imported);
            importedFavoritesCount++;
          }
        }
        if (importedFavoritesCount > 0) {
          ObjectSearchFavoriteStorage.serialize();
        }
        MessageDialog.openInformation(shell, Messages.ImportFavoritesAction_ImportSuccess_xtit, NLS
            .bind(Messages.ImportFavoritesAction_ImportSuccess_xmsg, importedFavoritesCount,
                favoritesInImportFile));
      } else {
        MessageDialog.openInformation(shell, Messages.ImportFavoritesAction_ImportSuccess_xtit,
            Messages.ImportFavoritesAction_NoFavoritesImported_xmsg);
      }
    }

  }
}
