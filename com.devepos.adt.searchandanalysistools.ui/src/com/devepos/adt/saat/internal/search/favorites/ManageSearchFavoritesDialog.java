package com.devepos.adt.saat.internal.search.favorites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;

/**
 * This dialog is for managing object search favorites
 *
 * @author stockbal
 */
public class ManageSearchFavoritesDialog extends SelectionDialog {

  private static final int REMOVE_ID = IDialogConstants.CLIENT_ID + 1;
  private static final int WIDTH_IN_CHARACTERS = 55;
  private static final int BUTTON_CHAR_WIDTH = 15;
  private IStructuredSelection dndSelection;

  private final List<IObjectSearchFavorite> input;
  private final List<IObjectSearchFavorite> removedEntries;

  private TableViewer viewer;
  private Button removeButton;
  private boolean orderChanged;

  public ManageSearchFavoritesDialog(final Shell parent) {
    super(parent);
    setTitle(Messages.ManageSearchFavoritesDialog_Title_xtit);
    setMessage(Messages.SearchHistorySelectionDialog_InfoMessage_xmsg);
    input = new ArrayList<>(SearchAndAnalysisPlugin.getDefault()
        .getFavoriteManager()
        .getFavorites());
    if (input != null && !input.isEmpty()) {
      setInitialSelections(input.get(0));
    }
    removedEntries = new ArrayList<>();
    setHelpAvailable(false);
  }

