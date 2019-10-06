package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * Factory for creating font stylers {@see Styler}
 *
 * @author stockbal
 */
public class StylerFactory {

	/**
	 * {@link Styler} for bold text
	 *
	 * @return
	 */
	public static Styler BOLD_STYLER = new CustomStyler(SWT.BOLD);

	/**
	 * {@link Styler} for italic text
	 *
	 * @return
	 */
	public static Styler ITALIC_STYLER = new CustomStyler(SWT.ITALIC);

	/**
	 * Creates custom {@link Styler} with the given font style and foreground and
	 * background colors
	 *
	 * @param  fontStyle
	 * @param  foregroundColorName
	 * @param  backgroundColorName
	 * @return
	 */
	public static Styler createCustomStyler(final int fontStyle, final String foregroundColorName,
		final String backgroundColorName) {
		return new CustomStyler(fontStyle, foregroundColorName, backgroundColorName);
	}

	private static final class CustomStyler extends Styler implements IPropertyChangeListener {
		private final int style;
		private final String foregroundColorName;
		private final String backgroundColorName;

		public CustomStyler(final int style) {
			this(style, null, null);
		}

		public CustomStyler(final int style, final String foregroundColorName, final String backgroundColorName) {
			this.style = style;
			this.foregroundColorName = foregroundColorName;
			this.backgroundColorName = backgroundColorName;
		}

		@Override
		public void applyStyles(final TextStyle textStyle) {
			final FontDescriptor fontDescriptor = JFaceResources.getDefaultFontDescriptor().setStyle(this.style);
			final Font font = fontDescriptor.createFont(Display.getCurrent());
			textStyle.font = font;

			final ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			colorRegistry.addListener(this);

			if (this.foregroundColorName != null) {
				textStyle.foreground = colorRegistry.get(this.foregroundColorName);
			}
			if (this.backgroundColorName != null) {
				textStyle.background = colorRegistry.get(this.backgroundColorName);
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent event) {

		}

	}
}
