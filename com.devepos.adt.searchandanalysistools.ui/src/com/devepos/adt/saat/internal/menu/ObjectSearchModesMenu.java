package com.devepos.adt.saat.internal.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.search.ui.ObjectSearchPage;

/**
 * Menu contribution which creates quick start menu items for the ABAP object
 * search + pull down menu in the main toolbar
 *
 * @author stockbal
 */
public class ObjectSearchModesMenu extends CompoundContributionItem {

  public ObjectSearchModesMenu() {
  }

  public ObjectSearchModesMenu(final String id) {
    super(id);
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    final IContributionItem[] items = createMenuItems();
    return items;
  }

  private IContributionItem[] createMenuItems() {
    final List<IContributionItem> items = new ArrayList<>();

    int i = 1;
    for (final SearchType type : SearchType.values()) {
      final String label = i < 10 ? String.format("&%d %s", i++, type.toString()) : type.toString();
      items.add(new ActionContributionItem(new OpenSearchDialogWithType(type, label)));
    }
    return items.toArray(new IContributionItem[items.size()]);
  }

  private void openSearchDialog(final SearchType searchType) {
    final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    final SearchDialog dialog = new SearchDialog(activeWindow, ObjectSearchPage.PAGE_ID);
    dialog.setBlockOnOpen(false);
    dialog.open();
    if (dialog.getSelectedPage() instanceof ObjectSearchPage) {
      final ObjectSearchPage searchDialog = (ObjectSearchPage) dialog.getSelectedPage();
      searchDialog.setSearchType(searchType);
      searchDialog.setFocusToFirstInput();
    }
    dialog.setBlockOnOpen(true);
  }

  private class OpenSearchDialogWithType extends Action {
    private final SearchType type;

    public OpenSearchDialogWithType(final SearchType type, final String label) {
      super(label, type.getImageDescriptor());
      this.type = type;
    }

    @Override
    public void run() {
      openSearchDialog(type);
    }
  }
}
