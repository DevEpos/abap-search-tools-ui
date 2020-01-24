package com.devepos.adt.saat.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;

/**
 * Factory for creating font stylers {@see Styler}
 *
 * @author stockbal
 */
public class StylerFactory {
	private static final String STYLE_KEY = "style:%s|foreground:%s|background:%s"; //$NON-NLS-1$
	private static Map<String, CustomStyler> customStylerRegistry;

	/**
	 * {@link Styler} for bold text
	 *
	 * @return
	 */
	public static Styler BOLD_STYLER = new CustomStyler(SWT.BOLD, true);

	/**
	 * {@link Styler} for italic text
	 *
	 * @return
	 */
	public static Styler ITALIC_STYLER = createCustomStyler(SWT.ITALIC, null, null);

	/**
	 * Creates/Retrieves a custom style with the given style, foreground color and
	 * background color
	 *
	 * @param  style               the style for the font. Possible values are
	 *                             {@link SWT#ITALIC}, {@link SWT#BOLD} or
	 *                             {@link SWT#NORMAL}
	 * @param  foregroundColorName the symbolic name of the foreground color
	 * @param  backgroundColorName the symbolic name of the background color
	 * @return                     the custom styler
	 */
	public static CustomStyler createCustomStyler(final int style, final String foregroundColorName,
		final String backgroundColorName) {
		final String stylerKey = String.format(STYLE_KEY, style, foregroundColorName, backgroundColorName);

		return getStyler(stylerKey, style, foregroundColorName, backgroundColorName);
	}

	/**
	 * Cleans the styler registry
	 */
	public static void cleanRegistry() {
		if (customStylerRegistry != null) {
			customStylerRegistry.values().stream().forEach(styler -> styler.dispose());
			customStylerRegistry.clear();
			customStylerRegistry = null;
		}
	}

	private static CustomStyler getStyler(final String stylerKey, final int style, final String foregroundColorName,
		final String backgroundColorName) {
		if (customStylerRegistry == null) {
			customStylerRegistry = new HashMap<>();
		}
		CustomStyler styler = customStylerRegistry.get(stylerKey);
		if (styler == null) {
			styler = new CustomStyler(style, foregroundColorName, backgroundColorName);
			customStylerRegistry.put(stylerKey, styler);
		}
		return styler;
	}
}