  @Override
  public void create() {
    super.create();

    final List<?> initialSelection = getInitialElementSelections();
    if (initialSelection != null) {
      viewer.setSelection(new StructuredSelection(initialSelection));
    }

    validateDialogState();
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings() {
    return SearchAndAnalysisPlugin.getDefault()
        .getDialogSettingsSection("DialogBounds_ManageSearchFavoritesDialog"); //$NON-NLS-1$
  }

  @Override
  protected int getDialogBoundsStrategy() {
    return DIALOG_PERSISTSIZE;
  }

  @Override
  protected Label createMessageArea(final Composite composite) {
    final Composite parent = new Composite(composite, SWT.NONE);
    GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(parent);
    GridDataFactory.fillDefaults().span(2, 1).applyTo(parent);

    final Label label = new Label(parent, SWT.WRAP);
    label.setText(getMessage());
    GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

    applyDialogFont(label);
    return label;
  }

  /*
   * Overrides method from Dialog
   */
  @Override
  protected Control createDialogArea(final Composite container) {
    final Composite ancestor = (Composite) super.createDialogArea(container);

    createMessageArea(ancestor);

    final Composite parent = new Composite(ancestor, SWT.NONE);

    GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(parent);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
        | SWT.FULL_SELECTION);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(new FavoriteLabelProvider());
    viewer.addSelectionChangedListener(event -> validateDialogState());

    final Table table = viewer.getTable();
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDoubleClick(final MouseEvent e) {
        buttonPressed(IDialogConstants.OPEN_ID);
      }
    });
    GridDataFactory.fillDefaults()
        .span(1, 2)
        .hint(convertWidthInCharsToPixels(WIDTH_IN_CHARACTERS), convertHeightInCharsToPixels(15))
        .grab(true, true)
        .applyTo(table);

    removeButton = new Button(parent, SWT.PUSH);
    removeButton.setText(Messages.SearchHistorySelectionDialog_DeleteHistoryEntry_xbut);
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent event) {
        buttonPressed(REMOVE_ID);
      }
    });
    GridDataFactory.fillDefaults()
        .align(SWT.BEGINNING, SWT.BEGINNING)
        .hint(convertWidthInCharsToPixels(BUTTON_CHAR_WIDTH), SWT.DEFAULT)
        .applyTo(removeButton);

    applyDialogFont(ancestor);

    // set input & selections last, so all the widgets are created.
    viewer.setInput(input);
    viewer.getTable().setFocus();

    // register Drag-n-Drop Support
    final Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
    viewer.addDragSupport(DND.DROP_MOVE, transfers, new DragSourceAdapter() {
      @Override
      public void dragSetData(final DragSourceEvent event) {
        final ISelection select = viewer.getSelection();
        if (select instanceof IStructuredSelection) {
          dndSelection = (IStructuredSelection) select;
          // save item to event.data
          event.data = "DATA"; //$NON-NLS-1$
        }
      }
    });
    viewer.addDropSupport(DND.DROP_MOVE, transfers, new DropTargetAdapter() {
      @Override
      public void drop(final DropTargetEvent event) {
        if (!TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
          return;
        }
        final TableItem item = (TableItem) event.item;
        int targetIndex = -1;
        if (item != null) {
          final Table table = item.getParent();
          targetIndex = table.indexOf(item);
        }

        // add an item
        performDrop(targetIndex);
      }

    });
    return ancestor;
  }

  protected final void validateDialogState() {
    final IStructuredSelection sel = viewer.getStructuredSelection();
    final int elementsSelected = sel.toList().size();

    removeButton.setEnabled(elementsSelected > 0);
    final Button openButton = getButton(IDialogConstants.OPEN_ID);
    if (openButton != null) {
      openButton.setEnabled(elementsSelected == 1);
    }
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == REMOVE_ID) {
      final IStructuredSelection selection = viewer.getStructuredSelection();
      final Iterator<?> favorites = selection.iterator();
      while (favorites.hasNext()) {
        final IObjectSearchFavorite curr = (IObjectSearchFavorite) favorites.next();
        removedEntries.add(curr);
        input.remove(curr);
        viewer.remove(curr);
      }
      if (viewer.getSelection().isEmpty() && !input.isEmpty()) {
        viewer.setSelection(new StructuredSelection(input.get(0)));
      }
      return;
    }
    if (buttonId == IDialogConstants.OPEN_ID) {
      // Build a list of selected children.
      final ISelection selection = viewer.getSelection();
      if (selection instanceof IStructuredSelection) {
        setResult(viewer.getStructuredSelection().toList());
      }
      okPressed();
      return;
    }
    super.buttonPressed(buttonId);
  }

  /*
   * Overrides method from Dialog
   */
  @Override
  protected void okPressed() {
    boolean orderChanged = false;
    // remove history entries
    for (final IObjectSearchFavorite favoriteEntry : removedEntries) {
      SearchAndAnalysisPlugin.getDefault().getFavoriteManager().removeFavorite(favoriteEntry);
    }
    if (!input.isEmpty() && this.orderChanged) {
      final List<IObjectSearchFavorite> favorites = SearchAndAnalysisPlugin.getDefault()
          .getFavoriteManager()
          .getFavorites();
      favorites.clear();
      favorites.addAll(input);
      orderChanged = true;
    }
    if (orderChanged || removedEntries != null && !removedEntries.isEmpty()) {
      ObjectSearchFavoriteStorage.serialize();
    }
    super.okPressed();
  }

  /*
   * Performs the drag operation
   */
  private void performDrop(final int targetIndex) {
    if (targetIndex >= 0) {
      int i = targetIndex + 1;
      for (final Object selectedObj : dndSelection.toList()) {
        input.remove(selectedObj);
        if (input.size() - 1 < i) {
          input.add((IObjectSearchFavorite) selectedObj);
          i = input.size() - 1;
        } else {
          input.add(i++, (IObjectSearchFavorite) selectedObj);
        }
        orderChanged = true;
      }
    } else {
      for (final Object selectedObj : dndSelection.toList()) {
        input.remove(selectedObj);
        input.add((IObjectSearchFavorite) selectedObj);
        orderChanged = true;
      }
    }

    dndSelection = null;
    // remove an item
    viewer.refresh();
  }

  private static final class FavoriteLabelProvider extends LabelProvider {

    @Override
    public String getText(final Object element) {
      return ObjectSearchFavoritesUtil.getFavoriteDisplayName((IObjectSearchFavorite) element);
    }

    @Override
    public Image getImage(final Object element) {
      final IObjectSearchFavorite favorite = (IObjectSearchFavorite) element;

      try {
        return SearchType.valueOf(favorite.getSearchType()).getImage();
      } catch (NullPointerException | IllegalArgumentException exc) {
        return null;
      }
    }
  }
}
