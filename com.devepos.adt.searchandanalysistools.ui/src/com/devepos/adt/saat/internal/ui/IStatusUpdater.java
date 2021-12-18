package com.devepos.adt.saat.internal.ui;

import org.eclipse.core.runtime.IStatus;

/**
 * Interface for UI classes that display a status bar
 */
public interface IStatusUpdater {
  /**
   * Updates the status with the given text and status code
   *
   * @param statusCode status which can be {@link IStatus#OK},
   *                   {@link IStatus#WARNING}, {@link IStatus#INFO} or
   *                   {@link IStatus#ERROR}
   * @param text       the text to be displayed in the status bar
   */
  void updateStatus(final int statusCode, final String text);
}
