package com.devepos.adt.saat.internal.menu;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.devepos.adt.base.ui.util.AdtUIUtil;

public class DynamicOpenInMenu extends CompoundContributionItem {
  private IMenuManager dynamicOpenInMenu;

  public DynamicOpenInMenu() {
    dynamicOpenInMenu = DynamicOpenInMenuUtility.buildAnalysisToolsSubMenu(AdtUIUtil
        .getAdtObjectsFromSelection(true));
  }

  public DynamicOpenInMenu(final String id) {
    super(id);
  }

  @Override
  public void fill(final Menu menu, final int index) {
    if (dynamicOpenInMenu != null) {
      super.fill(menu, index);
    }
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    if (dynamicOpenInMenu != null) {
      return new IContributionItem[] { dynamicOpenInMenu };
    }
    return new IContributionItem[] {};
  }

}
