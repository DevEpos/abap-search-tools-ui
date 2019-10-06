package com.devepos.adt.saat.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Preference page for DB Browser Tools
 *
 * @author stockbal
 */
public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {
	private final List<FieldEditor> fields = new ArrayList<>();

	@Override
	public void init(final IWorkbench workbench) {
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		GridLayoutFactory.fillDefaults().applyTo(composite);

//		createGlobalSettings(composite);
		createSearchSettings(composite);

		initFields();

		applyDialogFont(composite);
		return composite;
	}

	private void initFields() {
		for (final FieldEditor field : this.fields) {
			field.setPage(this);
			field.setPreferenceStore(getPreferenceStore());
			field.load();
			field.setPropertyChangeListener(this);
		}
	}

	/**
	 * Creates group with search settings
	 *
	 * @param composite
	 */
	private void createSearchSettings(final Composite composite) {
		final Group searchGroup = new Group(composite, SWT.NONE);
		searchGroup.setText(Messages.MainPreferencePage_SearchSettings_xgrp);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(searchGroup);

		final FieldEditor maxSearchResultsEditor = new IntegerFieldEditor(IPreferences.MAX_SEARCH_RESULTS,
			Messages.MainPreferencePage_MaxResultsSetting_xfld, searchGroup, 4);
		this.fields.add(maxSearchResultsEditor);

		addBooleanEditor(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT, Messages.MainPreferencePage_CursorAtEndSetting_xfld,
			searchGroup);

		/*
		 * Layout of group needs to be set at last as the field editors will change it
		 * final during their creation
		 */
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(searchGroup);

		createCDSSearchSettings(searchGroup);
		// reset group margins because of field editors
		adjustMargins(searchGroup);
	}

	/**
	 * Create special CDS search settings to control which sub folders should be
	 * loaded when a CDS view is expanded
	 *
	 * @param composite the parent control
	 */
	private void createCDSSearchSettings(final Composite composite) {
		final Group cdsSettingsGroup = new Group(composite, SWT.NONE);
		cdsSettingsGroup.setText(Messages.MainPreferencePage_CdsViewSettings_xgrp);
		GridDataFactory.fillDefaults().grab(true, false).indent(0, 10).span(2, 1).applyTo(cdsSettingsGroup);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(cdsSettingsGroup);

		addBooleanEditor(IPreferences.SHOW_FULL_ASSOCIATION_NAME, Messages.MainPreferencePage_DisplayAssociationName_xckl,
			cdsSettingsGroup);

		adjustMargins(cdsSettingsGroup);
	}

	private void adjustMargins(final Composite composite) {
		final GridLayout layout = (GridLayout) composite.getLayout();
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;
		layout.marginBottom = 5;

	}

	/**
	 * Creates group with main Plugin settings
	 *
	 * @param composite
	 */
	private void createGlobalSettings(final Composite composite) {
		final Group globalGroup = new Group(composite, SWT.NONE);
		globalGroup.setText(Messages.MainPreferencePage_GeneralSettings_xgrp);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(globalGroup);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(globalGroup);

		// reset group margins because of field editors
		adjustMargins(globalGroup);
	}

	private BooleanFieldEditor addBooleanEditor(final String preferenceId, final String labelText, final Composite parent) {
		final BooleanFieldEditor booleanEditor = new BooleanFieldEditor(preferenceId, labelText, parent);
		this.fields.add(booleanEditor);

		// adjust control layouts
		GridDataFactory.fillDefaults().span(2, 1).applyTo(booleanEditor.getDescriptionControl(parent));

		return booleanEditor;
	}

	@Override
	protected void performDefaults() {
		for (final FieldEditor field : this.fields) {
			field.loadDefault();
		}
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		for (final FieldEditor field : this.fields) {
			field.store();
		}
		return super.performOk();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getProperty() == FieldEditor.IS_VALID) {
			final boolean isValid = (Boolean) event.getNewValue();
			if (isValid) {
				for (final FieldEditor field : this.fields) {
					if (!field.isValid()) {
						setValid(false);
						return;
					}
				}
				setValid(true);
			} else {
				setValid(false);
			}
		}
	}
}
