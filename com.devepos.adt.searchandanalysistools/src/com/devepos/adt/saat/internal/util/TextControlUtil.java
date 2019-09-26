package com.devepos.adt.saat.internal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * Utility methods for {@link Text} controls
 *
 * @author stockbal
 */
public class TextControlUtil {
	private static final String WORD_BOUNDARIES_REGEX = "([:,=\\.]|\\s+)";

	/**
	 * Adds navigation and deletion support to text control
	 *
	 * @param control the text control to be enhanced
	 */
	public static void addWordSupport(final Text control) {
		addWordSupport(control, WORD_BOUNDARIES_REGEX);
	}

	/**
	 * Adds navigation and deletion support to text control
	 *
	 * @param control             the text control to be enhanced
	 * @param wordBoundariesRegex RegEx String containing all the boundary
	 *                            characters to find the next word
	 */
	public static void addWordSupport(final Text control, final String wordBoundariesRegex) {
		Assert.isNotNull(control);
		control.addKeyListener(new WordKeyListener(control, wordBoundariesRegex));
	}

	private static final class WordKeyListener extends KeyAdapter {
		private final String wordBoundariesRegEx;
		private final Text control;
		private Point lastSelection;
		private boolean shiftActive;

		public WordKeyListener(final Text control, final String wordBoundaries) {
			this.control = control;
			this.wordBoundariesRegEx = wordBoundaries;
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			final boolean ctrlOnly = e.stateMask == SWT.CTRL;
			final boolean ctrlShift = e.stateMask == SWT.CTRL + SWT.SHIFT;
			final boolean isDeletion = ctrlOnly && e.keyCode == SWT.BS;
			final boolean isNavigation = e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT;

			if (isDeletion || (ctrlOnly || ctrlShift) && isNavigation) {
				this.lastSelection = this.control.getSelection();
				this.shiftActive = ctrlShift;
				e.doit = false;
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			final boolean ctrlOnly = e.stateMask == SWT.CTRL;
			final boolean ctrlShift = e.stateMask == SWT.CTRL + SWT.SHIFT;
			final boolean isDeletion = ctrlOnly && e.keyCode == SWT.BS;
			final boolean isNavigation = e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT;

			if (isDeletion || (ctrlOnly || ctrlShift) && isNavigation) {
				if (e.keyCode == SWT.BS) {
					deletePreviousWord();
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					navigateToPreviousWord();
				} else {
					navigateToNextWord();
				}
			}
		}

		private void navigateToNextWord() {
			final String text = this.control.getText();
			final Pattern wordBorderPattern = Pattern.compile(this.wordBoundariesRegEx);
			final Matcher matcher = wordBorderPattern.matcher(text.substring(this.lastSelection.y));
			int wordBoundaryIndex = -1;
			while (matcher.find()) {
				wordBoundaryIndex = matcher.end();
				if (wordBoundaryIndex >= 0) {
					break;
				}
			}
			int selectionStart = this.lastSelection.x;
			int selectionEnd = 0;
			if (wordBoundaryIndex != -1) {
				if (wordBoundaryIndex == 0) {
					wordBoundaryIndex++;
				}
				selectionEnd = this.lastSelection.y + wordBoundaryIndex;
			} else {
				selectionEnd = text.length();
			}
			if (!this.shiftActive) {
				selectionStart = selectionEnd;
			}
			this.control.setSelection(selectionStart, selectionEnd);
		}

		private void navigateToPreviousWord() {
			final String text = this.control.getText();
			final Pattern wordBorderPattern = Pattern.compile(this.wordBoundariesRegEx);
			final Matcher matcher = wordBorderPattern.matcher(text.substring(0, this.lastSelection.x));
			int wordBoundaryIndex = -1;
			String lastMatchedGroup = null;
			while (matcher.find()) {
				lastMatchedGroup = matcher.group();
				if (matcher.start() >= this.lastSelection.x) {
					break;
				} else {
					wordBoundaryIndex = matcher.start();
				}
			}
			int selectionStart = 0;
			int selectionEnd = this.lastSelection.y;
			if (wordBoundaryIndex != -1) {
				if (wordBoundaryIndex == this.lastSelection.x - 1) {
					selectionStart = wordBoundaryIndex;
				} else {
					selectionStart = wordBoundaryIndex;
					if (lastMatchedGroup == null || !lastMatchedGroup.matches("\\s+")) {
						selectionStart++;
					}
				}
			} else {
				selectionStart = 0;
			}
			if (!this.shiftActive) {
				selectionEnd = selectionStart;
			}
			this.control.clearSelection();
			this.control.setSelection(selectionStart, selectionEnd);
		}

		private void deletePreviousWord() {
			final String text = this.control.getText();
			if (this.lastSelection.x != this.lastSelection.y) {
				// delete the selected text
				this.control
					.setText(text.substring(0, this.lastSelection.x) + text.substring(this.lastSelection.y, text.length()));
				this.control.setSelection(this.lastSelection);
			} else {
				// determine string after selection
				final String rest = this.lastSelection.x < text.length() ? text.substring(this.lastSelection.x, text.length())
					: "";
				// find the next word separator
				final Pattern wordBorderPattern = Pattern.compile(this.wordBoundariesRegEx);
				final Matcher matcher = wordBorderPattern.matcher(text.substring(0, this.lastSelection.x));
				int wordBoundaryIndex = -1;
				String lastMatchedGroup = null;
				while (matcher.find()) {
					lastMatchedGroup = matcher.group();
					if (matcher.start() >= this.lastSelection.x) {
						break;
					} else {
						wordBoundaryIndex = matcher.start();
					}
				}
				if (wordBoundaryIndex != -1) {
					if (wordBoundaryIndex == this.lastSelection.x - 1) {
						this.control.setText(text.substring(0, wordBoundaryIndex) + rest);
						this.control.setSelection(wordBoundaryIndex);
					} else {
						if (lastMatchedGroup == null || !lastMatchedGroup.matches("\\s+")) {
							wordBoundaryIndex++;
						}
						this.control.setText(text.substring(0, wordBoundaryIndex) + rest);
						this.control.setSelection(wordBoundaryIndex);
					}
				} else {
					this.control.setText(rest);
					this.control.setSelection(0);
				}

			}
		}

	}
}
