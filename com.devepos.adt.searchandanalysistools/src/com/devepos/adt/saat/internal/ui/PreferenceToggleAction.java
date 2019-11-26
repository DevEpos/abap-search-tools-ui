package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.util.Logging;

/**
 * Action which holds a boolean value
 *
 * @author stockbal
 */
public class PreferenceToggleAction extends Action {
	protected final IPreferenceStore prefStore;
	private final String preferenceKey;

	public PreferenceToggleAction(final String text, final ImageDescriptor imageDescriptor, final String preferenceKey,
		final boolean defaultPreferenceValue) {
		super(text, Action.AS_CHECK_BOX);
		setImageDescriptor(imageDescriptor);
		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.preferenceKey = preferenceKey;
		this.prefStore.setDefault(this.preferenceKey, defaultPreferenceValue);
		final boolean checked = this.prefStore.getBoolean(this.preferenceKey);
		setChecked(checked);
	}

	/**
	 * Returns the preference key this action toggles
	 *
	 * @return
	 */
	public String getPreferenceKey() {
		return this.preferenceKey;
	}

	@Override
	public final void run() {
		try {
			final boolean checked = isChecked();
			this.prefStore.setValue(this.preferenceKey, checked);
			toggled(checked);
		} catch (final Exception e) {
			Logging.getLogger(getClass()).error(e);
		}
	}

	@Override
	public void setChecked(final boolean checked) {
		super.setChecked(checked);
		this.prefStore.setValue(this.preferenceKey, checked);
	}

	/**
	 * Will be called with the new value of the toggle action
	 * <p>
	 * Default implementation does nothing. Subclasses may override
	 * </p>
	 *
	 * @param checked if <code>true</code> the action is checked
	 */
	protected void toggled(final boolean checked) {
	}
}
