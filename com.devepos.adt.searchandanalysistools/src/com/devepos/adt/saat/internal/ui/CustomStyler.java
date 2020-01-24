package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * Custom styler for JFace Viewers
 *
 * @author stockbal
 */
class CustomStyler extends Styler {
	private final int style;
	private final String foregroundColorName;
	private final String backgroundColorName;
	private Font font;

	public CustomStyler(final int style) {
		this(style, null, null);
	}

	public CustomStyler(final int style, final boolean preventDispose) {
		this(style, null, null);
	}

	public CustomStyler(final int style, final String foregroundColorName, final String backgroundColorName) {
		this.style = style;
		this.foregroundColorName = foregroundColorName;
		this.backgroundColorName = backgroundColorName;
	}

	@Override
	public void applyStyles(final TextStyle textStyle) {
		if (this.font == null) {
			final FontDescriptor fontDescriptor = JFaceResources.getDefaultFontDescriptor().setStyle(this.style);
			this.font = fontDescriptor.createFont(Display.getCurrent());
		}

		textStyle.font = this.font;

		final ColorRegistry colorRegistry = JFaceResources.getColorRegistry();

		if (this.foregroundColorName != null) {
			textStyle.foreground = colorRegistry.get(this.foregroundColorName);
		}
		if (this.backgroundColorName != null) {
			textStyle.background = colorRegistry.get(this.backgroundColorName);
		}
	}

	/**
	 * Disposes allocated resources
	 */
	void dispose() {
		if (this.font != null && !this.font.isDisposed()) {
			this.font.dispose();
			this.font = null;
		}
	}
}
