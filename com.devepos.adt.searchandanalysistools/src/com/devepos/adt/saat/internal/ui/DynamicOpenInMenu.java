package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.devepos.adt.saat.internal.util.AdtUtil;

public class DynamicOpenInMenu extends CompoundContributionItem {
	private IMenuManager dynamicOpenInMenu;

	public DynamicOpenInMenu() {
		this.dynamicOpenInMenu = DynamicOpenInMenuUtility.buildShowInVirtualTreeSubMenu(AdtUtil.getAdtObjectsFromSelection(true));
	}

	public DynamicOpenInMenu(final String id) {
		super(id);
	}

	@Override
	public void fill(final Menu menu, final int index) {
		if (this.dynamicOpenInMenu != null) {
			super.fill(menu, index);
		}
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		if (this.dynamicOpenInMenu != null) {
			return new IContributionItem[] { this.dynamicOpenInMenu };
		}
		return new IContributionItem[] {};
	}

}
