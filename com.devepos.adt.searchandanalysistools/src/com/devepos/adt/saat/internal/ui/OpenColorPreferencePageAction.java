package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.devepos.adt.saat.internal.messages.Messages;

/**
 * Opens the colors and fon
 * 
 * @author stockbal
 */
public class OpenColorPreferencePageAction extends Action {
	private static final String COLORS_AND_FONTS_PREFERENCES = "org.eclipse.ui.preferencePages.ColorsAndFonts" //$NON-NLS-1$
	;
	private final String colorId;

	public OpenColorPreferencePageAction() {
		this(null);
	}

	public OpenColorPreferencePageAction(final String colorId) {
		super(Messages.OpenColorPreferencePageAction_ActionName_xtol);
		this.colorId = colorId != null && !colorId.isEmpty() ? String.format("selectColor:%s", colorId) : null; //$NON-NLS-1$
	}

	@Override
	public void run() {
		PreferencesUtil
			.createPreferenceDialogOn(null, COLORS_AND_FONTS_PREFERENCES, new String[] { COLORS_AND_FONTS_PREFERENCES },
				this.colorId)
			.open();
	}
}
