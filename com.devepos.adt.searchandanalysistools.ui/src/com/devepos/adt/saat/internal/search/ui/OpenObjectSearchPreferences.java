package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;

/**
 * Action for opening the DB Browser tools preference page from this view
 *
 * @author stockbal
 */
public class OpenObjectSearchPreferences extends Action {
  public OpenObjectSearchPreferences() {
    super(Messages.ObjectSearchOpenPreferences_xmit);

  }

  @Override
  public void run() {
    PreferencesUtil.createPreferenceDialogOn(null, IPreferences.OBJECT_SEARCH_PREF_PAGE_ID,
        new String[] { IPreferences.OBJECT_SEARCH_PREF_PAGE_ID }, (Object) null).open();
  }
}