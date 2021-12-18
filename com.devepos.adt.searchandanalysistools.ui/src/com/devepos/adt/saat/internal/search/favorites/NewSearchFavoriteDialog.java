package com.devepos.adt.saat.internal.search.favorites;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchRequest;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesFactory;

/**
 * Dialog for creating an executed search as a favorite
 *
 * @author stockbal
 */
public class NewSearchFavoriteDialog extends StatusDialog {

  private static final int LABEL_WIDTH = 12;

  private Button createButton;
  private String favoriteDescription;
  private boolean isProjectIndependent;
  private final ObjectSearchRequest searchRequest;
  private final IObjectSearchFavorites favoriteManager;

  public NewSearchFavoriteDialog(final Shell parent, final ObjectSearchRequest searchRequest) {
    super(parent);
    setTitle(Messages.NewSearchFavoriteDialog_Title_xtit);
    setHelpAvailable(false);
    this.searchRequest = searchRequest;
    favoriteManager = SearchAndAnalysisPlugin.getDefault().getFavoriteManager();
    validateDialogState();
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected int getDialogBoundsStrategy() {
    return DIALOG_PERSISTSIZE;
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings() {
    return SearchAndAnalysisPlugin.getDefault()
        .getDialogSettingsSection("DialogBounds_NewSearchFavoritesDialog"); //$NON-NLS-1$
  }

  /*
   * Overrides method from Dialog
   */
  @Override
  protected Control createDialogArea(final Composite container) {
    final Composite ancestor = (Composite) super.createDialogArea(container);

    setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.FAVORITES));

    createSearchParametersGroup(ancestor);
    createFavoriteParameters(ancestor);

    return ancestor;
  }

  /*
   * Creates group for holding the parameters of the search query
   */
  private void createSearchParametersGroup(final Composite ancestor) {
    final Group group = new Group(ancestor, SWT.NONE);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
    group.setText(Messages.NewSearchFavoriteDialog_SearchParameters_xgrp);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

    createReadOnlyTextWithLabel(Messages.NewSearchFavoriteDialog_Project_xfld, searchRequest
        .getDestinationId(), group);
    createReadOnlyTextWithLabel(Messages.NewSearchFavoriteDialog_SearchType_xfld, searchRequest
        .getSearchType()
        .toString(), group);
    createReadOnlyTextWithLabel(Messages.NewSearchFavoriteDialog_ObjectName_xlfd, searchRequest
        .getSearchTerm(), group);
    createReadOnlyTextWithLabel(Messages.NewSearchFavoriteDialog_SearchFilter_xlfd, searchRequest
        .getParametersString(), group);

    final Button isAndSearchActiveCheckBox = new Button(group, SWT.CHECK);
    isAndSearchActiveCheckBox.setText(Messages.ObjectSearch_UseAndFilter_xtol);
    isAndSearchActiveCheckBox.setSelection(searchRequest.isAndSearchActive());
    isAndSearchActiveCheckBox.setEnabled(false);
    GridDataFactory.fillDefaults().span(2, 1).applyTo(isAndSearchActiveCheckBox);
  }

  private void createReadOnlyTextWithLabel(final String label, final String content,
      final Composite parent) {
    final Label labelControl = new Label(parent, SWT.NONE);
    labelControl.setText(label);
    GridDataFactory.fillDefaults()
        .hint(convertWidthInCharsToPixels(LABEL_WIDTH), SWT.DEFAULT)
        .applyTo(labelControl);

    final Text textControl = new Text(parent, SWT.READ_ONLY | SWT.BORDER | SWT.NO_FOCUS);
    textControl.setText(content);
    textControl.setToolTipText(content);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(textControl);
  }

