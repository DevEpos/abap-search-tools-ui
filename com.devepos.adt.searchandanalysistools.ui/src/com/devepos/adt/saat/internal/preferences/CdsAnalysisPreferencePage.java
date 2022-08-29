package com.devepos.adt.saat.internal.preferences;

import java.util.stream.Stream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.devepos.adt.base.ui.preferences.FieldEditorPrefPageBase;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.FieldAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisPreferences;
import com.devepos.adt.saat.internal.messages.Messages;

public class CdsAnalysisPreferencePage extends FieldEditorPrefPageBase implements
    IWorkbenchPreferencePage, IPropertyChangeListener {

  @Override
  public void init(final IWorkbench workbench) {
    setPreferenceStore(SearchAndAnalysisPlugin.getDefault().getPreferenceStore());
  }

  @Override
  protected void createPreferenceControls(final Composite parent) {
    createTopDownPreferences(parent);
    createWhereUsedInPreferences(parent);
    createFieldAnalysisPreferences(parent);
  }

  @Override
  protected void fieldValueChanged(final FieldEditor field, final Object oldValue,
      final Object newValue) {
    if (!ICdsAnalysisPreferences.WHERE_USED_USES_IN_SELECT.equals(field.getPreferenceName())
        && !ICdsAnalysisPreferences.WHERE_USED_USES_IN_ASSOC.equals(field.getPreferenceName())) {
      return;
    }
    if (!(Boolean) newValue) {
      BooleanFieldEditor usedInSelectFromPrefEditor = (BooleanFieldEditor) fieldMap.get(
          ICdsAnalysisPreferences.WHERE_USED_USES_IN_SELECT);
      BooleanFieldEditor usedInAssocPrefEditor = (BooleanFieldEditor) fieldMap.get(
          ICdsAnalysisPreferences.WHERE_USED_USES_IN_ASSOC);

      if (!usedInSelectFromPrefEditor.getBooleanValue() && !usedInAssocPrefEditor
          .getBooleanValue()) {
        setErrorMessage(NLS.bind(Messages.CdsAnalysisPreferencePage_whereUsedInPartError_xmsg,
            new Object[] { Messages.WhereUsedInCdsAnalysisView_ShowUsesInSelectPartAction_xmit,
                Messages.WhereUsedInCdsAnalysisView_ShowUsesInAssociationsAction_xmit }));

        setValid(false);
      }
    } else {
      setValid(true);
      setErrorMessage(null);
    }
  }

  private void createFieldAnalysisPreferences(final Composite parent) {
    Group group = createGroup(Messages.CdsAnalysisPreferencePage_fieldAnalysisGroup_xtit, parent);

    String[][] typeLabelsAndKeys = Stream.of(FieldAnalysisType.values())
        .map(type -> new String[] { type.toString(), type.getPrefKey() })
        .toArray(size -> new String[size][1]);

    addEditor(new RadioGroupFieldEditor(ICdsAnalysisPreferences.FIELD_ANALYSIS_ANALYSIS_DIRECTION,
        Messages.CdsAnalysisPreferencePage_fieldAnalysisMode_xlbl, 1, typeLabelsAndKeys, group,
        true));

    Group bottomUpSettingsGroup = createGroup(
        Messages.CdsAnalysisPreferencePage_fieldAnalysisBottomUpGroup_xtit, group);
    addBooleanEditor(ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_CALC_FIELDS,
        Messages.FieldHierarchyView_CalculatedFieldsSearch_xtol, bottomUpSettingsGroup);
    addBooleanEditor(ICdsAnalysisPreferences.FIELD_ANALYSIS_SEARCH_IN_DB_VIEWS,
        Messages.CdsAnalysisPreferencePage_whereUsedInSearchInDbViewSetting_xchk,
        bottomUpSettingsGroup);

    adjustMargins(bottomUpSettingsGroup);
    adjustMargins(group);
  }

  private Group createGroup(final String label, final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults().applyTo(group);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
    group.setText(label);
    return group;
  }

  private void createTopDownPreferences(final Composite parent) {
    Group group = createGroup(Messages.CdsAnalysisPreferencePage_topDownAnalysisGroup_xtit, parent);

    addBooleanEditor(ICdsAnalysisPreferences.TOP_DOWN_SHOW_DESCRIPTIONS,
        Messages.CdsTopDownAnalysisView_ShowDescriptionsToggleAction_xmit, group);
    addBooleanEditor(ICdsAnalysisPreferences.TOP_DOWN_SHOW_ALIAS_NAMES,
        Messages.CdsTopDownAnalysisView_ShowAliasNamesToggleAction_xmit, group);
    addBooleanEditor(ICdsAnalysisPreferences.TOP_DOWN_LOAD_ASSOCIATIONS,
        Messages.CdsTopDownAnalysisView_LoadAssociationsToggleAction_xmit, group);

    adjustMargins(group);
  }

  private void createWhereUsedInPreferences(final Composite parent) {
    Group group = createGroup(Messages.CdsAnalysisPreferencePage_whereUsedInAnalysisGroup_xtit,
        parent);

    addBooleanEditor(ICdsAnalysisPreferences.WHERE_USED_USES_IN_SELECT,
        Messages.WhereUsedInCdsAnalysisView_ShowUsesInSelectPartAction_xmit, group);
    addBooleanEditor(ICdsAnalysisPreferences.WHERE_USED_USES_IN_ASSOC,
        Messages.WhereUsedInCdsAnalysisView_ShowUsesInAssociationsAction_xmit, group);
    addBooleanEditor(ICdsAnalysisPreferences.WHERE_USED_LOCAL_ASSOCIATIONS_ONLY,
        Messages.WhereUsedInCdsAnalysisView_OnlyLocallyDefinedAssocUsages_xmit, group);
    addBooleanEditor(ICdsAnalysisPreferences.WHERE_USED_ONLY_RELEASED_USAGES,
        Messages.WhereUsedInCdsAnalysisView_OnlyUsagesInReleasedEntities_xmit, group);

    adjustMargins(group);
  }
}