  /*
   * Creates group for holding favorite specific parameters used, to identify the
   * search favorite
   */
  private void createFavoriteParameters(final Composite ancestor) {
    final Group group = new Group(ancestor, SWT.NONE);
    group.setText(Messages.NewSearchFavoriteDialog_FavoriteSettings_xgrp);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

    // description for the favorite
    final Label favoriteDescriptionLabel = new Label(group, SWT.NONE);
    favoriteDescriptionLabel.setText(Messages.NewSearchFavoriteDialog_Description_xfld);
    GridDataFactory.fillDefaults()
        .hint(convertWidthInCharsToPixels(LABEL_WIDTH), SWT.DEFAULT)
        .applyTo(favoriteDescriptionLabel);

    final Text favoriteDescription = new Text(group, SWT.BORDER);
    favoriteDescription.addModifyListener(e -> {
      NewSearchFavoriteDialog.this.favoriteDescription = favoriteDescription.getText();
      validateDialogState();
    });
    GridDataFactory.fillDefaults().grab(true, false).applyTo(favoriteDescription);

    // flag "is project independent"
    final Button isProjectIndependentCheckBox = new Button(group, SWT.CHECK);
    isProjectIndependentCheckBox.setText(
        Messages.NewSearchFavoriteDialog_ProjectIndependentSetting_xckl);
    isProjectIndependentCheckBox.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        isProjectIndependent = isProjectIndependentCheckBox.getSelection();
      }
    });
    GridDataFactory.fillDefaults()
        .span(2, 1)
        .hint(convertWidthInCharsToPixels(60), SWT.DEFAULT)
        .applyTo(isProjectIndependentCheckBox);

    favoriteDescription.setFocus();
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton = createButton(parent, IDialogConstants.OK_ID,
        Messages.NewSearchFavoriteDialog_CreateFavorite_xbut, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected void updateButtonsEnableState(final IStatus status) {
    if (createButton != null && !createButton.isDisposed()) {
      createButton.setEnabled(status == null || !status.matches(IStatus.ERROR));
    }
  }

  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == IDialogConstants.OK_ID) {
      validateDialogState();
    }
    super.buttonPressed(buttonId);
  }

  protected final boolean validateDialogState() {
    IStatus status = null;
    if (favoriteDescription == null || favoriteDescription.isEmpty()) {
      status = new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, IStatus.ERROR,
          Messages.NewSearchFavoriteDialog_NoDescriptionError_xmsg, null);
    } else {
      // check if there already is a favorite with this description
      if (favoriteManager.contains(isProjectIndependent ? null : searchRequest.getDestinationId(),
          searchRequest.getSearchType().name(), favoriteDescription)) {
        status = new Status(IStatus.WARNING, SearchAndAnalysisPlugin.PLUGIN_ID, IStatus.WARNING, NLS
            .bind(Messages.NewSearchFavoriteDialog_DuplicateFavoriteError_xmsg,
                favoriteDescription), null);
      }
    }
    updateStatus(status);
    return status == null || !status.matches(IStatus.ERROR);
  }

  @Override
  protected void updateStatus(final IStatus status) {
    super.updateStatus(status);
    final IStatus currentStatus = getStatus();
    if (currentStatus != null && currentStatus.matches(IStatus.ERROR | IStatus.WARNING)) {
      final Shell shell = getShell();
      if (shell == null) {
        return;
      }
      shell.pack(true);
      shell.layout(true);
    }
  }

  @Override
  protected void okPressed() {
    final IObjectSearchFavorite newFavorite = IObjectSearchFavoritesFactory.eINSTANCE
        .createObjectSearchFavorite();
    newFavorite.setDescription(favoriteDescription);
    newFavorite.setProjectIndependent(isProjectIndependent);
    if (!isProjectIndependent) {
      newFavorite.setDestinationId(searchRequest.getDestinationId());
    }
    newFavorite.setAndSearchActive(searchRequest.isAndSearchActive());
    newFavorite.setSearchFilter(searchRequest.getParametersString());
    newFavorite.setObjectName(searchRequest.getSearchTerm());
    newFavorite.setSearchType(searchRequest.getSearchType().name());
    newFavorite.setMaxResults(searchRequest.getMaxResults());
    favoriteManager.addFavorite(newFavorite);
    ObjectSearchFavoriteStorage.serialize();
    super.okPressed();
  }

}
